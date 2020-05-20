/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server_program;

import java.util.ArrayList;

/**
 *
 * @author HP
 */
public class Chat {
    long chatContact;

    ArrayList<String> msges ;

    public Chat(long chatContact ) {
        this.chatContact=chatContact;
        this.msges = new ArrayList<>();
    }


 
    public long getChatContact() {
        return chatContact;
    }

//    public ArrayList<Msg> getSeenSentMsg() {
//    }
//
//    public ArrayList<Msg> getNewMsg() {
//        return newMsg;
//    }

    
}
