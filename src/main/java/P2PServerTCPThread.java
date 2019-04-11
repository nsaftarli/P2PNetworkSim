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

    public P2PServerTCPThread(int port, Socket clientSocket, HashMap file_map) {
        this.port = port;
        this.clientSocket = clientSocket;
        this.file_map = file_map;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String request = in.readLine();
            scanner = new Scanner(request);

            if (scanner.next().equals("GET")) {
                String filename = scanner.next();
                File file = file_map.get(filename);
                System.out.println(file.toString());
                System.out.println("CCCCCC");

//                byte[] fileBytes = new byte[(int) file.length()];
//                FileInputStream fileInputStream = new FileInputStream(file);
//                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
//                bufferedInputStream.read(fileBytes, 0, fileBytes.length);
//
//                OutputStream outputStream = clientSocket.getOutputStream();
//                outputStream.write(fileBytes, 0, fileBytes.length);
//                outputStream.flush();
//                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
//                OutputStream outputStream = clientSocket.getOutputStream();
//                DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
//                BufferedImage image = ImageIO.read(file);
//                ImageIO.write(image, "png", dataOutputStream);
//
//                BufferedImage image = ImageIO.read(file);
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                ImageIO.write(image, "png", byteArrayOutputStream);
//
//                byte[] img_bytes = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
//                outputStream.write(img_bytes);
//                outputStream.write(byteArrayOutputStream.toByteArray());
//                outputStream.flush();

//                OutputStream outputStream = clientSocket.getOutputStream();
//                BufferedImage image = ImageIO.read(file);
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                ImageIO.write(image, "png", byteArrayOutputStream);
//
//                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
//                outputStream.write(size);
//                outputStream.write(byteArrayOutputStream.toByteArray());
//                outputStream.flush();
//                clientSocket.close();

//                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
//                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
//
//                byte[] buff = new byte[4096];
//                int bytesRead;
//                while ( (bytesRead = bufferedInputStream.read(buff)) != -1 ) {
//                    bufferedOutputStream.write(buff, 0, bytesRead);
//                }
//                bufferedInputStream.close();
//                bufferedOutputStream.flush();
//                bufferedOutputStream.close();

                DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

                BufferedImage img = ImageIO.read(file);
                ImageIO.write(img, "PNG", clientSocket.getOutputStream());

                System.out.println("Sent file");
                clientSocket.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
