/**
 * This is a server running on one of the peers in the network.
 */

public class P2PServer extends Server {
    public P2PServer(){
        super();
    }
    public P2PServer(int id) {
        super(id);
    }

    private Thread thread = null;

    public void start() {}
    public void stop(){}

}
