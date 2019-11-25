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


    public BankClientThread(int clientNumber, Socket serverClient) {
        this.serverClient = serverClient;
        this.clientNumber = clientNumber;
    }

    public void run() {
        try {
            inputStream = new DataInputStream(serverClient.getInputStream());
            outputStream = new DataOutputStream(serverClient.getOutputStream());
            while (!clientMessage.equals("terminate")) {
                serverMessage = "You are now connected to bank server.";
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
        while (!clientMessage.equals("terminate")) {
            try {
                allHouses.add(new AuctionHouse(clientNumber,hostName,portNumber));
                System.out.println("AuctionHouse got registered in Bank");
                outputStream.writeUTF("Your house is successfully registered.");
                System.out.println(" with host name +" + hostName + "and port number "+ portNumber);
                outputStream.flush();
                waitForAuctionHouse();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void waitForAuctionHouse() throws IOException {
        clientMessage = "";
        while(!clientMessage.equals("terminate")){
            try{
                clientMessage = inputStream.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        if(clientMessage.equals("terminate")){
            outputStream.close();
            serverClient.close();
        }
    }



    public void interactWithAgent(String agentName, double amount){
        while(!clientMessage.equals("terminate")){

            try{
                agents.add(new Agent(clientNumber, amount, agentName));
                System.out.println("Agent got registered in Bank");

                waitForAgent();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void waitForAgent() throws IOException {
        clientMessage = "";
        while(!clientMessage.equals("terminate")){
            try{
                int menuOption = -1;
                serverMessage = inputStream.readUTF();
                if(isInteger(serverMessage)) {
                    menuOption = Integer.parseInt(serverMessage);
                }
                switch (menuOption){
                    case 1:
                        System.out.println("we got case 1 ");
                        outputStream.writeUTF(auctionInformation());
                        outputStream.flush();
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(clientMessage.equals("terminate")){
            outputStream.close();
            serverClient.close();
        }
    }

    public String auctionInformation(){
        String str="";
        for(int i =0; i <allHouses.size();i ++){
            str += allHouses.get(i).getHostname() + " "+allHouses.get(i).getPort();
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
