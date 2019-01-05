package com.makemusiccount.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makemusiccount.android.R;
import com.makemusiccount.android.model.HistoryPackageList;

import java.util.List;

public class PackageHistoryAdapter extends RecyclerView.Adapter<PackageHistoryAdapter.ViewHolder> {

    private List<HistoryPackageList> historyPackageLists;

    private Context context;

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvValidity, tvAmount, tvPlan, tvId, tvStartDate;

        ViewHolder(View v) {
            super(v);
            tvValidity = v.findViewById(R.id.tvValidity);
            tvAmount = v.findViewById(R.id.tvAmount);
            tvPlan = v.findViewById(R.id.tvPlan);
            tvId = v.findViewById(R.id.tvId);
            tvStartDate = v.findViewById(R.id.tvStartDate);
        }
    }

    public PackageHistoryAdapter(Activity activity, List<HistoryPackageList> items) {
        context = activity;
        historyPackageLists = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_package_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final HistoryPackageList historyPackageList = historyPackageLists.get(position);

        holder.tvValidity.setText(historyPackageList.getEnd_date());
        holder.tvAmount.setText(historyPackageList.getTotal_amount());
        holder.tvPlan.setText(historyPackageList.getPack_name());
        holder.tvId.setText(historyPackageList.getOrderID());
        holder.tvStartDate.setText(historyPackageList.getStart_date());
    }

    @Override
    public int getItemCount() {
        return historyPackageLists.size();
    }
}