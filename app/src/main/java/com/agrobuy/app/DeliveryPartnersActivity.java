package com.agrobuy.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference myRef = database.getReference();
    public List<ContentObject> partnerList = new ArrayList<>();
    RecyclerView recyclerView;
    ContentDisplayAdapter adapter;
    TextView emptyView;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyer_network);
        emptyView = findViewById(R.id.empty_view);
        emptyView.setText("Loading...");

        adapter = new ContentDisplayAdapter(this, partnerList,
                item ->{
                    Intent i = new Intent(DeliveryPartnersActivity.this, ExportLogisticsActivity.class);
                    i.putExtra("delivery_partner_id",item.getID());
                    startActivity(i);
                });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecorator = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        Drawable divider = ContextCompat.getDrawable(recyclerView.getContext(),R.drawable.divider);
        if(divider!=null){
            itemDecorator.setDrawable(divider);
            recyclerView.addItemDecoration(itemDecorator);
        }
        recyclerView.setAdapter(adapter);

        if(auth.getCurrentUser()!=null){
            database.getReference("delivery_partners" + "/" + auth.getCurrentUser().getUid()).get()
                    .addOnCompleteListener(task -> {
                        if(!task.isSuccessful()){
                            emptyView.setText("Error getting partners data");
                            Log.d("TradeFinance", ": Error getting partners data");
                        }
                        else{
                            DataSnapshot snapshot = task.getResult();
                            if(!snapshot.exists()){
                                emptyView.setText(R.string.no_delivery_partners);
                                return;
                            }
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                Map<String,Object> map = (Map<String, Object>) postSnapshot.getValue();
                                if(map==null){
                                    emptyView.setText(R.string.no_delivery_partners);
                                    Log.d("TradeFinance", ":Received Map is null");
                                    return;
                                }
                                ContentObject item = new ContentObject(
                                        String.valueOf(map.get("name")),
                                        String.valueOf(map.get("pic")), postSnapshot.getKey());
                                partnerList.add(item);
                                adapter.notifyItemInserted(partnerList.size()-1);
                            }
                            if (partnerList.size()>0) findViewById(R.id.empty_view).setVisibility(View.GONE);

                        }
                    });
        }


    }

}
