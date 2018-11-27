package com.example.ang.dineanddate.chat;

import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.example.ang.dineanddate.Matches.MatchesAdapter;
import com.example.ang.dineanddate.Matches.MatchesObject;
import com.example.ang.dineanddate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class chatActivity extends AppCompatActivity {
    private RecyclerView myRecyclerView;
    private RecyclerView.Adapter theChatAdapter;
    private RecyclerView.LayoutManager chatLayoutManager;

    private EditText sendMessage;

    private Button sendButton;

    private TextView matchNameText;

    private String currentUserID, matchID, chatID, matchName;



    private ImageView profileImage;

    DatabaseReference chatDBUsers, chatDBChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatDBChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        matchID = getIntent().getExtras().getString("matchID");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        matchName = getIntent().getExtras().getString("matchName");
        profileImage = (ImageView) findViewById(R.id.profileImageTop);
        Bundle extras = getIntent().getExtras();
        Bitmap bmp = (Bitmap) extras.getParcelable("image");



        getUserGender();

        //Set text and name at top
        matchNameText = findViewById(R.id.username);
        matchNameText.setText(matchName);
        profileImage.setImageBitmap(bmp );

        sendMessage = findViewById(R.id.sendBox);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               sendMessage();
           } 
        });

        myRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        myRecyclerView.setNestedScrollingEnabled(false);
        myRecyclerView.setHasFixedSize(true);

        // Merge everything together
        chatLayoutManager = new LinearLayoutManager(chatActivity.this);
        // Pass the layout manager to the recycler view
        myRecyclerView.setLayoutManager(chatLayoutManager);

        theChatAdapter = new ChatAdapter(getDataSetMatches(), chatActivity.this);
        myRecyclerView.setAdapter(theChatAdapter);



    }

    private String userGender;
    private String oppositeUserGender;
    private void getUserGender() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference maleDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Male");
        maleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals(user.getUid())) {
                    userGender = "Male";
                    oppositeUserGender = "Female";
                    chatDBUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userGender).child(currentUserID).child("linked").child("Matches").child(matchID).child("ChatId");
                    getChatID();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        DatabaseReference femaleDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Female");
        femaleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.getKey().equals(user.getUid())) {
                    userGender = "Female";
                    oppositeUserGender = "Male";
                    chatDBUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userGender).child(currentUserID).child("linked").child("Matches").child(matchID).child("ChatId");
                    getChatID();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }



    private void sendMessage() {
        String sendMessageText = sendMessage.getText().toString();

        if(!sendMessageText.isEmpty()){
            DatabaseReference newMessageDb = chatDBChat.push();

            Map newMessage = new HashMap();
            newMessage.put("createdByUser", currentUserID);
            newMessage.put("text", sendMessageText);

            newMessageDb.setValue(newMessage);
        }
        sendMessage.setText(null);
    }
    private void getChatID() {
        chatDBUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    chatID = dataSnapshot.getValue().toString();
                    chatDBChat = chatDBChat.child(chatID);
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getChatMessages() {
        chatDBChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    String message = null;
                    String createdByUser = null;

                    if(dataSnapshot.child("text").getValue()!=null){
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if(dataSnapshot.child("createdByUser").getValue()!=null){
                        createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                    }

                    if(message!=null && createdByUser!=null){
                        Boolean currentUserBoolean = false;
                        if(createdByUser.equals(currentUserID)){
                            currentUserBoolean = true;
                        }
                        ChatObject newMessage = new ChatObject(message, currentUserBoolean);
                        resultsChat.add(newMessage);
                        theChatAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private ArrayList<ChatObject> resultsChat = new ArrayList<ChatObject>();

    private List<ChatObject> getDataSetMatches() {
        return resultsChat;
    }
}
