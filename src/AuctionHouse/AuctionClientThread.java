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

    private static int secondsPassed = 0;

    private static boolean timerRunning = true;

    private int itemLocation = -1;

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
        itemLocation = Integer.parseInt(message.split(" ")[0]);
        int itemBidAmount = Integer.parseInt(message.split(" ")[1]);

        try {
            if(itemList.get(itemLocation - 1).getMinBid() > itemBidAmount) {

                    outputStream.writeUTF("fail");
                    outputStream.flush();
                    removeCurrentAgent();
            }

            else {
                bankOutputStream.writeUTF("checkAgentAmount "+agentNumber);
                bankOutputStream.flush();
                int agentBalance = Integer.parseInt(bankInputStream.readUTF());
                if(agentBalance>=itemBidAmount) {
                    System.out.println("agent has this balance " + agentBalance);
                    System.out.println("agent has enough amount");
                    System.out.println("this is last agent id " + itemList.get(itemLocation-1).getAgentWithBid());
                    if (!(itemList.get(itemLocation - 1).getAgentWithBid() == -1)) {
                        timerRunning = false;
                        secondsPassed = 0;
                        System.out.println("this should be printed ");
                        agentId = itemList.get(itemLocation - 1).getAgentWithBid();
                        System.out.println("this is previous agent id " + agentId);
                        for (int i = 0; i < agents.size(); i++) {
                            if (agents.get(i).getAgentId() == agentId) {
                                agent = agents.get(i);
                            }
                        }
                        itemList.get(itemLocation - 1).setMinBid(itemBidAmount, agentNumber);
                        agentSocket = agent.getAgentClient();

                        agentInputStream = new DataInputStream(agentSocket.getInputStream());
                        agentOutputStream = new DataOutputStream(agentSocket.getOutputStream());

                        agentOutputStream.writeUTF("Your bid was OutBidded.");
                        Thread.sleep(100);
                        timerRunning = true;
                        timerStart();




                        //send message to agent with id itemList.get(itemLocation).getAgentWithBid()

                    } else {
                        System.out.println("here i am 111111111  ");
                        itemList.get(itemLocation - 1).setMinBid(itemBidAmount, agentNumber);
                        outputStream.writeUTF("pass");
                        outputStream.flush();
                        if (timerRunning) {
                            timerStart();
                        }
                    }
                }else{
                    outputStream.writeUTF("fail");
                    outputStream.flush();
                    removeCurrentAgent();
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

        Thread.currentThread().stop();
    }

    Timer timerSecond = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            secondsPassed++;
            if(secondsPassed > 20) {
                setTimeIsOver(true);
            }
            if(!timerRunning){
                timerSecond.cancel();
            }

            System.out.println("Seconds passed: " + secondsPassed);
        }
    };

    public void setTimeIsOver(boolean timeIsOver) {
        secondsPassed = 0;
        timerSecond.cancel();
        String itemName = itemList.get(itemLocation - 1).getName();
        itemList.remove(itemLocation - 1);
        try {
            outputStream.writeUTF("Congratulations!! Bid Successful, You got item " + itemName + ".");
            outputStream.flush();
            removeCurrentAgent();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void timerStart() {
        timerSecond.scheduleAtFixedRate(task, 1000, 1000);
    }
}
