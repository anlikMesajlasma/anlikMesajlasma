/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server_program;

import java.io.ObjectOutputStream;

/**
 *
 * @author HP
 */
public class OutputStreams {
    ObjectOutputStream clientoutput;
    long clientNo;

    public OutputStreams() {
    }

    public OutputStreams(ObjectOutputStream clientoutput, long clientNo) {
        this.clientoutput = clientoutput;
        this.clientNo = clientNo;
    }
    
}
