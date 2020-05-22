import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class MasterMind_ServerClient implements Runnable{
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;
    private MasterMind_Server server;
    private boolean isConnected = true;
//    private ArrayList<MasterMind_ServerClient> clients2;

    public MasterMind_ServerClient(Socket socket, String name, MasterMind_Server server){
        this.socket = socket;
        this.name = name;
        this.server = server;

//        this.clients2 = server.getClients();
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeUTF(String text){
        try {
            this.out.writeUTF(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        while(isConnected){
           // String receiving = "";
            try {
               String receiving = this.in.readUTF();
                if (receiving.equals("quit") ){
                    isConnected = false;
                    this.server.removeClient(this);
                }
                if (server.getClients().size() == 3){
                    isConnected = false;
//                    System.out.println("game has begun");
                    sendToLastClient();
                    this.server.removeClient(this);
                   // System.out.println(server.getClients().size());
                } else{
                    this.server.sendToAllClients("<" + this.name + "> : " + receiving);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getName(){
        return name;
    }

    public void sendToLastClient(){
        MasterMind_ServerClient lastClient = server.getClients().get(server.getClients().size()-1);
        lastClient.writeUTF("A game has already begun...");
    }
}
