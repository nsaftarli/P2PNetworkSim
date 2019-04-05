import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Abstract server. Final product has servers in the directory pool as well as peers which run servers.
 * These two are different, so they should have some different server functionality.
 * Overall though, all servers should have the following.
 */
public abstract class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int id;

    public Server(){
        this.id = 1;
    }

    public  Server(int id) {
        this.id = id;
    }

    public abstract void start(int port);
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
