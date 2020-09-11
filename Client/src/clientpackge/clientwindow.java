package clientpackge;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class clientwindow {
    JFrame clients = new JFrame();
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 3443;
    private Socket clientSocket;
    private Scanner inMessage;
    private PrintWriter outMessage;

    private JTextField jtfMessage; //элементы формы
    private JLabel jtfName;
    private JTextArea jtaTextAreaMessage;

    private String clientName = "";

    public void getClientName() {
        JFrame name = new JFrame();
        name.setBounds(600,300,200,100);

        JTextField namespace = new JTextField(clientName);
        name.add(namespace, BorderLayout.NORTH);

        JButton sendname = new JButton("Подтвердить никнейм");
        name.add(sendname, BorderLayout.SOUTH);

        sendname.addActionListener(e -> {
            if (!namespace.getText().trim().isEmpty()) {  // если имя клиента непустое
                clientName = namespace.getText();
                jtfName.setText(clientName);
                try {
                    outMessage = new PrintWriter(clientSocket.getOutputStream());
                    outMessage.println(clientName);
                    outMessage.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                clients.setVisible(true);
                name.setVisible(false);
            }
        });
        namespace.addActionListener(e -> {
            if (!namespace.getText().trim().isEmpty()) {  // если имя клиента непустое
                clientName = namespace.getText();
                jtfName.setText(clientName);
                try {
                    outMessage = new PrintWriter(clientSocket.getOutputStream());
                    outMessage.println(clientName);
                    outMessage.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                clients.setVisible(true);
                name.setVisible(false);
            }
        });
        name.setVisible(true);
    }

    public clientwindow() {
        getClientName();
        try {
            clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
            inMessage = new Scanner(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            try {
                clientSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        clients.setBounds(600, 300, 600, 500);
        clients.setTitle("Чатец-молодец");
        clients.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jtaTextAreaMessage = new JTextArea();
        jtaTextAreaMessage.setEditable(false);
        jtaTextAreaMessage.setLineWrap(true);

        JScrollPane jsp = new JScrollPane(jtaTextAreaMessage);
        clients.add(jsp, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        clients.add(bottomPanel, BorderLayout.SOUTH);

        JButton jbSendMessage = new JButton("Отправить");
        bottomPanel.add(jbSendMessage, BorderLayout.EAST);
        jtfMessage = new JTextField("Введите ваше сообщение: ");
        bottomPanel.add(jtfMessage, BorderLayout.CENTER);
        jtfName = new JLabel();
        clients.add(jtfName, BorderLayout.NORTH);

        jbSendMessage.addActionListener(e -> {
            if (!jtfMessage.getText().trim().isEmpty()) {  // если сообщение непустое, то отправляем по кнопке
                sendMsg();
                jtfMessage.grabFocus();
            }
        });
        jtfMessage.addActionListener(e -> {
            if (!jtfMessage.getText().trim().isEmpty()) {  // если сообщение непустое, то отправляем по Энтеру
                sendMsg();
                jtfMessage.grabFocus();
            }
        });
        jtfMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { jtfMessage.setText(""); } // при фокусе поле сообщения очищается
        });

        new Thread(() -> {
            try {
                while (true) {
                    if (inMessage.hasNext()) {
                        String inMes = inMessage.nextLine();
                            if (inMes.contains("@") && !inMes.contains(clientName)){ }
                            else if (inMes.contains(clientName + ": @")) {
                                jtaTextAreaMessage.append(inMes);
                                jtaTextAreaMessage.append("\n");
                            }
                            else if (inMes.contains("@") && !inMes.contains("@" + clientName + " ")){}
                            /*else if (inMes.contains("@" + clientName)) {
                                JFrame pm = new JFrame();
                                pm.setBounds(600, 300, 600, 500);
                                pm.setTitle("Чатец-приватец");

                        }*/
                            else{
                                jtaTextAreaMessage.append(inMes);
                                jtaTextAreaMessage.append("\n");
                            }
                        }
                    }
                }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }).start();

        clients.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    outMessage.flush();
                    outMessage.close();
                    inMessage.close();
                    clientSocket.close();
                }
                catch (IOException exc)
                {
                    exc.printStackTrace();
                }
            }
        });

    }

    public void sendMsg() {
        String messageStr = jtfName.getText() + ": " + jtfMessage.getText();
        outMessage.println(messageStr);
        outMessage.flush();
        jtfMessage.setText("");
    }
}