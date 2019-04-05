
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Toy example of a client.
 */
public class TestClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        System.out.println("Trying to start a connection to " + ip + " at port " + port);

        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void stopConnection() throws IOException {
        try {
            in.close();
            out.close();
            clientSocket.close();

            System.out.println("Connection closed");
        } catch(IOException e) {
            e.printStackTrace();
            throw new IOException("Closing connections failed");
        }
    }

    public String sendMessage(String msg) throws IOException {
        String response;

        out.println(msg);
        try {
            response = in.readLine();
            return response;
        } catch(IOException e) {
            e.printStackTrace();
        }

        throw new IOException("No response from server");
    }
}
