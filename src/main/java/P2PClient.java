//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
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
    private final String LOCALHOST2 = "127.0.0.2";
    private HashMap<Integer, Integer> directoryHashMap;
    private HashMap<String, Record> recordHashMap = new HashMap<String, Record>();
    private ArrayList files;
    DatagramSocket ds;

    private String ip;
    private Socket clientSocket;
    private PrintWriter out;
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

                // p.getFile();// Returns File using HTTP GET req
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


    // For now assume only 1 file per client, and for now just a string
    public P2PClient() {
//        this.fileString = fileString;
//        this.ip = this.LOCALHOST;
        recordHashMap = new HashMap<String, Record>();
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


    public void informAndUpdate(String[] filenames) {
        int[] fileHashes = new int[filenames.length];
        int i = 0;
        for (String filename : filenames) {
            fileHashes[i] = MiscFunctions.hashFunction(filename);
            i++;
        }

    }
    public void storeRecord(String name) {
        int serverID = MiscFunctions.hashFunction(name);
        int UDPPort = getServerPort(serverID);
        String serverIP = Integer.toString(serverID);
        String msg = "Upload " + name;

        // Contact directory server with serverID to store record (content name, client IP)
        sendDataToDS(msg, serverIP, UDPPort);

        // Keep the local record (content name, server ID, server's IP address)
        Record record = new Record(name, serverID, UDPPort);
        recordHashMap.put(name, record);
    }

    public void queryRecord(String name) throws IOException {
        int serverID = MiscFunctions.hashFunction(name);
        int UDPPort = getServerPort(serverID);
        int clientPort = this.C1_PORT;
        String serverIP = Integer.toString(serverID);
        String msg = "Query " + name;
        String statusCode;
        String fileClientIP;

        // Contact directory server to query for record
        sendDataToDS(msg, serverIP, UDPPort);
        msg = receiveDataFromDS(UDPPort);
        System.out.println(msg);
        Scanner scan = new Scanner(msg);
        statusCode = scan.next();
        System.out.println("StatusCode: " + statusCode);

        if (statusCode.equals("200")) {
            System.out.println("FROM SERVER -> Content Found, IP given ");
            scan = new Scanner(msg);
            statusCode = scan.next(); // status code
            System.out.println(statusCode);
            startConnection(this.LOCALHOST, clientPort);
//            sendMessageToPeer("hello server!");

            sendImageToPeer();
        }
    }

    public void sendImageToPeer() {
        try{
            BufferedImage image = ImageIO.read(new File("/Users/ibm/Documents/Ryerson/Winter 2019/Computer-Networking/P2PPhotoAlbum/resources/test.jpg"));

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", byteArrayOutputStream);

            byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
            out.write(String.valueOf(size));
            out.write(String.valueOf(byteArrayOutputStream.toByteArray()));
            out.flush();
            System.out.println("Flushed: " + System.currentTimeMillis());

            Thread.sleep(120000);
            System.out.println("Closing: " + System.currentTimeMillis());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch(InterruptedException ie){
            ie.printStackTrace();
        }

    }

    public Object sendMessageToPeer(String msg) throws IOException {
        Object response;

        out.println(msg);
        try {
            response = in.readObject();
            return response;
        } catch(IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        throw new IOException("No response from P2P server");
    }

    public String receiveDataFromDS(int UDPPort) throws IOException {
        String msg;
        byte[] receiveData = new byte[1024];
        System.out.printf("Listening on udp:%s:%d%n",
                InetAddress.getByName(this.LOCALHOST), UDPPort);
        DatagramPacket recievePacket = new DatagramPacket(receiveData, receiveData.length);
        ds.receive(recievePacket);
        msg = new String(recievePacket.getData(), 0, recievePacket.getLength());
        System.out.println("Received packet!");
        return msg;
    }

    public void sendDataToDS(String msg, String serverIP, int serverPort){
        try {
            ds = new DatagramSocket();
            InetAddress inetAddress = InetAddress.getByName(this.LOCALHOST); // Get the Inet address of the server.
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


    public int getServerPort(int id) {
        return directoryHashMap.get(id);
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
