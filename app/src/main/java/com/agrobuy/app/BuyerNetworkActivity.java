package com.agrobuy.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference myRef = database.getReference();
    public List<BuyerObject> buyerList = new ArrayList<>();
    RecyclerView recyclerView;
    BuyerDetailAdapter adapter;
    TextView emptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyer_network);
        emptyView = findViewById(R.id.empty_view);
        emptyView.setText(R.string.loading);
        adapter = new BuyerDetailAdapter(this, buyerList,
                item -> {
                    Intent i = new Intent(this, BuyerDetailActivity.class);
                    i.putExtra("buyerList", (Parcelable) item);
                    startActivity(i);
                });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration itemDecorator = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        Drawable divider = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.divider);
        if (divider != null) {
            itemDecorator.setDrawable(divider);
            recyclerView.addItemDecoration(itemDecorator);
        }
        recyclerView.setAdapter(adapter);

        if (auth.getCurrentUser() != null) {
            database.getReference("buyer_network" + "/" + auth.getCurrentUser().getUid()).get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            emptyView.setText(R.string.error_getting_buyers);
                            Log.d("BuyerNetwork", ": Error getting buyers data");
                        } else {
                            DataSnapshot snapshot = task.getResult();
                            if (!snapshot.exists()) {
                                emptyView.setText(R.string.no_buyers);
                                return;
                            }
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                Map<String, Object> map = (Map<String, Object>) postSnapshot.getValue();
                                if (map == null) {
                                    emptyView.setText(R.string.no_buyers);
                                    return;
                                }
                                BuyerObject item = new BuyerObject(
                                        String.valueOf(map.get("contact_person_name")),
                                        String.valueOf(map.get("categories")),
                                        String.valueOf(map.get("date_of_incorporation")),
                                        String.valueOf(map.get("importer_credit_rating")),
                                        String.valueOf(map.get("importer_history")),
                                        String.valueOf(map.get("contact_time_preference")),
                                        String.valueOf(map.get("company_phone_number")),
                                        String.valueOf(map.get("contact_personal_number")),
                                        String.valueOf(map.get("contact_person_email")),
                                        String.valueOf(map.get("pic_url")),
                                        String.valueOf(map.get("buyer_name")),
                                        postSnapshot.getKey());
                                buyerList.add(item);
                                adapter.notifyItemInserted(buyerList.size() - 1);
                            }
                            if (buyerList.size() > 0)
                                findViewById(R.id.empty_view).setVisibility(View.GONE);

                        }
                    });
        }


    }

}
