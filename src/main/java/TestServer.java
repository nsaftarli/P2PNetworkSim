import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Toy example of a server
 */
public class TestServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientMsg;

    public static void main(String[] args) throws IOException {
        if(args.length < 1) {
            throw new IOException("Usage: java TestServer [portNumber]");
        }
        int portNumber = Integer.parseInt(args[0]);
        TestServer server = new TestServer();
        server.start(portNumber);

    }

    public void start(int portNumber) {
        System.out.println("Server started.");
        try {
            serverSocket = new ServerSocket(portNumber);
            clientSocket = serverSocket.accept();

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


            clientMsg = in.readLine();

            if(clientMsg.equals("hello")) {
                System.out.println("Got client message");
                out.println("hi");
            } else {
                System.out.println("Got wrong message");
                out.println("no");
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
}
