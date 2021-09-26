package com.agrobuy.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agrobuy.app.adapters.BuyerDetailAdapter;
import com.agrobuy.app.object.BuyerObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BuyerNetworkActivity extends Activity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    public List<BuyerObject> buyerList = new ArrayList<>();
    RecyclerView recyclerView;
    BuyerDetailAdapter adapter;
    TextView emptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyer_network);
        emptyView = findViewById(R.id.empty_view);
        emptyView.setText("Loading...");
        adapter = new BuyerDetailAdapter(this, buyerList,
                item ->{
                    Intent i = new Intent(this, BuyerDetailActivity.class);
                    i.putExtra("buyerList", (Parcelable) item);
                    startActivity(i);
                });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecorator = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(recyclerView.getContext(),R.drawable.divider));
        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(adapter);


        database.getReference("buyer_network" + "/" + auth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        emptyView.setText("Error getting buyers data");
                        Log.d("BuyerNetwork", ": Error getting buyers data");
                        Toast.makeText(this, "Error getting buyers data", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        DataSnapshot snapshot = task.getResult();
                        if(!snapshot.exists()){
                            emptyView.setText("No buyers found.");
                            return;
                        }
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Map<String,Object> map = (Map<String, Object>) postSnapshot.getValue();
                            BuyerObject item = new BuyerObject(
                                    map.get("contact_person_name").toString(),
                                    map.get("categories").toString(),
                                    map.get("date_of_incorporation").toString(),
                                    map.get("importer_credit_rating").toString(),
                                    map.get("importer_history").toString(),
                                    map.get("contact_time_preference").toString(),
                                    map.get("company_phone_number").toString(),
                                    map.get("contact_personal_number").toString(),
                                    map.get("contact_person_email").toString(),
                                    map.get("pic_url").toString(),
                                    map.get("buyer_name").toString(),
                                    postSnapshot.getKey());
                            buyerList.add(item);
                            adapter.notifyItemInserted(buyerList.size()-1);
                        }
                        if (buyerList.size()>0) findViewById(R.id.empty_view).setVisibility(View.GONE);

                    }
                });

    }

}
