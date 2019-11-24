package AuctionHouse;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class AuctionHouse {

    public void startServer() {
        try {
            Socket socket = new Socket("127.0.0.1", 8888);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String clientMessage = "", serverMessage = "";
            while (!clientMessage.equals("terminate")) {
                serverMessage = inputStream.readUTF();
                System.out.println(serverMessage);
                outputStream.writeUTF("h");
                outputStream.flush();
                serverMessage = inputStream.readUTF();
                System.out.println(serverMessage);
                outputStream.writeUTF(br.readLine());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
