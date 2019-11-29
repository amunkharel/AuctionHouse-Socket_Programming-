package Agent;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Agent {

    private Socket bankSocket = null;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;

    private Socket auctionSocket = null;

    private DataInputStream auctionInputStream = null;

    private DataOutputStream auctionOutputStream = null;

    private int agentNumber = 0;
    private int currentAuctionPort;
    private String currentAuctionHost;
    private String[] auctionList;
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
            System.out.println("Type in your name : ");
            String agentName = br.readLine();

            String money = "";
            boolean isMoney = false;
            while(!isMoney){
                System.out.println("Type in the money : ");
                money = br.readLine();
                if(isInteger(money) && Integer.parseInt(money)>=0){
                    isMoney = true;
                }
            }
            outputStream.writeUTF("a " + agentName +" "+ money);
            outputStream.flush();
            serverMessage = inputStream.readUTF();
            System.out.println(serverMessage);
            int i = 0;
            String number = "";

            while (serverMessage.charAt(i) != ' ' ) {
                number = number + serverMessage.charAt(i);
                i++;
            }

            agentNumber = Integer.parseInt(number);

            menu();


        }
        catch (IOException e) {
            System.out.println(e.toString());
        }

    }

    public void menu() {
        String menuInput = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Menu\n1) Type '1' to get list of Auction Houses \n " +
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
                    outputStream.writeUTF("ListAuctionHouse");
                    outputStream.flush();

                    auctionList = inputStream.readUTF().split(" ");
                    auctionHouseMenu();
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
    public void auctionHouseMenu(){
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String menuInput = "";
        for(int i = 0; i<auctionList.length; i++){
            if(i%3 == 0 &&auctionList.length>2){
                System.out.println("Auction House Number : " + (i/3));
            }
        }
        if(auctionList.length<3){
            System.out.println("There are no currently any auction House. Waiting...");
            return;
        }
        System.out.println("Type number to select the Auction House from list");
        boolean isHouse = false;
        int houseNumber =0;
        while(!isHouse){
            System.out.println("Type in the House Number :\n Type 'b' to go back");
            try {
                menuInput = br.readLine();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
            if(isInteger(menuInput)){
                houseNumber = Integer.parseInt(menuInput);
                if(houseNumber>=0 && houseNumber<auctionList.length/3){
                    isHouse = true;
                }
            }
            else if (menuInput.equals("b")) {
                return;
            }
        }
        connectToAuctionHouse(auctionList[houseNumber*3+1],Integer.parseInt(auctionList[houseNumber*3+2]));

    }

    public void exitProgram() {

    }

    public void connectToAuctionHouse(String hostNumber, int portNumber) {

        System.out.println("from agent:::  hostnumber =" + hostNumber+ " portNumber = "+ portNumber);
        try{
            auctionSocket =new Socket(hostNumber,portNumber);
            auctionInputStream =new DataInputStream(auctionSocket.getInputStream());
            auctionOutputStream =new DataOutputStream(auctionSocket.getOutputStream());
            String clientMessage="",serverMessage="";

            clientMessage = agentNumber + " Agent Present";

            auctionOutputStream.writeUTF(clientMessage);

            serverMessage = auctionInputStream.readUTF();

            System.out.println(serverMessage);

            singleAuctionHouseMenu();

            auctionInputStream.close();
            auctionOutputStream.close();
            auctionSocket.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public void singleAuctionHouseMenu() {
        String agentInput = "";
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        System.out.println("1) Please Type 1 to get list of items \n" +
                "2) Please type 2 to go back to previous menu");
        try {
             agentInput = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch(agentInput){
            case "1":
                try {
                    auctionOutputStream.writeUTF("ItemList");
                    String[] strArr = auctionInputStream.readUTF().split(" ");
                    bidMenu(strArr);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            case "2":
                return;
        }
    }

    public void bidMenu(String[] arr){
        System.out.println("Item list are given below: ");
        for (int i = 0; i<arr.length && arr.length>1;i+=2){
            System.out.println((i/2+1) + "." +arr[i] + " has bid amount "+arr[i+1]);
        }
        String itemNumber = "";
        String amountBid = "";
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        boolean isValid = false;
        while(!isValid){
            System.out.println("Type in the Item number :\nOr Type 'b' to go back:");
            try {
                 itemNumber = br.readLine();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
            if(itemNumber.equals("1")||itemNumber.equals("2")||itemNumber.equals("3")) {
                System.out.println("Type in the bid amount :\nOr Type 'b' to go back:");
                try {
                    amountBid = br.readLine();
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
                if (isInteger(amountBid)) {
                    isValid = true;
                } else if (itemNumber.equals("b")) {
                    return;
                }
            }
            else if (itemNumber.equals("b")) {
                return;
            }
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
