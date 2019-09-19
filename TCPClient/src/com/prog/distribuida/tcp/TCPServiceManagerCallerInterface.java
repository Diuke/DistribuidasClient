/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prog.distribuida.tcp;

import java.net.Socket;

/**
 *
 * @author edangulo
 */
public interface TCPServiceManagerCallerInterface {
    
    public void messageReceiveFromClient(Socket clientSocket, byte[] data);
    public void errorHasBeenThrown(Exception error);
    public void notify(String message, String type);
    
}
