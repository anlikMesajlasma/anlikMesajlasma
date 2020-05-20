package client_program;

import java.awt.Color;
import java.awt.List;
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
import server_program.Chat;
import server_program.Msg;

/**
 * @file TCP_Client.java
 * @date Feb 17, 2020 , 13:07:59
 * @author Muhammet Alkan
 */
public class TCP_Client {

    private final int serverPort = 44444;//server adresi sabit 
    private Socket clientSocket;
    private ObjectInputStream clientInput;
    private ObjectOutputStream clientOutput;
    private javax.swing.JTextPane historyJTextPane;
    private javax.swing.JFrame jFrame_singUp;
    private javax.swing.JFrame jFrame_logIn;
    private DefaultListModel model = new DefaultListModel();
    private int count = 0;
    //private  allServerCont;
    private Contact accuontOwner;
    private Contact chatReciverContact;
    private javax.swing.JLabel JLabel;
    private Thread clientThread;
    private javax.swing.JLabel JLabe2;
    private JFrame searchContactjFrame;
    private JLabel errorjLabel;
    private JLabel namejLabel;
    private JLabel teljLabel;
    private Contact contactToBeadded;
//    private static TCP_Client clientfronAddingFrmae;
    private javax.swing.JList contacts_jList;
    private javax.swing.JList msg_jList;

    protected Contact getContact() {

        return accuontOwner;
    }

    protected void askForAnynewMsg(long sender, long reciver, javax.swing.JList msgHistory) throws IOException {
        sendMessage("@I am on chat");
        sendMessage(sender);
        sendMessage(reciver);

    }

    protected void sayToServerThatIleftTheChat() throws IOException {
        sendMessage("I left the chat");
    }

    protected void sing_up_to_server(Contact contact, javax.swing.JLabel jLabelName, javax.swing.JFrame jframe) throws IOException {
        this.JLabel = jLabelName;
        jFrame_singUp = jframe;

        sendMessage(contact);// send to  the server  contact you want to creat 
        accuontOwner = contact;
        sendMessage("Creat Client");//say to server that you need to creat contact

    }

    protected void log_in_to_server(Contact client, javax.swing.JLabel jLabelName, javax.swing.JFrame jframe) throws IOException {
        this.JLabel = jLabelName;
        jFrame_logIn = jframe;
        sendMessage(client);// send to  the server  client you want to creat 
        // accuontOwner = client;
        sendMessage("log in");//say to server that you need to creat client

    }

    protected void askForContact(long accountOwner, javax.swing.JList contactHisot) throws IOException {
        this.contacts_jList = contactHisot;
        sendMessage("@ my contacts  pleas");
        sendMessage(accountOwner);

    }

    protected void askForHistory(long sender, long reciver, javax.swing.JList msgHistory) throws IOException {
        this.msg_jList = msgHistory;
        System.out.println("msg_jList" + msg_jList);

        sendMessage("@ History pleas");
        System.out.println("@ History pleas");
        sendMessage(sender);
        sendMessage(reciver);

    }

    void getVariationQustion(Contact client, javax.swing.JLabel jLabelName1, javax.swing.JLabel jLabelName2) throws IOException {
        this.JLabel = jLabelName1;
        this.JLabe2 = jLabelName2;

        sendMessage(client);// send to  the server  client you want to creat 

        sendMessage("get variation qustion");//say to server that you need to creat client

    }

    void restPass(Contact client, javax.swing.JLabel jLabelName, String answer, javax.swing.JFrame jframe) throws IOException {
        this.JLabel = jLabelName;
        jFrame_singUp = jframe;
        sendMessage("$" + answer);// send to  the server  client you want to creat 
        sendMessage("reset pass");//say to server that you need to creat client

    }

//    void setlest(JList jlist) {
//        TCP_Client.jList = jlist;
//    }
    protected void searchAllClientsListOnServer(long telTobeadded, javax.swing.JLabel errorjLabel, javax.swing.JFrame searchContactjFrame) throws IOException {
        this.searchContactjFrame = searchContactjFrame;
        this.errorjLabel = errorjLabel;

        addContact(errorjLabel, telTobeadded);
    }

    void addContact(JLabel errorjLabel, long contactTobaAdded) {
        boolean exsit = false;
        boolean itAccountOwnerNo = false;
        if (contactTobaAdded == accuontOwner.getTelefon()) {
            errorjLabel.setText("You can not add yourself !");
        } else {

            try {
                sendMessage("@ add new contact");
                sendMessage(contactTobaAdded);
            } catch (IOException ex) {
                Logger.getLogger(TCP_Client.class.getName()).log(Level.SEVERE, null, ex);
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

    protected void start(InetAddress inetAddress, TCP_Client tCP_client) throws IOException {
        // client soketi oluşturma (ip + port numarası)

        clientSocket = new Socket(inetAddress, serverPort);

        clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
        clientInput = new ObjectInputStream(clientSocket.getInputStream());

        // server'ı sürekli dinlemek için Thread oluştur
        clientThread = new ListenThread(tCP_client);
//        clientThread = new ListenThread();
        clientThread.start();
    }

//    protected void sendMessage(Msg msg, javax.swing.JList jlist) throws IOException {
//        // gelen mesajı server'a gönder
//        msg_jList = jlist;
//        clientOutput.writeObject("@-send msg for other client:");
//        clientOutput.writeObject(msg);
//    }
    protected void sendMessage(long senderNo, long reciver, Object msg, javax.swing.JList jlist) throws IOException {
        // gelen mesajı server'a gönder
        msg_jList = jlist;
        clientOutput.writeObject("@-send string msg for other client:");
        clientOutput.writeObject(senderNo);
        System.out.println("rdeciver" + reciver);
        clientOutput.writeObject(reciver);

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
                        jFrame_singUp.setVisible(false);// sing-up jframini kapat 
                        client.accuontOwner = (Contact) mesaj;
                        new login_UI(this.client).setVisible(true);

                    }
                    if (mesaj.equals("Sucessfully log-in")) {

                        mesaj = clientInput.readObject();
                        client.accuontOwner = (Contact) mesaj;
                        new main_UI(this.client).setVisible(true);// uygulamanin ana j framini ac 
                        jFrame_logIn.setVisible(false);
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
                    }
                    if (mesaj.toString().charAt(0) == '0') {
                        JOptionPane.showMessageDialog(jFrame_singUp, "Your password is  " + mesaj.toString().substring(2));
                    }
                    if (mesaj.equals("you have nwe msg")) {// eger serverden bunu aldiysan 

                        //jList.setModel(model);
                        Object sender = clientInput.readObject();
                        Object mesg = clientInput.readObject();

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
                        searchContactjFrame.setVisible(false);
                        new main_UI(this.client).setVisible(true);

                    }
                    if (mesaj.equals("No contact found with this number !")) {
                        errorjLabel.setText(mesaj + "");

                    }

                    if (mesaj.equals("You have this contact already !")) {
                        mesaj = clientInput.readObject();
                        ArrayList<Contact> updatedList = new ArrayList<>((ArrayList<Contact>) mesaj);

                        errorjLabel.setText("You have this contact already !");

                    }
                    if(mesaj.equals("@you have new mesaj")){
                    mesaj = clientInput.readObject();
                    DefaultListModel model2 = new DefaultListModel();
                        System.out.println("from you have new msg ");
                        mesaj = clientInput.readObject();
                        if(msg_jList!=null){
                        msg_jList.setModel(model2);
                        
                        System.out.println("msg_jList" + msg_jList);
                        ArrayList<String> msgHistoy = new ArrayList<>((ArrayList<String>) mesaj);

                        int count = 0;
                        for (String msg : msgHistoy) {
                            model2.add(count, msg);
                            count++;
                        }

                        }
                    }
                    if (mesaj.equals("@Here is Your Chat History")) {
                        DefaultListModel model2 = new DefaultListModel();

                        mesaj = clientInput.readObject();
                        msg_jList.setModel(model2);
                        System.out.println("msg_jList" + msg_jList);
                        ArrayList<String> msgHistoy = new ArrayList<>((ArrayList<String>) mesaj);

                        int count = 0;
                        for (String msg : msgHistoy) {
                            model2.add(count, msg);
                            count++;
                        }

                    }
                    if (mesaj.equals("@Your contacts ")) {
                        DefaultListModel model = new DefaultListModel();
                        mesaj = clientInput.readObject();
                        contacts_jList.setModel(model);

                        ArrayList<Contact> updatedList = new ArrayList<>((ArrayList<Contact>) mesaj);

                        int count = 0;
                        for (Contact contact : updatedList) {
                            model.add(count, contact.getTelefon() + "");
                            count++;
                        }

                    }
                }

            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Error - ListenThread : " + ex);
            }
        }
    }

}
