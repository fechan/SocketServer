FROM openjdk:19
COPY . /usr/src/SocketServer
WORKDIR /usr/src/SocketServer
RUN javac SocketServer.java
CMD ["java", "SocketServer"]