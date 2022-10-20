/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.smd.jcluster.entity;

import com.mypower24.smd.rar.lib.JcMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
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

    ObjectInputStream ois;
    private final Thread readThread;
    private final Map<Integer, JcMessage> reqRespMap = new HashMap<>();

    public ServerConnection(Socket client) {
        this.client = client;
        this.port = client.getPort();
        this.host = client.getInetAddress().getHostAddress();

        readThread = new Thread(this::readMessage);
        readThread.setName(ServerConnection.class.getSimpleName() + "-readThread");

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

    public void sendData(JcMessage data) {

        synchronized (data) {
            try {
                outWriter.writeObject(data);
                reqRespMap.put(data.getRequestId(), data);
                data.wait(2000);

                if (data.getResponse() == null) {
                    throw new IOException("No response received, timeout");
                }

            } catch (InterruptedException | IOException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void readMessage() {
        JcMessage readObject;

        try {
            while ((readObject = (JcMessage) ois.readObject()) != null) {
//                Object readObject = ois.readObject();
                if (readObject instanceof JcMessage) {
                    JcMessage response = (JcMessage) readObject;
                    JcMessage request = reqRespMap.remove(response.getRequestId());
                    if (request != null) {
                        synchronized (request) {
                            request.setResponse(response);
                            request.notifyAll();
                        }
                    }
                }
            }
            client.close();

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        JcMessage inline;
        JcMessage outline;

        try {
            JclusterBroker broker = JclusterBroker.getINSTANCE();
            outWriter = new ObjectOutputStream(client.getOutputStream());
            InputStreamReader isr = new InputStreamReader(client.getInputStream());
            BufferedReader in = new BufferedReader(isr);

            ois = new ObjectInputStream(client.getInputStream());
            System.out.println("Client connected.");
            readThread.start();

            JcMessage readObject;

            while ((readObject = (JcMessage) ois.readObject()) != null) {
//                Object readObject = ois.readObject();
                if (readObject instanceof JcMessage) {
                    JcMessage response = (JcMessage) readObject;
                    JcMessage request = reqRespMap.remove(response.getRequestId());
                    if (request != null) {
                        synchronized (request) {
                            request.setResponse(response);
                            request.notifyAll();
                        }
                    }
                }
            }
            client.close();

        } catch (IOException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, "Closing connection: {0}", ex.getClass());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        parallelConnectionCount--;
    }
}
