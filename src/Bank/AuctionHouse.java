package Bank;

public class AuctionHouse {
    private String hostname;
    private int port;

    private  int id;

    public AuctionHouse(int id, String hostname, int port){
        this.hostname = hostname;
        this.port = port;
        this.id = id;
    }


    public String getHostname(){
        return hostname;
    }

    public int getPort(){
        return port;
    }
}
