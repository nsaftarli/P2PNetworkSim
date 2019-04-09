import java.io.*;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread extends Thread {
    protected ServerSocket serverSocket;
    protected Socket clientSocket;
    protected DatagramSocket clientDatagramSocket;
//    protected PrintWriter out;
    protected BufferedReader in;
    protected String clientMsg;
    protected HashMap<Integer, Integer> directory_map;
//    protected DirectoryMapRecord[] records;

    public ServerThread(DatagramSocket clientDatagramSocket) {
        this.clientDatagramSocket = clientDatagramSocket;
    }
    public ServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    public ServerThread(Socket clientSocket, HashMap map) {
        this.clientSocket = clientSocket;
        this.directory_map = map;

    }
    public void run() {

        try {
//            out = new PrintWriter(clientSocket.getOutputStream(), true);
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            clientMsg = in.readLine();
            if (clientMsg.equals("serverLocs")) {
                System.out.println("serverLocs");
//                DirectoryMapRecord[] serverLocs = getRecords();
                int[] serverLocs = getServerLocs();
                out.writeObject(serverLocs);
            }
            if(clientMsg.equals("hello")) {
                System.out.println("hi");
//                out.println("hi");
            } else {
                System.out.println("noo");
//                out.println("noo");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] getServerLocs() {
        int[] locs = new int[4];
        for (int i = 0; i < 4; i++) {
            int sPort = directory_map.get(i+1);
            locs[i] = sPort;
        }
        return locs;
    }

//    public DirectoryMapRecord[] getRecords() {
//        DirectoryMapRecord[] locs = new DirectoryMapRecord[4];
//        for (int i = 0; i < 4; i++) {
//            locs[i] = new DirectoryMapRecord(i+1, directory_map.get(i+1));
//        }
//        return locs;
//    }
}

