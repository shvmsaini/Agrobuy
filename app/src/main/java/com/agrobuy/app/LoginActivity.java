package com.agrobuy.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    public TextView signup;
    public Button loginButton;
    public TextView email;
    public TextView password;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        signup = findViewById(R.id.signup);
        SpannableString signupSpan = new SpannableString(signup.getText().toString());
        signupSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent signupIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signupIntent);
            }
        },23,30, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        signup.setText(signupSpan);
        signup.setMovementMethod(LinkMovementMethod.getInstance());

        // Login Button
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> {
            Toast.makeText(LoginActivity.this, "Button CLICKED", Toast.LENGTH_SHORT).show();

        });
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }
}
