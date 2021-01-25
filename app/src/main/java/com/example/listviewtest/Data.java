package com.example.listviewtest;

public class Data {

    private String weight;
    private String msgid;
    private String theid;
    private String num;

    Data( String weight,String msgid,String theid,String num){

        this.weight = weight;
        this.msgid = msgid;
        this.theid = theid;
        this.num = num;
    }
    public String getWeight(){
        return weight;
    }
    public void setWeight(String weight){
        this.weight = weight;
    }
    public String getMsgid(){
        return msgid;
    }
    public void setMsgid(String msgid){this.msgid = msgid;}
    public String getTheid(){
        return theid;
    }
    public void setTheid(String theid){
        this.theid = theid;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}

