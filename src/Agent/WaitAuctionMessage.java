package Agent;

import java.io.DataInputStream;
import java.io.IOException;

public class WaitAuctionMessage implements Runnable {
    private DataInputStream auctionInputStream = null;
    public WaitAuctionMessage(DataInputStream auctionInputStream){
        this.auctionInputStream = auctionInputStream;
    }

    @Override
    public void run() {
        auctionResponse();
    }


    public void auctionResponse() {
        try {
            String serverMessage = auctionInputStream.readUTF();
            String congratsMessage = serverMessage;
            if (serverMessage.contains("Congratulations")) {
                serverMessage = "Sold";

            }
            switch (serverMessage) {
                case "fail":
                    System.out.println("Your bid was rejected.");
                    break;
                case "pass":
                    System.out.println("Waiting for 30 seconds ....");
                    serverMessage = auctionInputStream.readUTF();
                    System.out.println(serverMessage);
                    break;
                case "Sold":
                    System.out.println(congratsMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
