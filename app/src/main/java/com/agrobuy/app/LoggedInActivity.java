package com.agrobuy.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.agrobuy.app.databinding.LoggedinLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoggedInActivity extends AppCompatActivity {
    public LoggedinLayoutBinding loggedinLayout;
    private FirebaseAuth mAuth;
    private FirebaseUser currUser;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loggedinLayout = LoggedinLayoutBinding.inflate(getLayoutInflater());
        setContentView(loggedinLayout.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();
        assert currUser != null;
        DatabaseReference myRef = database.getReference("users");
        myRef.child(currUser.getUid()).child("name").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                final String name = String.valueOf(task.getResult().getValue());
                loggedinLayout.userName.setText("Hi, " + name +"!");

            }
        });

        loggedinLayout.topAppBar.setOnClickListener(v -> loggedinLayout.drawerLayout.openDrawer(Gravity.LEFT));
        loggedinLayout.navigationDrawer.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.settings: {

                    return true;
                }
                case R.id.my_products: {

                    return true;
                }

                case R.id.support: {
                    return true;
                }

                case R.id.sign_out: {
                    mAuth.signOut();
                    finish();
//                    // closing all previous activities
                    Intent i = new Intent(LoggedInActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    return true;
                }

                default:
                    return false;
            }
        });
        loggedinLayout.exportFinancing.setOnClickListener(v->{
          Intent i = new Intent(this, TradeFinanceActivity.class);
          startActivity(i);
        });
        loggedinLayout.buyerNetwork.setOnClickListener(v->{

        });



        // calling link
        SpannableString callSpan = new SpannableString(
                loggedinLayout.loggedinFooter.getText().toString());
        callSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+ "8105109480"));
                startActivity(callIntent);
            }
        },33,46, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        loggedinLayout.loggedinFooter.setText(callSpan);
        loggedinLayout.loggedinFooter.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
