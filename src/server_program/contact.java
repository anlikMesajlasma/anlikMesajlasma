/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server_program;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import server_program.Chat;

/**
 *
 * @author HP
 */
public class Contact implements Serializable{
  protected long telefon;
   protected String name;
   protected ArrayList <Chat> allChat= new ArrayList<>();// client log-in yapttiktan sonra gostirilmek uzere 
   protected ArrayList <Contact> contacts= new ArrayList<>(); //clientin arkadslarini saklamak icin
   protected String state="-";//it could be :null (while singing up) log-in (if client not siged-out ) log out (if client registered but signed-out)
 
   protected String password;
   protected String variationQustion ;
   protected String answer ;
    public Contact() {
    }
    public Contact(long telphone_no, String name , String password) {
        this.telefon = telphone_no;
        this.name = name;
        this.password=password;
    }

    public Contact(long telefon, String name, ArrayList <Chat> allChat, ArrayList <Contact> contacts, String state) {
        this.telefon = telefon;
        this.name = name;
        this.allChat=allChat;
        this.contacts=contacts;
        this.state = state;
       
    }
     public Contact(long telphone_no, String name , String password, String variationQustion , String answer ) {
        this.telefon = telphone_no;
        this.name = name;
        this.password=password;
        this.variationQustion=variationQustion;
        this.answer=answer;
        this.allChat=new ArrayList<>();        
        this.contacts=new ArrayList<>();

    }
    
      public Contact(long telphone_no, String password) {
        this.telefon = telphone_no;
        this.password=password;
    }
     public Contact(long telphone_no ) {
        this.telefon = telphone_no;
    }
    

    public ArrayList<Chat> getallChat() {
        return allChat;
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public long getTelefon() {
        return telefon;
    }

   
    
    
    
 
    
    
    
}
