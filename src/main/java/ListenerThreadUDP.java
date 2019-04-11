import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Scanner;

public class ListenerThreadUDP extends Thread {
    DatagramSocket datagramSocket;
    int port;
    HashMap<Integer, Integer> directory_map;
    HashMap<String, Integer> peerMap;

    /**
     * Thread to handle requests for information on files from peers
     * @param port Port this DHT server runs on
     * @param directory_map Locations of other DHT servers
     * @param peerMap Locations of files
     */
    public ListenerThreadUDP(int port, HashMap directory_map, HashMap peerMap) {
        this.port = port;
        this.directory_map = directory_map;
        this.peerMap = peerMap;
    }

    public void run() {
        String msg;
        try {
            System.out.println("UDP is starting up...");
            datagramSocket = new DatagramSocket(port);
            byte[] receive = new byte[65535];
            DatagramPacket DpReceive = null;

            while (true) {

                DpReceive = new DatagramPacket(receive, receive.length); // receives data from P2P Client
                datagramSocket.receive(DpReceive); // Data in the byte buffer
                msg = new String(DpReceive.getData());
                System.out.println("Client:-" + msg);


                // TEMP: Exits server if the P2P Client content has been received
                if (msg.length() > 0 && msg.contains("Upload")) {

                    System.out.println("Client has received Upload: " + msg);
                    Scanner sc = new Scanner(msg);
                    sc.next();
                    String fileName = sc.next().trim();
                    System.out.println(fileName);
                    int srcPort = Integer.parseInt(sc.next().trim());

                    insertInHash(fileName, srcPort); // Inserts record into hash map
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
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
        datagramSocket.send(sendPacket); // Send the UDP packet.
    }

    public Integer getFromHash(String fileName) {
        return peerMap.get(fileName);
    }
    public void insertInHash(String key, int value) {
        peerMap.put(key, value);
    }
}
