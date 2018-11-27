package com.example.ang.dineanddate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SettingsActivity extends AppCompatActivity {

    private EditText nameField, phoneField;

    private Button back, confirm;

    private ImageView profileImage;


    private FirebaseAuth auth;
    private DatabaseReference userDatabase;

    private String userId, name, phone, foodChoice1List, foodChoice2List, foodChoice3List, profileImageUrl, userGender;


    private Uri resultUri;

    Spinner dropdown3, dropdown2, dropdown1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Food list 1
        dropdown1 = findViewById(R.id.foodChoice1);
        String items1[] = new String[]{"Burger", "Cake", "Fries", "Pasta", "Pie", "Pizza", "Chinese", "Mexican"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);
        dropdown1.setAdapter(adapter1);
        String foodChoice1 = dropdown1.getSelectedItem().toString();


        // Food list 2
        dropdown2 = findViewById(R.id.foodChoice2);
        String[] items2 = new String[]{"Coffee", "Tea", "Soda", "Hot Chocolate", "Apple Cider", "H20"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown2.setAdapter(adapter2);


        //Food list 3
        dropdown3 = findViewById(R.id.foodChoice3);
        String[] items3 = new String[]{"McDonalds", "Wendys", "Burger King", "Arbys", "Dennys", "UpperCrust", "Wing City", "AppleBees", "Bob Evans", "Azteca", "China King"};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items3);
        dropdown3.setAdapter(adapter3);


        nameField = (EditText) findViewById(R.id.name);


        profileImage = (ImageView) findViewById(R.id.profileImage);

        back = (Button) findViewById(R.id.back);
        confirm = (Button) findViewById(R.id.confirm);

        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        userGender = getIntent().getExtras().getString("userGender");
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userGender).child(userId);

        getUserInfo();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                return;
            }
        });
    }


    private void getUserInfo() {
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        name = map.get("name").toString();
                        nameField.setText(name);
                    }
                    if(map.get("foodChoice1")!=null){
                        foodChoice1List = map.get("foodChoice1").toString();
                        dropdown1 = findViewById(R.id.foodChoice1);
                        String items1[] = new String[]{"Burger", "Cake", "Fries", "Pasta", "Pie", "Pizza", "Chinese", "Mexican"};
                        ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, items1);
                        int selectionPosition = adapter.getPosition(foodChoice1List);
                        dropdown1.setSelection(selectionPosition);

                    }
                    if(map.get("foodChoice2")!=null){
                        foodChoice2List = map.get("foodChoice2").toString();
                        dropdown2 = findViewById(R.id.foodChoice2);
                        String items2[] = new String[]{"Coffee", "Tea", "Soda", "Hot Chocolate", "Apple Cider"};
                        ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, items2);
                        int selectionPosition = adapter.getPosition(foodChoice2List);
                        dropdown2.setSelection(selectionPosition);

                    }
                    if(map.get("foodChoice3")!=null){
                        foodChoice3List = map.get("foodChoice3").toString();
                        dropdown3 = findViewById(R.id.foodChoice3);
                        String items3[] = new String[]{"McDonalds", "Wendys", "Burger King", "Arbys", "Dennys", "UpperCrust", "Wing City", "AppleBees", "Bob Evans", "Azteca", "China King"};
                        ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, items3);
                        int selectionPosition = adapter.getPosition(foodChoice3List);
                        dropdown3.setSelection(selectionPosition);

                    }
                    if(map.get("gender")!=null){
                        userGender = map.get("gender").toString();
                    }
                    Glide.clear(profileImage);
                    if(map.get("profileImageUrl")!=null){
                        profileImageUrl = map.get("profileImageUrl").toString();
                        switch(profileImageUrl){
                            case "default":
                                Glide.with(getApplication()).load(R.drawable.fries).into(profileImage);
                                break;
                            default:
                                Glide.with(getApplication()).load(profileImageUrl).into(profileImage);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void saveUserInformation() {
        name = nameField.getText().toString();
        foodChoice1List = dropdown1.getSelectedItem().toString();
        foodChoice2List = dropdown2.getSelectedItem().toString();
        foodChoice3List = dropdown3.getSelectedItem().toString();

        Map userInfo = new HashMap();
        userInfo.put("name", name);
        userInfo.put("foodChoice1", foodChoice1List);
        userInfo.put("foodChoice2", foodChoice2List);
        userInfo.put("foodChoice3", foodChoice3List);
        userDatabase.updateChildren(userInfo);
        if(resultUri != null){

            final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
            UploadTask uploadTask = filepath.putFile(resultUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri taskResult = task.getResult();
                        Map userInfo = new HashMap();
                        userInfo.put("profileImageUrl", taskResult.toString());
                        userDatabase.updateChildren(userInfo);

                        finish();
                        return;
                    }
                }
            });

            }else{
                finish();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            profileImage.setImageURI(resultUri);
        }
    }
    public void logoutUser(View view) {
        auth.signOut();
        Intent intent = new Intent(SettingsActivity.this, OpeningPage.class);
        startActivity(intent);
        finish();
        return;
    }
}
