package Bank;

public class Agent {

    private int id;

    private int amount;

    private String name;

    public Agent(int id, int amount, String name) {
        this.id = id;

        this.amount = amount;

        this.name = name;
    }

    public void subtract(int price){
        amount = amount -price;
    }

    public void add(int price){
        amount = amount +price;
    }
    public int getAmount(){
        return amount;
    }

    public int getId(){
        return id;
    }

}
