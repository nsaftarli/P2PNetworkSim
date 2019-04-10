import java.io.*;
import java.net.*;

public class TestFileClient {

    public static void main(String[] args) throws IOException {
        Socket sock = new Socket("127.0.0.1", 4444);

        // sendfile
        File myFile = new File("/Users/Nariman/Documents/School/CPS706/ClientServer/scnetworks.png");
        byte[] mybytearray = new byte[(int) myFile.length()];

        FileInputStream fis = new FileInputStream(myFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(mybytearray, 0, mybytearray.length);

        OutputStream os = sock.getOutputStream();

        os.write(mybytearray, 0, mybytearray.length);

        os.flush();

        sock.close();
    }
}
