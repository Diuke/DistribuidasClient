/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prog.distribuida.tcp;

import com.prog.distribuida.models.FilePart;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Del cliente
 * @author pjduque
 */
public class ClientSocketManager extends Thread {

    public static final int PARTITION_SIZE = 1500;

    Socket clientSocket;
    ObjectInputStream reader;
    ObjectOutputStream writer;
    boolean isEnable = true;
    private TCPServiceManagerCallerInterface caller;

    private String serverIpAddress;
    private int port;

    final Object mutex = new Object();

    public void waitForAWhile() {
        try {
            synchronized (mutex) {
                mutex.wait();
            }
        } catch (Exception ex) {
            caller.errorHasBeenThrown(ex);
        }
    }

    public void notifyMutex() {
        try {
            synchronized (mutex) {
                mutex.notify();
            }
        } catch (Exception ex) {
            caller.errorHasBeenThrown(ex);
        }
    }

    public ClientSocketManager(TCPServiceManagerCallerInterface caller) {
        this.caller = caller;
        this.start();
    }

    public ClientSocketManager(Socket clientSocket, TCPServiceManagerCallerInterface caller) {
        this.clientSocket = clientSocket;
        this.caller = caller;
        this.start();
    }

    public ClientSocketManager(String serverIpAddress, int port, TCPServiceManagerCallerInterface caller) {
        this.serverIpAddress = serverIpAddress;
        this.port = port;
        this.caller = caller;
        initializeSocket();
        this.start();
    }

    public void assignSocketToThisThread(Socket socket) {
        this.clientSocket = socket;
        
        this.notifyMutex();
    }

    public boolean initializeSocket() {
        try {
            this.clientSocket = new Socket(serverIpAddress, port);
            this.initializeStreams();
            this.caller.notify("Conexión lista");
            return true;
        } catch (Exception ex) {
            caller.errorHasBeenThrown(ex);
        }
        return false;
    }

    public boolean initializeStreams() {
        try {
            if (clientSocket == null) {
                if (!initializeSocket()) {
                    return false;
                }
            }
            reader = new ObjectInputStream(clientSocket.getInputStream());
            writer = new ObjectOutputStream(clientSocket.getOutputStream());
            return true;
        } catch (Exception error) {
            caller.errorHasBeenThrown(error);
        }
        return false;
    }

    @Override
    public void run() {
        try {
            while (isEnable) {
                if (clientSocket == null) {
                    clearLastSocket();
                    this.waitForAWhile();
                }
            }
        } catch (Exception error) {
            caller.errorHasBeenThrown(error);
        }
    }

    public void sendFile(File file) {
        FilePart fp = new FilePart((int)Math.ceil((double)file.length() / (double)PARTITION_SIZE), null, file.getName()); 
        sendMessage(fp);
        System.out.println("Part number");
        long currentBytes = 0;
        long fileSize = file.length();
        
        byte[] partData = new byte[PARTITION_SIZE];
        int bytesAmount = 0;
        try {
            FileInputStream fis = new FileInputStream(file);
            int i = 0;
            while ((bytesAmount = fis.read(partData)) > 0) {
                //write each chunk of data into separate file with different number in name
                currentBytes += bytesAmount;
                FilePart part = new FilePart(i, partData, file.getName());
                sendMessage(part);
                System.out.println("part " + i);
                i++;
                int newPartSize = (fileSize - currentBytes) < PARTITION_SIZE ? (int)(fileSize - currentBytes) : PARTITION_SIZE;
                partData = new byte[newPartSize];
            }
            this.clientSocket.close();
            this.clientSocket = null;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void sendMessage(FilePart message) {
        try {
            if (clientSocket.isConnected()) {

                writer.writeObject(message);
                //writer.flush();
            }
        } catch (Exception error) {
            caller.errorHasBeenThrown(error);
        }
    }

    private void clearLastSocket() {
        try {
            writer.close();
        } catch (Exception ex) {
            caller.errorHasBeenThrown(ex);
        }
        try {
            reader.close();
        } catch (Exception ex) {
            caller.errorHasBeenThrown(ex);
        }
        try {
            clientSocket.close();
        } catch (Exception ex) {
            caller.errorHasBeenThrown(ex);
        }
        clientSocket = null;
    }

    public boolean isThisThreadBusy() {
        return clientSocket != null;
    }

}
