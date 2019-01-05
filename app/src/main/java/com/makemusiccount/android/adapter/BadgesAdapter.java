package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.model.BadgesList;

import java.util.List;

public class BadgesAdapter extends RecyclerView.Adapter<BadgesAdapter.ViewHolder> {

    Activity context;
    private List<BadgesList> listData;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public BadgesAdapter(Activity context, List<BadgesList> bean) {
        this.listData = bean;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_badges, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final BadgesList bean = listData.get(position);
        holder.tvTitle.setText(bean.getName(), TextView.BufferType.SPANNABLE);

        Glide.with(context)
                .load(bean.getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.ivImage);

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(position, view, 0);
            }
        });

        if (bean.getStatus().equals("Active")) {
            holder.ivImageOver.setVisibility(View.GONE);
            holder.ivStatus.setImageResource(R.drawable.correct);
        } else {
            holder.ivImageOver.setVisibility(View.VISIBLE);
            holder.ivStatus.setImageResource(R.drawable.red_lock);
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView ivImage, ivImageOver, ivStatus;
        LinearLayout llMain;

        public ViewHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            ivImage = v.findViewById(R.id.ivImage);
            llMain = v.findViewById(R.id.llMain);
            ivStatus = v.findViewById(R.id.ivStatus);
            ivImageOver = v.findViewById(R.id.ivImageOver);
        }
    }
}