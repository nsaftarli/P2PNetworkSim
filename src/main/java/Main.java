public class Main {
    public static void main(String[] args) {
        String response;
        GreetClient client = new GreetClient();
        client.startConnection("127.0.0.1", 6666);
        response = client.sendMessage("hello server");
        System.out.println(response);

        client.startConnection("127.0.0.1", 6666);
        response = client.sendMessage("hello");
        System.out.println(response);
    }
}
