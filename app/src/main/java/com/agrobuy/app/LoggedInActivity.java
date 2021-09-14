package com.agrobuy.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageButton;

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
        DatabaseReference myRef = database.getReference(currUser.getUid());
        myRef.child("name").get().addOnCompleteListener(task -> {
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
            setContentView(R.layout.trade_finance);
            ImageButton back = findViewById(R.id.back_button);
            back.setOnClickListener(view -> setContentView(loggedinLayout.getRoot()));
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
