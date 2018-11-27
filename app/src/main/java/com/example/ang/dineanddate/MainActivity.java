package com.example.ang.dineanddate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ang.dineanddate.Matches.MatchesActivity;
import com.example.ang.dineanddate.box.arrayAdapter;
import com.example.ang.dineanddate.box.box;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String currentUserFood1;
    String currentUserFood2;
    String currentUserFood3;
    private box box_data[];
    private com.example.ang.dineanddate.box.arrayAdapter arrayAdapter;
    private int i;
    private LinearLayout noMatch;

    private FirebaseAuth auth;

    private String currentUId;

    private DatabaseReference usersDb;

    private com.lorentzos.flingswipe.SwipeFlingAdapterView swiperView;
    private ContactsContract.Data userId;
    ListView listView;
    List<box> rowItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // First start!!
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);
        if(firstStart){
            goToSettingsFirstTime();
        }



        noMatch = findViewById(R.id.noMatches);

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        auth = FirebaseAuth.getInstance();
        currentUId = auth.getCurrentUser().getUid();

        checkUserGender();


        rowItems = new ArrayList<box>();
        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);


        SwipeFlingAdapterView filingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        filingContainer.setAdapter(arrayAdapter);
        filingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                box object = (box) dataObject;
                String userId = object.getUserId();
                usersDb.child(oppositeUserGender).child(userId).child("linked").child("no").child(currentUId).setValue(true);
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                box object = (box) dataObject;
                String userId = object.getUserId();
                usersDb.child(oppositeUserGender).child(userId).child("linked").child("yes").child(currentUId).setValue(true);
                isMatch(userId);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        filingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                box object = (box) dataObject;
                String userId = object.getUserId();
                String userGender = oppositeUserGender;
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userGender", userGender);
                startActivity(intent);
                return;

            }
        });


    }



    private void isMatch(String userId) {
        DatabaseReference currentUserLinksDb = usersDb.child(userGender).child(currentUId).child("linked").child("yes").child(userId);
        currentUserLinksDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(MainActivity.this, "New Match!", Toast.LENGTH_LONG).show();

                    //Get the key for the chat
                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                    usersDb.child(oppositeUserGender).child(dataSnapshot.getKey()).child("linked").child("Matches").child(currentUId).child("ChatId").setValue(key);
                    usersDb.child(userGender).child(currentUId).child("linked").child("Matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);

//                    usersDb.child(oppositeUserGender).child(dataSnapshot.getKey()).child("linked").child("Matches").child(currentUId).setValue(true);
//                    usersDb.child(userGender).child(currentUId).child("linked").child("Matches").child(dataSnapshot.getKey()).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String userGender;
    private String oppositeUserGender;
    public void checkUserGender() {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference maleDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Male");
        maleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.getKey().equals(user.getUid())) {
                    userGender = "Male";
                    oppositeUserGender = "Female";
                    getUserFoodChoices();

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

                if(dataSnapshot.getKey().equals(user.getUid())) {
                    userGender = "Female";
                    oppositeUserGender = "Male";
                    getUserFoodChoices();

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

    private FirebaseAuth auth2 = FirebaseAuth.getInstance();
    private String currentUId2 = auth2.getCurrentUser().getUid();
    private void getUserFoodChoices() {
        DatabaseReference userFoodDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userGender).child(currentUId2);
        DatabaseReference currentFood1 = userFoodDB.child("foodChoice1");
        DatabaseReference currentFood2 = userFoodDB.child("foodChoice2");
        DatabaseReference currentFood3 = userFoodDB.child("foodChoice2");

        final ArrayList<String> foods = new ArrayList<>();
        userFoodDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String loadedcurrentUserFood1 = dataSnapshot.child("foodChoice1").getValue(String.class);
                String loadedcurrentUserFood2 = dataSnapshot.child("foodChoice2").getValue(String.class);
                String loadedcurrentUserFood3 = dataSnapshot.child("foodChoice3").getValue(String.class);
                foods.add(loadedcurrentUserFood1);
                foods.add(loadedcurrentUserFood2);
                foods.add(loadedcurrentUserFood3);
                setFoods(foods);
                getOppositeGenderUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    public void setFoods(ArrayList<String> Foods) {
        currentUserFood1 = Foods.get(0);
        currentUserFood2 = Foods.get(1);
        currentUserFood3 = Foods.get(2);

    }
    public void getOppositeGenderUsers() {
        DatabaseReference oppositeGenderDB = FirebaseDatabase.getInstance().getReference().child("Users").child(oppositeUserGender);
        oppositeGenderDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //If they have seen the card before, it will not show. You need to make an if statement here to only display users with the same food interests
                if(dataSnapshot.exists() && !dataSnapshot.child("linked").child("no").hasChild(currentUId) && !dataSnapshot.child("linked").child("yes").hasChild(currentUId))
                {


                    String profileImageUrl = "default";
                    if(!dataSnapshot.child("profileImageUrl").getValue().equals("default")) {
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    }
                    String foodOppChoice1 = dataSnapshot.child("foodChoice1").getValue(String.class);
                    String foodOppChoice2 = dataSnapshot.child("foodChoice2").getValue(String.class);
                    String foodOppChoice3 = dataSnapshot.child("foodChoice3").getValue(String.class);


                    if(foodOppChoice1 != null && (foodOppChoice1.equals(currentUserFood1) || foodOppChoice2.equals(currentUserFood2) || foodOppChoice3.equals(currentUserFood3))) {

                        box Item = new box(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImageUrl);
                        rowItems.add(Item);
                        arrayAdapter.notifyDataSetChanged();
                        noMatch.setVisibility(View.GONE);
                    }

                }
//                else {
//                    noMatch.setVisibility(View.VISIBLE);
//                }
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




    private void goToSettingsFirstTime() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference maleDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Male");
        maleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.getKey().equals(user.getUid())) {
                    userGender = "Male";
                    auth = FirebaseAuth.getInstance();
                    String userId2 = auth.getCurrentUser().getUid();
                    String userGender2;
                    Intent intent = new Intent(MainActivity.this, onboarding.class);
                    intent.putExtra("userGender", userGender);
                    intent.putExtra("userId", userId2);
                    startActivity(intent);
                    SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("firstStart", false);
                    editor.apply();
                    return;
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

                if(dataSnapshot.getKey().equals(user.getUid())) {
                    userGender = "Female";
                    auth = FirebaseAuth.getInstance();
                    String userId2 = auth.getCurrentUser().getUid();
                    String userGender2;
                    Intent intent = new Intent(MainActivity.this, onboarding.class);
                    intent.putExtra("userGender", userGender);
                    intent.putExtra("userId", userId2);
                    startActivity(intent);
                    SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("firstStart", false);
                    editor.apply();
                    return;
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
        return;
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        intent.putExtra("userGender", userGender);
        startActivity(intent);
        return;
    }
    public void goToMatches(View view) {
        Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
        startActivity(intent);
        return;
    }

}
