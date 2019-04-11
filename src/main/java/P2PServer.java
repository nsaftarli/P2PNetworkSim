import java.io.*;
import java.net.Socket;
import java.util.HashMap;

/**
 * This is a server running on one of the peers in the network.
 */

public class P2PServer extends Server {
    final int statusCode200 = 200; // OK
    final int statusCode400 = 400; // Bad Request
    final int statusCode404 = 404; // Not Found
    final int statusCode505 = 505; // HTTP Version Not Supported
    private final int S1_PORT = 20680;
    private static final int C1_PORT = 20684;
    private static HashMap<String, File> file_map;
    private static int port;

    Socket serverSocket;

    public P2PServer(){
        super();
    }
    public P2PServer(int port, HashMap file_map) {
//        super(port);
        this.port = port;
        this.file_map = file_map;
    }

    private Thread thread = null;

    public void start() {}
    public void stop(){}

    public static void main(String[] args) throws IOException {
        new P2PServerListenerThread(port, file_map);

    }



}