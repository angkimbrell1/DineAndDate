package com.example.ang.dineanddate.chat;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.ang.dineanddate.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder>{

    private List<ChatObject> chatList;
    private Context context;


    public ChatAdapter(List<ChatObject> chatList, Context context){
        this.chatList = chatList;
        this.context = context;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatViewHolder rcv = new ChatViewHolder(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        holder.message.setText(chatList.get(position).getMessage());
        if(chatList.get(position).getCurrentUser()){
            holder.message.setGravity(Gravity.END);
            holder.message.setTextColor(Color.parseColor("#404040"));
            holder.container.setBackgroundColor(Color.parseColor("#F4F4F4"));
        }else{
            holder.message.setGravity(Gravity.START);
            holder.message.setTextColor(Color.parseColor("#FFFFFF"));
            holder.container.setBackgroundColor(Color.parseColor("#80A8FF"));
        }

    }

    @Override
    public int getItemCount() {
        return this.chatList.size();
    }
    // This is the class that will take the responsibility to populate item_match.xml

}
