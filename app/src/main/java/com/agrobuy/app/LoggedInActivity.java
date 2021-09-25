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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loggedinLayout = LoggedinLayoutBinding.inflate(getLayoutInflater());
        setContentView(loggedinLayout.getRoot());
        //getting instances
        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();
        assert currUser != null;
        DatabaseReference myRef = database.getReference("users");
        myRef.child(currUser.getUid()).child("name").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data.", task.getException());
                Toast.makeText(this, "Error getting data. Make sure your internet is stable.", Toast.LENGTH_SHORT).show();
                
            }
            else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                final String name = String.valueOf(task.getResult().getValue());
                loggedinLayout.userName.setText("Hi, " + name +"!");

            }
        });
        //navigation drawer
        loggedinLayout.topAppBar.setNavigationOnClickListener(v -> loggedinLayout.drawerLayout.openDrawer(Gravity.LEFT));
        loggedinLayout.navigationDrawer.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.my_invoices: {
                    Intent intent = new Intent(this,MyInvoicesActivity.class);
                    startActivity(intent);
                    loggedinLayout.drawerLayout.closeDrawers();
                    return true;
                }

                case R.id.support: {
                    Intent intent = new Intent(this,ContactUs.class);
                    startActivity(intent);
                    loggedinLayout.drawerLayout.closeDrawers();
                    return true;
                }

                case R.id.sign_out: {
                    mAuth.signOut();
                    finish();
//                    closing all previous activities
                    Intent i = new Intent(LoggedInActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    loggedinLayout.drawerLayout.closeDrawers();
                    return true;
                }

                default:
                    return false;
            }
        });

        loggedinLayout.buyerNetwork.setOnClickListener(v->{
            Intent intent = new Intent(this,BuyerNetworkActivity.class);
            startActivity(intent);
        });

        loggedinLayout.exportFinancing.setOnClickListener(v->{
            //Checking invoice_count
            database.getReference("users").child(currUser.getUid()).child("invoice_count")
                    .get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    //updating invoice count
                    if(Integer.parseInt(task.getResult().getValue().toString())>0){
                        Intent i = new Intent(this, TradeFinanceActivity.class);
                        startActivity(i);
                    }
                    else{
                        AlertDialog.Builder noInvoiceBuilder = new AlertDialog.Builder(this);
                        noInvoiceBuilder.setTitle("No Invoice Found")
                                .setMessage("To avail our logistics and Trade Finance Services create at least one Invoice")
                                .setPositiveButton("Create an invoice", (dialogInterface, i) -> {
                                    loggedinLayout.createInvoice.performClick();
                                })
                                .setNegativeButton("Upload an invoice", (dialogInterface, i) -> {
                                    loggedinLayout.uploadInvoice.performClick();
                                })
                                .setNeutralButton("Go back", (dialogInterface, i) -> {
                                   return;
                                });
                        noInvoiceBuilder.show();
                    }
                }
            });

        });

        loggedinLayout.deliveryPartners.setOnClickListener(v->{
            database.getReference("users").child(currUser.getUid()).child("invoice_count")
                    .get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    //updating invoice count
                    if(Integer.parseInt(task.getResult().getValue().toString())>0){
                        Intent i = new Intent(this, DeliveryPartnersActivity.class);
                        startActivity(i);
                    }
                    else{
                        AlertDialog.Builder noInvoiceBuilder = new AlertDialog.Builder(this);
                        noInvoiceBuilder.setTitle("No Invoice Found")
                                .setMessage("To avail our logistics and Trade Finance Services create at least one Invoice")
                                .setPositiveButton("Create an invoice", (dialogInterface, i) -> {
                                    loggedinLayout.createInvoice.performClick();
                                })
                                .setNegativeButton("Upload an invoice", (dialogInterface, i) -> {
                                    loggedinLayout.uploadInvoice.performClick();
                                })
                                .setNeutralButton("Go back", (dialogInterface, i) -> {
                                    return;
                                });
                        noInvoiceBuilder.show();
                    }
                }
            });
        });

        loggedinLayout.myInvoices.setOnClickListener(v->{
            Intent intent = new Intent(this,MyInvoicesActivity.class);
            startActivity(intent);
        });

        loggedinLayout.uploadInvoice.setOnClickListener(v->{
            Intent intent = new Intent(this,UploadInvoiceActivity.class);
            startActivity(intent);
        });
        loggedinLayout.createInvoice.setOnClickListener(v->{
            Intent intent = new Intent(this,CreateInvoiceActivity.class);
            startActivity(intent);
        });

        // calling link
        SpannableString callSpan = new SpannableString(
                loggedinLayout.loggedinFooter.getText().toString());
        callSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+ getString(R.string.phone_number)));
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
