import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TestFileServer {
    static ServerSocket serverSocket;
    static Socket clientSocket;
    public static void main(String[] args) throws IOException {
        int bytesRead;
        int current = 0;

        serverSocket = new ServerSocket(4444);

        while (true) {
            clientSocket = serverSocket.accept();
            InputStream in = clientSocket.getInputStream();

            OutputStream out = new FileOutputStream("/Users/Nariman/Documents/School/CPS706/ClientServer/resources/abc.png");

            byte[] buffer = new byte[2048];

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();

        }
    }
}
