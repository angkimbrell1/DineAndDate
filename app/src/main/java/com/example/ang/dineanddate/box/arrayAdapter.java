package com.example.ang.dineanddate.box;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ang.dineanddate.R;
import com.example.ang.dineanddate.box.box;

import java.util.List;

public class arrayAdapter extends ArrayAdapter<box>{
    // This array adapter is called to
    // populate the card presented to the user
    Context context;

    public arrayAdapter(Context context, int resourceID, List<box> items) {
        super(context, resourceID, items);
    }
    // populates the box presented to the user
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets everything from box.java
        box box_item = getItem(position);

        //Make sure the convert view is good
        if(convertView == null) {
            // Pass everything into the convert view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

        name.setText(box_item.getName());
        switch(box_item.getProfileImageUrl()){
            case "default":
                Glide.with(getContext()).load(R.mipmap.frenchfries).into(image);
                break;
            default:
                Glide.with(getContext()).load(box_item.getProfileImageUrl()).into(image);
                break;
        }


        return convertView;
    }
}
