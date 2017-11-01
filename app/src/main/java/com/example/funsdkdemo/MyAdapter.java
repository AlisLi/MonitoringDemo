package com.example.funsdkdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Lizhiguo on 2017/9/26.
 */

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<String> lists;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view,int position);

        void onItemLongClick(View view,int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){  //向外提供的一个监听方法
        this.mOnItemClickListener = listener;
    }

    public MyAdapter(Context context,List<String> lists){
        this.context = context;
        this.lists = lists;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.start_play_item,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {//绑定ViewHolder
        holder.mTextView.setText(lists.get(position));
        if(mOnItemClickListener != null){//如果设置了监听那么它就不为空，然后回调相应的方法
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView,position);//得到当前点击的item的位置position
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    int postion = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, postion);
                    return true;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }
}


class MyViewHolder extends RecyclerView.ViewHolder{
    TextView mTextView;

    public MyViewHolder(View itemView) {
        super(itemView);
        mTextView = (TextView) itemView.findViewById(R.id.play_ip_txt);
    }
}