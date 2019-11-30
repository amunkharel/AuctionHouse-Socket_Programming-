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

    private DataInputStream bankInputStream = null;
    private DataOutputStream bankOutputStream = null;

    private String clientMessage = "";
    private String serverMessage = "";
    private List<Item> itemList = new ArrayList<Item>();
    private Socket bankSocket = null;

    private static List<Agent> agents = new ArrayList<Agent>();

    private int agentNumber;

    public  AuctionClientThread(Socket agentClient,  List<Item> itemList, Socket bankSocket) {
        this.agentClient = agentClient;
        this.itemList = itemList;
        this.bankSocket = bankSocket;
        agentNumber = 0;

    }
    @Override
    public void run() {
        try {

            inputStream = new DataInputStream(agentClient.getInputStream());
            outputStream = new DataOutputStream(agentClient.getOutputStream());

            bankInputStream = new DataInputStream(bankSocket.getInputStream());
            bankOutputStream = new DataOutputStream(bankSocket.getOutputStream());

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
        int agentId = 0;
        Agent agent = null;

        DataInputStream agentInputStream = null;

        DataOutputStream agentOutputStream = null;

        Socket agentSocket = null;
        int itemLocation = Integer.parseInt(message.split(" ")[0]);
        int itemBidAmount = Integer.parseInt(message.split(" ")[1]);

        try {
            if(itemList.get(itemLocation - 1).getMinBid() > itemBidAmount) {

                    outputStream.writeUTF("fail");
                    outputStream.flush();
                    removeCurrentAgent();
            }

            else {
                bankOutputStream.writeUTF("checkAgentAmount "+agentNumber);
                int agentBalance = Integer.parseInt(bankInputStream.readUTF());
                if(agentBalance>=itemBidAmount){
                    System.out.println("agent has this balance "+ agentBalance);
                    System.out.println("agent has enough amount");
                    System.out.println("this is last agent id "+ itemList.get(itemLocation).getAgentWithBid());
                    if(!(itemList.get(itemLocation-1).getAgentWithBid()==-1)){
                        System.out.println("this should be printed ");
                        agentId = itemList.get(itemLocation - 1).getAgentWithBid();
                        System.out.println("this is previous agent id " + agentId);
                        for (int i = 0; i < agents.size(); i ++) {
                            if(agents.get(i).getAgentId() == agentId) {
                                agent = agents.get(i);
                            }
                        }
                        agentSocket = agent.getAgentClient();

                        agentInputStream = new DataInputStream(agentSocket.getInputStream());
                        agentOutputStream = new DataOutputStream(agentSocket.getOutputStream());

                        agentOutputStream.writeUTF("Hello World!!!!!");


                        //send message to agent with id itemList.get(itemLocation).getAgentWithBid()

                    }
                    itemList.get(itemLocation - 1).setMinBid(itemBidAmount, agentNumber);
                    outputStream.writeUTF("pass");
                    outputStream.flush();
                } else{
                    System.out.println("agent does not have enough amount");
                }
                bankOutputStream.flush();

            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void removeCurrentAgent() {
        for (int i = 0; i < agents.size(); i ++) {
            if(agents.get(i).getAgentId() == agentNumber) {
                agents.remove(i);
            }
        }

        Thread.currentThread().stop();
    }
}
