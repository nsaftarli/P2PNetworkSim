import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class ListenerThreadTCP extends Thread {
    ServerSocket serverSocket;
    int port;
    HashMap<Integer, Integer> directory_map;
    HashMap<String, Integer> peerMap;
    Scanner scanner;

    public ListenerThreadTCP(int port, HashMap directory_map, HashMap peerMap) throws IOException {
        this.port = port;
        this.directory_map = directory_map;
        this.peerMap = peerMap;
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ServerThread(clientSocket, directory_map, peerMap).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
