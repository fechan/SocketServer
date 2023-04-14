import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class SocketServer {

    public static final int PORT = 3001; // TODO: make this 17

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        
        threadPool.execute(() -> listenUDP(PORT));
        listenTCP(PORT, threadPool);
    }

    private static byte[] getQuote() {
        return "THIS IS A TEST QUOTE".getBytes(StandardCharsets.US_ASCII);
    }

    private static void listenTCP(int port, ExecutorService threadPool) {
        try (
            ServerSocket server = new ServerSocket(port);
        ) {
            while (true) {
                System.out.println("[TCP] Waiting for quote request");
                Socket sock = server.accept();
                System.out.println("[TCP] TCP request accepted");
                threadPool.execute(() -> respondTCP(sock));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void respondTCP(Socket sock) {
        try {
            System.out.println("[TCP] Sending quote");
            OutputStream sockOut = sock.getOutputStream();
            sockOut.write(getQuote());
            System.out.println("[TCP] Closing socket after quote sent");
            sock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void listenUDP(int port) {
        while (true) {
            try (
                DatagramSocket sock = new DatagramSocket(port);
            ) {
                System.out.println("[UDP] Waiting for quote request");
                byte[] dummy = new byte[0];
                DatagramPacket quoteRequest = new DatagramPacket(dummy, 0);
                sock.receive(quoteRequest);
                InetAddress requestor = quoteRequest.getAddress();
                int requestorPort = quoteRequest.getPort();
                System.out.println("[UDP] Got quote request from " + requestor.getHostAddress());

                byte[] responseBody = getQuote();
                DatagramPacket quoteResponse = new DatagramPacket(responseBody, responseBody.length, requestor, requestorPort);
                sock.send(quoteResponse);
                System.out.println("[UDP] Quote sent to " + requestor.getHostAddress());
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}