package com.example.listviewtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;

import static com.example.listviewtest.R.layout.list_item;

public class MyAdapter extends BaseAdapter {
    private Context mContext;
    private LinkedList<Data>mData;
    public MyAdapter(){}
    public MyAdapter(LinkedList<Data> mData,Context mContext){
        this.mData = mData;
        this.mContext = mContext;
    }
    public void add(Data data){
        if(mData == null){
            mData = new LinkedList<>();
        }

        mData.add(data);
        notifyDataSetChanged();
    }
    public void remove(int position) {
        if(mData != null) {
            mData.remove(position);
        }
        notifyDataSetChanged();
    }
    public void ColorChange(int position){


    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).
                    inflate(list_item,parent,false);
            holder = new ViewHolder();
            holder.mWeight = (TextView) convertView.findViewById(R.id.weight);
            holder.mMsgID = (TextView) convertView.findViewById(R.id.mMsgID);
            holder.mTheID = (TextView)convertView.findViewById(R.id.TheID);
            holder.mNum = (TextView)convertView.findViewById(R.id.num);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mWeight.setText(mData.get(position).getWeight());//重量
        holder.mMsgID.setText(mData.get(position).getMsgid());//产品名称
        holder.mTheID.setText(mData.get(position).getTheid());//材料号
        holder.mNum.setText(mData.get(position).getNum());
        return convertView;
    }
    static class ViewHolder{

        TextView mWeight;
        TextView mMsgID;
        TextView mTheID;
        TextView mNum;
    }


}
