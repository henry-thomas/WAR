/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.mypower24.smd.dns.entity;

import com.mypower24.smd.rar.lib.TestRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author henry
 */
public class SmdDns {

    private final List<ServerConnection> serConnList = new ArrayList<>();
    private static SmdDns INSTANCE;

    public static SmdDns getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new SmdDns();
        }
        return INSTANCE;
    }

    public List<ServerConnection> getSerConnList() {
        return serConnList;
    }

    public String greetOnConnection() {
        return "Connected to SMD DNS";
    }

    public String processMessage(TestRequest command, ServerConnection conn) {
        System.out.println("Processing Command: " + command);
//        switch (command.getReqHeader()) {
//            case val:
//                
//                break;
//            default:
//                throw new AssertionError();
//        }
//        conn.sendData(conn);
        //Command should send serverId here so that it can be set
        return "Message Processed";
    }

    public ServerConnection addConnection(ServerConnection conn) {
        if (serConnList.contains(conn)) {
            return null;
        }
        broadcastNewServer(conn);
        serConnList.add(conn);
        return conn;
    }

    public boolean removeConnection(ServerConnection conn) {
        return serConnList.remove(conn);
    }

    public synchronized void broadcastNewServer(ServerConnection conn) {
//        for (ServerConnection serverConnection : serConnList) {
//            try {
//                serverConnection.sendData(conn);
//                conn.sendData(serverConnection);
//            } catch (IOException ex) {
//                Logger.getLogger(SmdDns.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }
}
