package com.android.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.android.popularmovies.R;
import com.android.popularmovies.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    public List<Review> reviewList;
    private Context rContext;

    public ReviewAdapter(List<Review> reviewList, Context rContext){
        this.reviewList = reviewList;
        this.rContext = rContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_review , parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Review reviewSelected = reviewList.get(position);
        holder.author.setText(reviewSelected.getAuthor());
        holder.setClickListener(new ItemClickListener() {    // Bind the listener
            @Override
            public void onClick(View view, int position) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(reviewSelected.getUrl()));
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                rContext.startActivity(browserIntent);
                Log.d("Click on Review ",rContext.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (reviewList != null){ return reviewList.size(); }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView author;
        private ItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.tv_author);
            itemView.setOnClickListener(this);
        }

        private void setClickListener(ItemClickListener itemClickListener){
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick( view, getAdapterPosition());
        }

    }

}
