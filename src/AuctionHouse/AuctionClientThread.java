package AuctionHouse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AuctionClientThread implements Runnable{

    private Socket agentClient = null;

    private int secondsPassed = 0;

    private static boolean timerRunning = true;

    private int itemLocation = -1;

    private boolean killThread = false;

    private int itemBidAmount = -1;

    private static  boolean currentlyBidding = false;

    private String itemNameWithBid = "";


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

    private int auctionHouseNumber;

    public  AuctionClientThread(Socket agentClient,  List<Item> itemList, Socket bankSocket, int auctionHouseNumber) {
        this.agentClient = agentClient;
        this.itemList = itemList;
        this.bankSocket = bankSocket;
        agentNumber = 0;
        this.auctionHouseNumber = auctionHouseNumber;

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
                    //removeCurrentAgent();
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
        if(killThread == true){
            timerSecond.cancel();
            Thread.currentThread().stop();
        }
        DataInputStream agentInputStream = null;

        DataOutputStream agentOutputStream = null;

        Socket agentSocket = null;

        itemLocation = Integer.parseInt(message.split(" ")[0]);
        itemBidAmount = Integer.parseInt(message.split(" ")[1]);

        try {
            if(itemList.get(itemLocation - 1).getMinBid() > itemBidAmount) {

                    outputStream.writeUTF("fail");
                    outputStream.flush();
                   // removeCurrentAgent();
            }

            else {
                bankOutputStream.writeUTF("checkAgentAmount "+agentNumber);
                bankOutputStream.flush();
                int agentBalance = Integer.parseInt(bankInputStream.readUTF());
                if(agentBalance>=itemBidAmount) {
                    System.out.println("this is last agent id " + itemList.get(itemLocation-1).getAgentWithBid());
                    if (!(itemList.get(itemLocation - 1).getAgentWithBid() == -1)) {
                        timerRunning = false;
                        secondsPassed = 0;
                        agentId = itemList.get(itemLocation - 1).getAgentWithBid();
                        for (int i = 0; i < agents.size(); i++) {
                            if (agents.get(i).getAgentId() == agentId) {
                                agent = agents.get(i);
                            }
                        }
                        itemNameWithBid = itemList.get(itemLocation - 1).getName();

                        bankOutputStream.writeUTF("Unblock "+ agentId +  " " +
                                itemList.get(itemLocation - 1).getMinBid() );
                        bankOutputStream.flush();


                        itemList.get(itemLocation - 1).setMinBid(itemBidAmount, agentNumber);
                        agentSocket = agent.getAgentClient();

                        agentInputStream = new DataInputStream(agentSocket.getInputStream());
                        agentOutputStream = new DataOutputStream(agentSocket.getOutputStream());

                        bankOutputStream.writeUTF("Block "+ agentNumber+  " " + itemBidAmount );
                        bankOutputStream.flush();
                        agentOutputStream.writeUTF("Your bid was OutBidded.");
                        Thread.sleep(10000);
                        timerRunning = true;
                        timerStart();


                    } else {
                        itemNameWithBid = itemList.get(itemLocation - 1).getName();
                        itemList.get(itemLocation - 1).setMinBid(itemBidAmount, agentNumber);

                        bankOutputStream.writeUTF("Block "+ agentNumber+  " " + itemBidAmount );
                        bankOutputStream.flush();

                        outputStream.writeUTF("pass");
                        outputStream.flush();

                        if (timerRunning) {
                            timerStart();
                        }
                    }
                }else{
                    outputStream.writeUTF("fail");
                    outputStream.flush();
                    //removeCurrentAgent();
                }

            }
        }

        catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void  removeCurrentAgent() {
        for (int i = 0; i < agents.size(); i ++) {
            if(agents.get(i).getAgentId() == agentNumber) {

                agents.get(i).closeSocket();
                agents.remove(i);

            }
        }

        //Thread.currentThread().stop();
    }

    Timer timerSecond = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            currentlyBidding = true;
            secondsPassed++;
            if(!timerRunning){
                timerSecond.cancel();
            }
            if(secondsPassed > 20) {
                setTimeIsOver();
                timerSecond.cancel();
            }
            //System.out.println("Seconds passed: " + secondsPassed);
        }
    };



    public void setTimeIsOver() {

        String itemName = "";
        for(int i = 0; i<itemList.size(); i++){
            if(itemList.get(i).getName().equals(itemNameWithBid)){
                itemName = itemList.get(i).getName();
                itemList.remove(i);
            }
        }
        //itemList.remove(itemLocation - 1);
        try {
            outputStream.writeUTF("Congratulations !! Bid Successful, You got item " + itemName + ".");
            outputStream.flush();
            currentlyBidding = false;
            bankOutputStream.writeUTF("Sold "+ agentNumber+ " " + auctionHouseNumber + " " + itemBidAmount );
            bankOutputStream.flush();

            //removeCurrentAgent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        killThread = true;
        timerSecond.cancel();

    }

    public void timerStart() {
        timerSecond.scheduleAtFixedRate(task, 1000, 1000);
    }

    public static boolean isCurrentlyBidding() {
        return currentlyBidding;
    }
}
