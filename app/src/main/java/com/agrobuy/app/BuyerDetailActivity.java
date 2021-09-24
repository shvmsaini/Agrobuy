package com.agrobuy.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.agrobuy.app.databinding.BuyerDetailBinding;
import com.agrobuy.app.object.BuyerObject;
import com.bumptech.glide.Glide;

public class BuyerDetailActivity extends Activity {
    public BuyerDetailBinding binding;
    public BuyerObject buyerObject;
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

        binding.contactCall.setOnClickListener(v->{
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:"+ buyerObject.getPhoneNumber()));
            startActivity(i);
        });
        binding.contactEmail.setOnClickListener(v->{
            Intent i = ExportLogisticsActivity.makeMailIntent(new String[]{buyerObject.getEmail()},
                    "Buyer Network",null,null);
            i.putExtra(Intent.EXTRA_TEXT,"Hi!");
            startActivity(i);
        });
        binding.contactWhatsapp.setOnClickListener(v->{
            String url = "https://api.whatsapp.com/send?phone="+ buyerObject.getPersonalNumber();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        binding.sell.setOnClickListener(v->{
            Intent i = ExportLogisticsActivity.makeMailIntent(new String[]{buyerObject.getEmail()},
                    "Custom Subject Here",null,null);
            i.putExtra(Intent.EXTRA_TEXT,"Hi!");
            startActivity(i);
        });

        binding.exportFinancing.setOnClickListener(v->{
            Intent i = new Intent(this,TradeFinanceActivity.class);
            startActivity(i);
        });


    }
}