import javafx.scene.chart.PieChart;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
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
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * This is a server running in the directory pool. IDs should be numbers [1,4]
 * Inherits from Server so basic functionality is in that class. Overrides should be different.
 * Implements Runnable for multi-threading.
 */
public class DirectoryServer extends Server {
    private String clientMsg;

    // DHT server ID -> DHT server port
    private static HashMap<Integer, Integer> directory_map = new HashMap<Integer, Integer>();

    //
    private static HashMap<String, Integer> peerMap = new HashMap<String, Integer>();
    public static DatagramSocket ds;
    public static DatagramChannel dc;
    public static ServerSocketChannel sc;

    private final String LOCALHOST = "127.0.0.1";
    private final int C1_PORT = 20684;




    public DirectoryServer(int id) {
        super(id);
        buildServerTable();
    }
    public DirectoryServer(int id, int port) {
        super(id, port);
        printStartupMessage();
        buildServerTable();
    }

    @Override
    public void start(){}

    @Override
    public void stop(){}

    public void insertInHash(String key, int value) {
        peerMap.put(key, value);
    }

    public Integer getFromHash(String fileName) {
        return peerMap.get(fileName);
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[1]);	       // The server's main port. The client will connect to this port using UDP. The predecessor server will also connect to this port using TCP.
        int serverID = Integer.parseInt(args[0]);	   // The server's ID, which can be a number from 1 to n depending on the number of servers.

        DirectoryServer dirServer = new DirectoryServer(serverID, port);
//        ListenerThreadTCP listenerThreadTCP =  new ListenerThreadTCP(port, directory_map, peerMap);
        new ListenerThreadTCP(port, directory_map, peerMap).start();
        new ListenerThreadUDP(port, directory_map, peerMap).start();
//        while (true) {
//            new ListenerThreadTCP()
//        }

//        dirServer.runUDPConnection(port);
//        int successorID = serverID++; // ID of the next server in the DHT
//        int successorPort = port++; // The port of the next server in the DHT.

//        try {
//            ServerSocket serverSocket = new ServerSocket(port);
//            dirServer.ds = new DatagramSocket(port);
//
//            ds = new DatagramSocket(port);
//            while (true) {
//                Socket clientSocket = serverSocket.accept();
//                if (clientSocket != null) {
//                    new ServerThread(clientSocket, directory_map, peerMap).start();
//                }
//                new ServerThread(clientSocket, directory_map, peerMap).start();
//                new ServerThread(ds, directory_map, peerMap).start();
//                dirServer.runUDPConnection(port);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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

                    System.out.println("Client has received Upload: " + msg);
                    Scanner sc = new Scanner(msg);
                    sc.next();
                    String fileName = sc.next().trim();
                    System.out.println(fileName);

                    insertInHash(fileName, port); // Inserts record into hash map
                    int val = getFromHash(fileName);
                    System.out.println(val);
                } else if (msg.length() > 0 && msg.contains("Query")){
                    System.out.println("Query done!!!");
                    Scanner sc = new Scanner(msg);
                    sc.next();
                    String fileName = sc.next().trim();
                    // Port Number for client
                    Integer ip = getFromHash(fileName);
                    System.out.println(ip);
                    String ipString = Integer.toString(ip);

                    //
                    String clientIP = DpReceive.getAddress().toString().substring(1);
                    System.out.println(clientIP);

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

    public void printStartupMessage() {
        System.out.println("Started new server. This server has id " + id +
                           " and is at port " + port);
    }
    public void buildServerTable() {
        for (int i = 0; i <= 3; i++) {
            System.out.println("Inserted into directory map server " + (i+1) + " with port " + (20680+i));
            directory_map.put(i + 1, 20680 + i);
        }

    }

}
