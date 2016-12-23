package com.company;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class HttpsConvertor {

    public String toHttps(ArrayList<String> request) throws UnsupportedEncodingException{
        String[] reqSplit = request.get(0).split(" ");
        String url = reqSplit[1];
        //System.out.println("reqSplit[0]: " + reqSplit[0]);
        //System.out.println("url: " + url);
        if (reqSplit[0].equals("GET") || reqSplit[0].equals("CONNECT")) {
            if (!url.contains("http://")) {
                url = "http://" + url;
            }
            String encoded = URLEncoder.encode(url, "UTF-8");
            url = url.replaceAll("http://", "https://");
            //System.out.println("-----------" + url);
            return url;
        }
        return null;
    }
}
