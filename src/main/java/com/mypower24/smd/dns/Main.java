/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.smd.dns;

import com.mypower24.smd.dns.entity.ServerConnection;
import com.mypower24.smd.dns.entity.SmdDns;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author henry
 */
public class Main {

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(4004);
            System.out.println("SMD DNS Open on port 4004");

            while (true) {
                Socket client = server.accept();
                Thread sthread = new Thread(new ServerConnection(client));
                sthread.setName(client.getInetAddress().getHostAddress());
                sthread.start();
            }
            
        } catch (IOException ex) {
            Logger.getLogger(SmdDns.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
