package com.example.ang.dineanddate.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ang.dineanddate.R;

public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView message;
    public LinearLayout container;
    public ChatViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        message = itemView.findViewById(R.id.message);
        container = itemView.findViewById(R.id.container);
    }

    @Override
    public void onClick(View view) {
    }
}
