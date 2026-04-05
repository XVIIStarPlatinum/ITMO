#include <arpa/inet.h>
#include <netdb.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>

#include <algorithm>
#include <atomic>
#include <cerrno>
#include <chrono>
#include <csignal>
#include <cstring>
#include <ctime>
#include <functional>
#include <iostream>
#include <memory>
#include <mutex>
#include <string>
#include <thread>
#include <vector>

using std::cerr;
using std::cout;
using std::endl;

static const uint32_t MAX_NICK = 256;
static const uint32_t MAX_BODY = 65536;

static int listen_fd = -1;
static std::atomic_bool running{true};

static void handle_sigint(int) {
  running = false;
  if (listen_fd != -1) close(listen_fd);
}

ssize_t read_n(int fd, void* buf, size_t n) {
  char* p = (char*)buf;
  size_t left = n;
  while (left) {
    ssize_t r = recv(fd, p, left, 0);
    if (r == 0) return 0;
    if (r < 0) {
      if (errno == EINTR) continue;
      return -1;
    }
    left -= r;
    p += r;
  }
  return (ssize_t)n;
}

ssize_t write_n(int fd, const void* buf, size_t n) {
  const char* p = (const char*)buf;
  size_t left = n;
  while (left) {
    ssize_t w = send(fd, p, left, 0);
    if (w <= 0) {
      if (w < 0 && errno == EINTR) continue;
      return -1;
    }
    left -= w;
    p += w;
  }
  return (ssize_t)n;
}

struct Client {
  int fd;
  std::thread thr;
  std::atomic_bool alive{true};
  Client(int fd_) : fd(fd_) {}
};

std::mutex clients_m;
std::vector<std::shared_ptr<Client>> clients;

std::string time_hhmm() {
  std::time_t t = std::time(nullptr);
  std::tm tm{};
  localtime_r(&t, &tm);
  char buf[16];
  if (std::strftime(buf, sizeof(buf), "%H:%M", &tm) == 0) {
    return std::string("00:00");
  }
  return std::string(buf);
}

void broadcast_to_all(const std::string& buf) {
  std::vector<std::shared_ptr<Client>> copy;
  {
    std::lock_guard<std::mutex> lk(clients_m);
    copy = clients;
  }

  for (auto& c : copy) {
    if (!c->alive) continue;
    if (write_n(c->fd, buf.data(), buf.size()) != (ssize_t)buf.size()) {
      c->alive = false;
      close(c->fd);
    }
  }

  {
    std::lock_guard<std::mutex> lk(clients_m);
    clients.erase(std::remove_if(clients.begin(), clients.end(),
                                 [](const std::shared_ptr<Client>& p) {
                                   return !p->alive.load();
                                 }),
                  clients.end());
  }
}

void client_handler(std::shared_ptr<Client> client_ptr) {
  int fd = client_ptr->fd;
  while (running && client_ptr->alive) {
    uint32_t net_nick_len;
    ssize_t r = read_n(fd, &net_nick_len, sizeof(net_nick_len));
    if (r == 0 || r == -1) break;
    uint32_t nick_len = ntohl(net_nick_len);
    if (nick_len == 0 || nick_len > MAX_NICK) break;

    std::string nick;
    nick.resize(nick_len);
    if (read_n(fd, &nick[0], nick_len) <= 0) break;

    uint32_t net_body_len;
    if (read_n(fd, &net_body_len, sizeof(net_body_len)) <= 0) break;
    uint32_t body_len = ntohl(net_body_len);
    if (body_len > MAX_BODY) break;

    std::string body;
    body.resize(body_len);
    if (read_n(fd, &body[0], body_len) <= 0) break;

    std::string date = time_hhmm();
    uint32_t dn = htonl((uint32_t)nick.size());
    uint32_t db = htonl((uint32_t)body.size());
    uint32_t dd = htonl((uint32_t)date.size());

    std::string out;
    out.reserve(4 + nick.size() + 4 + body.size() + 4 + date.size());
    out.append(reinterpret_cast<const char*>(&dn), 4);
    out.append(nick);
    out.append(reinterpret_cast<const char*>(&db), 4);
    out.append(body);
    out.append(reinterpret_cast<const char*>(&dd), 4);
    out.append(date);

    broadcast_to_all(out);
  }

  client_ptr->alive = false;
  close(fd);
  {
    std::lock_guard<std::mutex> lk(clients_m);
    clients.erase(std::remove_if(clients.begin(), clients.end(),
                                 [&](const std::shared_ptr<Client>& p) {
                                   return p.get() == client_ptr.get() ||
                                          !p->alive.load();
                                 }),
                  clients.end());
  }
}

int main(int argc, char* argv[]) {
  signal(SIGINT, handle_sigint);
  const char* port = "5001";
  if (argc >= 2) port = argv[1];

  struct addrinfo hints {
  }, *res, *rp;
  hints.ai_family = AF_UNSPEC;
  hints.ai_socktype = SOCK_STREAM;
  hints.ai_flags = AI_PASSIVE;

  int s = getaddrinfo(nullptr, port, &hints, &res);
  if (s != 0) {
    cerr << "getaddrinfo: " << gai_strerror(s) << endl;
    return 1;
  }

  for (rp = res; rp != nullptr; rp = rp->ai_next) {
    listen_fd = socket(rp->ai_family, rp->ai_socktype, rp->ai_protocol);
    if (listen_fd == -1) continue;
    int yes = 1;
    setsockopt(listen_fd, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(yes));
    if (bind(listen_fd, rp->ai_addr, rp->ai_addrlen) == 0) break;
    close(listen_fd);
    listen_fd = -1;
  }

  freeaddrinfo(res);

  if (listen_fd == -1) {
    cerr << "Failed to bind socket on port " << port << endl;
    return 1;
  }

  if (listen(listen_fd, 10) == -1) {
    perror("listen");
    close(listen_fd);
    return 1;
  }

  cout << "Server listening on port " << port << endl;

  while (running) {
    struct sockaddr_storage cli_addr;
    socklen_t cli_len = sizeof(cli_addr);
    int newsock = accept(listen_fd, (struct sockaddr*)&cli_addr, &cli_len);
    if (newsock == -1) {
      if (errno == EINTR) continue;
      perror("accept");
      break;
    }

    auto client = std::make_shared<Client>(newsock);
    {
      std::lock_guard<std::mutex> lk(clients_m);
      clients.push_back(client);
    }
    client->thr = std::thread([client] { client_handler(client); });
    client->thr.detach();

    {
      std::lock_guard<std::mutex> lk(clients_m);
      clients.erase(std::remove_if(clients.begin(), clients.end(),
                                   [](const std::shared_ptr<Client>& p) {
                                     return !p->alive.load();
                                   }),
                    clients.end());
    }
  }

  {
    std::lock_guard<std::mutex> lk(clients_m);
    for (auto& c : clients) {
      c->alive = false;
      close(c->fd);
    }
    clients.clear();
  }
  if (listen_fd != -1) close(listen_fd);
  cout << "Server shutting down" << endl;
  return 0;
}
