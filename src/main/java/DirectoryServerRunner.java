public class DirectoryServerRunner {

    public static void main(String[] args){
        DirectoryServer s1 = new DirectoryServer(1, 20680);
        DirectoryServer s2 = new DirectoryServer(2, 20681);
        DirectoryServer s3 = new DirectoryServer(3, 20682);
        DirectoryServer s4 = new DirectoryServer(4, 20683);

        s1.start();
        s2.start();
        s3.start();
        s4.start();
    }
}
