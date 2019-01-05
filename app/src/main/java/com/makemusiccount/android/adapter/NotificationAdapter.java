package com.makemusiccount.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.makemusiccount.android.activity.MainActivity;
import com.makemusiccount.android.model.NotificationList;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationList> notificationLists;

    private Context context;

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvTitle, tvMsg, tvButton;
        ImageView ivIcon;
        LinearLayout llMain;

        ViewHolder(View v) {
            super(v);
            tvDate = v.findViewById(R.id.tvDate);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvMsg = v.findViewById(R.id.tvMsg);
            tvButton = v.findViewById(R.id.tvButton);
            ivIcon = v.findViewById(R.id.ivIcon);
            llMain = v.findViewById(R.id.llMain);
        }
    }

    public NotificationAdapter(Activity activity, List<NotificationList> items) {
        context = activity;
        notificationLists = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final NotificationList notificationList = notificationLists.get(position);

        holder.tvDate.setText(notificationList.getDate());

        if (position > 0) {
            if (notificationLists.get(position).getDate().equals(notificationLists.get(position - 1).getDate())) {
                holder.tvDate.setVisibility(View.GONE);
            } else {
                holder.tvDate.setVisibility(View.VISIBLE);
            }
        }

        holder.tvTitle.setText(notificationList.getTitle());

        holder.tvMsg.setText(notificationList.getMessage());

        holder.tvButton.setText(notificationList.getButton_name());

        Glide.with(context)
                .load(notificationList.getIcon())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivIcon);

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (notificationList.getType()) {
                    case "Leaderboard": {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("screenType", "Leaderboard");
                        context.startActivity(intent);
                        break;
                    }
                    case "Songs": {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("screenType", "Songs");
                        context.startActivity(intent);
                        break;
                    }
                    case "Category": {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("screenType", "Category");
                        context.startActivity(intent);
                        break;
                    }
                    case "Tutorial": {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("screenType", "Tutorial");
                        context.startActivity(intent);
                        break;
                    }
                    default: {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("screenType", "Songs");
                        context.startActivity(intent);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationLists.size();
    }
}