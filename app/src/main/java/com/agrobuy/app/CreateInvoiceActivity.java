package com.agrobuy.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.agrobuy.app.databinding.CreateInvoiceBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
            HashMap<String,String> details = new HashMap<>();
            //TODO: no field is empty
            //TODO: invoice number doesn't pre-exist
            details.put("customer_name" ,createInvoice.customerName.getText().toString());
            details.put("invoice_amount" ,createInvoice.invoiceAmount.getText().toString());
            details.put("invoice_due_date" ,createInvoice.datePicker.getText().toString());
            details.put("payment_terms" ,createInvoice.terms.getText().toString());
            details.put("delivery_mode" ,createInvoice.destination.getText().toString());
            HashMap<String,Object> item = new HashMap<>();
            item.put(createInvoice.invoiceNumber.getText().toString(),details);

            //updating invoice count
            myRef.updateChildren(item, (error, ref) -> {
                database.getReference("users").child(currUser.getUid()).child("invoice_count")
                        .get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        database.getReference("users").child(currUser.getUid()).child("invoice_count")
                                .setValue(Integer.parseInt(task.getResult().getValue().toString())+1);
                    }
                    Toast.makeText(this, "invoice created", Toast.LENGTH_SHORT).show();
                });

            });

        });
        createInvoice.backButton.setOnClickListener(v->{
            onBackPressed();
        });

    }
}
