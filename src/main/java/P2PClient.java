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

    public static void main(String args[]) throws IOException {

        P2PClient p = new P2PClient("20680"); // IP of Directory server ID=1 is 20680
        p.storeRecord("Test");
    }

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
        int UDPport = Integer.parseInt(serverIP);

        // Contact directory server with serverID to store record (content name, client IP)
        try {
            DatagramSocket ds = new DatagramSocket();
            InetAddress ip = InetAddress.getLocalHost(); // IP address
            byte buf[] = null;

            // convert the String input into the byte array.
            buf = name.getBytes();

            // Create data packet
            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, UDPport);

            // Send data to the directory server
            ds.send(DpSend);

        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        } catch (SocketException se){
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // Keep the local record (content name, server ID, server's IP address)
        Record record = new Record(name, serverID, serverIP);
        recordHashMap.put(name, record);
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
}
