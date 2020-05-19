package server_program;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;

/**
 * @file TCP_Server.java
 * @date Feb 17, 2020 , 12:28:55
 * @author Muhammet Alkan
 */
public class TCP_Server {

    ArrayList<OutputStreams> onlineClientList = new ArrayList<>();
    private ServerSocket serverSocket;
    private javax.swing.JList historyJList;
    private Thread serverThread;
    private ArrayList<contact> allClients = new ArrayList<>();
    private DefaultListModel model = new DefaultListModel();
    private int count = 0;

    protected void creatClient(contact newContact, ObjectOutputStream outputstream) throws IOException {

        boolean exsit = false;
        for (contact client : allClients) {// check if server has this client by phone no which is the uniq about every client
            if (client.telefon == newContact.telefon) {
                exsit = true;
            }
        }
        if (!exsit) {//bu tel numarasi ile daha once kayitli olan clientimiz yoksa eger 

            newContact.state = "";// cleintin durumunu degistir 
            allClients.add(newContact);// cleinti ekle 
            outputstream.writeObject("Created");// clientin outputstream'ini kullanarak kendisine   eklendi diye bi cevap yolla 
            outputstream.writeObject(newContact);

        } else {//varsa 

            outputstream.writeObject("This telefon already exist!");// clientin outputstream'ini kullanarak kendisine   eklenmedi diye bi cevap yolla 

        }
    }

    protected void login(long tel, String pass, ObjectOutputStream outputstream) throws IOException {
//        contact contact = null;
        contact contct = this.findContactByPhone(tel);
        boolean exsit = false;
        String pasw = "";
        for (contact clientt : allClients) {// check if server has this client by phone no
            if (clientt.telefon == tel) {
                exsit = true;
                pasw = clientt.password;
//                contact = clientt;
                break;
            }
        }
        if (exsit) {//bu tel numarasi ile daha once kayitli olan clientimiz yoksa eger 
            if (pass.equals(pasw)) {
                contct.state = "online";// cleintin durumunu degistir 
                outputstream.writeObject("Sucessfully log-in");// clientin outputstream'ini kullanarak kendisine   "basrili giris " diye bi cevap yolla 
                //client.outputstream.writeObject(client.getContacts());
                outputstream.writeObject(contct);// sending acoountOwner info

            } else {
                outputstream.writeObject("invalid password !");
            }
        } else {//yoksa

            outputstream.writeObject("No client with this tel no ");// clientin outputstream'ini kullanarak kendisine   eklenmedi diye bi cevap yolla 
        }
    }

    protected void getVarietionQustion(contact client, ObjectOutputStream outputstream) throws IOException {

        boolean exsit = false;
        String qustion = "";
        for (contact clientt : allClients) {// check if server has this client by phone no
            if (clientt.telefon == client.telefon) {
                exsit = true;
                qustion = clientt.variationQustion;
                break;
            }
        }
        if (exsit) {

            outputstream.writeObject(qustion);

        } else {//yoksa
            outputstream.writeObject("No client with this tel no ");// clientin outputstream'ini kullanarak kendisine   eklenmedi diye bi cevap yolla 
        }
    }

    void addContactToClientContactList(contact cont, long contactTobeAdded, ObjectOutputStream outputstream) throws IOException {
        boolean exist = false;

        for (contact client : allClients) {

            if (contactTobeAdded == client.telefon) {
                exist = true;
                break;
            }
        }
        if (exist) {
            exist = false;

            for (contact client : allClients) {
                if (client.telefon == cont.telefon) {
                    for (contact contact : client.getContacts()) {
                        if (contact.getTelefon() == contactTobeAdded) {

                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        contact contactWillAdded = findContactByPhone(contactTobeAdded);
                        client.contacts.add(contactWillAdded);
                        for (contact contact : client.contacts) {
                        }
                        try {
                            outputstream.writeObject("@ contact added");
                        } catch (IOException ex) {
                            Logger.getLogger(TCP_Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        outputstream.writeObject("You have this contact already !");
                        outputstream.writeObject(client.getContacts());
                    }
                }
            }
        } else {
            outputstream.writeObject("No contact found with this number !");
        }
    }

    contact findContactByPhone(long telNum) {
        contact cotct = null;
        for (contact con : allClients) {
            if (con.getTelefon() == telNum) {
                cotct = con;
            }
        }
        return cotct;
    }

    protected void resetPass(contact client, String answer, ObjectOutputStream outputstream) throws IOException {

        boolean exsit = false;
        String pasw = "";
        String answerOfclientInlist = "";
        for (contact clientt : allClients) {// check if server has this client by phone no
            if (clientt.telefon == client.telefon) {
                exsit = true;
                pasw = "0" + clientt.password;
                answerOfclientInlist = clientt.answer;
                break;
            }
        }
        if (exsit) {//bu tel numarasi ile daha once kayitli olan clientimiz varsa eger 
            if (answerOfclientInlist.equals(answer.substring(1))) {
                outputstream.writeObject("0" + pasw);
            } else {
                outputstream.writeObject("Wrong answer !");
            }
        } else {//yoksa
            outputstream.writeObject("No client with this tel no ");// clientin outputstream'ini kullanarak kendisine   eklenmedi diye bi cevap yolla 
        }
    }

    protected void sendContactList(long tel) throws IOException {
        ObjectOutputStream clientOutput = null;
        for (OutputStreams client : onlineClientList) {
            if (client.clientNo == tel) {
                clientOutput = client.clientoutput;
            }
        }
        boolean found = false;
        ArrayList<contact> updatedList = null;
        for (contact client : allClients) {
            if (tel == client.telefon) {
                updatedList = new ArrayList<>(client.contacts);
                found = true;
            }
        }
        if (found) {
            clientOutput.writeObject("@Your contacts ");
            clientOutput.writeObject(updatedList);
        }

    }

    protected void searchClient(String tel, contact client_info, ObjectOutputStream outputstream) {
        try {
            boolean Found = false;
            for (contact contact : allClients) {
                if (Long.parseLong(tel) == contact.telefon) {
                    outputstream.writeObject(contact);
                    Found = true;
                }
            }
            if (!Found) {
                outputstream.writeObject("tel number not found");
            } else {

            }
        } catch (Exception e) {
            System.out.println("Error-sending object : " + e.getMessage());
        }
    }

    protected void start(int port, javax.swing.JList jTextPaneHistory) throws IOException {
        // server soketi oluşturma (sadece port numarası)
        serverSocket = new ServerSocket(port);
        System.out.println("Server başlatıldı ..");

        // server arayüzündeki allChat alanı, bütün olaylar buraya yazılacak
        this.historyJList = jTextPaneHistory;

        // arayüzü kitlememek için, server yeni client bağlantılarını ayrı Thread'de beklemeli
        serverThread = new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    // blocking call, yeni bir client bağlantısı bekler
                    Socket clientSocket = serverSocket.accept();
//                    System.out.println("Yeni bir client bağlandı : " + clientSocket);

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

    /*
    protected void sendmsg(Msg msg, ObjectOutputStream outputstream) throws IOException {
        //  ilgili clienta mesaj gönder
        contact reciverContact = null;
        for (contact client : allClients) {
            if (client.telefon == msg.reciver) {
                reciverContact = client;
            }
        }
        if (reciverContact.state.equals("online")) {
            outputstream.writeObject("you have nwe msg");

            outputstream.writeObject(msg.sender);
            outputstream.writeObject(msg.content);

            System.out.println("from method " + "mesaj.equals(\"send msg\")");

        } else {
            for (chatRoom chat : reciverContact.allChat) {
                if (chat.chatContact == msg.sender) {
                    chat.newMsg.add(msg);
                }
            }
        }

    }*/
//    void chekIfFirstTimeOpeningChat(long senderNo, long reciverNo) {
//        boolean firstTimeOpeningChat = true;
//        contact reciverContact = null;
//        contact senderContact = null;
//        for (contact client : allClients) {
//            if (client.telefon == senderNo) {
//                for (chatRoom contact : client.allChat) {
//                    if (contact.chatContact == reciverNo) {
//                        firstTimeOpeningChat = false;
//                        break;
//
//                    }
//                }
//            }
//            // while looping if we found reciver client we will stor it , so if this chat getting opening for the first time we will need it 
//            if (client.telefon == reciverNo) {
//                reciverContact = client;
//                System.out.println("iam here in assining reciver ");
//            }
//            if (client.telefon == senderNo) {
//                senderContact = client;
//            }
//        }
//        if (firstTimeOpeningChat) {
//            senderContact.allChat.add(new chatRoom(reciverNo));
//
//            reciverContact.allChat.add(new chatRoom(senderNo));
//            System.out.println("from chaeking if chat firdr");
//        }
//
//    }
    protected void sendmsg(long senderNo, long reciverNo, Object msg, ObjectOutputStream senderClientOutput) throws IOException {
//        chekIfFirstTimeOpeningChat(senderNo, reciverNo);
//        for (contact client : allClients) {
//            if (client.telefon == senderNo) {
//                for (chatRoom contact : client.allChat) {
//                    if (contact.chatContact == reciverNo) {
//                        contact.msges.add((String) msg);
//                    }
//                }
//            }
//            if (client.telefon == reciverNo) {
//                for (chatRoom contact : client.allChat) {
//                    if (contact.chatContact == senderNo) {
//                        contact.msges.add((String) msg);
//                        System.out.println("added to archive of both");
//                        senderClientOutput.writeObject("added to archive of both");
//
//                    }
//                }
//            }
//        }

    }

    protected void sendBroadcast(String message, ObjectOutputStream outputstream) throws IOException {
        // bütün bağlı client'lara mesaj gönder
        for (contact client : allClients) {
            outputstream.writeObject("Server : " + message);
        }
    }

    protected void writeToHistory(Object message) {
        // server arayüzündeki allChat alanına mesajı yaz

        historyJList.setModel(model);

        // Initialize the list with items
        model.add(count, message);
        count++;
        //historyJTextPane.setText(historyJTextPane.getText() + "\n" + message);
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

    private byte[] convertToBytes(Object object) throws Exception {
        // parametre olarak alınan nesneyi byte dizisine çevirir
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();

        }
    }

    class ListenThread extends Thread implements Serializable {

        // dinleyeceğimiz client'ın soket nesnesi, input ve output stream'leri
        private final Socket clientSocket;
        private ObjectInputStream clientInput;
        private ObjectOutputStream clientOutput;
        private String UI;
        contact client_info;
        String variationQustionAnswer;

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
                    if (mesaj instanceof contact) {// cleinttan gelen msaj client objesi ise  
                        client_info = (contact) mesaj; // cleintin bilgilerini sakla 

                        writeToHistory(" : " + ((contact) mesaj).name + " " + ((contact) mesaj).telefon);

                    }
                    // client'in gönderdiği mesajı server ekranına yaz  

                    if (mesaj.equals("Creat Client") && client_info != null) {// when sing-up button is pressed the client will send to the server this msg 
                        creatClient(client_info, clientOutput);
                        onlineClientList.add(new OutputStreams(clientOutput, client_info.telefon));

                    }
                    if (mesaj.equals("log in")) {
                        long tel = (long)clientInput.readObject();
                        String pass = (String)clientInput.readObject();
                        
                        login(tel, pass, clientOutput);
                        onlineClientList.add(new OutputStreams(clientOutput, client_info.telefon));

                    }
                    // clientin attigi mesaji serverin arayuzune yaz 
                    if (mesaj instanceof File) {
                        writeToHistory(mesaj);//bilgisini yaz

                    }
                    if (mesaj instanceof ImageIcon) {
                        writeToHistory(mesaj);//bilgisini yaz

                    }
                    if (mesaj.toString().charAt(0) == '$') {
                        variationQustionAnswer = mesaj + "";
                    }
                    if (mesaj.equals("reset pass")) {
                        resetPass(client_info, variationQustionAnswer, clientOutput);
                    }
                    if (mesaj.equals("get variation qustion")) {
                        getVarietionQustion(client_info, clientOutput);
                    }
                    /* if (mesaj.equals("@-send msg for other client:")) {
                        Msg msg = (Msg) clientInput.readObject();
                        sendmsg(msg, clientOutput);
                    }*/
                    if (mesaj.equals("@-send string msg for other client:")) {
                        long sender = Long.parseLong(clientInput.readObject() + "");

                        long reciver = Long.parseLong(clientInput.readObject() + "");
                        Object msg = clientInput.readObject();

                        sendmsg(sender, reciver, msg, clientOutput);
                    }

//                    if (mesaj instanceof String && ((String) mesaj).contains("@tel-")) {
//                        clientOutput.writeObject("sending contact");
//                        String tel = ((String) mesaj).substring(5);
//                        
//                    }
                    if (mesaj.equals("@ add new contact")) {
                        mesaj = clientInput.readObject();

                        long contectTobeAdded = Long.parseLong(mesaj.toString());
                        addContactToClientContactList(client_info, contectTobeAdded, clientOutput);

                    }
                    if (mesaj.equals("@ my contacts  pleas")) {
                        long accountOwner = Long.parseLong(clientInput.readObject() + "");
                        sendContactList(accountOwner);
                    }
                }
                // baglanan clientin icin   bilgisini server arayuzune yazdiryorum

            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Hata - ListenThread : " + ex);
            } finally {
                try {
                    // client_info.state="offline";
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
                    for (contact client : allClients) {
                        if (client_info != null && client_info.telefon == client.telefon) {

                            client.state = "offline";
                        }
                    }
                    for (int i = 0; i < onlineClientList.size(); i++) {
                        if (onlineClientList.get(i).clientNo == client_info.telefon) {
                            onlineClientList.remove(i);
                        }
                    }

                    writeToHistory("Soket kapatıldı : " + clientSocket);
                } catch (IOException ex) {
                    System.out.println("Hata - Soket kapatılamadı : " + ex);
                }
            }
        }
    }

}
