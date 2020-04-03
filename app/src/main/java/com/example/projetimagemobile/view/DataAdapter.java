package com.example.projetimagemobile.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projetimagemobile.R;
import com.example.projetimagemobile.api.ApiUtils;
import com.example.projetimagemobile.model.ImagesApiResponse;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private List<ImagesApiResponse> imageUrls;
    private Context context;

    public DataAdapter(Context context, List<ImagesApiResponse> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_layout, viewGroup, false);
        return new ViewHolder(view);
    }
    /**
     * gets the image url from adapter and passes to Glide API to load the image
     * @param viewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Glide.with(context).load(ApiUtils.API_BASE_URL+imageUrls.get(i).getImg_path()).into(viewHolder.img);
    }
    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.imageView);
        }
    }



}