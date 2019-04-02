import java.net.*;
import java.io.*;


public class GreetServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void start(int port) {
        try {
            // Opens server socket bound to port
            serverSocket = new ServerSocket(port);

            /*
                Client socket is the socket that attempts to connect to server.
                serverSocket.accept() listens for client to attempt to establish connection
                Output is retrieved in a PrintWriter object from the client socket's output stream
                input streamed from client socket as bytes are decoded using InputStreamReader using default charset
                This decoded input is put into a buffer
                Reads a line of text

             */
            clientSocket = serverSocket.accept();
//            while (true) {
//                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String greeting = in.readLine();
                if ("hello server".equals(greeting)) {
                    out.println("hello client");
                    System.out.println("Received greeting");
                } else {
                    System.out.println("Received unrecognized greeting");
                    out.println("unrecognized greeting");
                }
//            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stop() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        GreetServer server=new GreetServer();
        server.start(6666);
    }
}