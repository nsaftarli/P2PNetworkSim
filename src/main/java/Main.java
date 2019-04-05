import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        if(args.length < 2) {
            throw new IOException("Usage: java Main [ipAddress] [portNumber]");
        }
        String ipAddress = args[0];
        int portNumber = Integer.parseInt(args[1]);

        TestClient client = new TestClient();
        client.startConnection(ipAddress, portNumber);
        String serverResponse = client.sendMessage("hello");
        System.out.println(serverResponse);
//        client.stopConnection();

//        client.startConnection(ipAddress, portNumber);

        Scanner sc = new Scanner(System.in);

        // Step 1:Create the socket object for
        // carrying the data.
        DatagramSocket ds = new DatagramSocket();
        InetAddress ip = InetAddress.getLocalHost();
        byte buf[] = null;

        // loop while user not enters "bye"
        while (true) {
            String inp = sc.nextLine();

            // convert the String input into the byte array.
            buf = inp.getBytes();

            // Step 2 : Create the datagramPacket for sending
            // the data.
            DatagramPacket DpSend =
                    new DatagramPacket(buf, buf.length, ip, 4444);

            // Step 3 : invoke the send call to actually send
            // the data.
            ds.send(DpSend);

            // break the loop if user enters "bye"
            if (inp.equals("bye"))
                break;
        }

//        serverResponse = client.sendMessage("hi");
//        System.out.println(serverResponse);
//        client.stopConnection();
        client.startConnection(ipAddress, portNumber);
        serverResponse = client.sendMessage("hi");
        System.out.println(serverResponse);
        client.stopConnection();
    }
}
