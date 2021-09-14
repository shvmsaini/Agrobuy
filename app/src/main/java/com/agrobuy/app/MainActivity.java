package com.agrobuy.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    public Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        next = findViewById(R.id.next);


        // Check if a user is already logged in
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener authStateListener = firebaseAuth1 -> {
            FirebaseUser firebaseUser = firebaseAuth1.getCurrentUser();
            if (firebaseUser == null) {
                next.setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });

            }
            if (firebaseUser != null) {
                Intent intent = new Intent(MainActivity.this, LoggedInActivity.class);
                startActivity(intent);
                finish();
            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
        }
    }

