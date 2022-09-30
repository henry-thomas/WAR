/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.smd.dns.entity;

import com.mypower24.smd.rar.lib.TestRequest;
import com.mypower24.smd.rar.lib.TestResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
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

    public void sendData(TestResponse data) throws IOException {
        outWriter.writeObject(data);
        outWriter.flush();
    }

    @Override
    public void run() {
        TestRequest inline;
        TestResponse outline;

        ObjectInputStream ois;
        try {
            SmdDns smdDns = SmdDns.getINSTANCE();
            outWriter = new ObjectOutputStream(client.getOutputStream());
            InputStreamReader isr = new InputStreamReader(client.getInputStream());
            BufferedReader in = new BufferedReader(isr);

            ois = new ObjectInputStream(client.getInputStream());
            System.out.println("Client connected.");
            outWriter.writeObject(new TestResponse(smdDns.greetOnConnection()));
            smdDns.addConnection(this);

            while ((inline = (TestRequest) ois.readObject()) != null) {
                System.out.println("Received: " + inline);
                String processMessage = smdDns.processMessage(inline, this);
                sendData(new TestResponse(processMessage));

                //Handle this in the closing lifecycle hook in resource adapter
                if (processMessage.compareTo("closing") == 0) {
                    break;
                }
            }
            smdDns.removeConnection(this);
            client.close();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
