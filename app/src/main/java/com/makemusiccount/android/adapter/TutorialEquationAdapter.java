package com.makemusiccount.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makemusiccount.android.R;
import com.makemusiccount.android.model.SongEquationList;
import com.makemusiccount.android.model.TutorialEquationList;

import java.util.List;

public class TutorialEquationAdapter extends RecyclerView.Adapter<TutorialEquationAdapter.MyViewHolder> {

    private List<TutorialEquationList> tutorialEquationLists;

    private Context context;

    OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvLabel;

        MyViewHolder(View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvLabel);
        }
    }

    public TutorialEquationAdapter(Context context, List<TutorialEquationList> tutorialEquationLists) {
        this.context = context;
        this.tutorialEquationLists = tutorialEquationLists;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_song_equation, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        holder.tvLabel.setText(tutorialEquationLists.get(listPosition).getLabel());

    }

    @Override
    public int getItemCount() {
        return tutorialEquationLists.size();
    }
}