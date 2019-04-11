# Setup Documentation

A peer to peer shared photo album with distributed directory.
Our main Java programs are the DirectoryServer, P2PServer and P2PClient.
The directory server has ID 1 to 4, and is implemented using a distributed hash table.

The directory servers are connected in a ring using TCP connections.
P2PClient and directory server interacts with a unique UDP port from the directory servers.
P2PClient to P2PServer interacts with a TCP connection.


## SETUP FOR LOCAL TESTING

1. Start Intellij on a computer and open the project folder. File -> Open
2. Navigate to "P2PPhotoAlbum"

## SETTING UP THE DIRECTORY SERVERS

1. Second toolbar from the top beside the hammer icon. Click the drop down -> edit configuations
2. Plus icon -> Application
3. Name: DServer1(2, 3, 4 - set up 4 different configurations)
4. Main class -> DirectoryServer
5. Program Arguments for each DServer is the ID# and Port#
		For DServer1, this will be: 1 20680
		DServer2, this will be: 2 20681
		DServer3, this will be 3 20682
		Dserver4, this will be 4 20683

## SETTING UP P2PClient for testing Upload

1. Second toolbar from the top beside the hammer icon. Click the drop down -> edit configuations
2. Plus icon -> Application
3. Name: P2PClientUpload
4. Main class -> P2PClient
5. Program Arguments will take a port#: 20684

## SETTING UP P2PClient for testing Download

1. Second toolbar from the top beside the hammer icon. Click the drop down -> edit configuations
2. Plus icon -> Application
3. Name: P2PClientDownload
4. Main class -> P2PClient
5. Program Arguments will take a port#: 20685

## SETTING UP Resource Folder file paths
	Within the P2PPhotoAlbum, create two directories: resources_in and resources_out.
	Within resources_in, store the image files you need for testing. resources_out is
	where the transferred images will be stored.

	On line 321 of the P2PClient.java file, there is an addFile(String name) method,
    here update `String path = "<with your full file path>/resources_in/"`

	On line 255 of the P2PClient.java file, there is an Image.write function call,
	here update `ImageIO.write(image, "jpg", new File("<with your full file path>/resources_out/" + name));`

## RUNNING LOCALLY

1. Run all four Directory Servers
2. Run the P2PClientUpload
3. Run the P2PClientDownload
4. In the P2PClientUpload command line, enter U to select Upload.
5. Enter `<yourFileName>.jpg` in the command line, to upload the test image file.
6. In the P2PClientDownload command line, enter Q to select Querying for downloading the test file
7. Enter `<yourFileName>.jpg` in the command line for P2PClientDownload for the download to begin
8. Wait for the image to load


## SETTING UP FOR LAB TESTING
	For lab testing purposes, we were able to run Directory servers on one computer, and Peer Clients on separate servers
		This was done by changing the value on the `LOCALHOST` constant value within P2PClient.java from `127.0.0.1` to
		the IPAddress of the machine that runs the Directory Servers.

## RUNNING IN THE LAB

1. Run all four Directory Servers on one machine (This will be the IPAddress that the Peer Clients connect to)
2. Run the P2PClientUpload on a second machine (Change `LOCALHOST` constant value within P2PClient.java from `127.0.0.1` to
		the IPAddress of the machine that runs the Directory Servers.)
3. Run the P2PClientDownload on a third machine (Change `LOCALHOST` constant value within P2PClient.java from `127.0.0.1` to
		the IPAddress of the machine that runs the Directory Servers)
4. In the P2PClientUpload command line, enter U to select Upload.
5. Enter `<yourFileName>.jpg` in the command line, to upload the test image file.
6. In the P2PClientDownload command line, enter Q to select Querying for downloading the test file
7. Enter `<yourFileName>.jpg` in the command line for P2PClientDownload for the download to begin
8. Wait for the image to load

 