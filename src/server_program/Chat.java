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
    long acountOwner;
    long chatContact;

    ArrayList<Msg> seenSentMsg ;
    ArrayList<Msg> newMsg ;

    public Chat(long acountOwner ,long chatContact) {
        this.acountOwner = acountOwner;
        this.seenSentMsg = new ArrayList<>();
        this.newMsg=new ArrayList<>();
    }

    public long getAcountOwner() {
        return acountOwner;
    }

 
    public long getChatContact() {
        return chatContact;
    }

    public ArrayList<Msg> getSeenSentMsg() {
        return seenSentMsg;
    }

    public ArrayList<Msg> getNewMsg() {
        return newMsg;
    }

    
}
