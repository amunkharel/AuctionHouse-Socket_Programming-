package AuctionHouse;

public class Item {

    private String name;
    private double minBid;
    private double currentBid;

    public Item(String name, double minBid){
        this.name = name;
        this.minBid = minBid;
    }

    public String getName(){
        return name;
    }

    public double getMinBid(){
        return minBid;
    }

    public void setCurrentBid(double bid){
        currentBid = bid;
    }

    public double getCurrentBid(){
        return currentBid;
    }
}
