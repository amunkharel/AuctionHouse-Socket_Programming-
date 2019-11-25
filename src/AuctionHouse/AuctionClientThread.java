package AuctionHouse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class AuctionClientThread implements Runnable{

    private Socket agentClient = null;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String clientMessage = "";
    private String serverMessage = "";

    public  AuctionClientThread(Socket agentClient) {
        this.agentClient = agentClient;
    }
    @Override
    public void run() {
        try {
            inputStream = new DataInputStream(agentClient.getInputStream());
            outputStream = new DataOutputStream(agentClient.getOutputStream());

            while (!clientMessage.equals("terminate")) {
                serverMessage = "You are now connected with Auction House";
                outputStream.writeUTF(serverMessage);

                outputStream.flush();

                clientMessage = inputStream.readUTF();

                System.out.println(clientMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
