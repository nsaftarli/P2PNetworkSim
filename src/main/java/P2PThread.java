import java.io.*;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class P2PThread extends Thread {
    protected ServerSocket serverSocket;
    protected Socket clientSocket;
    protected DatagramSocket clientDatagramSocket;
    protected PrintWriter str_out;
    protected BufferedReader in;
    protected String clientMsg;
    protected HashMap<Integer, Integer> directory_map;
    protected HashMap<String, String> file_map;
    private String LOCALHOST = "127.0.0.1";

    public P2PThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    public P2PThread(Socket clientSocket, HashMap directory_map, HashMap file_map) {
        this.clientSocket = clientSocket;
        this.directory_map = directory_map;
        this.file_map = file_map;

    }
    public void run() {

        try {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            clientMsg = in.readLine();
            System.out.println(clientMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

