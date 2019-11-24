package finalProject;

import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class Main {

    AuctionHouse auctionHouse;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        auctionHouse = new AuctionHouse();
    }
}
