package Bank;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.List;

public class BankClientThread extends Thread {

    private Socket serverClient;
    private int clientNumber;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String clientMessage = "";
    private String serverMessage = "";
    private List<AuctionHouse> allHouses = new ArrayList<AuctionHouse>();


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
                    interactWithAgent();
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
            e.printStackTrace();
        }finally{
            System.out.println("Client -" + clientNumber + " exit!! ");
        }
    }

    public void interactWithAuctionHouse(String hostName, int portNumber) {
        while (!clientMessage.equals("terminate")) {
            try {
                allHouses.add(new AuctionHouse(hostName,portNumber));
                System.out.println("AuctionHouse got registered in Bank");
                outputStream.writeUTF("Your house is successfully registered.");
                System.out.println(" with host name +" + hostName + "and port number "+ portNumber);
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

//    public boolean isInteger(String str){
//        try{
//            Integer.parseInt(str);
//        } catch(NumberFormatException e){
//            return false;
//        } catch (NullPointerException e){
//            return false;
//        }
//        return true;
//    }

    public void interactWithAgent(){
        while(!clientMessage.equals("terminate")){
            try{
                clientMessage = inputStream.readUTF();
                if(clientMessage.equals("Give Auction House List")){
                    for(int i = 0; i<allHouses.size(); i++){
                        outputStream.writeUTF("Hostname : "+allHouses.get(i).getHostname()+" Portname" +
                                allHouses.get(i).getPort());
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }


}
