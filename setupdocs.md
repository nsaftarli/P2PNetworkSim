A peer to peer shared photo album with distributed directory.
Our main Java programs are the DirectoryServer, P2PServer and P2PClient.
The directory server has ID 1 to 4, and is implemented using a distributed hash table.

The directory servers are connected in a ring using TCP connections.
P2PClient and directory server interacts with a unique UDP port from the directory servers.
P2PClient to P2PServer interacts with a TCP connection.


SETUP
Step 1: Start Intellij on a computer and open the project folder. File -> Open
Step 2: Navigate to "P2PPhotoAlbum"

SETTING UP THE DIRECTORY SERVERS
Step 1: Second toolbar from the top beside the hammer icon. Click the drop down -> edit configuations
Step 2: Plus icon -> Application
Step 3: Name: DServer1(2, 3, 4 - set up 4 different configurations)
Step 4: Main class -> DirectoryServer
Step 5: Program Arguments for each DServer is the ID# and Port#
		For DServer1, this will be: 1 20680
		DServer2, this will be: 2 20681
		DServer3, this will be 3 20682
		Dserver4, this will be 4 20683

SETTING UP P2PClient for testing Upload
Step 1: Second toolbar from the top beside the hammer icon. Click the drop down -> edit configuations
Step 2: Plus icon -> Application
Step 3: Name: P2PClientUpload
Step 4: Main class -> P2PClient
Step 5: Program Arguments will take a port#: 20684

SETTING UP P2PClient for testing Download
Step 1: Second toolbar from the top beside the hammer icon. Click the drop down -> edit configuations
Step 2: Plus icon -> Application
Step 3: Name: P2PClientDownload
Step 4: Main class -> P2PClient
Step 5: Program Arguments will take a port#: 20685

SETTING UP Resource Folder file paths
	Within the P2PPhotoAlbum, create two directories: resources_in and resources_out.
	Within resources_in, store the image files you need for testing. resources_out is
	where the transferred images will be stored.

	On line 321 of the P2PClient.java file, there is an addFile(String name) method,
    here update `String path = "<with your full file path>/resources_in/"`

	On line 255 of the P2PClient.java file, there is an Image.write function call,
	here update `ImageIO.write(image, "jpg", new File("<with your full file path>/resources_out/" + name));`


RUNNING 
Step 1: Run all four Directory Servers
Step 2: Run the P2PClientUpload
Step 3: Run the P2PClientDownload
Step 4: In the P2PClientUpload command line, enter U to select Upload.
Step 5: Enter `<yourFileName>.jpg` in the command line, to upload the test image file.
Step 6: In the P2PClientDownload command line, enter Q to select Querying for downloading the test file
Step 7: Enter `<yourFileName>.jpg` in the command line for P2PClientDownload for the download to begin
Step 8: Wait for the image to load

 