package AuctionHouse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AuctionClientThread implements Runnable{

    private Socket agentClient = null;

    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private String clientMessage = "";
    private String serverMessage = "";
    private List<Item> itemList = new ArrayList<Item>();

    private static List<Agent> agents = new ArrayList<Agent>();

    private int agentNumber;

    public  AuctionClientThread(Socket agentClient,  List<Item> itemList) {
        this.agentClient = agentClient;
        this.itemList = itemList;
        agentNumber = 0;
    }
    @Override
    public void run() {
        try {

            inputStream = new DataInputStream(agentClient.getInputStream());
            outputStream = new DataOutputStream(agentClient.getOutputStream());

            serverMessage = "You are now connected with Auction House";

            outputStream.writeUTF(serverMessage);

            outputStream.flush();
            clientMessage = inputStream.readUTF();

            int i = 0;
            String number = "";

            while (clientMessage.charAt(i) != ' ' ) {
                number = number + clientMessage.charAt(i);
                i++;
            }

            agentNumber = Integer.parseInt(number);

            agents.add(new Agent(agentClient, agentNumber));


            waitForAgent();

        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public void waitForAgent() {
        try {
            System.out.println("waiting for agent input");
            clientMessage = inputStream.readUTF();
            System.out.println("what i am reading here : "+ clientMessage);

            switch(clientMessage){
                case "ItemList":
                    serverMessage = "";
                    for(int i = 0; i<3 && i <itemList.size(); i++){

                        serverMessage += " "+itemList.get(i).getName() + " "+itemList.get(i).getMinBid();
                        if(i == 0){
                            serverMessage = serverMessage.replaceFirst(" ", "");
                        }
                    }
                    outputStream.writeUTF(serverMessage);
                    outputStream.flush();
                    break;
                case "b":
                    System.out.println("back is performed");
                    break;
                case "Terminate":
                    removeCurrentAgent();
                default:
                    System.out.println("this is client message "+clientMessage);

                    checkBidMenu(clientMessage);
                    break;
            }
            if(clientMessage.equals("terminate")){
                outputStream.close();
                inputStream.close();
                agentClient.close();
            }
            waitForAgent();

        } catch (IOException e) {
            System.out.println(e.toString());
        }

    }

    public void checkBidMenu(String message) {
        int itemLocation = Integer.parseInt(message.split(" ")[0]);
        int itemBid = Integer.parseInt(message.split(" ")[1]);

        if(itemList.get(itemLocation - 1).getMinBid() > itemBid) {
            try {
                outputStream.writeUTF("fail");
                outputStream.flush();
                removeCurrentAgent();



            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void removeCurrentAgent() {
        System.out.println("size of agentlist  = "+agents.size());
        for (int i = 0; i < agents.size(); i ++) {
            if(agents.get(i).getAgentId() == agentNumber) {
                agents.remove(i);
            }
        }

        System.out.println("size of agentlist  = "+agents.size());
        for(int i = 0; i < agents.size(); i++) {
            System.out.println(agents.get(i).getAgentId());
        }

        Thread.currentThread().stop();
    }
}
