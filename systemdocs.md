We are building a peer to peer shared photo album with a distributed directory.
Using Java we are implementing a distributed directory server pool, with a P2PClient and P2PServer.
The purpose of the client and directory: 
	inform and update the server with records of content name and client IP address.
	query for content by hashing the content name into the server ID.
	done by using a UDP connection
The purpose of the P2PClient and P2PServer
	file transfer with a GET request using a TCP connection
The purpose of the directory server pool 
	implemented as a distributed hash table, 4 servers are linked with IDs 1 to 4.
	the records are stored here using a hash function, that clients are updating

Design choices
	Design contraints:
	Ports were limited to 20680-20689, 
	where we let our directory servers ID 1 to 4 be in the range 20680-20683.
	we let our peers have the ports 20684-20689.
	Currently we are limited to local host because we are running this using multiple Intellij terminals.
	
Important Algorithms
	Hashing
	
Data structures
	HashMaps
	Arrays - buffered image thing
	
