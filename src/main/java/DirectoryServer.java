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
import java.util.Scanner;

/**
 * This is a server running in the directory pool. IDs should be numbers [1,4]
 * Inherits from Server so basic functionality is in that class. Overrides should be different.
 * Implements Runnable for multi-threading.
 */
public class DirectoryServer extends Server implements Runnable {


    private HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
    public DatagramSocket ds;

    public DirectoryServer(int id) { super(id); }
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

    public String getFromHash(String fileName) {
        int newKey = MiscFunctions.hashFunction(fileName);
        return hashMap.get(newKey);
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[1]);	       // The server's main port. The client will connect to this port using UDP. The predecessor server will also connect to this port using TCP.
        int serverID = Integer.parseInt(args[0]);	   // The server's ID, which can be a number from 1 to n depending on the number of servers.

        DirectoryServer dirServer = new DirectoryServer(serverID, port);
        dirServer.runUDPConnection(port);
        int successorID = serverID++; // ID of the next server in the DHT
        int successorPort = port++; // The port of the next server in the DHT.
    }


    public void runUDPConnection(int port){
        String msg;
        try {
            System.out.println("UDP is starting up...");

            ds = new DatagramSocket(port);
            byte[] receive = new byte[65535];

            DatagramPacket DpReceive = null;

            while (true) {

                DpReceive = new DatagramPacket(receive, receive.length); // receives data from P2P Client
                ds.receive(DpReceive); // Data in the byte buffer
                msg = new String(DpReceive.getData());
                System.out.println("Client:-" + msg);


                // TEMP: Exits server if the P2P Client content has been received
                if (msg.length() > 0 && msg.contains("Upload")) {
                    int portNum = DpReceive.getPort();

                    System.out.println("Client has received Upload: " + msg);
                    Scanner sc = new Scanner(msg);
                    sc.next();
                    String fileName = sc.next().trim();
                    System.out.println(fileName);

                    String portNumStr = Integer.toString(portNum);
                    insertInHash(fileName, portNumStr); // Inserts record into hash map
                    String val = getFromHash(fileName);
                    System.out.println(val);
                } else if (msg.length() > 0 && msg.contains("Query")){
                    System.out.println("Query done!!!");
                    Scanner sc = new Scanner(msg);
                    sc.next();
                    String fileName = sc.next();
                    String ip = getFromHash(fileName);
                    System.out.println(ip);

                    String clientIP = DpReceive.getAddress().toString();

                    if (ip == null) {
                        System.out.println("TO CLIENT -> " + "404" + " Padding" + "\n");
                        sendDataToClient("404" + " Padding", clientIP, DpReceive.getPort());
                    }
                    // If the file is found, output the HTTP status code 200 OK.
                    else {
                        System.out.println("TO CLIENT -> " + "200" + " " + ip + " Padding" + "\n");
                        sendDataToClient("200" + " " + ip + " Padding", clientIP, DpReceive.getPort());
                    }
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

    public void sendDataToClient(String theMessage, String clientIP, int clientPort) throws IOException {
        byte[] sendData = new byte[1024];
        sendData = theMessage.getBytes();	// The String message converted into bytes, so it can be sent.
        InetAddress ip = InetAddress.getByName(clientIP);	// Get Inet address of the client, given the IP.
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, clientPort); // Create the UDP packet.
        ds.send(sendPacket); // Send the UDP packet.
    }
}
