import java.util.HashMap;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * This is a server running in the directory pool. IDs should be numbers [1,4]
 * Inherits from Server so basic functionality is in that class. Overrides should be different.
 */
public class DirectoryServer extends Server {

    private HashMap<Integer, String> hashMap = new HashMap<>();


    public DirectoryServer(int id) {
        super(id);
    }

    @Override
    public void start(int port){

    }

    @Override
    public void stop(){}


    public void insertInHash(String key, String value) {
        int newKey = MiscFunctions.hashFunction(key);
        System.out.println(newKey);
        System.out.println(value);
        hashMap.put(newKey, value);
    }

    public static void main(String[] args) throws IOException {
        DirectoryServer dirServer = new DirectoryServer(12456);

        // Step 1 : Create a socket to listen at port 1234
        DatagramSocket ds = new DatagramSocket(1234);
        byte[] receive = new byte[65535];

        DatagramPacket DpReceive = null;
        while (true)
        {

            // Step 2 : create a DatgramPacket to receive the data.
            DpReceive = new DatagramPacket(receive, receive.length);

            // Step 3 : retrieve the data in byte buffer.
            ds.receive(DpReceive);

            System.out.println("Client:-" + data(receive));

            // Exit the server if the client sends "bye"
            if (data(receive).length() > 0) {
                System.out.println("Client has received content.....EXITING");
                int portNum = DpReceive.getPort();
                String portNumStr = Integer.toString(portNum);
                dirServer.insertInHash(portNumStr, data(receive).toString());
                break;
            }

            // Clear the buffer after every message.
            receive = new byte[65535];

        }

        for (Integer name: dirServer.hashMap.keySet()){

            String key = name.toString();
            String value = dirServer.hashMap.get(name);
            System.out.println(key + " " + value);


        }
    }

    // A utility method to convert the byte array
    // data into a string representation.
    public static StringBuilder data(byte[] a)
    {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0)
        {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }

}
