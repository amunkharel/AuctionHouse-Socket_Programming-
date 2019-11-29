package Bank;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BankClientThread extends Thread {

    private Socket serverClient;
    private int clientNumber;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String clientMessage = "";
    private String serverMessage = "";
    private static List<AuctionHouse> allHouses = new ArrayList<AuctionHouse>();
    private static List<Agent> agents = new ArrayList<Agent>();
    private AuctionHouse auctionHouse = null;
    private Agent agent = null;


    public BankClientThread(int clientNumber, Socket serverClient) {
        this.serverClient = serverClient;
        this.clientNumber = clientNumber;
    }

    public void run() {
        try {
            inputStream = new DataInputStream(serverClient.getInputStream());
            outputStream = new DataOutputStream(serverClient.getOutputStream());
            serverMessage = clientNumber + " You are now connected to bank server.";
            outputStream.writeUTF(serverMessage);
            outputStream.flush();
            clientMessage = inputStream.readUTF();
            if(clientMessage.split(" ")[0].equals("a")) {
                System.out.println("Code for agent");
                interactWithAgent(clientMessage.split(" ")[1],
                        Integer.parseInt(clientMessage.split(" ")[2]));
            }

            else if(clientMessage.split(" ")[0].equals("h")) {
                System.out.println("Code for auction house");
                interactWithAuctionHouse(clientMessage.split(" ")[1],
                        Integer.parseInt(clientMessage.split(" ")[2]));
            }

            inputStream.close();
            outputStream.close();
            serverClient.close();

        } catch (IOException e) {
            System.out.println(e.toString());
        }finally{
            System.out.println("Client -" + clientNumber + " exit!! ");
        }
    }

    public void interactWithAuctionHouse(String hostName, int portNumber) {

        try {
                auctionHouse = new AuctionHouse(clientNumber,hostName,portNumber);
                allHouses.add(auctionHouse);
                System.out.println("AuctionHouse got registered in Bank");
                outputStream.writeUTF("Your house is successfully registered.");
                System.out.println(" with host name " + hostName + "and port number "+ portNumber);
                outputStream.flush();
                waitForAuctionHouse();
            } catch (IOException e) {
            System.out.println(e.toString());
        }

    }

    public void waitForAuctionHouse() throws IOException {
        clientMessage = inputStream.readUTF();
        switch(clientMessage){
            case "balance":
                outputStream.writeUTF("Your balance is "+ auctionHouse.getBalance());
                waitForAuctionHouse();
                break;
            case "terminate":
                outputStream.writeUTF("Your program is terminating");
                break;
            default:
                waitForAuctionHouse();
        }
        if(clientMessage.equals("terminate")){
            outputStream.close();
            inputStream.close();
            serverClient.close();
        }
    }



    public void interactWithAgent(String agentName, int amount){

        try{
            agent = new Agent(clientNumber, amount, agentName);
            agents.add(agent);
            System.out.println("Agent got registered in Bank");
            waitForAgent();
        } catch (IOException e){
            System.out.println(e.toString());
        }

    }

    public void waitForAgent() throws IOException {
        clientMessage = inputStream.readUTF();
        switch(clientMessage){
            case "ListAuctionHouse":
                outputStream.writeUTF(auctionInformation());
                outputStream.flush();
                waitForAgent();
                break;
            case "balance":
                outputStream.writeUTF(""+agent.getAmount());
                break;
        }
        if(clientMessage.equals("terminate")){
            outputStream.close();
            serverClient.close();
        }
    }

    public String auctionInformation(){
        String str="";
        for(int i =0; i <allHouses.size();i ++){
            str += allHouses.get(i).getId() + " " +allHouses.get(i).getHostname() + " "+allHouses.get(i).getPort();
        }
        System.out.println("bank has this information "+ str);
        return str;
    }

        public boolean isInteger(String str){
        try{
            Integer.parseInt(str);
        } catch(NumberFormatException e){
            return false;
        } catch (NullPointerException e){
            return false;
        }
        return true;
    }

}
