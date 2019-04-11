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
    private final String DHT_IP = LOCALHOST;
    // DHT ID -> DHT Port
    private HashMap<Integer, Integer> directoryHashMap;
    // Content -> (Server ID, Server IP)
    private HashMap<String, Record> recordHashMap;
    // Filename -> File Object
    private static HashMap<String, File> fileHashMap;

    DatagramSocket ds;

    private String ip;
    private static int peerPort;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader str_in;
    private ObjectInputStream in;
    private final String OUTPATH = "/Users/Nariman/Documents/School/CPS706/ClientServer/resources_out/";
    private final String INPATH = "/Users/Nariman/Documents/School/CPS706/ClientServer/resources_in/";

    public static void main(String args[]) throws IOException {
        String userInput;
        // Host this peer on given port.
        peerPort = Integer.parseInt(args[0]);
        P2PClient p = new P2PClient(); // IP of Directory server ID=1 is 20680

        // Start a thread to listen for TCP file transfer requests
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

    /**
     * Initialize peer (query DHT server 1 for information on other DHT servers)
     */
    public P2PClient() {
        recordHashMap = new HashMap<String, Record>();
        fileHashMap = new HashMap<String, File>();
        directoryHashMap = new HashMap<Integer, Integer>();
        directoryHashMap.put(1, S1_PORT);
        init();

    }

    /**
     * Queries server with ID=1 to get records for other servers.
     */
    public void init() {
        System.out.println("Trying to retrieve locations of other servers from server 1");
        startConnection(DHT_IP, S1_PORT);
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

    /**
     * Starts a connection and initializes in/out streams.
     * @param ip IP address to connect to
     * @param port Port at IP address hosting the socket.
     */
    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts a connection with a peer, initializing I/O for file transfer
     * @param ip
     * @param port
     */
    public void startPeerConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            str_in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes I/O streams and socket.
     * @throws IOException if I/O streams are un-instantiated.
     */
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

    /**
     * Sends a message to wherever there exists a TCP connection
     * @param msg The message to send (e.g. GET request)
     * @return An object from the output stream
     * @throws IOException if IO is un-instantiated
     */
    public Object sendMessage(String msg) throws IOException {
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

        throw new IOException("No response from server");
    }

    /**
     * Sends a message to a peer as opposed to DHT server for file transfer
     * @param msg The message to send (e.g. GET request)
     * @return Response is usually file name
     * @throws IOException if IO is un-instantiated.
     */
    public String sendPeerMessage(String msg) throws IOException {
        String response;
        out.println(msg);
        return null;
    }

    /**
     * Store a record locally, request to store a record on DHT
     * @param name name of the file to upload
     */
    public void storeRecord(String name) {
        // Read file, store in HashMap
        addFile(name);

        // Hash to a server ID based on file name, and then get the port associated with that server
        int serverID = MiscFunctions.hashFunction(name);
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
     * @param name Name of the file
     * @throws IOException if IO is un-instantiated
     */
    public void queryRecord(String name) throws IOException {
        // Get server based on hashed name.
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
            startPeerConnection(DHT_IP, peerServerPort);
            String resp = sendPeerMessage(http_header);
            System.out.println(resp);
            InputStream inputStream = clientSocket.getInputStream();

            // Read in image and write it to disk at OUTPATH.
            byte[] size_arr = new byte[4];
            inputStream.read(size_arr);
            int size = ByteBuffer.wrap(size_arr).asIntBuffer().get();

            byte[] image_arr = new byte[size];
            inputStream.read(image_arr);

            BufferedImage image = ImageIO.read(new ByteArrayInputStream(image_arr));
            ImageIO.write(image, "jpg", new File(OUTPATH + name));

            // Finally, store a record for the file since we now have it.
            storeRecord(name);
            // Show the file.
            JFrame frame = new JFrame();
            frame.getContentPane().add(new JLabel(new ImageIcon(image)));
            frame.pack();
            frame.setVisible(true);
        }

    }

    /**
     * Used for sending datagrams to DHT for querying for file locations
     * @param msg Message to send to DHT (composed of file name and IP address)
     * @param serverPort The port that the DHT server is on.
     */
    public void sendDataToDS(String msg, int serverPort){
        try {
            ds = new DatagramSocket();
            InetAddress inetAddress = InetAddress.getByName(this.DHT_IP); // Get the Inet address of the server.
            System.out.println(inetAddress);

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

    /**
     * Requests serviced by DHT return in this method
     * @param UDPPort port to connect to
     * @return A packet containing information on a file's existence/whereabouts
     * @throws IOException if IO is uninitialized
     */
    public String receiveDataFromDS(int UDPPort) throws IOException {
        String msg;
        byte[] receiveData = new byte[1024];
        System.out.printf("Listening on udp:%s:%d%n",
                InetAddress.getByName(this.DHT_IP), UDPPort);
        DatagramPacket recievePacket = new DatagramPacket(receiveData, receiveData.length);
        ds.receive(recievePacket);
        msg = new String(recievePacket.getData(), 0, recievePacket.getLength());
        System.out.println("Received packet!");
        return msg;
    }

    /**
     * Reads a file and puts it in this peer's hash map.
     * @param name name of the file to read
     */
    public void addFile(String name) {
        String path = INPATH;
        File file = new File(path + name);
        fileHashMap.put(name, file);
    }

    /**
     * Closes DatagramSocket
     * @throws Exception if socket is already closed/uninitialized.
     */
    public void exit() throws Exception {
        byte[] receiveData = new byte[1024];
        ds.close(); // Close the client's UDP socket.
        System.exit(0); // Quit the application with exit code 0 (success).
    }
}
