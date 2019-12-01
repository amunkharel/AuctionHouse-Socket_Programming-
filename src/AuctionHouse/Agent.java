package AuctionHouse;

import java.io.IOException;
import java.net.Socket;

public class Agent {

    private Socket agentClient = null;

    private int agentId;

    public Agent(Socket socket, int agentId){
        this.agentClient = socket;
        this.agentId = agentId;
    }


    public Socket getAgentClient() {
        return agentClient;
    }

    public int getAgentId() {
        return agentId;
    }

    public void closeSocket() {
        try {
            agentClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
