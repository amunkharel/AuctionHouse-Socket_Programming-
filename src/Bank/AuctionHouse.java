package Bank;

public class AuctionHouse {
    private String hostname;
    private int port;
    private int balance = 0;


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

    public int getBalance(){
        return balance;
    }

    public int getId(){
        return id;
    }

    public void addBalance(int add){
        balance += add;
    }
}
