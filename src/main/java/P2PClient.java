import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
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
    private String ip;
    private Socket clientSocket;
    private PrintWriter out;
//    private BufferedReader in;
    private ObjectInputStream in;
    public static void main(String args[]) throws IOException {


        P2PClient p = new P2PClient("20680", "abc");
        p.storeRecord("Test");
    }

    public P2PClient(String ip){
        // P2P client starts knowing IP of directory server with ID=1
        //directoryHashMap.put(1, ip);
        for(int i = 2; i <= 4; i++) {
            // Use IP to ask DHT for IP addresses of remaining servers
            // retrievedIP = ...
            // directoryHashMap.put(i, retrievedIP);
        }
    }

    // For now assume only 1 file per client, and for now just a string
    public P2PClient(String ip, String fileString) {
        this.ip = ip;
        directoryHashMap = new HashMap<Integer, Integer>();
        directoryHashMap.put(1, S1_PORT);
        init();
        sendRecordsToDHT(fileString);
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
//        int serverID = MiscFunctions.hashFunction(name);
//        String serverIP = getServerIP(serverID);
        // Contact server with serverID to store record (content name, client IP)
        try {
            DatagramSocket ds = new DatagramSocket();
            InetAddress ip = InetAddress.getLocalHost();
            byte buf[] = null;

            // convert the String input into the byte array.
            buf = name.getBytes();

            // Step 2 : Create the datagramPacket for sending
            // the data.
            DatagramPacket DpSend =
                    new DatagramPacket(buf, buf.length, ip, 1234);

            // Step 3 : invoke the send call to actually send
            // the data.
            ds.send(DpSend);

        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        } catch (SocketException se){
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // Keep the local record (content name, server ID, server's IP address)
//        Record record = new Record(name, serverID, serverIP);
//        recordHashMap.put(name, record);

    }




    public void sendRecordsToDHT(String fileString) {
        // Hash name to server ID
        int serverID = MiscFunctions.hashFunction(fileString);
        int serverPort = directoryHashMap.get(serverID);

        startConnection(LOCALHOST, serverPort);

        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outputStream.defaultWriteObject();
        } catch (IOException e) {
            e.printStackTrace();
        }



        DirectoryRecord r = new DirectoryRecord(fileString, this.ip);
        System.out.println("Trying to send record for" + fileString + " to server " + serverID);


    }
//    public int getServerID(String val){
//
//    }

    public int getServerPort(int id) {
        return directoryHashMap.get(id);
    }

    public void insertIntoRecordHashMap(String key, Record record) {
        recordHashMap.put(key, record);
    }
}
