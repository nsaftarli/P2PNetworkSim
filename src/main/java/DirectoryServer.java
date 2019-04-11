import javafx.scene.chart.PieChart;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * This is a server running in the directory pool. IDs should be numbers [1,4]
 * Inherits from Server so basic functionality is in that class. Overrides should be different.
 * Implements Runnable for multi-threading.
 */
public class DirectoryServer extends Server {
    private String clientMsg;

    // DHT server ID -> DHT server port
    private static HashMap<Integer, Integer> directory_map = new HashMap<Integer, Integer>();
    // Content Name -> Peer port
    private static HashMap<String, Integer> peerMap = new HashMap<String, Integer>();
    public static DatagramSocket ds;

    public DirectoryServer(int id, int port) {
        super(id, port);
        printStartupMessage();
        buildServerTable();
    }

    @Override
    public void start(){}

    @Override
    public void stop(){}


    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[1]);	       // The server's main port. The client will connect to this port using UDP. The predecessor server will also connect to this port using TCP.
        int serverID = Integer.parseInt(args[0]);	   // The server's ID, which can be a number from 1 to n depending on the number of servers.

        DirectoryServer dirServer = new DirectoryServer(serverID, port);
        // Starts listeners for both TCP and UDP (since they block otherwise)
        new ListenerThreadTCP(port, directory_map, peerMap).start();
        new ListenerThreadUDP(port, directory_map, peerMap).start();
    }


    public void printStartupMessage() {
        System.out.println("Started new server. This server has id " + id +
                           " and is at port " + port);
    }
    public void buildServerTable() {
        for (int i = 0; i <= 3; i++) {
            System.out.println("Inserted into directory map server " + (i+1) + " with port " + (20680+i));
            directory_map.put(i + 1, 20680 + i);
        }

    }

}
