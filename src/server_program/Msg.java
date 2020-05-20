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
  Long sender ;
  Long reciver ;

    public Msg(Object content, Long sender, Long reciver) {
        this.content = content;
        this.seen = seen;
        this.sender = sender;
        this.reciver = reciver;
    }
  
    public Msg(Long senderTel, Object content) {
       this.content = content;
       this.sender=senderTel;
       
    }

    public Object getContent() {
        return content;
    }
    
   
}
