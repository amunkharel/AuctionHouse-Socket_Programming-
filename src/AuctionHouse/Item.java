package AuctionHouse;

public class Item {

    private String name;
    private double minBid;
    private int agentWithBid;

    public Item(String name, double minBid){
        this.name = name;
        this.minBid = minBid;
        this.agentWithBid = -1;

    }

    public String getName(){
        return name;
    }

    public double getMinBid(){
        return minBid;
    }

    public void setMinBid(int amount, int agentID){
        this.minBid = amount;
        this.agentWithBid = agentID;
    }

    public int getAgentWithBid(){
        return agentWithBid;
    }
}
