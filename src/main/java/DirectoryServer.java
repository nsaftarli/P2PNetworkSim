import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.HashMap;
import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;


/**
 * This is a server running in the directory pool. IDs should be numbers [1,4]
 * Inherits from Server so basic functionality is in that class. Overrides should be different.
 * Implements Runnable for multi-threading.
 */
public class DirectoryServer extends Server implements Runnable {
    private String clientMsg;

    private HashMap<Integer, Integer> directory_map = new HashMap<Integer, Integer>();

    private HashMap<Integer, String> hashMap = new HashMap<Integer, String>();


    public DirectoryServer(int id, Socket s) {
        super(id);
        this.clientSocket = s;
        buildServerTable();
    }
    public DirectoryServer(int id, int port, Socket s) {
        super(id, port);
        this.clientSocket = s;
        buildServerTable();
    }

    @Override
    public void start(){
        System.out.println("Server " + id + " started at port " + port);
        try {
            serverSocket = new ServerSocket(port);
//            datagramSocket = new DatagramSocket(port);
            while (true) {
                clientSocket = serverSocket.accept();
//                new Thread(new DirectoryServer(id)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stop(){}

    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            clientMsg = in.readLine();
            if (clientMsg.equals("hello")) {
                System.out.println("Got client message");
                out.println("hi");
            } else {
                System.out.println("Got wrong message");
                out.println("no");
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void insertInHash(String key, String value) {
        int newKey = MiscFunctions.hashFunction(key);
        hashMap.put(newKey, value);
    }

    public static void main(String[] args) throws IOException {
        int id = Integer.parseInt(args[0]);
        int port = Integer.parseInt(args[1]);



//        DirectoryServer dirServer = new DirectoryServer(id, port);

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
//                new Thread(new DirectoryServer(id, port, clientSocket)).start();
                new ServerThread(clientSocket).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
//        dirServer.start();
        // Step 1 : Create a socket to listen at port 1234
//        DatagramSocket ds = new DatagramSocket(1234);
//        byte[] receive = new byte[65535];
//
//        DatagramPacket DpReceive = null;
//        while (true)
//        {
//
//            // Step 2 : create a DatgramPacket to receive the data.
//            DpReceive = new DatagramPacket(receive, receive.length);
//
//            // Step 3 : retrieve the data in byte buffer.
//            ds.receive(DpReceive);
//
//            System.out.println("Client:-" + data(receive));
//
//            // Exit the server if the client sends "bye"
//            if (data(receive).length() > 0) {
//                System.out.println("Client has received content.....EXITING");
//                int portNum = DpReceive.getPort();
//                String portNumStr = Integer.toString(portNum);
//                dirServer.insertInHash(portNumStr, data(receive).toString());
//                break;
//            }
//
//            // Clear the buffer after every message.
//            receive = new byte[65535];
//
//        }
//
//        for (Integer name: dirServer.hashMap.keySet()){
//
//            String key = name.toString();
//            String value = dirServer.hashMap.get(name);
//            System.out.println(key + " " + value);
//
//
//        }
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

    public void buildServerTable() {
        for (int i = 0; i < 3; i++) {
            System.out.println("Inserted into directory map server " + (i+1) + " with port " + (50680+i));
            directory_map.put(i + 1, 50680 + i);
        }

    }

}
