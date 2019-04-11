# System Documentation

We are building a peer to peer shared photo album with a distributed directory.
Using Java we are implementing a distributed directory server pool, with a P2PClient and P2PServer.

## Purpose of Client and Directory
	inform and update the server with records of content name and client IP address.
	query for content by hashing the content name into the server ID.
	done by using a UDP connection


## Purpose of the P2PClient and P2PServer
    P2PClient is the main process for both the client and server side of a peer in the network.
    When a client is initialized, a server thread is spawned to listen for TCP connections. Since the server
    side of a P2P client is only responsible for file transfer, spawning a thread to handle TCP file transfer requests
    (using HTTP's GET) is sufficient.

## Purpose of the directory server pool 
	Implemented as a distributed hash table, 4 servers are linked with IDs 1 to 4.
	the records are stored here using a hash function, that clients are updating.
	Directory servers know the locations of other directory servers. Server 1 updates new peers on
	the locations of other directory servers. From there, directory servers are used to track
	files on the network and their locations.

## Design choices
	Design contraints: 
		- Ports were limited to 20680-20689, 
		- We selected our directory servers IDs 1 to 4 be in the range 20680-20683.
		- We selected our peers have the ports 20684-20689.
		- For testing locally on our laptops, we are limited to localhost and multiple Intellij terminals
		- For lab testing purposes, we were able to run Directory servers on one computer, and Peer Clients on separate servers
		This was done by changing the value on the `LOCALHOST` constant value within P2PClient.java from `127.0.0.1` to
		the IPAddress of the machine that runs the Directory Servers.
		
	Solution design:
		- Because of blocking IO, DHT servers run 2 listener threads, each of which acts as a server for TCP and UDP, respectively.
		- The 2 listener threads themselves spawn threads when they get a connection (TCP) or a datagram (UDP)
		- While DHT servers and server processes of peers have different functionality, they're fundamentally similar,
		so we have a Server superclass, which P2PServer and DirectoryServer extend.
	
## Important Algorithms
	We used a Hash Function that takes a key, and hashes a value based on the number of servers: 4
	This can be viewed under MiscFunctions.java
	We leveraged multi-threading to allow servers (which ordinarily infinitely) to handle blocking IO and servicing
	of multiple clients. Important threads are located in the following classes: `DirectoryThread`, `ListenerThreadTCP`,
	`ListenerThreadUDP`, `P2PServerListenerThread`, `P2PServerTCPThread`.
	Listener threads take the role of servers, waiting for connection requests. Since there are multiple threads,
	blocking IO is not a problem. Once the listener threads get a connection, they spawn TCP/UDP threads to deal
	with the requests.
	
## Data structures
	
	HashMaps
		- Used to save the directory servers, records, peers, and keep track of files.
		- Directory HashMap: DHT Server ID -> DHT Server IP/Port
		- Record HashMap: File name -> Client IP/Port
		- Peer HashMap: File name -> (Server ID, Server IP)
		- File HashMap: File name -> File object

	Arrays 
		- Used for image file transfer, we used byte arrays as buffers to send and receive images over TCP
		- Also used byte arrays for the Datagram Packet transfers for UDP
	
