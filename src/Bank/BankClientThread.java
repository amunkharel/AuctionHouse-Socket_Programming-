package Bank;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BankClientThread extends Thread {

    private Socket serverClient;
    private int clientNumber;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String clientMessage = "";
    private String serverMessage = "";



    public BankClientThread(int clientNumber, Socket serverClient) {
        this.serverClient = serverClient;
        this.clientNumber = clientNumber;
    }

    public void run() {
        try {
            inputStream = new DataInputStream(serverClient.getInputStream());
            outputStream = new DataOutputStream(serverClient.getOutputStream());
            while (!clientMessage.equals("terminate")) {
                serverMessage = "You are now connected to bank server. Please type 'a' if you are a agent and 'h' if you represent auction house";
                outputStream.writeUTF(serverMessage);
                outputStream.flush();
                clientMessage = inputStream.readUTF();
                if(clientMessage.equals("a")) {
                    System.out.println("Code for agent");
                }

                else if(clientMessage.equals("h")) {
                    System.out.println("Code for auction house");
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

    }

}
