package com.example.ang.dineanddate.Matches;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.ang.dineanddate.R;

import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolder>{

    //List of all of the current users matches
    private List<MatchesObject> matchesList;

    private Context context;

    //Function to pass the information
    public MatchesAdapter(List<MatchesObject> matchesList, Context context) {
        this.matchesList = matchesList;
        this.context = context;
    }

    @Override
    public MatchesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matches, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolder rcv = new MatchesViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(MatchesViewHolder holder, int position) {
        // The holder contains everything that has to do with that specific object
        // Position is the position of item matches
        holder.myMatchID.setText(matchesList.get(position).getUserId());
        holder.matchName.setText(matchesList.get(position).getName());
        if(!matchesList.get(position).getProfileImageUrl().equals("default"))
        {
            Glide.with(context).load(matchesList.get(position).getProfileImageUrl()).into(holder.matchImage);
        }


    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }
    // This is the class that will take the responsibility to populate item_match.xml

}
