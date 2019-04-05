import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the client service running on a peer in the network.
 */
public class P2PClient {
    private HashMap<Integer, String> directoryHashMap;
    private HashMap<String, Record> recordHashMap;
    private ArrayList files;

    public P2PClient(String ip){
        // P2P client starts knowing IP of directory server with ID=1
        directoryHashMap.put(1, ip);
        for(int i = 2; i <= 4; i++) {
            // Use IP to ask DHT for IP addresses of remaining servers
            // retrievedIP = ...
            // directoryHashMap.put(i, retrievedIP);
        }
    }


    public void storeRecord(String name) {
        int serverID = MiscFunctions.hashFunction(name);
        String serverIP = getServerIP(serverID);
        // Contact server with serverID to store record (content name, client IP)


        // Keep the local record (content name, server ID, server's IP address)
        Record record = new Record(name, serverID, serverIP);
        recordHashMap.put(name, record);



    }

    public int getServerID(String val){

    }

    public String getServerIP(int id) {
        return directoryHashMap.get(id);
    }

    public void insertIntoRecordHashMap(String key, Record record) {
        recordHashMap.put(key, record);
    }
}
