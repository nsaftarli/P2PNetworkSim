import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

/**
 * This is a server running in the directory pool. IDs should be numbers [1,4]
 * Inherits from Server so basic functionality is in that class. Overrides should be different.
 */
public class DirectoryServer extends Server implements Runnable {

    private HashMap<Integer, String> hashMap;

    public static void main(String[] args) {
        int id = Integer.parseInt(args[0]);
        int port = Integer.parseInt(args[1]);
        DirectoryServer s = new DirectoryServer(id, port);
        s.start();
    }

    public DirectoryServer(int id) {
        super(id);
    }
    public DirectoryServer(int id, int port) {
        super(id, port);
    }

    @Override
    public void start(){
        System.out.println("Server " + id + " started at port " + port);
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                clientSocket = serverSocket.accept();
                new Thread(new DirectoryServer(id)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop(){}

    public void run() {

    }
    public void insertInHash(String key, String value) {
        int newKey = MiscFunctions.hashFunction(key);
        hashMap.put(newKey, value);
    }
}
