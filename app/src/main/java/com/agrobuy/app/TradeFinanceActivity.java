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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class TradeFinanceActivity extends Activity {
    public TradeFinanceBinding tradeFinance;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private String invoice_id="0";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==requestCode){
            Toast.makeText(getApplicationContext(), "Success! We will get back to you ASAP!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Failed! Please send email to complete", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tradeFinance = TradeFinanceBinding.inflate(getLayoutInflater());
        setContentView(tradeFinance.getRoot());
        tradeFinance.backButton.setOnClickListener(v-> super.onBackPressed());
        // financing type spinner
        String[] financeItems = new String[]{"item1", "item2", "item3"};
        ArrayAdapter<String> financeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,financeItems);
        tradeFinance.financeTypeSpinner.setAdapter(financeAdapter);
        // invoice spinner
        String[] invoiceItems = new String[]{"item1", "item2", "I don't have an invoice"};
        ArrayAdapter<String> invoiceAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,invoiceItems);
        tradeFinance.invoiceSpinner.setAdapter(invoiceAdapter);

        tradeFinance.apply.setOnClickListener(v->{
            Toast.makeText(this, "Wait...", Toast.LENGTH_SHORT).show();
            String customerName = tradeFinance.customerName.getText().toString();
            String invoiceAmount = tradeFinance.invoiceAmount.getText().toString();
            if(customerName.length()==0){
                Toast.makeText(this, "Enter customer name", Toast.LENGTH_SHORT).show();
                tradeFinance.customerName.requestFocus();
                return;
            }
            if(invoiceAmount.length()==0){
                Toast.makeText(this, "Please enter invoice amount", Toast.LENGTH_SHORT).show();
                tradeFinance.invoiceAmount.requestFocus();
                return;
            }

            //getting invoice id
            myRef.child("invoice_id").get().addOnCompleteListener(
                    task -> {
                        invoice_id = String.valueOf(task.getResult().getValue());

                        //creating a map of all details
                        HashMap<String,String> details = new HashMap<>();
                        HashMap<String,Object> item = new HashMap<>();
                        details.put("select_invoice",tradeFinance.invoiceSpinner.getSelectedItem().toString());
                        details.put("email",FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        details.put("finance_type",tradeFinance.financeTypeSpinner.getSelectedItem().toString());
                        details.put("curr_date", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
                        details.put("customer_name",customerName);
                        details.put("invoice_id",invoice_id);
                        details.put("invoice_amount",invoiceAmount);
//                        user.put(FirebaseAuth.getInstance().getUid(),details);
                        item.put(invoice_id,details);
                        //Changing invoice id
                        myRef.child("invoice_id").setValue(Integer.parseInt(invoice_id) + 1).addOnCompleteListener(task1 -> {

                            //putting everything in the database
                            myRef.child("applied").child(FirebaseAuth.getInstance().getUid()).updateChildren(item);
                            Log.d(TradeFinanceActivity.class.getName(),"success, " + item);
                            //opening email
                            //WARNING: Only Gmail with work
                            Intent intent = new Intent (Intent.ACTION_SEND);
                            intent.setType("message/rfc822");
                            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"yourEmail@gmail.com"});
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Trade Finance");
                            intent.putExtra(Intent.EXTRA_TEXT, "Please send this mail and we will get back to you ASAP!" +
                                    "\n\n\n" +
                                    "<--DON'T CHANGE THE LINES BELOW THIS-->\n"
                                    + item);
                            intent.setPackage("com.google.android.gm");
                            if (intent.resolveActivity(getPackageManager())!=null)
                                startActivityForResult(intent,800);
                            else
                                Toast.makeText(this,"Failed! Gmail App is not installed",Toast.LENGTH_SHORT).show();
                        });
                    }
            );
        });

        // calling link
        SpannableString callSpan = new SpannableString(
                tradeFinance.tradeFinanceFooter.getText().toString());
        callSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+ "8105109480"));
                startActivity(callIntent);
            }
        },61,74, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tradeFinance.tradeFinanceFooter.setText(callSpan);
        tradeFinance.tradeFinanceFooter.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
