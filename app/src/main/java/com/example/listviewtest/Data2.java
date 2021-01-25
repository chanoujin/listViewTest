package com.example.listviewtest;

public class Data2 {
    String mModel;
    String mNum;
    String mPlan_sub_no;
    Data2(String mModel,String mNum,String mPlan_sub_no){
        this.mModel = mModel;
        this.mNum = mNum;
        this.mPlan_sub_no = mPlan_sub_no;

    }

    public String getmModel() {
        return mModel;
    }

    public String getmNum() {
        return mNum;
    }

    public String getmPlan_sub_no() {
        return mPlan_sub_no;
    }

    public void setmModel(String mModel) {
        this.mModel = mModel;
    }

    public void setmNum(String mNum) {
        this.mNum = mNum;
    }

    public void setmPlan_sub_no(String mPlan_sub_no) {
        this.mPlan_sub_no = mPlan_sub_no;
    }
}
