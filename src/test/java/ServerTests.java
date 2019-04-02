import org.testng.Assert;
import org.testng.annotations.Test;
/**
 * Created by Nariman on 2019-03-19.
 */
public class ServerTests {

    @Test
    public void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() {
        GreetClient client = new GreetClient();
        client.startConnection("127.0.0.1", 6666);
        String response = client.sendMessage("hello server");
        Assert.assertEquals("hello client", response);
    }

}
