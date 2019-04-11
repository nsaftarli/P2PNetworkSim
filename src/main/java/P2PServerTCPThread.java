import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Scanner;

public class P2PServerTCPThread extends Thread{
    int port;
    Socket clientSocket;
    HashMap<String, File> file_map;
    Scanner scanner;

    /**
     * Handles servicing file transfer requests from peers
     * @param port Port this server sits on
     * @param clientSocket The socket the client is connected through
     * @param file_map File name maps to file object
     */
    public P2PServerTCPThread(int port, Socket clientSocket, HashMap file_map) {
        this.port = port;
        this.clientSocket = clientSocket;
        this.file_map = file_map;
    }

    public void run() {
        try {
            //Check if asking for a file
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String request = in.readLine();
            scanner = new Scanner(request);

            if (scanner.next().equals("GET")) {
                //Get the file from the map
                String filename = scanner.next();
                File file = file_map.get(filename);
                System.out.println(file.toString());

                //Write the file out to stream, close socket
                OutputStream out = clientSocket.getOutputStream();
                BufferedImage img = ImageIO.read(file);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(img, "JPG", byteArrayOutputStream);
                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
                out.write(size);
                out.write(byteArrayOutputStream.toByteArray());
                out.flush();
                Thread.sleep(15000);
                System.out.println("Sent file");
                clientSocket.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
