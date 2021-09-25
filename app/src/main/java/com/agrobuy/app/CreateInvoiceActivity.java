package com.agrobuy.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.agrobuy.app.databinding.CreateInvoiceBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CreateInvoiceActivity extends Activity {
    public CreateInvoiceBinding createInvoice;
    private FirebaseAuth mAuth;
    private FirebaseUser currUser;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createInvoice = CreateInvoiceBinding.inflate(getLayoutInflater());
        setContentView(createInvoice.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();

        createInvoice.apply.setOnClickListener(v->{
            DatabaseReference myRef = database.getReference("users").child(currUser.getUid()).child("invoices");
            // creating invoice
            HashMap<String,Object> item = new HashMap<>();
            if(checkNotEmpty()){
                HashMap<String,String> details = new HashMap<>();
                details.put("customer_name" ,createInvoice.customerName.getText().toString());
                details.put("invoice_amount" ,createInvoice.invoiceAmount.getText().toString());
                details.put("invoice_due_date" ,createInvoice.datePicker.getText().toString());
                details.put("payment_terms" ,createInvoice.terms.getText().toString());
                details.put("delivery_mode" ,createInvoice.deliveryMode.getText().toString());
                details.put("delivery_destination" ,createInvoice.deliveryDestination.getText().toString());
                item.put(createInvoice.invoiceNumber.getText().toString(),details);
            }

            // Checking if invoice already exist or not
            FirebaseDatabase.getInstance().getReference("users" + "/" + currUser.getUid() +  "/" + "invoices" +  "/"
                    + createInvoice.invoiceNumber.getText().toString())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Toast.makeText(getApplicationContext(), "Invoice number already exists", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                // putting invoice to database
                                myRef.updateChildren(item, (error, ref) -> {
                                    database.getReference("users").child(currUser.getUid()).child("invoice_count")
                                            .get().addOnCompleteListener(task -> {
                                        if (!task.isSuccessful()) {
                                            Log.e("firebase", "Error getting data", task.getException());
                                        }
                                        else {
                                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                            //updating invoice count
                                            database.getReference("users").child(currUser.getUid()).child("invoice_count")
                                                    .setValue(Integer.parseInt(task.getResult().getValue().toString())+1);
                                        }
                                        Toast.makeText(getApplicationContext(), "invoice created", Toast.LENGTH_SHORT).show();
                                    });
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        });
        createInvoice.topAppBar.setNavigationOnClickListener(v->{
            onBackPressed();
        });

    }
    public boolean checkNotEmpty(){
        if(createInvoice.invoiceNumber.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter Invoice Name", Toast.LENGTH_SHORT).show();
            createInvoice.invoiceNumber.requestFocus();
            return false;
        }

        if(createInvoice.customerName.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter Customer Name", Toast.LENGTH_SHORT).show();
            createInvoice.customerName.requestFocus();
            return false;
        }
        if(createInvoice.invoiceAmount.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter invoiceAmount", Toast.LENGTH_SHORT).show();
            createInvoice.invoiceAmount.requestFocus();
            return false;
        }
        if(createInvoice.datePicker.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter Date", Toast.LENGTH_SHORT).show();
            createInvoice.datePicker.requestFocus();
            return false;
        }
        if(createInvoice.terms.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter Terms", Toast.LENGTH_SHORT).show();
            createInvoice.terms.requestFocus();
            return false;
        }
        if(createInvoice.deliveryMode.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter delivery Mode", Toast.LENGTH_SHORT).show();
            createInvoice.deliveryMode.requestFocus();
            return false;
        }
        if(createInvoice.deliveryDestination.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter delivery Destination", Toast.LENGTH_SHORT).show();
            createInvoice.deliveryDestination.requestFocus();
            return false;
        }
        return true;
    }
}
