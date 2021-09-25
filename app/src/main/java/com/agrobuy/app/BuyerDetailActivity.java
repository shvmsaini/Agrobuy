package com.agrobuy.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.agrobuy.app.databinding.BuyerDetailBinding;
import com.agrobuy.app.object.BuyerObject;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class BuyerDetailActivity extends Activity {
    public BuyerDetailBinding binding;
    public BuyerObject buyerObject;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BuyerDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        buyerObject = getIntent().getParcelableExtra("buyerList");
        binding.phoneNumber.setText(buyerObject.getPhoneNumber());
        binding.personalNumber.setText(buyerObject.getPersonalNumber());
        binding.categories.setText(buyerObject.getCategories());
        binding.date.setText(buyerObject.getDate());
        binding.rating.setText(buyerObject.getRating());
        binding.history.setText(buyerObject.getHistory());
        binding.timepref.setText(buyerObject.getTimePref());
        binding.email.setText(buyerObject.getEmail());
        Glide.with(this).load(buyerObject.getPicURL()).into(binding.buyerImage);

        binding.topAppBar.setTitle(buyerObject.getBuyerName());

        binding.contactCall.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:" + buyerObject.getPhoneNumber()));
            startActivity(i);
        });
        binding.contactEmail.setOnClickListener(v -> {
            Intent i = ExportLogisticsActivity.makeMailIntent(new String[]{buyerObject.getEmail()},
                    "Buyer Network", null, null);
            i.putExtra(Intent.EXTRA_TEXT, "Hi!");
            startActivity(i);
        });
        binding.contactWhatsapp.setOnClickListener(v -> {
            String url = "https://api.whatsapp.com/send?phone=" + buyerObject.getPersonalNumber();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        binding.sell.setOnClickListener(v -> {
            Log.d(BuyerDetailActivity.class.getName(), "Nothing is empty");

            // putting item in the database
            HashMap<String,String > details = new HashMap<>();
            details.put("buyer_ID",buyerObject.getBuyerID());
            details.put("buyer_Name",buyerObject.getBuyerName());
            HashMap<String,Object> id = new HashMap<>();
            id.put(FirebaseAuth.getInstance().getCurrentUser().getUid(),details);
            HashMap<String,Object > item = new HashMap<>();
            item.put(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(System.currentTimeMillis()),id);
            database.getReference(getString(R.string.buyer_applied)).updateChildren(item);
            // opening email
            Intent i = ExportLogisticsActivity.makeMailIntent(new String[]{buyerObject.getEmail()},
                    "Custom Subject Here", null, null);
            i.putExtra(Intent.EXTRA_TEXT, "Hi!");
            if (i.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(Intent.createChooser(i, "Choose an email client"), 800);
            } else {
                Toast.makeText(this, "Failed! Please install a email app", Toast.LENGTH_SHORT).show();
            }
        });

        binding.exportFinancing.setOnClickListener(v -> {
            Intent i = new Intent(this, TradeFinanceActivity.class);
            startActivity(i);
        });

        binding.topAppBar.setNavigationOnClickListener(v->{
            Log.d(BuyerDetailActivity.class.getName(), "onBackPressed()");
            onBackPressed();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==800){
            if(resultCode==RESULT_OK)
            Toast.makeText(getApplicationContext(), "Success! We will get back to you ASAP!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "Failed! Please send email to complete", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}