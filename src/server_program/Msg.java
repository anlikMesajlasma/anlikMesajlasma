/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server_program;

/**
 *
 * @author HP
 */
public class Msg {
   Object content;
   boolean seen;

    public Msg(Long teleNo, Object content) {
       this.content = content;
    }
   
}
