/**
 * Each Peer should be running both client (P2PClient) and server (P2PServer) services.
 */

public class Peer {
    P2PServer p2pServer;
    P2PClient p2pClient;
    public Peer() {
        p2pServer = new P2PServer();
        p2pClient = new P2PClient("127.0.0.1");
    }



}
