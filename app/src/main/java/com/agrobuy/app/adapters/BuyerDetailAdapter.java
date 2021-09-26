package com.agrobuy.app.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrobuy.app.R;
import com.agrobuy.app.buyerOnItemClickListener;
import com.agrobuy.app.object.BuyerObject;
import com.bumptech.glide.Glide;

import java.util.List;

public class BuyerDetailAdapter extends RecyclerView.Adapter<BuyerDetailAdapter.ItemViewHolder> {
    private static final String LOG_TAG = BuyerDetailAdapter.class.getName();
    public List<BuyerObject> buyerObjectList;
    public Context mContext;
    private final buyerOnItemClickListener listener;

    public BuyerDetailAdapter(android.content.Context context, List<BuyerObject> buyerObjectList,
                              buyerOnItemClickListener listener) {
        this.buyerObjectList = buyerObjectList;
        this.mContext = context;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item,parent,false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Log.d(LOG_TAG,"bindViewHolder");
        BuyerObject buyerObject = buyerObjectList.get(position);
        holder.bind(buyerObject,listener);
    }

    @Override
    public int getItemCount() {
        return buyerObjectList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title,subTitle;
        ImageView pic;

        public ItemViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subTitle = itemView.findViewById(R.id.sub_title);
            pic=itemView.findViewById(R.id.thumbnail);

        }
        public void bind(final BuyerObject item, final buyerOnItemClickListener listener) {
            title.setText(item.getBuyerName());
//            subTitle.setText(item.getSubLine());
            Glide.with(itemView.getContext()).load(item.getPicURL()).placeholder(R.drawable.ic_baseline_person_24).into(pic);

            itemView.setOnClickListener(view -> listener.onItemClick(item));
        }

    }
}
