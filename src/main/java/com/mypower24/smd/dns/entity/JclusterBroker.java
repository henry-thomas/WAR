/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.mypower24.smd.dns.entity;

import com.mypower24.smd.rar.lib.JcMessage;
import com.mypower24.smd.rar.lib.JcResponseMsg;
import com.mypower24.smd.rar.lib.JcServerDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author henry
 */
public class JclusterBroker {

    private final Map<String, ServerConnection> serConnMap = new HashMap<>();
    private final Map<String, JcServerDescriptor> serConnDescMap = new HashMap<>();
    private static JclusterBroker INSTANCE;
    public final static String SERVER_ID = "JCluster_Broker";

    public static JclusterBroker getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new JclusterBroker();
        }
        return INSTANCE;
    }

    public Map<String, ServerConnection> getSerConnList() {
        return serConnMap;
    }

    public String greetOnConnection() {
        return "Connected to JCluster Broker";
    }

    public JcMessage processMessage(JcMessage request, ServerConnection conn) {
//        System.out.println("Processing Command: " + request.getCommand());
//        JcMessage resp = new JcResponseMsg(request.getRequestId());

        switch (request.getCommand()) {
            case "Hello": {
                JcServerDescriptor desc = (JcServerDescriptor) request.getData();
                conn.setHost(desc.getServerAddress());
                conn.setServerId(desc.getServerId());

                ServerConnection addConnection = addConnection(conn, desc);
                if (addConnection == null) {
                    //Throw custom exception here
//                    System.out.println("Server Connection already exists. Closing this one.");
                    request.setInfo("connectionExists " + request.getRequestId());
                }
                request.setCommand("Connected");
                break;
            }
            case "test": {
                request.setCommand("processMessage");
                request.setInfo("response to request: " + request.getRequestId());
                request.setDstServer(request.getSrcServer());
                request.setSrcServer(SERVER_ID);
//                try {
//                    Thread.sleep(Math.abs((new Random(10)).nextInt(10)));
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(JclusterBroker.class.getName()).log(Level.SEVERE, null, ex);
//                }

                break;
            }

            default:
                System.out.println("Invalid Command");
                request.setInfo("Invalid Command");
        }
//        conn.sendData(conn);
        //Command should send serverId here so that it can be set
        return request;
    }

    private ServerConnection addConnection(ServerConnection conn, JcServerDescriptor desc) {
        if (serConnDescMap.get(desc.getServerId()) != null) {
            serConnMap.put(desc.getServerId(), conn);
            return null;
        }

        serConnDescMap.put(desc.getServerId(), desc);
        serConnMap.put(desc.getServerId(), conn);
        broadcastNewServer(conn);
        return conn;
    }

    public boolean removeConnection(ServerConnection conn) {
        serConnDescMap.remove(conn.getServerId());
        return serConnMap.remove(conn.getServerId(), conn);
    }

    public synchronized void broadcastNewServer(ServerConnection conn) {

        for (Map.Entry<String, ServerConnection> entry : serConnMap.entrySet()) {
            String serverId = entry.getKey();
            ServerConnection serverConnection = entry.getValue();

            try {
                JcMessage resp = new JcMessage("New Node Notification");
                resp.setData(getSerConnList());
                if (conn.getServerId() == null ? serverConnection.getServerId() != null : !conn.getServerId().equals(serverConnection.getServerId())) {
                    serverConnection.sendData(resp);
                }
            } catch (IOException ex) {
                Logger.getLogger(JclusterBroker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
