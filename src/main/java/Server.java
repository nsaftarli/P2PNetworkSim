import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Abstract server. Final product has servers in the directory pool as well as peers which run servers.
 * These two are different, so they should have some different server functionality.
 * Overall though, all servers should have the following.
 */
public abstract class Server {
    protected ServerSocket serverSocket;
    protected DatagramSocket datagramSocket;
    protected Socket clientSocket;
    protected PrintWriter out;
    protected BufferedReader in;
    protected int id;
    protected int port;


    public Server(){
        this.id = 1;
        this.port = 4444;
    }
    public Server(int port) {
        this.port = port;
    }
    public Server(int id, int port) {
        this.port = port;
        this.id = id;
    }


    public abstract void start();
    public abstract void stop();

    public int idGetter(){
        return this.id;
    }
    public BufferedReader inGetter(){
        return this.in;
    }
    public PrintWriter outGetter(){
        return this.out;
    }
    public Socket clientSocketGetter(){
        return this.clientSocket;
    }
    public ServerSocket serverSocketGetter(){
        return this.serverSocket;
    }
}
