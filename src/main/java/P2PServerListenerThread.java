import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class P2PServerListenerThread extends Thread {
    int port;
    HashMap<String, File> file_map;
    ServerSocket serverSocket;

    public P2PServerListenerThread(int port, HashMap file_map) {
        this.port = port;
        this.file_map = file_map;
    }

    public void run() {
        String msg;

        try{
            System.out.println("P2P Server started at port " + port);
            this.serverSocket = new ServerSocket(port);

            while(true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection");
                new P2PServerTCPThread(port, clientSocket, file_map).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
