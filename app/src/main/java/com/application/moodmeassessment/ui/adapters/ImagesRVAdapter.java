package com.application.moodmeassessment.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.application.moodmeassessment.R;

import java.util.List;

public class ImagesRVAdapter extends RecyclerView.Adapter<ImagesRVAdapter.ViewHolder> {

    Context context;
    List<Integer> imagesList;
    private final OnItemClickListener listener;

    int selectedPosition=-1;

    public interface OnItemClickListener {
        void onItemClick(Integer item);
    }

    public ImagesRVAdapter(Context context, List<Integer> imagesList, OnItemClickListener listener) {
        this.context = context;
        this.imagesList = imagesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImagesRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesRVAdapter.ViewHolder holder, int position) {
        holder.bind(imagesList.get(position));

        if(selectedPosition==position)
            holder.itemView.findViewById(R.id.imageBorder).setBackground(context.getDrawable(R.drawable.selected_image));
        else
            holder.itemView.findViewById(R.id.imageBorder).setBackground(null);

        holder.itemView.setOnClickListener( view -> {
            listener.onItemClick(imagesList.get(position));
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public CardView imageBorder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.itemImage);
            imageBorder = itemView.findViewById(R.id.imageBorder);
        }

        public void bind(Integer image) {
            imageView.setImageResource(image);
        }
    }
}
