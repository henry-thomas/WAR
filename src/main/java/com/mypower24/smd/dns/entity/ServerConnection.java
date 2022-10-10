/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.smd.dns.entity;

import com.mypower24.smd.rar.lib.JcMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author henry
 */
public class ServerConnection implements Runnable, Serializable {

    private String serverId;
    private int port;
    private String host;
    private Socket client;
    private ObjectOutputStream outWriter;
    private static int parallelConnectionCount = 0;
    private int paralConnWaterMark = 0;

    public ServerConnection(Socket client) {
        this.client = client;
        this.port = client.getPort();
        this.host = client.getInetAddress().getHostAddress();
        parallelConnectionCount++;
        if (parallelConnectionCount > paralConnWaterMark) {
            paralConnWaterMark = parallelConnectionCount;
            System.out.println("Number of connections reached: " + paralConnWaterMark);
        }
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

    public void sendData(JcMessage data) throws IOException {
        outWriter.writeObject(data);
        outWriter.flush();
    }

    @Override
    public void run() {
        JcMessage inline;
        JcMessage outline;

        ObjectInputStream ois;
        try {
            JclusterBroker broker = JclusterBroker.getINSTANCE();
            outWriter = new ObjectOutputStream(client.getOutputStream());
            InputStreamReader isr = new InputStreamReader(client.getInputStream());
            BufferedReader in = new BufferedReader(isr);

            ois = new ObjectInputStream(client.getInputStream());
            System.out.println("Client connected.");

            while ((inline = (JcMessage) ois.readObject()) != null) {

//                System.out.println("Received: " + inline);
                JcMessage processMessage = broker.processMessage(inline, this);
                sendData(processMessage);
                //Handle closing in the closing lifecycle hook in resource adapter
            }

            client.close();
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, "Closing connection: {0}", client.getPort());

//            JclusterBroker.getINSTANCE().removeConnection(this);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, "Closing connection: {0}", ex.getClass());
        }
        parallelConnectionCount--;
    }
}
