# SocketServer
UW Info networking class QOTD server listening on both TCP/UDP port 17. Conforms to RFC 865.

## Requirements
SocketServer was built and tested on Java 19 on Arch Linux and Oracle Linux (via Docker image).

## Build instructions
### Bare metal
Depending on how your machine is configured, you will need to run SocketServer as root, since this listens on port 17.
1. `javac SocketServer.java`
2. `java SocketServer`
### Docker compose
You will need docker and docker-compose. You can change the port Docker proxies to in `docker-compose.yml`.
1. `sudo docker-compose build`
2. `sudo docker-compose up`

## Getting quotes
You can use netcat to request quotes from TCP or UDP. If the server is running from localhost, for TCP, run `nc localhost 17`. For UDP, run `nc -u localhost 17` (this might give you a blank console. You have to hit "enter" to send the first UDP packet. See the netcat docs for more information).