package com.example.projetimagemobile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImagesApiResponse {

    @SerializedName("img_path")
    @Expose
    private String img_path;

    @SerializedName("score")
    @Expose
    private float score;

    public String getImg_path() {
        return img_path;
    }

    public float getScore() {
        return score;
    }



}
