package com.example.listviewtest;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class http_Get {
    private static Context context;

    public static String getData(String myurl) throws IOException {
        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() != 200){
            return null;
            //throw new RuntimeException("请求超时");
        }
        else {
            InputStreamReader is = new InputStreamReader(conn.getInputStream());

            BufferedReader bufferedReader = new BufferedReader(is);

            StringBuilder strBuffer = new StringBuilder();

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {

                strBuffer.append(line);

            }

            String result = strBuffer.toString();
            is.close();

            conn.disconnect();
            return result;
        }
    }


}
