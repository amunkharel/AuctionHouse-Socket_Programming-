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
                if(clientMessage.equals("a")) {
                    System.out.println("Code for agent");
                }

                else if(clientMessage.equals("h")) {
                    System.out.println("Code for auction house");
                    interactWithAuctionHouse();
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

    public void interactWithAuctionHouse() {
        String hostname = "";
        String portNumber = "";
        while (!clientMessage.equals("terminate")) {
            try {
                serverMessage = "Please enter your hostname and port number : ";
                outputStream.writeUTF(serverMessage);
                outputStream.flush();
                clientMessage = inputStream.readUTF();
                System.out.println(clientMessage);
                hostname = clientMessage.split(" ")[0];
                System.out.println(hostname);
                portNumber = clientMessage.split(" ")[1];
                System.out.println(portNumber);
                if(isInteger(portNumber)){
                    allHouses.add(new AuctionHouse(hostname,Integer.parseInt(portNumber)));
                    System.out.println("bank got registered");
                    outputStream.writeUTF("Your house is successfully registered.");
                    waitForAuctionHouse();
                } else {
                    System.out.println("please give the valid port number");
                }

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
