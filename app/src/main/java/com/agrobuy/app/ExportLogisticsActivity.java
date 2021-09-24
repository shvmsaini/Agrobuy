package com.agrobuy.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.agrobuy.app.databinding.ExportLogisticsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class ExportLogisticsActivity extends Activity {

    public ExportLogisticsBinding exportLogistics;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exportLogistics = ExportLogisticsBinding.inflate(getLayoutInflater());
        setContentView(exportLogistics.getRoot());
        exportLogistics.availLogistics.setOnClickListener(v->{
            Log.d(ExportLogisticsBinding.class.getName(),"Clicked");
            // Check nothing is empty
            if(checkNotEmpty()){
                Log.d(ExportLogisticsBinding.class.getName(),"Nothing is empty");
                HashMap<String,Object> item = createItem();
                Log.d(ExportLogisticsBinding.class.getName(),item.toString());
                // putting item in the database
                database.getReference(getString(R.string.delivery_partners_applied)).updateChildren(item);
                // opening email
                Intent intent = makeMailIntent(new String[]{"yourEmail@gmail.com"},"Export Logistics",
                        "Please send this mail to continue availing export logistics.", item);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser(intent, "Choose an email client"), 800);
                }
                else{
                    Toast.makeText(this,"Failed! Please install a email app",Toast.LENGTH_SHORT).show();
                }
            }

        });

        exportLogistics.topAppBar.setNavigationOnClickListener(v->{
            Log.d("TAG", "onBackPressed()");
            onBackPressed();
        });

    }

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

    public boolean checkNotEmpty(){
        if(exportLogistics.invoiceNumber.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter Invoice Number", Toast.LENGTH_SHORT).show();
            exportLogistics.invoiceNumber.requestFocus();
            return false;
        }

        if(exportLogistics.iecCode.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter IEC Code", Toast.LENGTH_SHORT).show();
            exportLogistics.iecCode.requestFocus();
            return false;

        }
        if(exportLogistics.deliveryDate.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter deliveryDate", Toast.LENGTH_SHORT).show();
            exportLogistics.deliveryDate.requestFocus();
            return false;

        }
        if(exportLogistics.deliveryTerms.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter Terms", Toast.LENGTH_SHORT).show();
            exportLogistics.deliveryTerms.requestFocus();
            return false;

        }
        if(exportLogistics.destinationCountry.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter Destination Country", Toast.LENGTH_SHORT).show();
            exportLogistics.destinationCountry.requestFocus();
            return false;

        }
        if(exportLogistics.destinationAddress.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter Destination Address", Toast.LENGTH_SHORT).show();
            exportLogistics.destinationAddress.requestFocus();
            return false;

        }
        if(exportLogistics.deliveryType.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter Delivery Type", Toast.LENGTH_SHORT).show();
            exportLogistics.deliveryType.requestFocus();
            return false;

        }
        if(exportLogistics.cargoType.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter Cargo Type", Toast.LENGTH_SHORT).show();
            exportLogistics.cargoType.requestFocus();
            return false;

        }
        if(exportLogistics.containerType.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter Container Type", Toast.LENGTH_SHORT).show();
            exportLogistics.containerType.requestFocus();
            return false;

        }
        if(exportLogistics.cargoCategory.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter Cargo category", Toast.LENGTH_SHORT).show();
            exportLogistics.cargoCategory.requestFocus();
            return false;

        }
        if(exportLogistics.restrictedGoods.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter Restricted Goods", Toast.LENGTH_SHORT).show();
            exportLogistics.restrictedGoods.requestFocus();
            return false;

        }
        if(exportLogistics.noContainers.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Enter no. of Containers", Toast.LENGTH_SHORT).show();
            exportLogistics.noContainers.requestFocus();
            return false;

        }
        return true;
    }

    public HashMap<String, Object> createItem(){
        HashMap<String,String > details = new HashMap<>();
        details.put("exporter_id",getIntent().getStringExtra("delivery_partner_id"));
        details.put("invoice_number",exportLogistics.invoiceNumber.getText().toString());
        details.put("iec_code",exportLogistics.iecCode.getText().toString());
        details.put("delivery_date",exportLogistics.deliveryDate.getText().toString());
        details.put("delivery_terms",exportLogistics.deliveryTerms.getText().toString());
        details.put("destination_country",exportLogistics.destinationCountry.getText().toString());
        details.put("destination_address",exportLogistics.destinationAddress.getText().toString());
        details.put("delivery_type",exportLogistics.deliveryType.getText().toString());
        details.put("cargo_type",exportLogistics.cargoType.getText().toString());
        details.put("container_type",exportLogistics.containerType.getText().toString());
        details.put("cargo_category",exportLogistics.destinationCountry.getText().toString());
        details.put("restricted_goods",exportLogistics.restrictedGoods.getText().toString());
        details.put("no_of_containers",exportLogistics.noContainers.getText().toString());
        HashMap<String,Object> id = new HashMap<>();
        id.put( FirebaseAuth.getInstance().getCurrentUser().getUid(),details);
        HashMap<String, Object> item = new HashMap<>();
        item.put(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(System.currentTimeMillis()),id);
        return item;
    }

    public static Intent makeMailIntent(String[] email, String subject, String TEXT, HashMap<String,Object> item){
        Intent intent = new Intent (Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject );
        if(TEXT!=null && item!=null)
        intent.putExtra(Intent.EXTRA_TEXT, TEXT +
                "\n\n\n" +
                "<--DON'T CHANGE ANY LINE BELOW THIS-->\n"
                + item);
        return intent;
    }

}
