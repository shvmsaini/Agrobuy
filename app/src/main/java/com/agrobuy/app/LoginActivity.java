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

import com.agrobuy.app.databinding.LoginLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    public LoginLayoutBinding loginLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginLayout = LoginLayoutBinding.inflate(getLayoutInflater());
        setContentView(loginLayout.getRoot());

        mAuth = FirebaseAuth.getInstance(); // get firebase auth instance
        // For sign up link
        SpannableString signupSpan = new SpannableString(loginLayout.signup.getText().toString());
        signupSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent signupIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signupIntent);
            }
        }, 23, 30, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        loginLayout.signup.setText(signupSpan);
        loginLayout.signup.setMovementMethod(LinkMovementMethod.getInstance());

        // Login Button
        loginLayout.loginButton.setOnClickListener(view -> {
            String email = loginLayout.emailForLogin.getText().toString();
            String pass = loginLayout.passwordForLogin.getText().toString();
            if (isValidEmail(email) && !pass.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Wait...", Toast.LENGTH_SHORT).show();
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(this, task -> {
                            if (!task.isSuccessful()) {
                                // If sign in fails, display a message to the user.
                                Log.w(LoginActivity.class.getName(), "signInWithEmail:failure", task.getException());
                                String exceptionName = task.getException().getClass().getSimpleName();
                                if (exceptionName.equals(FirebaseAuthInvalidUserException.class.getSimpleName())) {
                                    Toast.makeText(LoginActivity.this, "Cannot find user", Toast.LENGTH_SHORT).show();
                                    return;
                                } else if (exceptionName.equals(FirebaseAuthInvalidCredentialsException.class.getSimpleName())) {
                                    Toast.makeText(LoginActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Toast.makeText(LoginActivity.this, "Authentication failed ", Toast.LENGTH_SHORT).show();
//                              updateUI(null);
                            } else {
                                Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(LoginActivity.class.getName(), "signInWithEmail:success");
//                              FirebaseUser user = mAuth.getCurrentUser();
                                finish();
//                              updateUI(user);
                            }
                        });
            } else {
                if (!isValidEmail(email)) {
                    loginLayout.emailForLogin.requestFocus();
                    Toast.makeText(LoginActivity.this, "Enter a valid email",
                            Toast.LENGTH_SHORT).show();
                } else {
                    loginLayout.passwordForLogin.requestFocus();
                }
            }
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

    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}
