/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server_program;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author HP
 */
public class Contact  implements Serializable{
    long telefon;
    String name;
    ArrayList <Chat> allChat=new ArrayList<>();// client log-in yapttiktan sonra gostirilmek uzere 
    ArrayList <Contact> contacts=new ArrayList<>(); //clientin arkadslarini saklamak icin
    String state;//it could be :null (while singing up) log-in (if client not siged-out ) log out (if client registered but signed-out)
    ObjectOutputStream  outputstream;
    ObjectInputStream   inputStream;
    String password;

    public Contact() {
    }
    public Contact(long telphone_no, String name , String password) {
        this.telefon = telphone_no;
        this.name = name;
        this.password=password;
    }
    
 
      public Contact(long telphone_no, String password) {
        this.telefon = telphone_no;
        this.password=password;
    }
    
    
    
}
