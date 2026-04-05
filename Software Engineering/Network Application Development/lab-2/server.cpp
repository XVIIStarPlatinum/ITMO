// server.cpp — DNS A-record server for tests
#include "dns_protocol.h"

#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <unistd.h>

#include <cerrno>
#include <csignal>
#include <cstdint>
#include <cstring>
#include <iostream>
#include <string>
#include <vector>

static volatile bool keep_running = true;
static void sigint_handler(int) { keep_running = false; }

constexpr size_t MAX_BUF = 1500;

std::string extract_qname(const uint8_t* buf, size_t buflen, size_t pos) {
  std::string out;
  while (pos < buflen) {
    uint8_t len = buf[pos];
    if (len == 0) break;
    pos++;
    if (pos + len > buflen) break;
    if (!out.empty()) out.push_back('.');
    out.append(reinterpret_cast<const char*>(buf + pos), len);
    pos += len;
  }
  if (out.empty()) return ".";
  return out;
}

ssize_t skip_qname(const uint8_t* buf, size_t buflen, size_t pos) {
  while (pos < buflen) {
    uint8_t len = buf[pos];
    if (len == 0) return pos + 1;
    pos += 1 + len;
  }
  return -1;
}

int main(int argc, char** argv) {
  uint16_t port = 53;
  if (argc >= 2) port = static_cast<uint16_t>(std::stoi(argv[1]));

  signal(SIGINT, sigint_handler);

  int sock = socket(AF_INET, SOCK_DGRAM, 0);
  if (sock < 0) {
    std::perror("socket");
    return 1;
  }
  int opt = 1;
  setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));

  sockaddr_in srv{};
  srv.sin_family = AF_INET;
  srv.sin_addr.s_addr = INADDR_ANY;
  srv.sin_port = htons(port);

  if (bind(sock, reinterpret_cast<sockaddr*>(&srv), sizeof(srv)) < 0) {
    std::perror("bind");
    close(sock);
    return 2;
  }

  std::cout << "DNS server listening on 0.0.0.0:" << port << "\n";

  uint8_t buf[MAX_BUF];

  while (keep_running) {
    sockaddr_in cli{};
    socklen_t cli_len = sizeof(cli);

    ssize_t len = recvfrom(sock, buf, sizeof(buf), 0,
                           reinterpret_cast<sockaddr*>(&cli), &cli_len);

    if (len < 0) {
      if (errno == EINTR) continue;
      std::perror("recvfrom");
      break;
    }
    if (len < 12) continue;

    uint16_t id = (buf[0] << 8) | buf[1];
    uint16_t flags = (buf[2] << 8) | buf[3];

    std::string qname = extract_qname(buf, len, 12);

    ssize_t offset = skip_qname(buf, len, 12);
    if (offset < 0 || offset + 3 >= len) continue;

    uint16_t qtype = (buf[offset] << 8) | buf[offset + 1];
    uint16_t qclass = (buf[offset + 2] << 8) | buf[offset + 3];
    offset += 4;

    constexpr const char* SUFFIX = ".auto.internal";
    if (qname.size() > strlen(SUFFIX) &&
        qname.compare(qname.size() - strlen(SUFFIX), strlen(SUFFIX), SUFFIX) ==
            0) {
      qname = qname.substr(0, qname.size() - strlen(SUFFIX));
      if (qname.empty()) qname = ".";
    }

    std::vector<uint8_t> out;
    out.resize(12);

    out.insert(out.end(), buf + 12, buf + offset);

    out[0] = id >> 8;
    out[1] = id & 0xFF;

    uint16_t resp_flags = 0x8000;
    if (flags & 0x0100) resp_flags |= 0x0100;

    out[2] = resp_flags >> 8;
    out[3] = resp_flags & 0xFF;

    out[4] = 0x00;
    out[5] = 0x01;
    out[6] = 0x00;
    out[7] = 0x00;
    out[8] = 0x00;
    out[9] = 0x00;
    out[10] = 0x00;
    out[11] = 0x00;
    if (qtype == T_A && qclass == 1) {
      int dlen = qname.size();
      if (qname == ".") dlen = 1;

      if (dlen > 255) dlen %= 256;
      if (dlen == 0) dlen = 1;

      out[6] = 0x00;
      out[7] = 0x01;

      out.push_back(0xC0);
      out.push_back(0x0C);

      out.push_back(0x00);
      out.push_back(0x01);

      out.push_back(0x00);
      out.push_back(0x01);

      out.push_back(0x00);
      out.push_back(0x00);
      out.push_back(0x01);
      out.push_back(0x2C);

      out.push_back(0x00);
      out.push_back(0x04);

      out.push_back(0x00);
      out.push_back(0x00);
      out.push_back(0x00);
      out.push_back(static_cast<uint8_t>(dlen));
    }

    sendto(sock, out.data(), out.size(), 0, reinterpret_cast<sockaddr*>(&cli),
           cli_len);
  }

  close(sock);
  return 0;
}
