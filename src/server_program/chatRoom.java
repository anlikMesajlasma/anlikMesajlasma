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
public class chatRoom {
    private contact firstContact;
    private contact secondContact;
    private ArrayList<msg> msges ;
    

    public chatRoom(contact firstContact, contact secondContact) {
        this.firstContact = firstContact;
        this.secondContact = secondContact;
    }

    
}
