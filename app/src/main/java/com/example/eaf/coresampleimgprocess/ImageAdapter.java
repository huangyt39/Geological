package com.example.eaf.coresampleimgprocess;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private static final String TAG = "ImageAdapter";

    private List<SubImage> subImageList;
    private MainActivity activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.sub_item_item_image_view);
            textView = (TextView) view.findViewById(R.id.sub_item_item_text_view);
        }
    }

    public ImageAdapter(List<SubImage> subImages, MainActivity mainActivity) {
        this.subImageList = subImages;
        this.activity = mainActivity;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SubImage subImage = subImageList.get(position);
//        holder.imageView.setImageResource(subImage.getBitmap());
        holder.imageView.setImageBitmap(subImage.getBitmap());
        holder.textView.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return subImageList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_image_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                SubImage image = subImageList.get(position);
                Log.d(TAG, "onClick: the bitmap of imageDetailFragment changed");
                ImageDetailFragment.bitmap = image.getBitmap();

                activity.switchToImageDetailFragment();
            }
        });

        return holder;
    }
}
