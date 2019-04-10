import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

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
    private final String LOCALHOST = "127.0.0.1";
    private ObjectInputStream in;

    Socket serverSocket;

    public P2PServer(){
        super();
    }
    public P2PServer(int id) {
        super(id);
    }

    public void start() {}

    public void start(Socket clientSocket) {
        try {
            System.out.println("Starting P2P Server");
//            out = new PrintWriter(clientSocket.getOutputStream(), true);
//            in = new ObjectInputStream(clientSocket.getInputStream());
//            String greeting = in.readObject().toString();
//            out.println("Greeting: " + greeting);

            in = new ObjectInputStream(clientSocket.getInputStream());

            System.out.println("Reading: " + System.currentTimeMillis());

            byte[] sizeAr = new byte[4];
            in.read(sizeAr);
            int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

            byte[] imageAr = new byte[size];
            in.read(imageAr);

            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));

            System.out.println("Received " + image.getHeight() + "x" + image.getWidth() + ": " + System.currentTimeMillis());
            ImageIO.write(image, "jpg", new File("/Users/ibm/Documents/Ryerson/Winter 2019/Computer-Networking/P2PPhotoAlbum/resources/test2.jpg"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stop(){}

    public static void main(String[] args) throws IOException {
        P2PServer p2pServer = new P2PServer();

        try {
            ServerSocket p2pServerSocket = new ServerSocket(C1_PORT);

            while (true) {
                Socket clientSocket = p2pServerSocket.accept();
                new P2PThread(clientSocket).start();
                p2pServer.start(clientSocket);
//                p2pServer.transferFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void transferFile() {
        try {


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
