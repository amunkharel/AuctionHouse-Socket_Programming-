package Agent;

import java.io.DataInputStream;
import java.io.IOException;
/**
 * Project 5 - CS351, Fall 2019, Class for waiting the bid message to be received from the auction house.
 * @version Date 2019-12-07
 * @author Amun Kharel, Shreeman Gautam, Sandesh Timilsina
 *
 *
 */
public class WaitAuctionMessage implements Runnable {
    private DataInputStream auctionInputStream = null;

    private static boolean isWaiting;

    /**
     *
     * @param auctionInputStream gives access to class to keep waiting for the message from the auction house.
     */
    public WaitAuctionMessage(DataInputStream auctionInputStream){
        this.auctionInputStream = auctionInputStream;
        isWaiting = false;
    }

    /**
     * this method calls the auctionResponse method.
     */
    @Override
    public void run() {
        auctionResponse();
    }


    /**
     * this method gets message of any updates on bid agent made on the item of the auction house.
     */
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
                    isWaiting = false;
                    break;
                case "pass":
                    System.out.println("Your bid is placed.");
                    isWaiting = true;
                    serverMessage = auctionInputStream.readUTF();
                    System.out.println(serverMessage);
                    break;
                case "Sold":
                    isWaiting = false;
                    System.out.println(congratsMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isIsWaiting() {
        return isWaiting;
    }
}
