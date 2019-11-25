package Agent;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Agent {


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
                    System.out.println("Auction Host name "+ strArray[i] +" Auction Port: "+ strArray[i+1]);
                    System.out.println("\n");
                }
            }

        }
        catch (IOException e) {
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
