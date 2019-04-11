//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
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
    // Address for server 1
    private final int S1_PORT = 20680;
    // Address where client ports begin
    private final int C1_PORT = 20684;
    private final String LOCALHOST = "127.0.0.1";
    // DHT ID -> DHT Port
    private HashMap<Integer, Integer> directoryHashMap;
    // Content -> (Server ID, Server IP)
    private HashMap<String, Record> recordHashMap;
    private static HashMap<String, File> fileHashMap;
    private ArrayList files;
    DatagramSocket ds;

    private String ip;
    private static int peerPort;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader str_in;
    private ObjectInputStream in;

    public static void main(String args[]) throws IOException {
        String userInput;
        peerPort = Integer.parseInt(args[0]);
        P2PClient p = new P2PClient(); // IP of Directory server ID=1 is 20680
//        P2PServer p2PServer = new P2PServer(peerPort, fileHashMap);
        new P2PServerListenerThread(peerPort, fileHashMap).start();
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
        recordHashMap = new HashMap<String, Record>();
        fileHashMap = new HashMap<String, File>();
        directoryHashMap = new HashMap<Integer, Integer>();
        directoryHashMap.put(1, S1_PORT);

        //Change later:
//        peerPort = C1_PORT;

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

    public void startPeerConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            str_in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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

    public String sendPeerMessage(String msg) throws IOException {
        String response;
        out.println(msg);
        return null;
    }


    public void storeRecord(String name) {
        // Read file
        addFile(name);

        // Hash to a server based on file name
        int serverID = MiscFunctions.hashFunction(name);
        int UDPPort = peerPort;
        // Should be server port
//        String serverIP = Integer.toString(serverID);
        int serverPort = directoryHashMap.get(serverID);

        String msg = "Upload " + name + " " + this.peerPort;

        // Contact directory server with serverID to store record (content name, client IP)
        sendDataToDS(msg, serverPort);

        // Keep the local record (content name, server ID, server's IP address)
        Record record = new Record(name, serverID, serverPort);
        recordHashMap.put(name, record);
    }

    /**
     *
     * @param name
     * @throws IOException
     */
    public void queryRecord(String name) throws IOException {
        int serverID = MiscFunctions.hashFunction(name);
        int UDPPort = peerPort;
        String serverIP = Integer.toString(serverID);
        int serverPort = directoryHashMap.get(serverID);
        String msg = "Query " + name;

        // Contact directory server to query for record
        sendDataToDS(msg, serverPort);
        msg = receiveDataFromDS(UDPPort);
        System.out.println(msg);
        Scanner scan = new Scanner(msg);
        String statusCode = scan.next();
        if (Integer.parseInt(statusCode) == 200) {
            // Read in destination port
            int peerServerPort = Integer.parseInt(scan.next());

            // Generate HTTP header object
            String http_header = "GET " + name;

            // Send request to destination port (P2PServer)
            startPeerConnection(LOCALHOST, peerServerPort);
            String resp = sendPeerMessage(http_header);
            System.out.println(resp);
            InputStream inputStream = clientSocket.getInputStream();

            byte[] size_arr = new byte[4];
            inputStream.read(size_arr);
            int size = ByteBuffer.wrap(size_arr).asIntBuffer().get();

            byte[] image_arr = new byte[size];
            inputStream.read(image_arr);

            BufferedImage image = ImageIO.read(new ByteArrayInputStream(image_arr));
            ImageIO.write(image, "jpg", new File("/Users/Nariman/Documents/School/CPS706/ClientServer/resources_out/" + name));

            storeRecord(name);
            JFrame frame = new JFrame();
            frame.getContentPane().add(new JLabel(new ImageIcon(image)));
            frame.pack();
            frame.setVisible(true);
//            clientSocket.close();
        }

    }

    public void sendDataToDS(String msg, int serverPort){
        try {
            ds = new DatagramSocket();
            InetAddress inetAddress = InetAddress.getByName(this.LOCALHOST); // Get the Inet address of the server.
            System.out.println(inetAddress);
//            byte buf[] = null;

            // convert the String input into the byte array.
            byte buf[] = msg.getBytes();

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

    public void addFile(String name) {
        String path = "/Users/Nariman/Documents/School/CPS706/ClientServer/resources_in/";
        File file = new File(path + name);
        fileHashMap.put(name, file);
    }

    public File getFile(String name) {
        return fileHashMap.get(name);
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
