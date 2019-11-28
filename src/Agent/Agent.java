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
            System.out.println("Type in the House Number : ");
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
        }
        connectToAuctionHouse(auctionList[houseNumber*3+1],Integer.parseInt(auctionList[houseNumber*3+2]));

    }

    public void exitProgram() {

    }

    public void connectToAuctionHouse(String hostNumber, int portNumber) {

        System.out.println("from agent:::  hostnumber =" + hostNumber+ " portNumber = "+ portNumber);
//        try{
//            Socket socket=new Socket(hostNumber,portNumber);
//            DataInputStream inStream=new DataInputStream(socket.getInputStream());
//            DataOutputStream outStream=new DataOutputStream(socket.getOutputStream());
//            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
//            String clientMessage="",serverMessage="";

//            while(!clientMessage.equals("terminate")){
//                serverMessage=inStream.readUTF();
//                System.out.println(serverMessage);
//
//                clientMessage=br.readLine();
//                outStream.writeUTF(clientMessage);
//                outStream.flush();
//
//            }
//            outStream.close();
//            outStream.close();
//            socket.close();
//        }catch(Exception e){
//            System.out.println(e);
//        }
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
