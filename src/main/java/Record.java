public class Record {
    private final String CONTENT_NAME;
    private final int SERVER_ID;
    private final String SERVER_IP;

    public Record(String CONTENT_NAME, int SERVER_ID, String SERVER_IP){
        this.CONTENT_NAME = CONTENT_NAME;
        this.SERVER_ID = SERVER_ID;
        this.SERVER_IP = SERVER_IP;
    }

    public String getCONTENT_NAME() {return CONTENT_NAME;}
    public int getSERVER_ID() {return SERVER_ID;}
    public String getSERVER_IP() {return SERVER_IP;}
}
