package com.agrobuy.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agrobuy.app.adapters.InvoiceDisplayAdapter;
import com.agrobuy.app.object.Invoice;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyInvoicesActivity extends Activity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    public List<Invoice> invoiceList = new ArrayList<>();
    RecyclerView recyclerView;
    InvoiceDisplayAdapter adapter;
    TextView emptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyer_network);
        adapter = new InvoiceDisplayAdapter(this,invoiceList);
        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.empty_view);
        emptyView.setText("Loading...");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecorator = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(recyclerView.getContext(),R.drawable.divider));
        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(adapter);

        database.getReference("users" + "/" + auth.getCurrentUser().getUid() + "/" + "invoices").get()
                .addOnCompleteListener(task -> {
                    Log.d("MyInvoices","added onCompleteListener");
                    if(!task.isSuccessful()){
                        Log.d("MyInvoices", ": Error getting invoices data");
                        Toast.makeText(this, "Error getting invoice data", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.d("MyInvoices", ": Task Successful");
                        DataSnapshot snapshot = task.getResult();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Map<String,Object> map = (Map<String, Object>) postSnapshot.getValue();
                            Invoice item;
                            if(map ==null){
                                emptyView.setText("No invoices found. Go back and create one.");
                            }
                            if(map.containsKey("link")){
                              item = new Invoice(postSnapshot.getKey(),map.get("link").toString());
                            }else{
                                item = new Invoice(
                                        postSnapshot.getKey(),
                                        map.get("customer_name").toString(),
                                        map.get("invoice_amount").toString(),
                                        map.get("invoice_due_date").toString(),
                                        map.get("payment_terms").toString(),
                                        map.get("delivery_mode").toString(),
                                        map.get("delivery_destination").toString());
                            }
                            Log.d(MyInvoicesActivity.class.getName(),"item added: " + item);
                            invoiceList.add(item);
                            adapter.notifyItemInserted(invoiceList.size()-1);
                        }
                        if (invoiceList.size()>0) findViewById(R.id.empty_view).setVisibility(View.GONE);
                        else {
                            emptyView.setText("No invoices found. Go back and create one.");
                        }
                    }
                });


    }
}
