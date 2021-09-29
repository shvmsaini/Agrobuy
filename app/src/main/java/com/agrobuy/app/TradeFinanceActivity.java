package com.agrobuy.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.agrobuy.app.databinding.TradeFinanceBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeFinanceActivity extends Activity {
    public TradeFinanceBinding tradeFinance;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private String invoice_id = "0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tradeFinance = TradeFinanceBinding.inflate(getLayoutInflater());
        setContentView(tradeFinance.getRoot());

        // financing type spinner
        String[] financeItems = new String[]{"Low Cost Insurance","Invoice Discounting","I am not sure"};
        ArrayAdapter<String> financeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,financeItems);
        tradeFinance.financeTypeSpinner.setAdapter(financeAdapter);

        // invoice spinner
        String[] invoiceItems = new String[]{"item1", "item2", "item3"};
        ArrayAdapter<String> invoiceAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,invoiceItems);
        tradeFinance.invoiceSpinner.setAdapter(invoiceAdapter);

        // getting invoice data
        database.getReference("users" + "/" + auth.getCurrentUser().getUid() +  "/" + "invoices").get()
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        Log.d("TradeFinance", ": Error getting invoices data");
                        Toast.makeText(this, "Error getting invoice data", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        DataSnapshot snapshot = task.getResult();
                        Map<String,Object> invoicesMap = new HashMap<>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Map<String,Object> map = (Map<String, Object>) postSnapshot.getValue();
                            invoicesMap.put(postSnapshot.getKey(),map);
                        }

                        Log.d("invoicesMap",invoicesMap.toString());

                        // Adding keys to Spinner
                        List<Object> values = new ArrayList<>(invoicesMap.keySet());
                        String[] items = new String[values.size()];
                        for (int i = 0; i < values.size(); i++) items[i] = (String) values.get(i);

                        ArrayAdapter<String> inv = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,items);
                        tradeFinance.invoiceSpinner.setAdapter(inv);

                        // apply button
                        tradeFinance.apply.setOnClickListener(v->{
                            Toast.makeText(this, "Wait...", Toast.LENGTH_SHORT).show();
                            //getting invoice id
                            myRef.child("invoice_id").get().addOnCompleteListener(snapshotTask -> {
                                if(!snapshotTask.isSuccessful()){
                                    Toast.makeText(this, "Error getting data. Make sure your internet is stable.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    invoice_id = String.valueOf(snapshotTask.getResult().getValue());
                                    if(checkNotEmpty()){
                                        //creating a map of all details
                                        HashMap<String,String> details = new HashMap<>();
                                        HashMap<String,Object> item = new HashMap<>();
                                        // getting invoice from map
                                        details.put("select_invoice",tradeFinance.invoiceSpinner.getSelectedItem().toString());
                                        details.put("email",FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                        details.put("finance_type",tradeFinance.financeTypeSpinner.getSelectedItem().toString());
                                        details.put("customer_name",tradeFinance.customerName.getText().toString());
                                        details.put("invoice_id",invoice_id);
                                        details.put("invoice_amount",tradeFinance.invoiceAmount.getText().toString());
                                        details.put("phone_number",tradeFinance.phoneNumber.getText().toString());
                                        item.put(FirebaseAuth.getInstance().getUid(),details);

                                        //Changing invoice id
                                        myRef.child("invoice_id").setValue(Integer.parseInt(invoice_id) + 1).addOnCompleteListener(task1 -> {
                                            //putting everything in the database
                                            database.getReference(getString(R.string.trade_finance_applied))
                                                    .child(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date())).updateChildren(item);
                                            Log.d(TradeFinanceActivity.class.getName(),"success, " + item);
                                            //opening email
                                            Intent i = ExportLogisticsActivity.makeMailIntent( new String[]{"vishnu@agrobuy.co"},"Trade Finance",
                                                    "Please send this mail and we will get back to you ASAP!", item);
                                            if (i.resolveActivity(getPackageManager())!=null)
                                                startActivityForResult(Intent.createChooser(i, "Choose an email client"), 800);
                                            else
                                                Toast.makeText(this,"Failed! Please install a email app",Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                }

                            });
                        });
                    }
                });

        // calling link
        SpannableString callSpan = new SpannableString(
                tradeFinance.tradeFinanceFooter.getText().toString());
        callSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+ getString(R.string.phone_number)));
                startActivity(callIntent);
            }
        },61,74, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tradeFinance.tradeFinanceFooter.setText(callSpan);
        tradeFinance.tradeFinanceFooter.setMovementMethod(LinkMovementMethod.getInstance());

        // back button
        tradeFinance.topAppBar.setNavigationOnClickListener(v-> super.onBackPressed());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==800){
            if(resultCode==RESULT_OK)
            Toast.makeText(getApplicationContext(), "Success! We will get back to you ASAP!", Toast.LENGTH_SHORT).show();
            else Toast.makeText(getApplicationContext(), "Failed! Please send email to complete", Toast.LENGTH_SHORT).show();
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public boolean checkNotEmpty(){
        if(tradeFinance.customerName.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter Customer Name", Toast.LENGTH_SHORT).show();
            tradeFinance.customerName.requestFocus();
            return false;
        }

        if(tradeFinance.invoiceAmount.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter Invoice Amount", Toast.LENGTH_SHORT).show();
            tradeFinance.invoiceAmount.requestFocus();
            return false;
        }

        if(tradeFinance.phoneNumber.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter phone number", Toast.LENGTH_SHORT).show();
            tradeFinance.phoneNumber.requestFocus();
            return false;
        }

        return true;
    }

}
