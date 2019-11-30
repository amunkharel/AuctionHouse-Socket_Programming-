package AuctionHouse;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AuctionHouseController {

    public static void main(String[] args) {

        /*Socket socket = null;
        List<Item> itemList = new ArrayList<Item>();

        AuctionClientThread a1 = new AuctionClientThread(socket, itemList); */



        AuctionHouse auctionHouse = new AuctionHouse();
        auctionHouse.startServer();
    }
}
