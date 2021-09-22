package com.agrobuy.app;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BuyerNetworkActivity extends Activity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyer_network);

        database.getReference("users" + "/" + auth.getCurrentUser().getUid() +  "/" + "invoices").get()
                .addOnCompleteListener(task -> {

                });

    }

}
