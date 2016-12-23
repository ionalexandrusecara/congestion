package com.company;

import java.net.*;

public class ProxyServer {
    public static void main(String[] args) {
        try {
            String host = "localhost";
            int localport = 8000;
            System.out.println("Proxy server started at port: " + localport);
            ServerSocket server = new ServerSocket(localport);
            while (true) {
                new ProxyThread(server.accept(), host, localport);
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println(e.getMessage());
            System.err.println(e.getStackTrace());
        }
    }
}

