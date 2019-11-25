package Agent;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Agent {

    private int currentAuctionPort;
    private String currentAuctionHost;
    public void startServer(){
        try {
            Socket bankSocket = new Socket("127.0.01",8888);
            DataInputStream inputStream = new DataInputStream(bankSocket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(bankSocket.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            String clientMessage = "", serverMessage = "";
            while (!clientMessage.equals("terminate")) {
                outputStream.writeUTF("a " + "Amun " + "300");
                outputStream.flush();
                serverMessage = inputStream.readUTF();
                System.out.println(serverMessage);

                System.out.println("\nPress 1 for auction house.\nPress 2 for check balance");
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
                connectToAuctionHouse(currentAuctionHost,currentAuctionPort);
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }

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
