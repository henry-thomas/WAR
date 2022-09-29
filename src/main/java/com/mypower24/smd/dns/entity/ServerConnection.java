/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.smd.dns.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author henry
 */
public class ServerConnection implements Runnable {

    private String serverId;
    private int port;
    private String host;
    private Socket client;
    private PrintWriter outWriter;

    public ServerConnection(Socket client) {
        this.client = client;
        this.port = client.getPort();
        this.host = client.getInetAddress().getHostAddress();
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }
    
    public void sendData(Object data){
        outWriter.println(data);
    }

    @Override
    public void run() {
        String inline;
        String outline;
        try {
            SmdDns smdDns = SmdDns.getINSTANCE();
            outWriter = new PrintWriter(client.getOutputStream(), true);
            InputStreamReader isr = new InputStreamReader(client.getInputStream());
            BufferedReader in = new BufferedReader(isr);

            System.out.println("Client connected.");
            outWriter.println(smdDns.greetOnConnection());

            while ((inline = in.readLine()) != null) {
                System.out.println("Received: " + inline);
                outline = smdDns.processMessage(inline);
                outWriter.println(outline);
                
                //Handle this in the closing lifecycle hook in resource adapter
                if (outline.compareTo("closing") == 0) {
                    break;
                }
            }
            client.close();
        } catch (IOException ex) {
        }
    }
}
