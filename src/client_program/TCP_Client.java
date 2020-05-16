package client_program;

import server_program.Contact;
import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JOptionPane;
import java.io.Serializable;

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
    //private  allServerCont;
    private static javax.swing.JLabel NewRegisterSatuJLabel;
    private Thread clientThread;

    protected void sing_up_to_server(Contact contact, javax.swing.JLabel jLabelName, javax.swing.JFrame jframe) throws IOException {
        this.NewRegisterSatuJLabel = jLabelName;
        jFrame = jframe;
//        this.contact = contact;
        sendMessage(contact);// send to  the server  client you want to creat 

        sendMessage("Creat Client");//say to server that you need to creat client

    }

    protected void log_in_to_server(Contact client, javax.swing.JLabel jLabelName, javax.swing.JFrame jframe) throws IOException {
// merve yapacak bu kisimi 
    }

    private static javax.swing.JFrame searchContactjFrame;
    private static javax.swing.JLabel errorjLabel;
    private static javax.swing.JLabel namejLabel;
    private static javax.swing.JLabel teljLabel;

    protected void searchAllClientsListOnServer(String tel, javax.swing.JLabel errorjLabel, javax.swing.JLabel namejLabel, javax.swing.JLabel teljLabel, javax.swing.JFrame searchContactjFrame) throws IOException {
        this.searchContactjFrame = searchContactjFrame;
        this.errorjLabel = errorjLabel;
        this.namejLabel = namejLabel;
        this.teljLabel = teljLabel;
        this.namejLabel.setText(" ");
        this.errorjLabel.setText(" ");
        this.teljLabel.setText(" ");
//        sendMessage("search for a tel number exist or not");
//        sendMessage(tel);
        sendMessage("@tel-" + tel);
    }

    protected void writeBackSearchContactError() {
        errorjLabel.setForeground(Color.red);
        this.errorjLabel.setText("Telephone number not found !");
    }

    protected void writeBackContactDetailsToSearchContactjFrame(Contact contact) {
        this.namejLabel.setText(contact.getName());
        this.teljLabel.setText(String.valueOf((contact.getTelefon())));
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

    protected void sendMessage(Object message) throws IOException {
        // gelen mesajı server'a gönder
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
                    if (mesaj.equals("This telefon already exist!")) {
                        NewRegisterSatuJLabel.setText(mesaj + "");

                    }
                    if (mesaj.equals("Created")) {// client servere ilk defa baglanip basarali bir kayit yapttiktan sonra bu mesaji alacak 
                        JOptionPane.showMessageDialog(null, "Successfully singed-up !");// client sing-up jframde ise ve created mesaji geldiyse bunu goster
                        jFrame.setVisible(false);// sing-up jframini kapat 
//                        new main_UI().setVisible(true);// uygulamanin ana j framini ac                         
                        new main_UI(this.client).setVisible(true);// uygulamanin ana j framini ac 

                    }
//                    if (mesaj.equals("sending contact")) {
                    if (mesaj instanceof Contact) {
                        writeBackContactDetailsToSearchContactjFrame((Contact) mesaj);
                    }
                    if (mesaj.equals("tel number not found")) {
                        writeBackSearchContactError();
                    }
                }
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Error - ListenThread : " + ex);
            }
        }
    }

}
