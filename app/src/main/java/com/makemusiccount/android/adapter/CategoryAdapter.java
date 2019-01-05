package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.model.CategoryList;

import java.util.List;

/*
 * Created by Welcome on 25-01-2018.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private List<CategoryList> categoryLists;

    private Context context;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListenersxdxsdx) {
        this.mOnItemClickListener = mItemClickListenersxdxsdx;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvButton, tvTitle;
        ImageView ivImage;
        CardView llMain;

        MyViewHolder(View itemView) {
            super(itemView);
            tvButton = itemView.findViewById(R.id.tvButton);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivImage = itemView.findViewById(R.id.ivImage);
            llMain = itemView.findViewById(R.id.llMain);
        }
    }

    public CategoryAdapter(Context context, List<CategoryList> categoryLists) {
        this.context = context;
        this.categoryLists = categoryLists;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {

        Glide.with(context)
                .load(categoryLists.get(listPosition).getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.app_logo)
                .into(holder.ivImage);

        holder.tvButton.setText(categoryLists.get(listPosition).getName());

        holder.tvButton.setOnClickListener(view -> {
            if (categoryLists.get(listPosition).getSub_cats().equals("Yes")) {
                mOnItemClickListener.onItemClick(listPosition, view, 2);
            } else {
                mOnItemClickListener.onItemClick(listPosition, view, 3);
            }
        });

        holder.llMain.setOnClickListener(view -> {
            if (categoryLists.get(listPosition).getSub_cats().equals("Yes")) {
                mOnItemClickListener.onItemClick(listPosition, view, 2);
            } else {
                mOnItemClickListener.onItemClick(listPosition, view, 3);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryLists.size();
    }
}