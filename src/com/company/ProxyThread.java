package com.company;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.security.Security;
import java.util.ArrayList;


public class ProxyThread extends Thread {

    private Socket socket;
    private String serverUrl;
    private int serverPort;
    private PrintStream toServer;
    private BufferedReader readBrowserReq;
    private HttpsConvertor httpsConvertor;
    private byte[] response = new byte[32768];
    private ArrayList<String> request;

    ProxyThread(Socket socketClient, String serverUrl, int serverPort) throws IOException {
        this.socket = socketClient;
        this.serverUrl = serverUrl;
        this.serverPort = serverPort;

        //initialize http to https convertor
        httpsConvertor = new HttpsConvertor();

        //request from user to browser
        toServer = new PrintStream(socketClient.getOutputStream());
        //request from browser to user
        readBrowserReq = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));

        //start proxy server
        this.start();
    }

    public void run() {
        try {
            //retrieves request
            //stored in an arrayList, where each element contains a line of the request
            request = new ArrayList<String>();
            outerloop:
            while (true) {
                String line = readBrowserReq.readLine();
                System.out.println("LL: " + line);
                if (line == null || line.equals("")) {
                    break outerloop;
                }
                request.add(line);
            }

            if (request.size() > 0) {
                String httpsUrl = httpsConvertor.toHttps((request));
                System.out.println("httpsURL: " + httpsUrl);

                if (httpsUrl != null) {
                    InputStream in = secureConnection(httpsUrl);
                    response = new byte[32768];
                    int index = in.read(response, 0, 32768);
                    while (index != -1) {
                        toServer.write(response, 0, index);
                        index = (in.read(response, 0, 32768));
                    }
                }
                socket.close();
            }
            //System.out.println("Succesfully connected!");
        } catch (FileNotFoundException e) {
            System.out.println("File not accessible at " + serverUrl + ":" + serverPort);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Exception at " + serverUrl + ":" + serverPort);
            e.printStackTrace();
        }

    }

    public InputStream secureConnection(String urlString) throws IOException {
        /*System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());*/
        //System.setProperty("http.agent", "Chrome");
        URL url = new URL(urlString);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        for (int i = 0; i < request.size(); i++) {
            String[] requestAttributes = request.get(i).split(" ", 2);
            if (requestAttributes.length == 2) {
                if (!requestAttributes[0].equals("Connection:") && !requestAttributes[0].equals("Accept-Encoding:") && !requestAttributes[0].equals("Proxy-Connection:")) {
                    conn.addRequestProperty(requestAttributes[0].substring(0, requestAttributes[0].length() - 1), requestAttributes[1]);
                }
            }
        }
        return conn.getInputStream();
    }


}
