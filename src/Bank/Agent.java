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

    public int getAmount(){
        return amount;
    }


}