package com.agrobuy.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.agrobuy.app.databinding.SignupLayoutBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    public SignupLayoutBinding signupLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signupLayout = SignupLayoutBinding.inflate(getLayoutInflater()); // view binding
        setContentView(signupLayout.getRoot());

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


        // singing up
        signupLayout.signupButton.setOnClickListener(v -> {
            String email = signupLayout.emailForSignup.getText().toString();
            String pass = signupLayout.passwordForSignup.getText().toString();
            String confirmPass = signupLayout.confirmPasswordForSignup.getText().toString();
            if(pass.equals(confirmPass)){
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Please enter your E-mail address",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(pass) || pass.length() == 0){
                    signupLayout.passwordForSignup.requestFocus();
                    Toast.makeText(getApplicationContext(),"Please enter your Password",Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPass) || confirmPass.length() == 0){
                    signupLayout.confirmPasswordForSignup.requestFocus();
                    Toast.makeText(getApplicationContext(),"Please enter your Password again",Toast.LENGTH_LONG).show();
                    return;
                }
                if (pass.length()<8){
                    signupLayout.passwordForSignup.requestFocus();
                    Toast.makeText(getApplicationContext(),"Password must be more than 8 digit",Toast.LENGTH_LONG).show();
                }
                else{
                    auth.createUserWithEmailAndPassword(email,pass)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (!task.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this, "ERROR",Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        startActivity(new Intent(SignUpActivity.this, LoggedInActivity.class));
                                        finish();
                                    }
                                }
                            });}
            }
            else{
                Toast.makeText(this, "Password doesn't match!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
