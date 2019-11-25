package AuctionHouse;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AuctionHouse {

    private int portNumber;
    private List<Item> itemList = new ArrayList<Item>();
    public AuctionHouse() {
    }

    public void startServer() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            Socket socket = new Socket("127.0.0.1", 8888);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            System.out.println(inputStream.readUTF());
            System.out.println("\nType in your port number : ");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String readString  = br.readLine();
            if(isInteger(readString)){
                portNumber = Integer.parseInt(readString);
            }

            AuctionServer auctionServer = new AuctionServer(portNumber, address.getHostAddress());



            System.out.println("Hello World");
            String clientMessage = "", serverMessage = "";
            setItem();
            boolean runServer = true;
            while (!clientMessage.equals("terminate")) {
                outputStream.writeUTF("h " + address.getHostAddress() + " "+ portNumber);

                outputStream.flush();
                if(runServer) {
                    runServer= false;
                    auctionServer.run();

                }
                serverMessage = inputStream.readUTF();
                System.out.println(serverMessage);
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setItem(){
        itemList.add(new Item("microwave", 10));
        itemList.add(new Item("Freezer", 200));
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
