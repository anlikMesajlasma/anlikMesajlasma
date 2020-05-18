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

    ArrayList<OutputStreams> objectOutputStreams = new ArrayList<>();
    private ServerSocket serverSocket;
    private javax.swing.JList historyJList;
    private Thread serverThread;
    private  ArrayList<Contact> allClients = new ArrayList<>();
    private static DefaultListModel model = new DefaultListModel();
    private static int count = 0;

    protected void creatClient(Contact newContact, ObjectOutputStream outputstream) throws IOException {

        boolean exsit = false;
        for (Contact client : allClients) {// check if server has this client by phone no which is the uniq about every client
            if (client.telefon == newContact.telefon) {
                exsit = true;
            }
        }
        if (!exsit) {//bu tel numarasi ile daha once kayitli olan clientimiz yoksa eger 

            newContact.state = "online";// cleintin durumunu degistir 
            allClients.add(newContact);// cleinti ekle 
            outputstream.writeObject("Created");// clientin outputstream'ini kullanarak kendisine   eklendi diye bi cevap yolla 
            objectOutputStreams.add(new OutputStreams(outputstream, newContact.telefon));
            System.out.println("newContact.outputstream.writeObject(\"Created\")");
            outputstream.writeObject(newContact);
            System.out.println("sent Contact");
        } else {//varsa 

            outputstream.writeObject("This telefon already exist!");// clientin outputstream'ini kullanarak kendisine   eklenmedi diye bi cevap yolla 

        }
    }

    protected void login(Contact client, ObjectOutputStream outputstream) throws IOException {
        Contact contact = null;
        boolean exsit = false;
        String pasw = "";
        for (Contact clientt : allClients) {// check if server has this client by phone no
            if (clientt.telefon == client.telefon) {
                exsit = true;
                pasw = clientt.password;
                contact = clientt;
                break;
            }
        }
        if (exsit) {//bu tel numarasi ile daha once kayitli olan clientimiz yoksa eger 
            if (client.password.equals(pasw)) {
                client.state = "online";// cleintin durumunu degistir 
                outputstream.writeObject("Sucessfully log-in");// clientin outputstream'ini kullanarak kendisine   "basrili giris " diye bi cevap yolla 
                //client.outputstream.writeObject(client.getContacts());
                outputstream.writeObject(client);// sending acoountOwner info

            } else {
                System.out.println("pas1" + pasw + "pas2" + client.password);
                outputstream.writeObject("invalid password !");
            }
        } else {//yoksa

            outputstream.writeObject("No client with this tel no ");// clientin outputstream'ini kullanarak kendisine   eklenmedi diye bi cevap yolla 
        }
    }

    protected void getVarietionQustion(Contact client, ObjectOutputStream outputstream) throws IOException {

        boolean exsit = false;
        String qustion = "";
        for (Contact clientt : allClients) {// check if server has this client by phone no
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

    void addContactToClientContactList(Contact cont, long contactTobeAdded, ObjectOutputStream outputstream) throws IOException {
        System.out.println("fron server adding contatct");
        for (Contact client : allClients) {
            if (client.getTelefon() == cont.telefon) {
                if (client.telefon == cont.telefon) {
                    boolean exsit = false;
                    for (Contact contact : client.getContacts()) {
                        System.out.println("contact.getTelefon(): " + contact.getTelefon());
                        if (contact.getTelefon() == contactTobeAdded) {
                            exsit = true;
                            break;
                        }
                    }
                    if (!exsit) {
                        client.getContacts().add(new Contact(contactTobeAdded));

                        try {
                            outputstream.writeObject("@ contact added");
                            System.out.println(client);
                            for (Contact contact : client.getContacts()) {
                                System.out.println(cont+" : "+contact.telefon);
                            }
                            outputstream.writeObject(client.getContacts());

                        } catch (IOException ex) {
                            Logger.getLogger(TCP_Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        
                        outputstream.writeObject("You have this contact already !");
                        outputstream.writeObject(client.getContacts());
                    }
                }
             break;
            }
        }

    }

    protected void resetPass(Contact client, String answer, ObjectOutputStream outputstream) throws IOException {

        boolean exsit = false;
        String pasw = "";
        String answerOfclientInlist = "";
        for (Contact clientt : allClients) {// check if server has this client by phone no
            if (clientt.telefon == client.telefon) {
                exsit = true;
                pasw = "0" + clientt.password;
                answerOfclientInlist = clientt.answer;
                break;
            }
        }
        if (exsit) {//bu tel numarasi ile daha once kayitli olan clientimiz varsa eger 
            if (answerOfclientInlist.equals(answer.substring(1))) {
                System.out.println(answer);
                outputstream.writeObject("0" + pasw);
            } else {
                outputstream.writeObject("Wrong answer !");
            }
        } else {//yoksa
            outputstream.writeObject("No client with this tel no ");// clientin outputstream'ini kullanarak kendisine   eklenmedi diye bi cevap yolla 
        }
    }

    protected void searchClient(String tel, Contact client_info, ObjectOutputStream outputstream) {
        try {
            boolean Found = false;
            for (Contact contact : allClients) {
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

    protected void sendmsg(Msg msg, ObjectOutputStream outputstream) throws IOException {
        //  ilgili clienta mesaj gönder
        Contact targetClient = null;
        for (Contact client : allClients) {
            if (client.telefon == msg.reciver) {
                targetClient = client;
            }
        }
        if (targetClient.state.equals("online")) {
            outputstream.writeObject("you have nwe msg");

            outputstream.writeObject(msg.sender);
            outputstream.writeObject(msg.content);

            System.out.println("from method " + "mesaj.equals(\"send msg\")");

        } else {
            for (Chat chat : targetClient.allChat) {
                if (chat.chatContact == msg.sender) {
                    chat.newMsg.add(msg);
                }
            }
        }

    }

    protected void sendmsg(long senderNo, long reciverNo, Object msg) throws IOException {
        //  ilgili clienta mesaj gönder
        Contact targetClient = null;
        for (Contact client : allClients) {
            if (client.telefon == reciverNo) {
                targetClient = client;
            }
        }
        if (targetClient.state.equals("online")) {
            for (OutputStreams objectOutputStream : objectOutputStreams) {
                if(objectOutputStream.clientNo==senderNo){
                objectOutputStream.clientoutput.writeObject("you have nwe msg");

                objectOutputStream.clientoutput.writeObject(senderNo);
                objectOutputStream.clientoutput.writeObject(msg);}

            }

            System.out.println("from method " + "mesaj.equals(\"send msg\")");

        } else {

        }

    }

    protected void sendBroadcast(String message, ObjectOutputStream outputstream) throws IOException {
        // bütün bağlı client'lara mesaj gönder
        for (Contact client : allClients) {
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
        Contact client_info;
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
                    if (mesaj instanceof Contact) {// cleinttan gelen msaj client objesi ise  
                        client_info = (Contact) mesaj; // cleintin bilgilerini sakla 

                        writeToHistory(" : " + ((Contact) mesaj).name + " " + ((Contact) mesaj).telefon);

                    }
                    // client'in gönderdiği mesajı server ekranına yaz  

                    if (mesaj.equals("Creat Client") && client_info != null) {// when sing-up button is pressed the client will send to the server this msg 
                        System.out.println(client_info.name);
                        creatClient(client_info, clientOutput);
                    }
                    if (mesaj.equals("log in")) {
                        System.out.println("log in ");
                        login(client_info, clientOutput);
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
                    if (mesaj.equals("@-send msg for other client:")) {
                        Msg msg = (Msg) clientInput.readObject();
                        sendmsg(msg, clientOutput);
                    }
                    if (mesaj.equals("@-send string msg for other client:")) {
                        long sender = Long.parseLong(clientInput.readObject() + "");
                        long reciver = Long.parseLong(clientInput.readObject() + "");
                        Object msg = clientInput.readObject();

                        sendmsg(sender, reciver, msg);
                    }

//                    if (mesaj instanceof String && ((String) mesaj).contains("@tel-")) {
//                        clientOutput.writeObject("sending contact");
//                        String tel = ((String) mesaj).substring(5);
//                        
//                    }
                    if (mesaj.equals("@ add new contact")) {
                        mesaj = clientInput.readObject();
                        System.out.println("@ add new contact" + mesaj);

                        long contectTobeAdded = Long.parseLong(mesaj + "");
                        // searchClient(tel, client_info,clientOutput);
                        // System.out.println("size befor:" + client_info.contacts.size());
                        addContactToClientContactList(client_info, contectTobeAdded, clientOutput);

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
                    writeToHistory("Soket kapatıldı : " + clientSocket);
                } catch (IOException ex) {
                    System.out.println("Hata - Soket kapatılamadı : " + ex);
                }
            }
        }
    }

}
