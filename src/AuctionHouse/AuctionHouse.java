package AuctionHouse;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AuctionHouse {

    private List<Item> itemList = new ArrayList<Item>();

    private  DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private Socket socket = null;
    private int balance = 0;

    public AuctionHouse() {
    }

    public void startServer() {
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Type Bank Host Name: ");
            String bankHostNumber = br.readLine();
            setItem();

            String bankPortNumber = "";
            boolean isLegal = false;
            while(!isLegal){
                System.out.println("Type Bank's port number: ");
                bankPortNumber = br.readLine();
                if(isInteger(bankPortNumber)){
                    isLegal = true;
                }
            }

            System.out.println("Type Your Host Name: ");
            String auctionHostNumber = br.readLine();

            String auctionPortNumber = "";
            boolean isLegalPort = false;
            while(!isLegalPort){
                System.out.println("Type Auction's port number: ");
                auctionPortNumber = br.readLine();
                if(isInteger(auctionPortNumber)){
                    isLegalPort = true;
                }
            }

            Thread threadServer = new Thread(new AuctionServer(Integer.parseInt(auctionPortNumber),"127.0.0.1", itemList));
            threadServer.start();

            socket = new Socket("127.0.0.1", 8888);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            System.out.println(inputStream.readUTF());



            String clientMessage = "", serverMessage = "";


            outputStream.writeUTF("h " + "127.0.0.1" + " "+ auctionPortNumber);
            outputStream.flush();

            serverMessage = inputStream.readUTF();
            System.out.println(serverMessage);

            menu();



        } catch (IOException e) {
            System.out.println(e.toString());
        }

    }


    public void menu() {

        String menuInput = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Menu\n1) Type '1' to check balance \n" +"2) Type '2' to terminate the program");

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
        System.out.println("sending message, closing the account");
        try {
            socket.close();
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }

    }

    public void setItem(){
        itemList.add(new Item("microwave", 10));
        itemList.add(new Item("Freezer", 200));
        itemList.add(new Item("Game", 50));
        itemList.add(new Item("Paper Towel", 23));
        itemList.add(new Item("Laptop", 23));
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
