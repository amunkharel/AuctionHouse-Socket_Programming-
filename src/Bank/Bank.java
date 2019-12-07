package Bank;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Bank {

    private int clientCounter = 0;

    public void startServer() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String bankPortNumber = "";
            boolean isLegalPort = false;
            while(!isLegalPort){
                System.out.println("Type Bank's port number: ");
                bankPortNumber = br.readLine();
                if(isInteger(bankPortNumber)){
                    isLegalPort = true;
                }
            }

            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(bankPortNumber));

            System.out.println("Bank Server Started...");

            while (true) {
                clientCounter++;
                Socket serverClient = serverSocket.accept(); //accept client side
                System.out.println(">> " + "Client No: " + clientCounter + " started!!");
                BankClientThread bankClientThread = new BankClientThread(clientCounter,serverClient);
                bankClientThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
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
