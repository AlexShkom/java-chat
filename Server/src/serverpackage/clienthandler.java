package serverpackage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class clienthandler implements Runnable {
    private serverpackage.server server;
    private PrintWriter outMessage; // исходящее сообщение
    private Scanner inMessage;  // входящее собщение
    String clientslist;

    public clienthandler(Socket socket, serverpackage.server server) {
        try {
            this.server = server;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                clientslist = inMessage.nextLine();
                server.getname(clientslist);
                server.sendMessageToAllClients("В чате пополнение - '" + clientslist + "'!");
                break;
            }
            while (true) {
                if (inMessage.hasNext()) {
                    String clientMessage = inMessage.nextLine();
                    if (clientMessage.equalsIgnoreCase("##session##end##")) {
                        break;
                    }
                    System.out.println(clientMessage);
                    server.sendMessageToAllClients(clientMessage);
                }
                Thread.sleep(100);
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        finally {
            removing();
        }
    }

    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removing(){
        server.closing(this,clientslist);
    }
}