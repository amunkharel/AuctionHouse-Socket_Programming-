package Bank;

public class AuctionHouse {
    private String hostname;
    private int port;

    public AuctionHouse(String hostname, int port){
        this.hostname = hostname;
        this.port = port;
    }


    public String getHostname(){
        return hostname;
    }

    public int getPort(){
        return port;
    }
}
