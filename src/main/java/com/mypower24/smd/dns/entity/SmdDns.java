/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.mypower24.smd.dns.entity;

import java.util.ArrayList;
import java.util.List;

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

    public String processMessage(String command) {
        System.out.println("Processing Command: " + command);
        //Command should send serverId here so that it can be set
        return "Successfully Connected";
    }

    public boolean addConnection(ServerConnection conn) {
        if (serConnList.contains(conn)) {
            return false;
        }
        broadcastNewServer(conn);
        serConnList.add(conn);
        return true;
    }

    public boolean removeConnection(ServerConnection conn) {
        return serConnList.remove(conn);
    }

    public void broadcastNewServer(ServerConnection conn) {
        for (ServerConnection serverConnection : serConnList) {
            serverConnection.sendData(conn);
            conn.sendData(serverConnection);
        }
    }
}
