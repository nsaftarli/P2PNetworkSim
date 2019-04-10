import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DirectoryThread extends Thread {
    protected ServerSocket serverSocket;
    protected Socket clientSocket;
    protected DatagramSocket clientDatagramSocket;
    //    protected PrintWriter out;
    protected PrintWriter str_out;
    protected BufferedReader in;
    protected String clientMsg;
    // DHT server ID -> DHT server port
    protected HashMap<Integer, Integer> directory_map;
    // Content name -> Peer port
    protected HashMap<String, Integer> file_map;
    private String LOCALHOST = "127.0.0.1";

    public DirectoryThread(DatagramSocket clientDatagramSocket, HashMap directory_map, HashMap file_map) {
        this.clientDatagramSocket = clientDatagramSocket;
        this.directory_map = directory_map;
        this.file_map = file_map;
    }

    public void run() {

        try {
//            out = new PrintWriter(clientSocket.getOutputStream(), true);
            byte [] buffer = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            clientDatagramSocket.receive(packet);
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            clientMsg = in.readLine();
            if (clientMsg.equals("serverLocs")) {
                System.out.println("serverLocs");
                int[] serverLocs = getServerLocs();
                out.writeObject(serverLocs);
            }
            if(clientMsg.equals("hello")) {
                System.out.println("hi");
//                out.println("hi");
            } else {
                System.out.println("noo");
            }

            if (clientMsg.contains("leaving")) {
                String[] splitMsg = clientMsg.split(":");
                int clientID = Integer.parseInt(splitMsg[1]);
                //Remove files from hash map
                Set entrySet = file_map.entrySet();
                for (Map.Entry entry : file_map.entrySet()) {
                    String key = (String) entry.getKey();
//                    if (entry.getValue() == clientID) {
//                        file_map.remove(key);
//                    }
                }

                //Inform other servers
                for (int i = 0; i < 3; i++) {
                    int port = directory_map.get(i);
                    startConnection(LOCALHOST, port);
                    sendMessage(clientMsg);
                    stopConnection();
                }
                //Connect to other servers in DHT
                //Send a message to each DHT server to remove entries by leaving client.


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            str_out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//            in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stopConnection() throws IOException {
        try {
            in.close();
            str_out.close();
            clientSocket.close();
            System.out.println("Connection closed");
        } catch(IOException e) {
            e.printStackTrace();
            throw new IOException("Closing connections failed");
        }
    }

    public Object sendMessage(String msg) throws IOException {
        Object response;

        str_out.println(msg);
        try {

            response = in.readLine();
//            response = in.readObject();
            return response;
        } catch(IOException e) {
            e.printStackTrace();
        }
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

        throw new IOException("No response from server");
    }
    public int[] getServerLocs() {
        int[] locs = new int[4];
        for (int i = 0; i < 4; i++) {
            int sPort = directory_map.get(i+1);
            locs[i] = sPort;
        }
        return locs;
    }

}
