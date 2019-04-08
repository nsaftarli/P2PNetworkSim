import java.util.HashMap;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;

/**
 * This is a server running in the directory pool. IDs should be numbers [1,4]
 * Inherits from Server so basic functionality is in that class. Overrides should be different.
 * Implements Runnable for multi-threading.
 */
public class DirectoryServer extends Server implements Runnable {


    private HashMap<Integer, String> hashMap = new HashMap<Integer, String>();


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
            serverSocket = new ServerSocket(port); // TCP Connection
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

    public void run() {}


    public void insertInHash(String key, String value) {
        int newKey = MiscFunctions.hashFunction(key);
        hashMap.put(newKey, value);
    }

    public static void main(String[] args) throws IOException {
//        int id = Integer.parseInt(args[0]);
//        int port = Integer.parseInt(args[1]);

        DirectoryServer dirServer = new DirectoryServer(12456);

        dirServer.runUDPConnection();
    }


    public void runUDPConnection(){
        try {
            System.out.println("UDP is starting up...");


            DatagramSocket ds = new DatagramSocket(1234);
            byte[] receive = new byte[65535];

            DatagramPacket DpReceive = null;

            while (true) {

                DpReceive = new DatagramPacket(receive, receive.length); // receives data from P2P Client
                ds.receive(DpReceive); // Data in the byte buffer

                System.out.println("Client:-" + data(receive));

                // TEMP: Exits server if the P2P Client content has been received
                if (data(receive).length() > 0) {
                    int portNum = DpReceive.getPort();

                    System.out.println("Client has received content.....EXITING");

                    String portNumStr = Integer.toString(portNum);
                    insertInHash(portNumStr, data(receive).toString()); // Inserts record into hash map

                    break;
                }

                // Clear the buffer after every message.
                receive = new byte[65535];

            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }


    // A utility method to convert the byte array
    // data into a string representation.
    public static StringBuilder data(byte[] a)
    {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0)
        {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }

}
