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
    Contact target;
    ArrayList<Msg> seenSentMsg ;
    ArrayList<Msg> newMsg ;

    public Chat(Contact target) {
        this.target = target;
        this.seenSentMsg = new ArrayList<>();
        this.newMsg=new ArrayList<>();
    }

    
}
