package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.makemusiccount.android.R;
import com.makemusiccount.android.model.PackageList;

import java.util.List;

public class SubscribeAdapter extends RecyclerView.Adapter<SubscribeAdapter.ViewHolder> {

    private List<PackageList> packageLists;

    private Context context;

    private int selectedPosition = 0;

    public Integer getSelectedPosition() {
        return selectedPosition;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPrice;

        LinearLayout llMain;

        RadioButton rbSelect;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvPrice = v.findViewById(R.id.tvPrice);
            llMain = v.findViewById(R.id.llMain);
            rbSelect = v.findViewById(R.id.rbSelect);
        }
    }

    public SubscribeAdapter(Activity activity, List<PackageList> items) {
        context = activity;
        packageLists = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_subscriber, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.setIsRecyclable(false);

        final PackageList packageList = packageLists.get(position);

        holder.tvName.setText(packageList.getName());

        if (position == selectedPosition) {
            holder.rbSelect.setChecked(true);
            holder.llMain.setBackgroundColor(Color.parseColor("#23ffffff"));
        } else {
            holder.rbSelect.setChecked(false);
            holder.llMain.setBackgroundColor(Color.parseColor("#00ffffff"));
        }

        holder.tvPrice.setText(packageList.getPlan_price_info());

        holder.llMain.setOnClickListener(view -> {
            selectedPosition = position;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return packageLists.size();
    }
}