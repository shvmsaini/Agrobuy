package com.agrobuy.app.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.agrobuy.app.object.ContentObject;
import com.agrobuy.app.OnItemClickListener;
import com.agrobuy.app.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class ContentDisplayAdapter extends RecyclerView.Adapter<ContentDisplayAdapter.ItemViewHolder>{
    private final String LOG_TAG = getClass().toString();
    private List<ContentObject> mContentObjectList;
    private Context mContext;
    private final OnItemClickListener listener;

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view= inflater.inflate(R.layout.list_item,parent,false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        return viewHolder;
    }

    public ContentDisplayAdapter(Context mContext, List<ContentObject> mContentObjectList,OnItemClickListener listener) {
        this.mContext=mContext;
        this.mContentObjectList = mContentObjectList;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Log.d(LOG_TAG,"bindViewHolder");
        ContentObject contentObject = mContentObjectList.get(position);
        Glide.with(mContext).load(contentObject.getProfilePicURL()).placeholder(R.drawable.ic_baseline_person_96).into(holder.pic);
        holder.bind(contentObject,listener);
    }

    @Override
    public int getItemCount() {
        return mContentObjectList.size();
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
        public void bind(final ContentObject item, final OnItemClickListener listener) {
            title.setText(item.getName());
            subTitle.setText(item.getSubLine());
//            Glide.with(itemView.getContext()).load(item.profilePicURL).into(pic);
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}