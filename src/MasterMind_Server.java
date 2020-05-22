import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class MasterMind_Server extends Application {
    private int port = 10000;
    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream in;


    private ArrayList<MasterMind_ServerClient> clients = new ArrayList<>();
    private HashMap<String, Thread> clientThreads = new HashMap<>();

    @Override
    public void start(Stage stage) throws Exception {
        TextArea ta = new TextArea();
        Scene scene = new Scene(new ScrollPane(ta),450,200);
        stage.setTitle("Mindmaster");
        stage.setScene(scene);
        stage.show();

        ta.appendText("Starting mastermind server...");

        new Thread(()->{
            try {
                this.serverSocket = new ServerSocket(port);
                boolean isRunning = true;

                while (isRunning) {

                    if (clients.size() < 3) {
                        ta.appendText("Waiting for client");
                        this.socket = serverSocket.accept();

                        ta.appendText("Client connected via adddress: " + socket.getInetAddress().getHostAddress());
                        in = new DataInputStream(socket.getInputStream());
                        String name = in.readUTF();
                        MasterMind_ServerClient serverClient = new MasterMind_ServerClient(socket, name, this);
                        Thread t = new Thread(serverClient);
                        t.start();

                        this.clientThreads.put(name, t);
                        this.clients.add(serverClient);

                    }
                    // System.out.println(clients.size());
                }

                this.serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendToLastClient(){
        MasterMind_ServerClient lastClient = clients.get(clients.size()-1);
        lastClient.writeUTF("A game has already begun...");
    }


    public void sendToAllClients(String text) {
        for (MasterMind_ServerClient client : clients) {
            client.writeUTF(text);
        }
    }

    public void removeClient(MasterMind_ServerClient serverClient){
        String name = serverClient.getName();
        this.clients.remove(serverClient);

        Thread t = this.clientThreads.get(name);
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.clientThreads.remove(name);
    }

    public void writeStringToSocket(Socket socket, String text) {

        try {
            socket.getOutputStream().write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<MasterMind_ServerClient> getClients() {
        return clients;
    }


}
