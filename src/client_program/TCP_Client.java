package client_program;

import java.awt.Color;
import server_program.Contact;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 * @file TCP_Client.java
 * @date Feb 17, 2020 , 13:07:59
 * @author Muhammet Alkan
 */
public class TCP_Client {

    private final int serverPort = 44444;//server adresi sabit 
    private static Socket clientSocket;
    private static ObjectInputStream clientInput;
    private static ObjectOutputStream clientOutput;
    private javax.swing.JTextPane historyJTextPane;
    private static javax.swing.JFrame jFrame;
    private static javax.swing.JList jList;
    private static final DefaultListModel model = new DefaultListModel();
    private static int count = 0;
    //private  allServerCont;
    private static Contact accuontOwner;
    private static javax.swing.JLabel JLabel;
    private Thread clientThread;
    private static javax.swing.JLabel JLabe2;

    protected void sing_up_to_server(Contact client, javax.swing.JLabel jLabelName, javax.swing.JFrame jframe) throws IOException {
        this.JLabel = jLabelName;
        jFrame = jframe;
        sendMessage(client);// send to  the server  client you want to creat 
        accuontOwner = client;
        sendMessage("Creat Client");//say to server that you need to creat client

    }

    protected void log_in_to_server(Contact client, javax.swing.JLabel jLabelName, javax.swing.JFrame jframe) throws IOException {
        this.JLabel = jLabelName;
        jFrame = jframe;
        sendMessage(client);// send to  the server  client you want to creat 
        accuontOwner = client;
        sendMessage("log in");//say to server that you need to creat client
        System.out.println("sent");

    }

    void getVariationQustion(Contact client, javax.swing.JLabel jLabelName1, javax.swing.JLabel jLabelName2) throws IOException {
        this.JLabel = jLabelName1;
        this.JLabe2 = jLabelName2;

        sendMessage(client);// send to  the server  client you want to creat 

        sendMessage("get variation qustion");//say to server that you need to creat client
        System.out.println("sent");

    }

    void restPass(Contact client, javax.swing.JLabel jLabelName, String answer, javax.swing.JFrame jframe) throws IOException {
        this.JLabel = jLabelName;
        jFrame = jframe;
        sendMessage("$" + answer);// send to  the server  client you want to creat 
        sendMessage("reset pass");//say to server that you need to creat client
        System.out.println("sent");

    }

    void setlest(JList jlist) {
        TCP_Client.jList = jlist;
    }

    protected void start(InetAddress inetAddress) throws IOException {
        // client soketi oluşturma (ip + port numarası)

        clientSocket = new Socket(inetAddress, serverPort);

        clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
        clientInput = new ObjectInputStream(clientSocket.getInputStream());

        // server'ı sürekli dinlemek için Thread oluştur
        clientThread = new ListenThread();
        clientThread.start();
    }

    protected void sendMessage(Object message, long tel, javax.swing.JList jlist) throws IOException {
        // gelen mesajı server'a gönder
        jList = jlist;
        clientOutput.writeObject("send msg");
        clientOutput.writeObject(message);
        clientOutput.writeObject(tel);
    }

    protected void sendMessage(Object message) throws IOException {
        // gelen mesajı server'a gönder
        clientOutput.writeObject(message);
    }

    protected void sendObject(Object message) throws IOException {
        // gelen nesneyi server'a gönder
        clientOutput.writeObject(message);
    }

    protected void writeToHistory(Object message) {
        // client arayüzündeki history alanına mesajı yaz
        historyJTextPane.setText(historyJTextPane.getText() + "\n" + message);
    }

    protected void disconnect() throws IOException {
        // bütün streamleri ve soketleri kapat
        if (clientInput != null) {
            clientInput.close();
        }
        if (clientOutput != null) {
            clientOutput.close();
        }
        if (clientThread != null) {
            clientThread.interrupt();
        }
        if (clientSocket != null) {
            clientSocket.close();
        }
    }

    class ListenThread extends Thread {


        // server'dan gelen mesajları dinle
        @Override
        public void run() {
            try {

                Object mesaj;
                // server mesaj gönderdiği sürece gelen mesajı al
                while ((mesaj = clientInput.readObject()) != null) {
                    // serverden gelen mesaj This telefon already exist!ise clientin arayuzune yaz kayit edilmedigini bilsin

                    if (mesaj instanceof Contact) {

                        Contact con = (Contact) mesaj;
                    }
                    if (mesaj.equals("This telefon already exist!")) {
                        JLabel.setText(mesaj + "");

                    }
                    if (mesaj.equals("Created")) {// client servere ilk defa baglanip basarali bir kayit yapttiktan sonra bu mesaji alacak 
                        JOptionPane.showMessageDialog(null, "Successfully singed-up !");// client sing-up jframde ise ve created mesaji geldiyse bunu goster
                        jFrame.setVisible(false);// sing-up jframini kapat 
                        new main_UI().setVisible(true);// uygulamanin ana j framini ac 

                    }
                    if (mesaj.equals("Sucessfully log-in")) {
                        jFrame.dispose();
                        new main_UI().setVisible(true);
                    }
                    if (mesaj.equals("invalid password !")) {
                        JLabel.setText(mesaj + "");
                    }
                    if (mesaj.equals("No client with this tel no ")) {
                        JLabel.setText("no client with this number !");
                    }
                    if (mesaj.equals("Your faivorate teacher name") | mesaj.equals("Your best mark in math") | mesaj.equals(" Your childhood friend name")) {
                        JLabe2.setText(mesaj + "");
                    }
                    if (mesaj.equals("Wrong answer !")) {
                        JLabel.setText(mesaj + "");
                        System.out.println(mesaj);
                    }
                    if (mesaj.toString().charAt(0) == '0') {
                        System.out.println("mesaj");
                        JOptionPane.showMessageDialog(jFrame, "Your password is  " + mesaj.toString().substring(2));
                    }
                    if (mesaj.equals("you have nwe msg")) {// eger serverden bunu aldiysan 
                        System.out.println("from mesaj.equals(\"you have nwe msg\")");

                        //jList.setModel(model);
                        Object sender = clientInput.readObject();
                        System.out.println(sender);

                        Object content = clientInput.readObject();
                        System.out.println(content);

                        // model.add(count, sender + " : " + content);
                        //count++;
                    }
                    
                   

                }
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Error - ListenThread : " + ex);
            }
        }
    }

}
