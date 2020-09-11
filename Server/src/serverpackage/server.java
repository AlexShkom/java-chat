package serverpackage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class server {
    static final int PORT = 3443;
    private ArrayList<clienthandler> clients = new ArrayList<>();
    private ArrayList<String> clientsname = new ArrayList<>();

    public server() {
        Socket clientSocket = null;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Сервер запущен");
            while (true) {
                clientSocket = serverSocket.accept();
                clienthandler client = new clienthandler(clientSocket, this);
                clients.add(client);
                System.out.println(clients);
                new Thread(client).start();
            }
        }
        catch (IOException ex) { ex.printStackTrace(); }
        finally {
            try {
                    clientSocket.close();
                    System.out.println("Сервер остановлен");
                    serverSocket.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void sendMessageToAllClients(String msg) {
        for (clienthandler o : clients) { o.sendMsg(msg); }
    }

    public void getname (String name){
        this.clientsname.add(name);
        System.out.println(clientsname);
    }

    public void closing(clienthandler client, String clientname){
        clients.remove(client);
        clientsname.remove(clientname);
    }
}