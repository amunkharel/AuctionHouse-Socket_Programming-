package Bank;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Bank {

    private int clientCounter = 0;

    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);

            System.out.println("Bank Server Started...");

            while (true) {
                clientCounter++;
                Socket serverClient = serverSocket.accept(); //accept client side
                System.out.println(">> " + "Client No: " + clientCounter + "started!!");
                BankClientThread bankClientThread = new BankClientThread(clientCounter,serverClient);
                bankClientThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
