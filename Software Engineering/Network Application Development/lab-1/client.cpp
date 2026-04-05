#include <arpa/inet.h>
#include <netdb.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <termios.h>
#include <unistd.h>

#include <algorithm>
#include <atomic>
#include <cerrno>
#include <cstring>
#include <deque>
#include <functional>
#include <iostream>
#include <mutex>
#include <string>
#include <thread>

using std::cerr;
using std::cout;
using std::endl;

static const uint32_t MAX_NICK = 256;
static const uint32_t MAX_BODY = 65536;

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

std::mutex print_m;
std::atomic_bool running{true};
std::atomic_bool composing{false};
std::deque<std::string> pending_msgs;
std::mutex pending_m;
std::atomic_bool prompt_shown{false};

struct TermState {
  struct termios orig {};
  bool saved = false;
} term_state;

bool set_noncanonical_stdin() {
  if (!isatty(STDIN_FILENO)) return false;
  if (tcgetattr(STDIN_FILENO, &term_state.orig) == -1) return false;
  struct termios t = term_state.orig;
  t.c_lflag &= ~(ICANON | ECHO);
  t.c_cc[VMIN] = 1;
  t.c_cc[VTIME] = 0;
  if (tcsetattr(STDIN_FILENO, TCSANOW, &t) == -1) return false;
  term_state.saved = true;
  return true;
}

void restore_stdin() {
  if (term_state.saved) {
    tcsetattr(STDIN_FILENO, TCSANOW, &term_state.orig);
    term_state.saved = false;
  }
}

static std::string format_incoming(const std::string& date,
                                   const std::string& nick,
                                   const std::string& body) {
  return "{" + date + "} [" + nick + "] " + body;
}

void receiver_loop(int sockfd) {
  while (running) {
    uint32_t net_nick_len;
    ssize_t r = read_n(sockfd, &net_nick_len, sizeof(net_nick_len));
    if (r == 0) break;
    if (r < 0) {
      if (errno == EINTR) continue;
      perror("read");
      break;
    }
    uint32_t nick_len = ntohl(net_nick_len);
    if (nick_len == 0 || nick_len > MAX_NICK) break;

    std::string nick;
    nick.resize(nick_len);
    if (read_n(sockfd, &nick[0], nick_len) <= 0) break;

    uint32_t net_body_len;
    if (read_n(sockfd, &net_body_len, sizeof(net_body_len)) <= 0) break;
    uint32_t body_len = ntohl(net_body_len);
    if (body_len > MAX_BODY) break;

    std::string body;
    body.resize(body_len);
    if (read_n(sockfd, &body[0], body_len) <= 0) break;

    uint32_t net_date_len;
    if (read_n(sockfd, &net_date_len, sizeof(net_date_len)) <= 0) break;
    uint32_t date_len = ntohl(net_date_len);
    if (date_len > 64) break;
    std::string date;
    date.resize(date_len);
    if (read_n(sockfd, &date[0], date_len) <= 0) break;

    std::string formatted = format_incoming(date, nick, body);

    if (composing) {
      std::lock_guard<std::mutex> lk(pending_m);
      pending_msgs.push_back(formatted);
    } else {
      std::lock_guard<std::mutex> lk(print_m);
      cout << "\r" << formatted << "\n> " << std::flush;
      prompt_shown = true;
    }
  }
  running = false;
}

int connect_to(const char* host, const char* port) {
  struct addrinfo hints {
  }, *res = nullptr, *rp = nullptr;
  hints.ai_family = AF_UNSPEC;
  hints.ai_socktype = SOCK_STREAM;
  int s = getaddrinfo(host, port, &hints, &res);
  if (s != 0) {
    cerr << "getaddrinfo: " << gai_strerror(s) << endl;
    return -1;
  }
  int sock = -1;
  for (rp = res; rp; rp = rp->ai_next) {
    sock = socket(rp->ai_family, rp->ai_socktype, rp->ai_protocol);
    if (sock == -1) continue;
    if (connect(sock, rp->ai_addr, rp->ai_addrlen) == 0) break;
    close(sock);
    sock = -1;
  }
  freeaddrinfo(res);
  return sock;
}

int main(int argc, char* argv[]) {
  if (argc < 4) {
    cerr << "usage: " << argv[0] << " <host> <port> <nickname>" << endl;
    return 1;
  }
  const char* host = argv[1];
  const char* port = argv[2];
  std::string nickname = argv[3];
  if (nickname.empty() || nickname.size() > MAX_NICK) {
    cerr << "nickname length must be 1.." << MAX_NICK << endl;
    return 1;
  }

  int sock = connect_to(host, port);
  if (sock == -1) {
    cerr << "Failed to connect to " << host << ":" << port << endl;
    return 1;
  }

  std::thread recv_th(receiver_loop, sock);
  bool have_tty = set_noncanonical_stdin();

  {
    std::lock_guard<std::mutex> lk(print_m);
    cout << "Connected. Press 'm' to compose a message. Ctrl+C to quit."
         << endl;
  }
  prompt_shown = false;
  while (running) {
    if (!have_tty) {
      {
        std::string line;
        {
          std::lock_guard<std::mutex> lk(print_m);
          cout << "> " << std::flush;
        }
        if (!std::getline(std::cin, line)) {
          running = false;
          break;
        }
        if (line == "m") {
          std::string msg;
          if (!std::getline(std::cin, msg)) {
            running = false;
            break;
          }
          line = msg;
        }

        if (line.empty()) {
          continue;
        }
        if (line == "/quit" || line == "/exit") {
          running = false;
          break;
        }

        uint32_t net_nick = htonl((uint32_t)nickname.size());
        uint32_t net_body = htonl((uint32_t)line.size());
        std::string out;
        out.reserve(4 + nickname.size() + 4 + line.size());
        out.append(reinterpret_cast<const char*>(&net_nick), 4);
        out.append(nickname);
        out.append(reinterpret_cast<const char*>(&net_body), 4);
        out.append(line);
        if (write_n(sock, out.data(), out.size()) != (ssize_t)out.size()) {
          perror("write");
          running = false;
          break;
        }
      }
      {
        std::lock_guard<std::mutex> pl(pending_m);
        if (!pending_msgs.empty()) {
          std::lock_guard<std::mutex> lprint(print_m);
          while (!pending_msgs.empty()) {
            cout << pending_msgs.front() << endl;
            pending_msgs.pop_front();
          }
        }
      }
      continue;
    }

    if (!prompt_shown) {
      std::lock_guard<std::mutex> lk(print_m);
      cout << "> " << std::flush;
      prompt_shown = true;
    }

    char ch = 0;
    ssize_t rn = read(STDIN_FILENO, &ch, 1);
    if (rn <= 0) {
      running = false;
      break;
    }
    if (ch == 'm') {
      composing = true;
      tcflush(STDIN_FILENO, TCIFLUSH);
      restore_stdin();
      std::cin.clear();
      std::string line;
      {
        std::lock_guard<std::mutex> lk(print_m);
        cout << "Enter message: " << std::flush;
      }
      std::getline(std::cin, line);

      if (!running) break;
      if (!std::cin && line.empty()) {
        running = false;
        break;
      }

      if (!line.empty() && (line == "/quit" || line == "/exit")) {
        running = false;
        if (have_tty) {
          tcflush(STDIN_FILENO, TCIFLUSH);
          set_noncanonical_stdin();
        }
        break;
      }

      if (!line.empty()) {
        uint32_t net_nick = htonl((uint32_t)nickname.size());
        uint32_t net_body = htonl((uint32_t)line.size());
        std::string out;
        out.reserve(4 + nickname.size() + 4 + line.size());
        out.append(reinterpret_cast<const char*>(&net_nick), 4);
        out.append(nickname);
        out.append(reinterpret_cast<const char*>(&net_body), 4);
        out.append(line);

        if (write_n(sock, out.data(), out.size()) != (ssize_t)out.size()) {
          std::lock_guard<std::mutex> lk(print_m);
          perror("write");
          running = false;
        }
      }

      {
        std::lock_guard<std::mutex> pl(pending_m);
        if (!pending_msgs.empty()) {
          std::lock_guard<std::mutex> lprint(print_m);
          while (!pending_msgs.empty()) {
            cout << pending_msgs.front() << endl;
            pending_msgs.pop_front();
          }
        }
      }

      if (have_tty) {
        tcflush(STDIN_FILENO, TCIFLUSH);
        set_noncanonical_stdin();
      }
      prompt_shown = false;
      composing = false;
      continue;
    } else if (ch == '\n' || ch == '\r') {
      {
        std::lock_guard<std::mutex> lk(print_m);
        cout << "\r\n" << std::flush;
      }
      prompt_shown = false;
      continue;
    } else {
      continue;
    }
  }

  restore_stdin();
  shutdown(sock, SHUT_RDWR);
  close(sock);
  running = false;
  if (recv_th.joinable()) recv_th.join();
  cout << "Client exiting" << endl;
  return 0;
}