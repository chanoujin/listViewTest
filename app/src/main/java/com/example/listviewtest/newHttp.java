package com.example.listviewtest;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class newHttp {
    //public static  String LOGIN_URL = "http://192.168.1.174:8082/Login/NewLogin";
    public static String LoginPost(JSONObject object,String LOGIN_URL){
        String msg = "";
        try {
            URL url = new URL(LOGIN_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty( "Content-Type", "application/json;charset=UTF-8");

            /*JSONObject object = new JSONObject();
            object.put("userCode", "000");
            object.put("userPwd", "123456");
            object.put("device_number","string");*/

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(object.toString());
            out.flush();
            if(conn.getResponseCode()==200){
                InputStream inputs =conn.getInputStream();
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = inputs.read(buffer))!=-1){
                    message.write(buffer,0,len);
                }
                inputs.close();
                message.close();
                JSONObject object1 = new JSONObject(message.toString());
                //msg = new String(message.toByteArray(),"UTF-8");
                return (String) object1.get("success");
            }

        }
        catch (Exception e){
            System.out.println(e);
        }
        return msg;
    }
}
