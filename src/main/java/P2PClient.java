//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;
import java.util.Scanner;

/**
 * This is the client service running on a peer in the network.
 */
public class P2PClient {
    private final int S1_PORT = 20680;
    private final int C1_PORT = 20684;
    private final String LOCALHOST = "127.0.0.1";
    private HashMap<Integer, Integer> directoryHashMap;
    private HashMap<String, Record> recordHashMap;
    private ArrayList files;
    DatagramSocket ds;

    private String ip;
    private Socket clientSocket;
    private PrintWriter out;
//    private BufferedReader in;
    private ObjectInputStream in;

    public static void main(String args[]) throws IOException {
        String userInput;
        P2PClient p = new P2PClient(); // IP of Directory server ID=1 is 20680

        // Displays action information for client that is connecting to Directory Server
        while (true) {
            Scanner scan = new Scanner(System.in);

            System.out.println("Welcome to Poogle Photos!");
            System.out.println("Please pick an action: U=Upload, Q=Query for content, E=Exit");
            userInput = scan.next(); // Reads user input

            if (userInput.equalsIgnoreCase("U")) {
                System.out.println("Please enter a file name for your photo: ");
                userInput = scan.next(); // Filename
                p.storeRecord(userInput); // Stores file locally and on Directory server

            } else if (userInput.equalsIgnoreCase("Q")) {
                System.out.println("Enter a file name to query: ");
                userInput = scan.next();
                p.queryRecord(userInput);
            } else if (userInput.equalsIgnoreCase("E")){
                try {
                    p.exit();
                    System.out.println("Client is exiting...");
                } catch (Exception e) {
                    System.out.println("Error. Could not connect to server!");
                }

            }
        }
    }

//
//    public P2PClient(String ip){
//
//        // P2P client starts knowing IP of directory server with ID=1
////        directoryHashMap.put(1, ip);
//        for(int i = 2; i <= 4; i++) {
//            // Use IP to ask DHT for IP addresses of remaining servers
//            // retrievedIP = ...
//            // directoryHashMap.put(i, retrievedIP);
//            // TEMP: incrementing port
////            int ipVal = Integer.parseInt(ip);
////            ipVal++;
////            ip = Integer.toString(ipVal);
////
////            directoryHashMap.put(i, ip);
//        }
//    }

    // For now assume only 1 file per client, and for now just a string
    public P2PClient() {
//        this.fileString = fileString;
//        this.ip = this.LOCALHOST;
        directoryHashMap = new HashMap<Integer, Integer>();
        directoryHashMap.put(1, S1_PORT);
        init();
//        sendRecordsToDHT(fileString);
    }

    /**
     * Queries server with ID=1 to get records for other servers.
     */
    public void init() {
        System.out.println("Trying to retrieve locations of other servers from server 1");
        startConnection(LOCALHOST, S1_PORT);
        try {
            int[] resp = (int[]) sendMessage("serverLocs");
            System.out.println(resp);
            for(int i = 0; i < 4; i++) {
                directoryHashMap.put(i+1, resp[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
//            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stopConnection() throws IOException {
        try {
            in.close();
            out.close();
            clientSocket.close();
            System.out.println("Connection closed");
        } catch(IOException e) {
            e.printStackTrace();
            throw new IOException("Closing connections failed");
        }
    }
    public Object sendMessage(String msg) throws IOException {
        Object response;

        out.println(msg);
        try {

//            response = in.readLine();
            response = in.readObject();
            return response;
        } catch(IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        throw new IOException("No response from server");
    }


    public void storeRecord(String name) {
        int serverID = MiscFunctions.hashFunction(name);
        int UDPPort = getServerPort(serverID);
        String serverIP = this.LOCALHOST;
        String msg = "Upload " + name;

        // Contact directory server with serverID to store record (content name, client IP)
        sendDataToDS(msg, serverIP, UDPPort);

        // Keep the local record (content name, server ID, server's IP address)
//        Record record = new Record(name, UDPPort, serverIP);
//        recordHashMap.put(name, record);
    }

    public void queryRecord(String name) {
        int serverID = MiscFunctions.hashFunction(name);
        int UDPPort = getServerPort(serverID);
        String serverIP = this.LOCALHOST;
        String msg = "Query " + name;

        // Contact directory server to query for record
        sendDataToDS(msg, serverIP, UDPPort);
    }

    public void sendDataToDS(String msg, String serverIP, int serverPort){
        try {
            ds = new DatagramSocket();
            InetAddress inetAddress = InetAddress.getLocalHost(); // Get the Inet address of the server.
            System.out.println(inetAddress);
            byte buf[] = null;

            // convert the String input into the byte array.
            buf = msg.getBytes();

            // Create data packet
            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, inetAddress, serverPort);

            // Send data to the directory server
            ds.send(DpSend);

        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        } catch (SocketException se){
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void sendRecordsToDHT(String fileString) {
        int serverID = MiscFunctions.hashFunction(fileString);
        DirectoryRecord r = new DirectoryRecord(fileString, this.ip);
        System.out.println("Trying to send record for" + fileString + " to server " + serverID);

    }


    public int getServerPort(int id) {
        return directoryHashMap.get(id);
    }

    public void insertIntoRecordHashMap(String key, Record record) {
        recordHashMap.put(key, record);
    }

    public void exit() throws Exception {
        byte[] receiveData = new byte[1024];
//        String statusCode; // HTTP status code.
//        String message = "EXIT " + serverPortNumbers[0] + " " + serverPortNumbers[1] + " " + serverPortNumbers[2] + " " + serverPortNumbers[3] + " Padding";
//        sendDataToServer(message, serverIPs[0], serverPortNumbers[0]); // Send message exit to server.
//        message = receiveDataFromServer();
        ds.close(); // Close the client's UDP socket.
//        Scanner scan = new Scanner(message);
//        statusCode = scan.next();

        // If the status code returned by the server is 200 OK, then the client has been successfully removed.
//        if (statusCode.equals("200")) {
//            System.out.println("FROM SERVER -> All contents removed sucessfully");
//        }
        System.exit(0); // Quit the application with exit code 0 (success).
    }
}
