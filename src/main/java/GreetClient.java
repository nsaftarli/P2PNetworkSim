import java.net.*;
import java.io.*;

public class GreetClient  {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        try {
            // Try to create a new socket and connect to port on the ip
            clientSocket = new Socket(ip, port);
            // Output for client is using an output stream
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            // Input for client is retrieved through the client socket
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String sendMessage(String msg) {
        // prints msg to the printwriter which is connected to the client socket
        out.println(msg);
        try {
            String resp = in.readLine();
            return resp;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error";
//        String resp = "Hello";
//        return "Error";
//        return resp;
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}