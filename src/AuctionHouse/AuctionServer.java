package AuctionHouse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AuctionServer implements Runnable{

    private int portNumber;

    private String address;

    private List<Item> itemList = new ArrayList<Item>();

    public AuctionServer(int portNumber, String address, List<Item> itemList) {
        this.portNumber = portNumber;
        this.address = address;
        this.itemList = itemList;
    }


    @Override
    public void run() {
        startServer();
    }

    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);

            System.out.println("Auction House Server Started...");

            while (true) {
                Socket serverClient = serverSocket.accept(); //accept client side
                Thread threadAuctionClientThread = new Thread(new AuctionClientThread(serverClient, itemList));
                threadAuctionClientThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
