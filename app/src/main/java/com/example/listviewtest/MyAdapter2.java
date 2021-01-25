package com.example.listviewtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import static com.example.listviewtest.R.layout.list_item3;

public class MyAdapter2 extends BaseAdapter {
    private Context mContext2;
    private LinkedList<Data2> mData2;
    public MyAdapter2(){}
    public MyAdapter2(LinkedList<Data2> mData2,Context mContext2){
        this.mData2 = mData2;
        this.mContext2 = mContext2;
    }
    public void add(Data2 data2){
        if(mData2 == null){
            mData2 = new LinkedList<>();
        }
        mData2.add(data2);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData2.size();
    }

    @Override
    public Object getItem(int position) {
        return mData2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){

            convertView = LayoutInflater.from(mContext2).
                    inflate(list_item3,parent,false);
            holder = new ViewHolder();
            holder.mModel = convertView.findViewById(R.id.model);
            holder.mNum = convertView.findViewById(R.id.num);
            holder.mPlan_sub_no = convertView.findViewById(R.id.plan_sub_no);
            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.mModel.setText(mData2.get(position).getmModel());
        holder.mNum.setText(mData2.get(position).getmNum());
        holder.mPlan_sub_no.setText(mData2.get(position).getmPlan_sub_no());
        return convertView;
    }
    static class ViewHolder{
        TextView mModel;
        TextView mNum;
        TextView mPlan_sub_no;

    }
}
