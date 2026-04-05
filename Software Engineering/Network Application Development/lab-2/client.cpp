// client.cpp
// Simple UDP DNS client (A records only). Works with provided dns_protocol.h
#include "dns_protocol.h"

#include <arpa/inet.h>
#include <netdb.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <unistd.h>
#include <chrono>
#include <cstring>
#include <iostream>
#include <random>
#include <string>
#include <vector>

static uint16_t random_id() {
  static std::mt19937 rng(static_cast<unsigned int>(
      std::chrono::high_resolution_clock::now().time_since_epoch().count()));
  return static_cast<uint16_t>(rng() & 0xFFFF);
}

// convert textual name "ya.ru" or "ya.ru." to qname wire format (ends with 0)
std::vector<uint8_t> qname_from_text(const std::string &text) {
  std::string t = text;
  if (!t.empty() && t.back() == '.') {
    t.pop_back();
  }
  std::vector<uint8_t> out;
  size_t pos = 0;
  while (pos < t.size()) {
    size_t dot = t.find('.', pos);
    std::string label =
        (dot == std::string::npos) ? t.substr(pos) : t.substr(pos, dot - pos);
    if (label.size() > 63) {
      label = label.substr(0, 63);
    }
    out.push_back(static_cast<uint8_t>(label.size()));
    out.insert(out.end(), label.begin(), label.end());
    if (dot == std::string::npos) {
      break;
    }
    pos = dot + 1;
  }
  out.push_back(0);
  return out;
}

// parse qname, similar to server
ssize_t parse_qname(const uint8_t *buf, size_t buflen, size_t pos,
                    std::string &name_out) {
  name_out.clear();
  size_t orig = pos;
  bool jumped = false;
  int jumps = 0;
  const int MAX_JUMPS = 20;
  while (pos < buflen) {
    uint8_t len = buf[pos];
    if ((len & 0xC0) == 0xC0) {
      if (pos + 1 >= buflen) {
        return -1;
      }
      uint16_t ptr = ((len & 0x3F) << 8) | buf[pos + 1];
      if (++jumps > MAX_JUMPS) {
        return -1;
      }
      if (!jumped) {
        orig = pos + 2;
      }
      pos = ptr;
      jumped = true;
      continue;
    }
    if (len == 0) {
      if (!jumped) {
        orig = pos + 1;
      }
      if (name_out.empty()) {
        name_out = ".";
      } else if (name_out.back() != '.') {
        name_out.push_back('.');
      }
      return static_cast<ssize_t>(orig);
    }
    if (pos + 1 + len > buflen) return -1;
    if (!name_out.empty()) {
      name_out.push_back('.');
    }
    name_out.append(reinterpret_cast<const char *>(buf + pos + 1), len);
    pos += 1 + len;
  }
  return -1;
}

int main(int argc, char **argv) {
  if (argc < 2) {
    std::cerr << "Usage: " << argv[0] << " dns_name [server] [port]\n";
    return 2;
  }
  std::string dns_name = argv[1];
  std::string server = "127.0.0.1";
  uint16_t port = 53;
  if (argc >= 3) server = argv[2];
  if (argc >= 4) port = static_cast<uint16_t>(std::stoi(argv[3]));

  sockaddr_in serv{};
  serv.sin_family = AF_INET;
  serv.sin_port = htons(port);
  if (inet_pton(AF_INET, server.c_str(), &serv.sin_addr) != 1) {
    hostent *h = gethostbyname(server.c_str());
    if (!h) {
      std::cerr << "Cannot resolve server: " << server << "\n";
      return 3;
    }
    memcpy(&serv.sin_addr, h->h_addr_list[0], h->h_length);
  }

  int sock = socket(AF_INET, SOCK_DGRAM, 0);
  if (sock < 0) {
    std::perror("socket");
    return 4;
  }
  struct timeval tv {};
  tv.tv_sec = 3;
  setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));

  std::vector<uint8_t> msg;
  uint16_t id = random_id();
  msg.push_back(static_cast<uint8_t>((id >> 8) & 0xFF));
  msg.push_back(static_cast<uint8_t>(id & 0xFF));

  msg.push_back(0x01);
  msg.push_back(0x00);

  msg.push_back(0x00);
  msg.push_back(0x01);

  msg.push_back(0x00);
  msg.push_back(0x00);
  msg.push_back(0x00);
  msg.push_back(0x00);
  msg.push_back(0x00);
  msg.push_back(0x00);

  auto qwire = qname_from_text(dns_name);
  msg.insert(msg.end(), qwire.begin(), qwire.end());
  // QTYPE = A
  msg.push_back(0x00);
  msg.push_back(static_cast<uint8_t>(T_A & 0xFF));
  // QCLASS = IN
  msg.push_back(0x00);
  msg.push_back(0x01);

  ssize_t sent = sendto(sock, msg.data(), msg.size(), 0,
                        reinterpret_cast<sockaddr *>(&serv), sizeof(serv));
  if (sent < 0) {
    std::perror("sendto");
    close(sock);
    return 5;
  }

  uint8_t resp[1500];
  ssize_t r = recvfrom(sock, resp, sizeof(resp), 0, nullptr, nullptr);
  if (r <= 0) {
    std::perror("recvfrom");
    close(sock);
    return 6;
  }
  if (r < 12) {
    std::cerr << "Short response"
              << "\n";
    close(sock);
    return 7;
  }

  uint16_t ancount = (resp[6] << 8) | resp[7];
  uint16_t qdcount = (resp[4] << 8) | resp[5];

  size_t off = 12;
  // skip questions
  for (int i = 0; i < qdcount; ++i) {
    std::string skip;
    ssize_t nx = parse_qname(resp, r, off, skip);
    if (nx < 0) {
      std::cerr << "Malformed question"
                << "\n";
      close(sock);
      return 8;
    }
    off = static_cast<size_t>(nx);
    if (off + 4 > static_cast<size_t>(r)) {
      std::cerr << "Malformed question tail"
                << "\n";
      close(sock);
      return 9;
    }
    off += 4;
  }

  bool found = false;
  for (int i = 0; i < ancount; ++i) {
    std::string name;
    ssize_t nx = parse_qname(resp, r, off, name);
    if (nx < 0) {
      std::cerr << "Malformed answer name"
                << "\n";
      break;
    }
    off = static_cast<size_t>(nx);
    if (off + 10 > static_cast<size_t>(r)) {
      std::cerr << "Truncated answer\n";
      break;
    }
    uint16_t atype = (resp[off] << 8) | resp[off + 1];
    uint16_t aclass = (resp[off + 2] << 8) | resp[off + 3];
    uint32_t ttl = (resp[off + 4] << 24) | (resp[off + 5] << 16) |
                   (resp[off + 6] << 8) | resp[off + 7];
    uint16_t rdlen = (resp[off + 8] << 8) | resp[off + 9];
    off += 10;
    if (off + rdlen > static_cast<size_t>(r)) {
      std::cerr << "Truncated rdata\n";
      break;
    }

    if (atype == T_A && aclass == 1 && rdlen == 4) {
      char ipbuf[INET_ADDRSTRLEN];
      inet_ntop(AF_INET, resp + off, ipbuf, sizeof(ipbuf));
      std::cout << name << " -> " << ipbuf << " (ttl=" << ttl << ")\n";
      found = true;
    }
    off += rdlen;
  }

  if (!found) {
    std::cout << "No A records found"
              << "\n";
  }
  close(sock);
  return 0;
}
