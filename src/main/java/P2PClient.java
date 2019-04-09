import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;
import java.util.Scanner;

/**
 * This is the client service running on a peer in the network.
 */
public class P2PClient {
    private HashMap<Integer, String> directoryHashMap = new HashMap<>();
    private HashMap<String, Record> recordHashMap = new HashMap<>();
    private ArrayList files;
    DatagramSocket ds;

    public static void main(String args[]) throws IOException {
        Thread mainThread;	// Main thread.
        mainThread = new Thread(mainRunClient);
        mainThread.start(); // Start the main thread.
    }

    static Runnable mainRunClient = new Runnable() {
        public void run() {
            String userInput;
            P2PClient p = new P2PClient("20680"); // IP of Directory server ID=1 is 20680

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
    };

    public P2PClient(String ip){

        // P2P client starts knowing IP of directory server with ID=1
        directoryHashMap.put(1, ip);
        for(int i = 2; i <= 4; i++) {
            // Use IP to ask DHT for IP addresses of remaining servers
            // retrievedIP = ...
            // directoryHashMap.put(i, retrievedIP);
            // TEMP: incrementing port
            int ipVal = Integer.parseInt(ip);
            ipVal++;
            ip = Integer.toString(ipVal);

            directoryHashMap.put(i, ip);
        }
    }

    public void storeRecord(String name) {
        int serverID = MiscFunctions.hashFunction(name);
        String serverIP = getServerIP(serverID);
        int UDPPort = Integer.parseInt(serverIP);
        String msg = "Upload " + name;

        // Contact directory server with serverID to store record (content name, client IP)
        sendDataToDS(msg, serverIP, UDPPort);

        // Keep the local record (content name, server ID, server's IP address)
        Record record = new Record(name, UDPPort, serverIP);
        recordHashMap.put(name, record);
    }

    public void queryRecord(String name) {
        int serverID = MiscFunctions.hashFunction(name);
        String serverIP = getServerIP(serverID);
        int UDPPort = Integer.parseInt(serverIP);
        String msg = "Query " + name;

        // Contact directory server to query for record
        sendDataToDS(msg, serverIP, UDPPort);
    }

    public void sendDataToDS(String msg, String serverIP, int serverPort){
        try {
            ds = new DatagramSocket();
            InetAddress inetAddress = InetAddress.getLocalHost(); // Get the Inet address of the server.

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
//    public int getServerID(String val){
//
//    }

    public String getServerIP(int id) {
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
