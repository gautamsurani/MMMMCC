package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.model.PointModel;

import java.util.List;

public class PointHistoryAdapter extends RecyclerView.Adapter<PointHistoryAdapter.ViewHolder> {

    Activity context;
    private List<PointModel> listData;

    public PointHistoryAdapter(Activity context, List<PointModel> bean) {
        this.listData = bean;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wallethistory, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PointModel bean = listData.get(position);

        holder.tvTransactiondate.setText(bean.getDate());
        holder.tvTransactionTitle.setText(bean.getRemark());

        if (bean.getType().equalsIgnoreCase("blue")) {
            holder.tvTransactionAmount.setText(bean.getSymbol() + " " + bean.getPoints());
            holder.tvTransactionAmount.setTextColor(ContextCompat.getColor(context, R.color.green));
            Glide.with(context)
                    .load(R.drawable.ic_bluedot)
                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.app_logo)
                    .into(holder.imgTransactionStatus);
        } else {
            holder.tvTransactionAmount.setText(bean.getSymbol() + " " + bean.getPoints());
            holder.tvTransactionAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
            Glide.with(context)
                    .load(R.drawable.ic_reddot)
                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.app_logo)
                    .into(holder.imgTransactionStatus);
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

        TextView tvTransactiondate, tvTransactionTitle, tvTransactionAmount;
        ImageView imgTransactionStatus;

        public ViewHolder(View v) {
            super(v);
            tvTransactiondate = v.findViewById(R.id.tvTransactiondate);
            tvTransactionTitle = v.findViewById(R.id.tvTransactionTitle);
            tvTransactionAmount = v.findViewById(R.id.tvTransactionAmount);
            imgTransactionStatus = v.findViewById(R.id.imgTransactionStatus);

        }

    }

}

