/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prog.distribuida.utils;

import com.prog.distribuida.download.Downloader;
import com.prog.distribuida.tcp.ClientSocketManager;
import com.prog.distribuida.tcp.TCPServiceManagerCallerInterface;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import javax.swing.JTextArea;

/**
 *
 * @author juan-
 */
public class Loader extends Thread{
    
    ClientSocketManager clientSocketManager;
    String type;
    File file;
    
    Downloader downloader;
    String fileName;
    TCPServiceManagerCallerInterface caller;
    String server;
    String port;
    
    public Loader(String ip, String port, TCPServiceManagerCallerInterface caller, File file) {
        clientSocketManager = new ClientSocketManager(ip, Integer.parseInt(port), caller);
        this.file = file;
        this.type = "UPLOAD";
        start();
    }
    
    public Loader(String fileName, String server, String port, TCPServiceManagerCallerInterface caller){
        this.fileName = fileName;
        this.server = server;
        this.port = port;
        this.downloader = new Downloader();
        this.caller = caller;
        this.type = "DOWNLOAD";
        start();
    }
    
    
    
    @Override
    public void run() {
        if(type.equals("DOWNLOAD")){
            try {
                InputStream is = this.downloader.downloadFileFromServer(server, port, fileName, caller);
                FileOutputStream fos = new FileOutputStream(new File(fileName));
                int inByte;
                long currentBytes = 0;
                long mod = 1024;
                long multiplier = 100;
                String units = "Bytes";
                while((inByte = is.read()) != -1){
                    currentBytes++;
                    if(currentBytes % (mod*multiplier) == 0){
                        caller.notify("Downloaded " + (currentBytes) + " " + units + " from " + fileName, Constants.DOWNLOAD);
                    }
                    fos.write(inByte);
                }
                is.close();
                fos.close();
                caller.notify("File" + fileName + " finished downloading correctly", Constants.DOWNLOAD);
                caller.notify(fileName + " Total size: " + (currentBytes) + " " + units, Constants.DOWNLOAD);
            } catch (Exception e) {
                file.delete();
                e.printStackTrace();
            }
            
        } else if(type.equals("UPLOAD")){
            clientSocketManager.sendFile(this.file);
        }
    }
}
