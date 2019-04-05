import java.io.IOException;

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
        serverResponse = client.sendMessage("hi");
        System.out.println(serverResponse);
        client.stopConnection();
    }
}
