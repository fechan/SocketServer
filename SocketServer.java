import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class SocketServer {

    public static final int PORT = 3001; // TODO: make this 17

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        UniqueQuoteProvider quotes = new SocketServer().new UniqueQuoteProvider();
        
        threadPool.execute(() -> listenUDP(PORT, quotes));
        listenTCP(PORT, threadPool, quotes);
    }

    private static void listenTCP(int port, ExecutorService threadPool, UniqueQuoteProvider quotes) {
        try (
            ServerSocket server = new ServerSocket(port);
        ) {
            while (true) {
                System.out.println("[TCP] Waiting for quote request");
                Socket sock = server.accept();
                System.out.println("[TCP] TCP request accepted");
                threadPool.execute(() -> respondTCP(sock, quotes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void respondTCP(Socket sock, UniqueQuoteProvider quotes) {
        try {
            System.out.println("[TCP] Sending quote");
            OutputStream sockOut = sock.getOutputStream();
            sockOut.write(quotes.getQuote());
            System.out.println("[TCP] Closing socket after quote sent");
            sock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void listenUDP(int port, UniqueQuoteProvider quotes) {
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

                byte[] responseBody = quotes.getQuote();
                DatagramPacket quoteResponse = new DatagramPacket(responseBody, responseBody.length, requestor, requestorPort);
                sock.send(quoteResponse);
                System.out.println("[UDP] Quote sent to " + requestor.getHostAddress());
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private class UniqueQuoteProvider {
        private static final String[] quotesPrincessBride = {
            "Never get involved in a land war in Asia.",
            "Never go in against a Sicillian when death is on the line.",
            "Let me put it this way, have you ever heard of Plato, Aristotle, Socrates? Morons.",
            "You mean you'll put down your rock and I'll put down my sword, and we'll try and kill each other like civilized people?",
            "I'll explain and I'll use small words so that you'll be sure to understand, you warthog faced buffoon.",
            "My name is Inigo Montoya. You killed my father. Prepare to die.",
            "I do not mean to pry, but you don't by any chance happen to have six fingers on your right hand?",
            "Inconceivable!",
            "You keep using that word. I do not think it means what you think it means."
        };

        private static final String[] quotesOther = {
            "Life moves pretty fast. If you don't stop and look around once in a while, you could miss it.",
            "I wanted a car. I got a computer. How's that for being born under a bad sign?",
            "I don't trust this kid any further than I can throw him!",
            "Um, he's sick. My best friend's sister's boyfriend's brother's girlfriend heard from this guy who knows this kid who's going with the girl who saw Ferris pass out at 31 Flavors last night.",
            "The question isn't 'what are we going to do,' the question is 'what aren't we going to do?",
            "A man with priorities so far out of whack doesn't deserve such a fine automobile.",
            "First of all, you can never go too far. Second of all, if I'm going to be caught, it's not gonna be by a guy like that!",
            "If you're not over here in 15 minutes, you can find a new best friend."
        };

        private int princessBrideIndex = 0;
        private int otherIndex = 0;

        private final Random random = new Random();

        public byte[] getQuote() {
            String response = "== Quote generated by Frederick's QOTD server at " + new Date().toString() + "\n";
            boolean usePrincessBride = random.nextBoolean();
            if (usePrincessBride) {
                response += "\"" + quotesPrincessBride[princessBrideIndex] + "\"\n";
                response += "- from The Princess Bride";
                princessBrideIndex++;
                if (princessBrideIndex >= quotesPrincessBride.length) princessBrideIndex = 0;
            } else {
                response += "\"" + quotesOther[otherIndex] + "\"\n";
                response += "- from Ferris Bueller's Day Off";
                otherIndex++;
                if (otherIndex >= quotesOther.length) otherIndex = 0;
            }
            return response.getBytes(StandardCharsets.US_ASCII);
        }
    }
}