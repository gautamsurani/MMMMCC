package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import com.makemusiccount.android.model.SongList;

import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;

/*
 * Created by welcome on 24-01-2018.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    private List<SongList> songLists;

    private Context context;

    private OnItemClickListener mOnItemClickListener;

    private MaterialShowcaseSequence sequence;

    public void startAnimation() {
        sequence.start();
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private boolean isFirst = true;

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

    public SongAdapter(Context context, List<SongList> data, MaterialShowcaseSequence sequence) {
        this.context = context;
        this.songLists = data;
        this.sequence = sequence;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_song_list, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {

        Glide.with(context)
                .load(songLists.get(listPosition).getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivImage);

        holder.tvButton.setText("Play Now");

        if (songLists.get(listPosition).getStatus().equals("Inactive")) {
            holder.tvButton.setTextColor(Color.GRAY);
            holder.tvButton.setText("Locked");
        }

        if (songLists.get(listPosition).getPlay_songs().equals("Yes")) {
            holder.ivPlayed.setVisibility(View.VISIBLE);
        } else {
            holder.ivPlayed.setVisibility(View.GONE);
        }

        if (isFirst) {
            switch (listPosition) {
                case 0:
                    sequence.addSequenceItem(holder.tvTitle,
                            "“Click on this song to start and learn song. For each right ans of question you will get 10 point and for wrong, -1 point.”", "Exit");
                    isFirst = false;
                    break;
            }
        }

        holder.tvButton.setOnClickListener(view -> {
            if (songLists.get(listPosition).getStatus().equals("Inactive")) {
                mOnItemClickListener.onItemClick(listPosition, view, 4);
            } else {
                mOnItemClickListener.onItemClick(listPosition, view, 3);
            }
        });

        holder.cvMain.setOnClickListener(view -> {
            if (songLists.get(listPosition).getStatus().equals("Inactive")) {
                mOnItemClickListener.onItemClick(listPosition, view, 4);
            } else {
                mOnItemClickListener.onItemClick(listPosition, view, 3);
            }
        });

        holder.tvTitle.setText(songLists.get(listPosition).getName());
    }

    @Override
    public int getItemCount() {
        return songLists.size();
    }
}