# Assignment **DNS**

The DNS client and server must be implemented. It is implied that the protocol will be implemented at a minimum working
level. Allowed for the following restrictions:

- Support only for Type A records.
- The server address must be equal to the length of a domain name in a request. For example ya.ru. = 0.0.0.6 and
  test.ru. = 0.0.0.8
- There is no need to specify authoritarian servers or additional records in the answer. Only directly the answers.

Description server options:

```sh
./server [port]
port by default 53
```

Descfiption client options:

```sh
./client dns_name [server] [port]
dns_name mandatory option
server by default localhost
port by default 53
```

By default, utilities must use 53 ports

To test a DNS server, use host, dig, or nslookup consoles, explicitly telling them to access the server on the same
computer. Use any existing DNS server, such as Yandex DNS (77.88.8.8), to verify your DNS client.

[RFC DNS](https://tools.ietf.org/html/rfc1035) In addition, there are many simplified descriptions of the DNS protocol.

The directory structure of the project looks like this:

```
├── Dockerfile
├── dns_protocol.h
├── server.cpp
├── client.cpp
├── Makefile
└── README.md
```

It is forbidden to modify **Makefile/README.md/.clang-format/.gitlab-ci.yml** and other hidden files in this directory.
The rest of the files can be changed at your discretion. You can also add new cpp/h files if necessary. If you want to
use CMake instead of GMake, you can add **CMakeLists.txt **, but do not forget that ** Makefile** is forbidden to touch.

# Setup Environment

All tests are run under `ubuntu:18.04`. You can choose a different version of ubuntu or a different distribution. But
then the tests work on `ubuntu:18.04` is not guaranteed. To use `ubuntu:18.04` there are at least three ways:

- native OS installation
- installation using Virtual Box
- installation using docker

Recommended method using docker. If you want to try another way, then it's under your own setup. Docker usage example:

1. Building an image

 ```bash
$ sudo docker build -t dns:latest .
Sending build context to Docker daemon  26.03MB
Step 1/5 : FROM ubuntu:18.04
 ---> 3339fde08fc3
Step 2/5 : LABEL name="dns"
 ---> Using cache
 ---> 18d4d446880d
Step 3/5 : RUN apt update
 ---> Using cache
 ---> 7b8914da4b6a
Step 4/5 : RUN apt install -y --allow-unauthenticated     clang     g++     clang-tidy     clang-format-7     make
 ---> Using cache
 ---> 73647810ca79
Step 5/5 : RUN ln -s /usr/bin/clang-format-7 /usr/bin/clang-format
 ---> Using cache
 ---> d149e813ff12
Successfully built d149e813ff12
Successfully tagged dns:latest
 ```

2. Running an instance

 ```bash
$ sudo docker run -v $(pwd):/home/dns --rm -it dns
root@352d2930048c:/#
 ```

Running tidy tests (static analyzer):

```bash
$ make clang_tidy
clang-tidy *.cpp tests/*.cpp -checks=-*,clang-analyzer-*,-clang-analyzer-cplusplus* -- -I tests -I . -std=c++17 -g -O3 -Werror -Wall -Wextra -pthread -pedantic
```

Your code must comply with [google style guide](https://google.github.io/styleguide/cppguide.html ). To check this
condition, you need to run:

```bash
$ make clang_format
diff -u <(cat *.cpp *.h tests/*.cpp) <(clang-format *.cpp *.h tests/*.cpp)
```

If clang-format gives an error, then you can fix the code under style guide using the command:

```bash
$ make clang_format_fix
clang-format -i *.cpp *.h tests/*.cpp
```

# Publish Results

- In this repository, you need to develop in the development branch
- After you decide that the task is ready, you need to create a merge request in the master branch. You need to add one
  of the course teachers to reviewers
- Further communication, code review, testing will take place through this merge request
- All corrections of comments should go through one merge request. **No need to create separate merge requests after
  each fix.**
- After the merge request is approved and merged in the master, the task is considered completed

# Pipeline

After creating a merge request, a pipeline will appear in your branch consisting of 3 stages **build**, **tidy**, **test
**.

- **build** - checking the build of the project under different compilers clang++/g++
- **tidy** - static analyzer and style guide compliance check
- **test** - integration tests that check the performance of your project

In addition to pipeline stages, merge request will display tests and their execution results. For dropped tests, you can
see the detailed reason for the falls. Detailed information about the gitlab pipeline can be
found [here](https://docs.gitlab.com/ee/ci/pipelines/)

