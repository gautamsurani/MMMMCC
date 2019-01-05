package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.makemusiccount.android.model.PackageList;

import java.util.List;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.MyViewHolder> {

    private List<PackageList> packageLists;

    private Context context;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListenersxdxsdx) {
        this.mOnItemClickListener = mItemClickListenersxdxsdx;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvPrice, tvDesc, tvPoint1, tvPoint2, tvPoint3, tvPoint4;

        MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvPoint1 = itemView.findViewById(R.id.tvPoint1);
            tvPoint2 = itemView.findViewById(R.id.tvPoint2);
            tvPoint3 = itemView.findViewById(R.id.tvPoint3);
            tvPoint4 = itemView.findViewById(R.id.tvPoint4);
        }
    }

    public PackageAdapter(Context context, List<PackageList> data) {
        this.context = context;
        this.packageLists = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_package, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {

        holder.tvTitle.setText(packageLists.get(listPosition).getName());
        holder.tvPrice.setText(packageLists.get(listPosition).getPlan_price_info());
        holder.tvDesc.setText(packageLists.get(listPosition).getPackage_desc());
        holder.tvPoint1.setText(packageLists.get(listPosition).getPoint1());
        holder.tvPoint2.setText(packageLists.get(listPosition).getPoint2());
        holder.tvPoint3.setText(packageLists.get(listPosition).getPoint3());
        holder.tvPoint4.setText(packageLists.get(listPosition).getPoint4());

        /*Glide.with(context)
                .load(packageLists.get(listPosition).getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.app_logo)
                .into(holder.ivImage);*/
    }

    @Override
    public int getItemCount() {
        return packageLists.size();
    }
}