package client_program;

import java.awt.Color;
import server_program.Contact;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import static java.nio.file.Files.list;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import server_program.Msg;

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
    private Contact chatReciverContact;
    private static javax.swing.JLabel JLabel;
    private Thread clientThread;
    private static javax.swing.JLabel JLabe2;
    private JFrame searchContactjFrame;
    private JLabel errorjLabel;
    private JLabel namejLabel;
    private JLabel teljLabel;
    private static Contact contactToBeadded;

    protected Contact getContact() {

        return accuontOwner;
    }

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
        // accuontOwner = client;
        sendMessage("log in");//say to server that you need to creat client

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

    protected void searchAllClientsListOnServer(String tel, javax.swing.JLabel errorjLabel, javax.swing.JLabel namejLabel, javax.swing.JLabel teljLabel, javax.swing.JFrame searchContactjFrame, Contact Cahtcontact) throws IOException {
        this.searchContactjFrame = searchContactjFrame;
        this.errorjLabel = errorjLabel;
        this.namejLabel = namejLabel;
        this.teljLabel = teljLabel;
        this.namejLabel.setText("");
        this.errorjLabel.setText("");
        this.teljLabel.setText("");
        this.chatReciverContact = Cahtcontact;
        sendMessage("@tel-" + tel);
    }

    void addContact(JLabel errorjLabel) {

        boolean exsit = false;
        boolean itAccountOwnerNo = false;
        if (contactToBeadded.getTelefon() == accuontOwner.getTelefon()) {
            errorjLabel.setText("You can not add yourself !");
        } else {
            for (Contact contact : accuontOwner.getContacts()) {
                if (contact.getTelefon() == contactToBeadded.getTelefon()) {
                    exsit = true;
                }

            }
            if (!exsit) {
                try {
                    System.out.println("!exsit");
                    sendMessage("@ add new contact");
                    sendMessage(contactToBeadded);
                    System.out.println("sent to server ");
                } catch (IOException ex) {
                    Logger.getLogger(TCP_Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (exsit) {

                errorjLabel.setText("You have this contact already");
            } else {
                errorjLabel.setText("You can not add yourself !");

            }
        }
    }

    protected void writeBackSearchContactError() {
        errorjLabel.setForeground(Color.red);
        this.errorjLabel.setText("Telephone number not found !");
    }

    protected void writeBackContactDetailsToSearchContactjFrame(Contact contact) {
        this.namejLabel.setText(contact.getName());
        this.teljLabel.setText(String.valueOf((contact.getTelefon())));
        contactToBeadded = contact;
    }

    protected void start(InetAddress inetAddress) throws IOException {
        // client soketi oluşturma (ip + port numarası)

        clientSocket = new Socket(inetAddress, serverPort);

        clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
        clientInput = new ObjectInputStream(clientSocket.getInputStream());

        // server'ı sürekli dinlemek için Thread oluştur
        clientThread = new ListenThread(this);
//        clientThread = new ListenThread();
        clientThread.start();
    }

    protected void sendMessage(Msg msg, javax.swing.JList jlist) throws IOException {
        // gelen mesajı server'a gönder
        jList = jlist;
        clientOutput.writeObject("@-send msg for other client:");
        clientOutput.writeObject(msg);
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

    class ListenThread extends Thread implements Serializable {

        TCP_Client client;

        public ListenThread(TCP_Client client) {
            this.client = client;
        }

        // server'dan gelen mesajları dinle
        @Override
        public void run() {
            try {

                Object mesaj;
                // server mesaj gönderdiği sürece gelen mesajı al
                while ((mesaj = clientInput.readObject()) != null) {
                    // serverden gelen mesaj This telefon already exist!ise clientin arayuzune yaz kayit edilmedigini bilsin

                    if (mesaj instanceof Contact) {

                        accuontOwner = (Contact) mesaj;
                    }
                    if (mesaj.equals("This telefon already exist!")) {
                        JLabel.setText(mesaj + "");

                    }
                    if (mesaj.equals("Created")) {// client servere ilk defa baglanip basarali bir kayit yapttiktan sonra bu mesaji alacak 

                        mesaj = clientInput.readObject();
                        JOptionPane.showMessageDialog(null, "Successfully singed-up !");// client sing-up jframde ise ve created mesaji geldiyse bunu goster
                        jFrame.setVisible(false);// sing-up jframini kapat 
                        accuontOwner = (Contact) mesaj;
                        new main_UI(client).setVisible(true);// uygulamanin ana j framini ac 

                    }
                    if (mesaj.equals("Sucessfully log-in")) {
                        
                         mesaj = clientInput.readObject();
                         Contact me = (Contact) mesaj;
                         //Collections.copy(me.getContacts(),((Contact)mesaj).getContacts() );
                       System.out.println("from  log in tcp client : size of contact list " + me.getContacts()+ "Name "+ me.getState());
                        System.out.println("size: " + accuontOwner.getContacts().size());
                        new main_UI(this.client).setVisible(true);// uygulamanin ana j framini ac 
                        jFrame.dispose();
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
                    if (mesaj.equals("sending contact")) {
                        mesaj = clientInput.readObject();
                        if (mesaj instanceof Contact) {

                            writeBackContactDetailsToSearchContactjFrame((Contact) mesaj);
                        }
                        if (mesaj.equals("tel number not found")) {
                            writeBackSearchContactError();
                        }
                    }
                    if (mesaj.equals("@ contact added")) {
                        mesaj = clientInput.readObject();
                        ArrayList<Contact> arr =  (ArrayList<Contact>) mesaj;
                        System.out.println("client.accuontOwner.getContacts().size(): " + arr.size());
                       
                        

                    }
                }

            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Error - ListenThread : " + ex);
            }
        }
    }

}
