package AuctionHouse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AuctionServer implements Runnable{

    private int portNumber;

    private String address;

    public AuctionServer(int portNumber, String address) {
        this.portNumber = portNumber;
        this.address = address;
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
                Thread threadAuctionClientThread = new Thread(new AuctionClientThread(serverClient));
                threadAuctionClientThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
