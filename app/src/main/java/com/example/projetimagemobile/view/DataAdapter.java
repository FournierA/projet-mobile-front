package com.example.projetimagemobile.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        Glide.with(context).load(ApiUtils.API_BASE_URL+imageUrls.get(i).getImg_path()).into(viewHolder.img);
        viewHolder.score.setText("Score : "+String.valueOf((imageUrls.get(i).getScore())*100)+" %");
        ImageButton arrow_backward = (ImageButton) viewHolder.resultItem.getChildAt(0);
        ImageButton arrow_forward = (ImageButton) viewHolder.resultItem.getChildAt(2);
        if (i == 0) {
            arrow_backward.setVisibility(View.INVISIBLE);
            arrow_forward.setVisibility(View.VISIBLE);
        }else if (i == getItemCount() - 1) {
            arrow_backward.setVisibility(View.VISIBLE);
            arrow_forward.setVisibility(View.INVISIBLE);
        } else {
            arrow_backward.setVisibility(View.VISIBLE);
            arrow_forward.setVisibility(View.VISIBLE);
        }

    }
    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView score;
        RelativeLayout resultItem;

        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.imageView);
            score = view.findViewById(R.id.scoreView);
            resultItem = view.findViewById(R.id.resultItem);
        }

    }



}