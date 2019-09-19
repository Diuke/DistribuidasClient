/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prog.distribuida.download;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prog.distribuida.models.Response;
import com.prog.distribuida.tcp.TCPServiceManagerCallerInterface;
import com.prog.distribuida.utils.Constants;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JTextArea;



import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author juan-
 */
public class Downloader {

    public static final String USER_AGENT = "Mozilla/5.0";
    Gson gson;

    public Downloader() {
        gson = new Gson();
    }
    
    
    public ArrayList<String> getFileList(String server, String port, TCPServiceManagerCallerInterface caller){
        try {
            String url = "http://" + server + ":" + port + "/WebPool/api/files";

            HttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(url);

            // add request header
            request.addHeader("User-Agent", USER_AGENT);

            HttpResponse response = client.execute(request);
            if(response.getStatusLine().getStatusCode() == 404){
                caller.notify("Error 404 Not Found", Constants.DOWNLOAD);
                return null;
            }

            Scanner sc = new Scanner(response.getEntity().getContent());
            String data = "";
            while(sc.hasNext()) {
               data += sc.nextLine();
            }
            Response respObject;
            ArrayList<String> files = new ArrayList<>();
            
            respObject = gson.fromJson(data, new TypeToken<Response>() {}.getType());
            files = gson.fromJson(respObject.getData(), new TypeToken<ArrayList<String>>() {}.getType());
            
            caller.notify("List updated", Constants.DOWNLOAD);
            return files;
        } catch (Exception e) {
            caller.notify("Error al obtener la lista", Constants.DOWNLOAD);
            e.printStackTrace();
            return null;
        }
    }

    public InputStream downloadFileFromServer(String server, String port, String filename, TCPServiceManagerCallerInterface caller) throws Exception {
        
        filename.replaceAll(" ", "%20");
        String url = "http://" + server + ":" + port + "/WebPool/api/files" + "/" + filename;

        HttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);

        System.out.println("[LBS] Requesting " + filename + " to server@" + server);

        HttpResponse response = client.execute(request);

        return response.getEntity().getContent();
    }
}

