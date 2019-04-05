/**
 * This is a server running in the directory pool. IDs should be numbers [1,4]
 * Inherits from Server so basic functionality is in that class. Overrides should be different.
 */
public class DirectoryServer extends Server {
    public DirectoryServer(int id) {
        super(id);
    }

    public void start(int port){}
    public void stop(){}

    public int hashFunction(String key) {return -1;}
}
