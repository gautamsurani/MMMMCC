package com.makemusiccount.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.model.SongEquationList;
import com.makemusiccount.android.model.SongList;

import java.util.List;

/**
 * Created by Welcome on 26-01-2018.
 */

public class SongEquationAdapter extends RecyclerView.Adapter<SongEquationAdapter.MyViewHolder> {

    private List<SongEquationList> songEquationLists;

    private Context context;

    CategoryAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final CategoryAdapter.OnItemClickListener mItemClickListenersxdxsdx) {
        this.mOnItemClickListener = mItemClickListenersxdxsdx;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvLabel;

        MyViewHolder(View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvLabel);
        }
    }

    public SongEquationAdapter(Context context, List<SongEquationList> songEquationLists) {
        this.context = context;
        this.songEquationLists = songEquationLists;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_song_equation, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int listPosition) {
        holder.tvLabel.setText(songEquationLists.get(listPosition).getLabel());
    }

    @Override
    public int getItemCount() {
        return songEquationLists.size();
    }
}
