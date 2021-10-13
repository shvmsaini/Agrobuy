package com.agrobuy.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.agrobuy.app.databinding.SignupLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    public SignupLayoutBinding signupLayout;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signupLayout = SignupLayoutBinding.inflate(getLayoutInflater()); // view binding
        setContentView(signupLayout.getRoot());
        auth = FirebaseAuth.getInstance(); // getting firebase auth

        // back to login
        SpannableString loginSpan = new SpannableString(signupLayout.loginBack.getText());
        loginSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                onBackPressed();
            }
        },25,30, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        signupLayout.loginBack.setText(loginSpan);
        signupLayout.loginBack.setMovementMethod(LinkMovementMethod.getInstance());

        // signing up
        signupLayout.signupButton.setOnClickListener(v -> {
            String name = signupLayout.nameForSignup.getText().toString();
            String email = signupLayout.emailForSignup.getText().toString();
            String pass = signupLayout.passwordForSignup.getText().toString();
            String confirmPass = signupLayout.confirmPasswordForSignup.getText().toString();
            if(pass.equals(confirmPass)){
                if(TextUtils.isEmpty(name)){
                    signupLayout.nameForSignup.requestFocus();
                    Toast.makeText(getApplicationContext(),"Please enter your name",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    signupLayout.emailForSignup.requestFocus();
                    Toast.makeText(getApplicationContext(),"Please enter your E-mail address",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(pass) || pass.length() == 0){
                    signupLayout.passwordForSignup.requestFocus();
                    Toast.makeText(getApplicationContext(),"Please enter your Password",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(confirmPass) || confirmPass.length() == 0){
                    signupLayout.confirmPasswordForSignup.requestFocus();
                    Toast.makeText(getApplicationContext(),"Please enter your Password again",Toast.LENGTH_LONG).show();
                    return;
                }
                if(pass.length()<8){
                    signupLayout.passwordForSignup.requestFocus();
                    Toast.makeText(getApplicationContext(),"Password must be more than 8 digit",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!LoginActivity.isValidEmail(signupLayout.emailForSignup.getText().toString())){
                    signupLayout.emailForSignup.requestFocus();
                    Toast.makeText(getApplicationContext(),"Please enter a valid email",Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    auth.createUserWithEmailAndPassword(email,pass)
                            .addOnCompleteListener(SignUpActivity.this, task -> {
                                if (!task.isSuccessful()) {
                                    Log.d(SignUpActivity.class.getName(),"User creation failed",task.getException());
                                    Toast.makeText(SignUpActivity.this, "ERROR",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    FirebaseUser curruser = auth.getCurrentUser();
                                    assert curruser != null;
                                    DatabaseReference myRef = database.getReference("users");
                                    //storing data for the user
                                    myRef.child(curruser.getUid()).child("name").setValue(signupLayout.nameForSignup.getText().toString());
                                    myRef.child(curruser.getUid()).child("invoice_count").setValue(0L);
                                    myRef.child(curruser.getUid()).child("email").setValue(signupLayout.emailForSignup.getText().toString());
                                    //starting loggedin activity
                                    startActivity(new Intent(SignUpActivity.this, LoggedInActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();
                                }
                            });
                }
            }
            else{
                Toast.makeText(this, "Password doesn't match!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
