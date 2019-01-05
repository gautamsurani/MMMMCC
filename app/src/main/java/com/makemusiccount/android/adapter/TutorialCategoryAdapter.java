package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import com.makemusiccount.android.model.SongList;
import com.makemusiccount.android.model.TutorialCategoryList;

import java.util.List;

public class TutorialCategoryAdapter extends RecyclerView.Adapter<TutorialCategoryAdapter.MyViewHolder> {

    private List<TutorialCategoryList> tutorialCategoryLists;

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
        ImageView ivImage, ivPlayed;
        CardView cvMain;

        MyViewHolder(View itemView) {
            super(itemView);
            tvButton = itemView.findViewById(R.id.tvButton);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivPlayed = itemView.findViewById(R.id.ivPlayed);
            cvMain = itemView.findViewById(R.id.cvMain);
        }
    }

    public TutorialCategoryAdapter(Context context, List<TutorialCategoryList> data) {
        this.context = context;
        this.tutorialCategoryLists = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_song_list, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {

        Glide.with(context)
                .load(tutorialCategoryLists.get(listPosition).getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.app_logo)
                .into(holder.ivImage);

        holder.tvButton.setText("Play Now");

        if (tutorialCategoryLists.get(listPosition).getStatus().equals("Inactive")) {
            holder.tvButton.setTextColor(Color.GRAY);
            holder.tvButton.setText("Locked");
        }

        if (tutorialCategoryLists.get(listPosition).getComplete_status().equals("Yes")) {
            holder.ivPlayed.setVisibility(View.VISIBLE);
        } else {
            holder.ivPlayed.setVisibility(View.GONE);
        }

        holder.tvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tutorialCategoryLists.get(listPosition).getStatus().equals("Inactive")) {
                    mOnItemClickListener.onItemClick(listPosition, view, 4);
                } else {
                    mOnItemClickListener.onItemClick(listPosition, view, 3);
                }
            }
        });

        holder.cvMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tutorialCategoryLists.get(listPosition).getStatus().equals("Inactive")) {
                    mOnItemClickListener.onItemClick(listPosition, view, 4);
                } else {
                    mOnItemClickListener.onItemClick(listPosition, view, 3);
                }
            }
        });

        holder.tvTitle.setText(tutorialCategoryLists.get(listPosition).getName());
    }

    @Override
    public int getItemCount() {
        return tutorialCategoryLists.size();
    }
}