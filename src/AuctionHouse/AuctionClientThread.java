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
            clientMessage = inputStream.readUTF();

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

                    waitForAgent();
                    break;
                default:
                    waitForAgent();
            }
            if(clientMessage.equals("terminate")){
                outputStream.close();
                inputStream.close();
                agentClient.close();
            }

        } catch (IOException e) {
            System.out.println(e.toString());
        }

    }
}
