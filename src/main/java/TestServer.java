import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Toy example of a server
 */
public class TestServer implements Runnable {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientMsg;
    private Thread thread = null;
    private static int portNumber;

    public static void main(String[] args) throws IOException {
        if(args.length < 1) {
            throw new IOException("Usage: java TestServer [portNumber]");
        }
        portNumber = Integer.parseInt(args[0]);

        System.out.println("Server started");
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new TestServer(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TestServer(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

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


    public void start(int portNumber) {
        System.out.println("Server started.");
        try {
            serverSocket = new ServerSocket(portNumber);

            while(true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new TestServer(clientSocket)).start();
//                out = new PrintWriter(clientSocket.getOutputStream(), true);
//                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//
//
//                clientMsg = in.readLine();
//
//                if (clientMsg.equals("hello")) {
//                    System.out.println("Got client message");
//                    out.println("hi");
//                } else {
//                    System.out.println("Got wrong message");
//                    out.println("no");
//                }
            }

        }
        catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    public void stop() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void setPortNumber(int port) {
        this.portNumber = port;
    }
}
