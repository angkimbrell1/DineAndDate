package com.example.ang.dineanddate;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    private String userId, userGender, name;

    private TextView nameView, foodChoice1View, foodChoice2View, foodChoice3View;
    private ImageView profileImage;

    private Button back;

    private DatabaseReference usersDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        userId = getIntent().getExtras().getString("userId");
        userGender = getIntent().getExtras().getString("userGender");

        nameView = findViewById(R.id.name);
        foodChoice1View = findViewById(R.id.foodChoice1);
        foodChoice2View = findViewById(R.id.foodChoice2);
        foodChoice3View = findViewById(R.id.foodChoice3);
        profileImage = findViewById(R.id.profileImage);
        back = findViewById(R.id.back);

        getUserInfo(userId);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                return;
            }
        });

    }
    private void getUserInfo(String userId) {
        DatabaseReference currentUserLinksDb = usersDb.child(userGender).child(userId);
        currentUserLinksDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                String foodChoice1 = dataSnapshot.child("foodChoice1").getValue().toString();
                String foodChoice2 = dataSnapshot.child("foodChoice2").getValue().toString();
                String foodChoice3 = dataSnapshot.child("foodChoice3").getValue().toString();
                nameView.setText(name);
                foodChoice1View.setText(foodChoice1);
                foodChoice2View.setText(foodChoice2);
                foodChoice3View.setText(foodChoice3);
                switch(profileImageUrl){
                    case "default":
                        Glide.with(getApplication()).load(R.drawable.fries).into(profileImage);
                        break;
                    default:
                        Glide.with(getApplication()).load(profileImageUrl).into(profileImage);
                        break;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
