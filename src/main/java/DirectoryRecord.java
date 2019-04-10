public class DirectoryRecord {
    private final String CONTENT_NAME;
    private final String CLIENT_IP;

    public DirectoryRecord(String CONTENT_NAME, String CLIENT_IP){
        this.CONTENT_NAME = CONTENT_NAME;
        this.CLIENT_IP = CLIENT_IP;
    }

    public String getCONTENT_NAME() {return CONTENT_NAME;}
    public String getSERVER_IP() {return CLIENT_IP;}
}
