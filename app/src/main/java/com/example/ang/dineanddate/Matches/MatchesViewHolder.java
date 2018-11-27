package com.example.ang.dineanddate.Matches;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ang.dineanddate.R;
import com.example.ang.dineanddate.chat.chatActivity;

public class MatchesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView myMatchID, matchName;
    public ImageView matchImage;
    public Bitmap profileImage;

    public MatchesViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        myMatchID = (TextView) itemView.findViewById(R.id.matchID);
        matchName = (TextView) itemView.findViewById(R.id.matchName);

        matchImage = (ImageView) itemView.findViewById(R.id.MatchImage);
        matchImage.buildDrawingCache();
        profileImage = matchImage.getDrawingCache();


    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), chatActivity.class);
        Bundle b = new Bundle();
        b.putString("matchID", myMatchID.getText().toString());
        b.putString("matchName", matchName.getText().toString());
        b.putParcelable("image", profileImage);
        intent.putExtras(b);
        view.getContext().startActivity(intent);

    }
}
