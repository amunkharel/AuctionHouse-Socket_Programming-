package Agent;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Agent {

    Socket bankSocket = null;
    DataInputStream inputStream = null;
    DataOutputStream outputStream = null;
    private int currentAuctionPort;
    private String currentAuctionHost;
    public void startServer(){
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Type Bank Host Name: ");
            String bankHostNumber = br.readLine();

            String bankPortNumber = "";
            boolean isLegal = false;
            while(!isLegal){
                System.out.println("Type Bank's port number: ");
                bankPortNumber = br.readLine();
                if(isInteger(bankPortNumber)){
                    isLegal = true;
                }
            }

            bankSocket = new Socket("127.0.01",8888);
            inputStream = new DataInputStream(bankSocket.getInputStream());
            outputStream = new DataOutputStream(bankSocket.getOutputStream());



            String clientMessage = "", serverMessage = "";

            outputStream.writeUTF("a " + "Amun " + "300");
            outputStream.flush();
            serverMessage = inputStream.readUTF();
            System.out.println(serverMessage);

            menu();

            /*System.out.println("\nPress 1 for auction house.\nPress 2 for check balance");
            Scanner scanner = new Scanner(System.in);
            clientMessage = scanner.nextLine();
            //System.out.println("clime: "+clientMessage);
            if(isInteger(clientMessage)){
                outputStream.writeUTF(clientMessage);
            }
            serverMessage = inputStream.readUTF();
            String[] strArray = serverMessage.split(" ");
            for(int i =0; i< strArray.length; i+=2){
                currentAuctionPort = Integer.parseInt(strArray[i+1]);
                currentAuctionHost = strArray[i];
                System.out.println("Auction Host name "+ strArray[i] +" Auction Port: "+ strArray[i+1]);
                System.out.println("\n");
            }
            connectToAuctionHouse(currentAuctionHost,currentAuctionPort); */


        }
        catch (IOException e) {
            System.out.println(e.toString());
        }

    }

    public void menu() {
        String menuInput = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Menu \n 1) Type '1' to get list of Auction Houses \n " +
                "2) Type '2' to check balance \n" +
                "3) Type '3' to close the account");
        try {
            menuInput = br.readLine();
        } catch (IOException e) {
            System.out.println(e.toString());
        }

        switch (menuInput) {
            case "1":
                try {
                    outputStream.writeUTF("balance");
                    outputStream.flush();
                    System.out.println(inputStream.readUTF());
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
                menu();
                break;

            case "2":
                try {
                    //boolean condition, check if any item is currently on bidding.
                    outputStream.writeUTF("terminate");
                    outputStream.flush();
                    try {
                        outputStream.close();
                        inputStream.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } catch (IOException e) {
                    System.out.println(e.toString());

                }
                exitProgram();
                break;

            default:
                menu();
        }
    }

    public void exitProgram() {

    }

    public void connectToAuctionHouse(String hostNumber, int portName) {
        try{
            Socket socket=new Socket(hostNumber,portName);
            DataInputStream inStream=new DataInputStream(socket.getInputStream());
            DataOutputStream outStream=new DataOutputStream(socket.getOutputStream());
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            String clientMessage="",serverMessage="";
            while(!clientMessage.equals("terminate")){
                serverMessage=inStream.readUTF();
                System.out.println(serverMessage);

                clientMessage=br.readLine();
                outStream.writeUTF(clientMessage);
                outStream.flush();

            }
            outStream.close();
            outStream.close();
            socket.close();
        }catch(Exception e){
            System.out.println(e);
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
