package com.example.ang.dineanddate;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    private Button registerButton;
    private EditText emailInput, passwordInput, nameInput;

    private RadioGroup genderInput;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Checks to see if user is already logged in or not
        // We can know when user is logged in or not
        auth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // If user is logged in... move on to main page
                if(user !=null) {
                    Intent intent = new Intent(Registration.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };


        registerButton = (Button) findViewById(R.id.registerButton);

        emailInput = (EditText) findViewById(R.id.email);
        passwordInput = (EditText) findViewById(R.id.password);

        nameInput = (EditText) findViewById(R.id.name);

        genderInput = (RadioGroup) findViewById(R.id.gender);

        registerButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                int genderId = genderInput.getCheckedRadioButtonId();

                final RadioButton radioButton = (RadioButton) findViewById(genderId);

                if(radioButton.getText() == null){
                    return;
                }

                final String name = nameInput.getText().toString();
                final String email = emailInput.getText().toString();
                final String password = passwordInput.getText().toString();
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Registration fails so firebaseAuth function is not called
                        if(!task.isSuccessful()) {
                            Toast.makeText(Registration.this, "Sign Up Error", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String userId = auth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(radioButton.getText().toString()).child(userId);
                            Map userInfo = new HashMap<>();
                            userInfo.put("name",name);
                            userInfo.put("profileImageUrl", "default");
                            userInfo.put("foodChoice1", "Pizza");
                            userInfo.put("foodChoice2", "Coffee");
                            userInfo.put("foodChoice3","McDonalds");
                            currentUserDb.updateChildren(userInfo);
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(firebaseAuthStateListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
