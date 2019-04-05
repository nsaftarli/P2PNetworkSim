import java.util.HashMap;

/**
 * This is a server running in the directory pool. IDs should be numbers [1,4]
 * Inherits from Server so basic functionality is in that class. Overrides should be different.
 */
public class DirectoryServer extends Server {

    private HashMap<Integer, String> hashMap;


    public DirectoryServer(int id) {
        super(id);
    }

    @Override
    public void start(int port){}

    @Override
    public void stop(){}


    public void insertInHash(String key, String value) {
        int newKey = MiscFunctions.hashFunction(key);
        hashMap.put(newKey, value);
    }
}
