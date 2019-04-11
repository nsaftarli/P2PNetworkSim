# System Documentation

We are building a peer to peer shared photo album with a distributed directory.
Using Java we are implementing a distributed directory server pool, with a P2PClient and P2PServer.

## Purpose of Client and Directory
	inform and update the server with records of content name and client IP address.
	query for content by hashing the content name into the server ID.
	done by using a UDP connection


## Purpose of the P2PClient and P2PServer
	file transfer with a GET request using a TCP connection

## Purpose of the directory server pool 
	implemented as a distributed hash table, 4 servers are linked with IDs 1 to 4.
	the records are stored here using a hash function, that clients are updating

## Design choices
	Design contraints: 
		- Ports were limited to 20680-20689, 
		- We selected our directory servers IDs 1 to 4 be in the range 20680-20683.
		- We selected our peers have the ports 20684-20689.
		- For testing locally on our laptops, we are limited to localhost and multiple Intellij terminals
		- For lab testing purposes, we were able to run Directory servers on one computer, and Peer Clients on separate servers
		This was done by changing the value on the `LOCALHOST` constant value within P2PClient.java from `127.0.0.1` to
		the IPAddress of the machine that runs the Directory Servers.
	
## Important Algorithms
	We used a Hash Function that takes a key, and hashes a value based on the number of servers: 4
	This can be viewed under MiscFunctions.java
	
## Data structures
	
	HashMaps
		- Used to save the directory servers, records, and peers

	Arrays 
		- Used for image file transfer, we used byte arrays to send and receive images over TCP
		- Also used byte arrays for the Datagram Packet transfers for UDP
	
