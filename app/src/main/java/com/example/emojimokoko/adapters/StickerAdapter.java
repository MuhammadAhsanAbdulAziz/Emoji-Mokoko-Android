package com.example.emojimokoko.adapters;


import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emojimokoko.R;

import java.util.ArrayList;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(String str);
    }

    Context context;
    ArrayList<Integer> stickerResIds;
    OnItemClickListener listener;

    public StickerAdapter(Context context, ArrayList<Integer> stickerResIds, OnItemClickListener listener) {
        this.context = context;
        this.stickerResIds = stickerResIds;
        this.listener = listener;
    }

    public void setData(ArrayList<Integer> newEmojiData) {
        this.stickerResIds = newEmojiData;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sticker_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.stickerImageView.setImageResource(stickerResIds.get(position));
    }

    @Override
    public int getItemCount() {
        return stickerResIds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView stickerImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stickerImageView = itemView.findViewById(R.id.stickerImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getLayoutPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the clicked sticker resource ID
//                        int clickedStickerResId = stickerResIds.get(position);
//                        String str = String.valueOf(clickedStickerResId);
//                        // Handle sending the sticker
//                        listener.onItemClick(str);
                        int clickedStickerResId = stickerResIds.get(position);
                        Resources resources = context.getResources();
                        String str = resources.getResourceEntryName(clickedStickerResId);
                        listener.onItemClick(str);
                    }

                }
            });
        }
    }
}


