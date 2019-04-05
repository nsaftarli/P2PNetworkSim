import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;

/**
 * This is a server running in the directory pool. IDs should be numbers [1,4]
 * Inherits from Server so basic functionality is in that class. Overrides should be different.
 * Implements Runnable for multi-threading.
 */
public class DirectoryServer extends Server implements Runnable {

    private HashMap<String, String> contentToPort;


    public static void main(String[] args) {
        int id = Integer.parseInt(args[0]);
        int port = Integer.parseInt(args[1]);

        SocketAddress socketPort = new InetSocketAddress(port);
        ServerSocketChannel tcpSocket = ServerSocketChannel.open();
        tcpSocket.socket().bind(socketPort);
        tcpSocket.configureBlocking(false);
        DatagramChannel udpSocket = DatagramChannel.open();
        udpSocket.socket().bind(socketPort);
        tcpSocket.configureBlocking(false);


        DirectoryServer s = new DirectoryServer(id, port);
        s.start();
    }

    public DirectoryServer(int id) {
        super(id);
    }
    public DirectoryServer(int id, int port) {
        super(id, port);
    }

    @Override
    public void start(){
        System.out.println("Server " + id + " started at port " + port);
        try {
            serverSocket = new ServerSocket(port);
            datagramSocket = new DatagramSocket(port);
            while (true) {
                clientSocket = serverSocket.accept();
                new Thread(new DirectoryServer(id)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stop(){}

    public void run() {
        try {

        }


    }
    public void insertInHash(String key, String value) {
        int newKey = MiscFunctions.hashFunction(key);
//        hashMap.put(newKey, value);
    }
}
