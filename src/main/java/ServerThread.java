import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
    protected ServerSocket serverSocket;
    protected Socket clientSocket;
    protected PrintWriter out;
    protected BufferedReader in;
    protected String clientMsg;

    public ServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    public void run() {

        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            clientMsg = in.readLine();
            if(clientMsg.equals("hello")) {
                System.out.println("hi");
                out.println("hi");
            } else {
                System.out.println("noo");
                out.println("noo");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

