package com.agrobuy.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agrobuy.app.adapters.ContentDisplayAdapter;
import com.agrobuy.app.object.ContentObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeliveryPartnersActivity extends Activity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    public List<ContentObject> partnerList = new ArrayList<>();
    RecyclerView recyclerView;
    ContentDisplayAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyer_network);

        adapter = new ContentDisplayAdapter(this, partnerList,
                item ->{
                    Intent i = new Intent(DeliveryPartnersActivity.this, ExportLogisticsActivity.class);
                    i.putExtra("delivery_partner_id",item.getID());
                    startActivity(i);
                });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecorator = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(recyclerView.getContext(),R.drawable.divider));
        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(adapter);

        database.getReference("delivery_partners" + "/" + auth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        Log.d("TradeFinance", ": Error getting invoices data");
                        Toast.makeText(this, "Error getting invoice data", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        DataSnapshot snapshot = task.getResult();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Map<String,Object> map = (Map<String, Object>) postSnapshot.getValue();
                            ContentObject item = new ContentObject(
                                    map.get("name").toString(),map.get("pic").toString(), postSnapshot.getKey());
                            partnerList.add(item);
                            adapter.notifyItemInserted(partnerList.size()-1);
                        }
                        if (partnerList.size()>0) findViewById(R.id.empty_view).setVisibility(View.GONE);

                    }
                });

    }

}
