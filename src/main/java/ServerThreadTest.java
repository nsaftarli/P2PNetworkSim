import java.net.Socket;

public class ServerThreadTest {
    public static void main(String[] args) {
        int n = 8;
        Socket clientSocket = new Socket();
        for(int i = 0; i < n; i++) {
            ServerThread t = new ServerThread(clientSocket);
            t.start();
        }
    }
}
