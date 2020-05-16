package server_program;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * @file TCP_Server.java
 * @date Feb 17, 2020 , 12:28:55
 * @author Muhammet Alkan
 */
public class TCP_Server {

    private ServerSocket serverSocket;
    private javax.swing.JTextPane historyJTextPane;
    private Thread serverThread;
    private ArrayList<Contact> allContacts = new ArrayList<>();

    protected void creatClient(Contact newContact) throws IOException {

        boolean exsit = false;
        for (Contact client : allContacts) {// check if server has this client by phone no which is the uniq about every client
            if (client.telefon == newContact.telefon) {
                exsit = true;
            }
        }
        if (!exsit) {//bu tel numarasi ile daha once kayitli olan clientimiz yoksa eger 

            newContact.state = "log-in";// cleintin durumunu degistir 
            allContacts.add(newContact);// cleinti ekle 
            newContact.outputstream.writeObject("Created");// clientin outputstream'ini kullanarak kendisine   eklendi diye bi cevap yolla 

        } else {//varsa 

            newContact.outputstream.writeObject("This telefon already exist!");// clientin outputstream'ini kullanarak kendisine   eklenmedi diye bi cevap yolla 

        }
    }

    protected void searchClient(String tel, Contact client_info) {
        try {
            boolean Found = false;
            for (Contact contact : allContacts) {
                if (Long.parseLong(tel) == contact.telefon) {
                    client_info.outputstream.writeObject(new Contact(contact.telefon, contact.name, contact.allChat, contact.contacts, contact.state));
                    Found = true;
                }
            }
            if (!Found) {
                client_info.outputstream.writeObject("tel number not found");
            }
        } catch (Exception e) {
            System.out.println("Error-sending object : " + e.getMessage());
        }
    }

    protected void start(int port, javax.swing.JTextPane jTextPaneHistory) throws IOException {
        // server soketi oluşturma (sadece port numarası)
        serverSocket = new ServerSocket(port);
        System.out.println("Server başlatıldı ..");

        // server arayüzündeki allChat alanı, bütün olaylar buraya yazılacak
        this.historyJTextPane = jTextPaneHistory;

        // arayüzü kitlememek için, server yeni client bağlantılarını ayrı Thread'de beklemeli
        serverThread = new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    // blocking call, yeni bir client bağlantısı bekler
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Yeni bir client bağlandı : " + clientSocket);

                    // bağlanan her client için bir thread oluşturup dinlemeyi başlat
                    new ListenThread(clientSocket).start();
                } catch (IOException ex) {
                    System.out.println("Hata - new Thread() : " + ex);
                    break;
                }
            }
        });
        serverThread.start();
    }

    protected void sendMessage(Object message, Contact client) throws IOException {
        //  ilgili clienta mesaj gönder
        client.outputstream.writeObject(message);

    }

    protected void sendBroadcast(String message) throws IOException {
        // bütün bağlı client'lara mesaj gönder
        for (Contact client : allContacts) {
            client.outputstream.writeObject("Server : " + message);
        }
    }

    protected void writeToHistory(String message) {
        // server arayüzündeki allChat alanına mesajı yaz
        historyJTextPane.setText(historyJTextPane.getText() + "\n" + message);
    }

    protected void stop() throws IOException {
        // bütün streamleri ve soketleri kapat
        if (serverSocket != null) {
            serverSocket.close();
        }
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }

    class ListenThread extends Thread implements Serializable {

        // dinleyeceğimiz client'ın soket nesnesi, input ve output stream'leri
        private final Socket clientSocket;
        private ObjectInputStream clientInput;
        private ObjectOutputStream clientOutput;
        private String UI;
        Contact client_info;

        private ListenThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            // baglanan client icin  thread bilgisini server arayuzune yazdiryorum
            writeToHistory("Thread has ben created for connected client : " + this.getName());
            writeToHistory("Bağlanan client için thread oluşturuldu : " + this.getName());

            try {
                // input  : client'dan gelen mesajları okumak için
                // output : server'a bağlı olan client'a mesaj göndermek için
                clientInput = new ObjectInputStream(clientSocket.getInputStream());
                clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());

                Object mesaj;
                // client mesaj gönderdiği sürece mesajı al

                while ((mesaj = clientInput.readObject()) != null) {
                    if (mesaj instanceof Contact) {// cleinttan gelen msaj client objesi ise  
                        client_info = (Contact) mesaj; // cleintin bilgilerini sakla 
                        client_info.outputstream = clientOutput;// server clientla ilitisim kurabilsin diye outputstream , ve inputStream bilirle 
                        client_info.inputStream = clientInput;
                        writeToHistory(" : " + ((Contact) mesaj).name + " " + ((Contact) mesaj).telefon);

                    }
                    // client'in gönderdiği mesajı server ekranına yaz  

                    if (mesaj.equals("Creat Client") && client_info != null) {// when sing-up button is pressed the client will send to the server this msg 
                        creatClient(client_info);
                    }
                    // clientin attigi mesaji serverin arayuzune yaz 

//                    if (mesaj.equals("search for a tel number exist or not")) {
//                        
//                        String tel = (String) clientInput.readObject();
//                        searchClient(tel, client_info);
//                    }
                    if (mesaj instanceof String && ((String) mesaj).contains("@tel-")) {
//                        ((String) mesaj).substring(4)
                        String tel = ((String) mesaj).substring(5);
                        searchClient(tel, client_info);

                    }

                }
                // baglanan clientin icin   bilgisini server arayuzune yazdiryorum

                for (Contact client : allContacts) {
                    if (client.outputstream == this.clientOutput) {// server her client ayri thread'te dinliyor , burda diyorum ki bu threadteki clientin clientOutput bilgisine esit olan clienti bul 
                        writeToHistory("Username: " + client.telefon + "  Name: " + client.name);//bilgisini yaz

                    }
                }

            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Hata - ListenThread : " + ex);
            } finally {
                try {

                    // bütün streamleri ve soketleri kapat
                    if (clientInput != null) {
                        clientInput.close();
                    }
                    if (clientOutput != null) {
                        clientOutput.close();
                    }
                    if (clientSocket != null) {
                        clientSocket.close();
                    }
                    writeToHistory("Soket kapatıldı : " + clientSocket);
                } catch (IOException ex) {
                    System.out.println("Hata - Soket kapatılamadı : " + ex);
                }
            }
        }
    }

}
