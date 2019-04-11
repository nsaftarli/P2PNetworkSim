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
//        P2PServer p2pServer = new P2PServer(1);

        new P2PServerListenerThread(port, file_map);
//        try {
////            ServerSocket p2pServerSocket = new ServerSocket(p2pServer.C1_PORT);
//            ServerSocket serverSocket = new ServerSocket(C1_PORT);
//            while (true) {
//                Socket clientSocket = serverSocket.accept();
//                new ServerThread(clientSocket).start();
//
////                p2pServer.transferFile();
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public void transferFile() {
        int bytesRead;
        try {
            InputStream in = clientSocket.getInputStream();

            OutputStream out = new FileOutputStream("../../../resources_out/abc.png");

            byte[] buffer = new byte[2048];

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}