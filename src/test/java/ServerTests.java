import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by Nariman on 2019-03-19.
 */
public class ServerTests {

    @Test
    public void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() {
        TestClient client = new TestClient();
        client.startConnection("127.0.0.1", 6666);
        try{
            String response = client.sendMessage("hello server");
            Assert.assertEquals("hello client", response);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
