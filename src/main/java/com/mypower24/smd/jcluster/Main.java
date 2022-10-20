/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.smd.jcluster;

import com.mypower24.smd.jcluster.entity.ServerConnection;
import com.mypower24.smd.jcluster.entity.JclusterBroker;
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

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        Thread serverThread = new Thread() {
            @Override
            public void run() {
                try ( ServerSocket server = new ServerSocket(4004)) {
                    String appId = args.length > 0 ? args[0] : "Server-1";
                    Integer port = args.length > 1 ? Integer.valueOf(args[1]) : 4004;

                    System.out.println("JCluster Broker Open on port " + port);

                    while (!Thread.currentThread().isInterrupted()) {
                        Socket client = server.accept();
                        Thread sthread = new Thread(new ServerConnection(client));
                        sthread.setName(client.getInetAddress().getHostAddress() + "-" + client.getPort());
                        sthread.start();
                    }

                    server.close();

                } catch (IOException ex) {
                    Logger.getLogger(JclusterBroker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        };

        serverThread.setName("serverThread");
        serverThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    LOG.severe("Shutting down server");
                    serverThread.interrupt();
                    serverThread.join();
                    //handle closing socket here
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });

    }
}
