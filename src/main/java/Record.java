public class Record {
    private final String CONTENT_NAME;
    private final int SERVER_ID;
    private final int SERVER_PORT;

    public Record(String CONTENT_NAME, int SERVER_ID, int SERVER_PORT){
        this.CONTENT_NAME = CONTENT_NAME;
        this.SERVER_ID = SERVER_ID;
        this.SERVER_PORT = SERVER_PORT;
    }

    public String getCONTENT_NAME() {return CONTENT_NAME;}
    public int getSERVER_ID() {return SERVER_ID;}
    public int getSERVER_PORT() {return SERVER_PORT;}
}
