package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.makemusiccount.android.model.CategoryList;
import com.makemusiccount.android.model.LeaderList;

import java.util.List;

/**
 * Created by Welcome on 19-02-2018.
 */

public class DashboardLeaderAdapter extends RecyclerView.Adapter<DashboardLeaderAdapter.MyViewHolder> {

    private List<LeaderList> leaderLists;

    private Context context;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListenersxdxsdx) {
        this.mOnItemClickListener = mItemClickListenersxdxsdx;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPoint;

        MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPoint = itemView.findViewById(R.id.tvPoint);
        }
    }

    public DashboardLeaderAdapter(Context context, List<LeaderList> leaderLists) {
        this.context = context;
        this.leaderLists = leaderLists;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dashboard_leader_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {

        holder.tvName.setText(leaderLists.get(listPosition).getName());

        holder.tvPoint.setText(leaderLists.get(listPosition).getPoint());
    }

    @Override
    public int getItemCount() {
        return leaderLists.size();
    }
}