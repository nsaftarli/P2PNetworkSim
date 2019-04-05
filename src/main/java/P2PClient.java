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
    private HashMap<Integer, String> directoryHashMap;
    private HashMap<String, Record> recordHashMap;
    private ArrayList files;

    public static void main(String args[]) throws IOException {

        P2PClient p = new P2PClient("2068");
        p.storeRecord("Test");
    }

    public P2PClient(String ip){
        // P2P client starts knowing IP of directory server with ID=1
//        directoryHashMap.put(1, ip);
        for(int i = 2; i <= 4; i++) {
            // Use IP to ask DHT for IP addresses of remaining servers
            // retrievedIP = ...
            // directoryHashMap.put(i, retrievedIP);
        }
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
