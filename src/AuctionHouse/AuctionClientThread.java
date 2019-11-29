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

    public  AuctionClientThread(Socket agentClient,  List<Item> itemList) {
        this.agentClient = agentClient;
        this.itemList = itemList;
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

            System.out.println(clientMessage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
