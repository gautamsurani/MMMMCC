package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.MainActivity;
import com.makemusiccount.android.model.DashboardList;
import com.makemusiccount.android.ui.MyRoundImageView;

import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.MyViewHolder> {

    private List<DashboardList> dashboardLists;

    private Activity context;

    private OnItemClickListener mOnItemClickListener;

    private MaterialShowcaseSequence sequence;

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvCount, tvValue;

        MyRoundImageView ivImg;

        LinearLayout llMain;

        MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCount = itemView.findViewById(R.id.tvCount);
            tvValue = itemView.findViewById(R.id.tvValue);
            ivImg = itemView.findViewById(R.id.ivImg);
            llMain = itemView.findViewById(R.id.llMain);
        }
    }

    private boolean isFirst = true;

    public void startAnimation() {
        sequence.start();
    }

    public DashboardAdapter(Activity context, List<DashboardList> data, MaterialShowcaseSequence sequence) {
        this.context = context;
        this.dashboardLists = data;
        this.sequence = sequence;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dashboard, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {

        holder.tvTitle.setText(dashboardLists.get(listPosition).getTitle());

        if (dashboardLists.get(listPosition).getTitle_sup().isEmpty()) {
            holder.tvCount.setVisibility(View.GONE);
        } else {
            holder.tvCount.setText(dashboardLists.get(listPosition).getTitle_sup());
        }
        if (isFirst) {
            switch (listPosition) {
                case 0:
                    sequence.addSequenceItem(holder.ivImg,
                            "“You can track your progress through the lessons here. Keep track of how well you are applying your math.”", "NEXT");
                    break;
                case 1:
                    sequence.addSequenceItem(holder.ivImg,
                            "“Here’s your badges. Become the most skilled piano mathematician! Your skill will increase as you solve more lessons”", "NEXT");
                    break;
                case 2:
                    sequence.addSequenceItem(holder.ivImg,
                            "“Check can track your point here. Earn more points to came first on leaderboard.”", "NEXT");
                    break;
                case 3:
                    isFirst = false;
                    sequence.addSequenceItem(holder.ivImg, "“Here’s the leader board, score points to keep the number one spot!”", "NEXT");
                    sequence.addSequenceItem(MainActivity.menuView, "“Tap the hamburger menu to get started”", "NEXT");
                    break;
            }
        }

        holder.tvValue.setText(dashboardLists.get(listPosition).getValue());

        holder.tvValue.setTextColor(Color.parseColor(dashboardLists.get(listPosition).getColor_code()));

        Glide.with(context)
                .load(dashboardLists.get(listPosition).getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.app_logo)
                .into(holder.ivImg);

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(listPosition, view, 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dashboardLists.size();
    }
}