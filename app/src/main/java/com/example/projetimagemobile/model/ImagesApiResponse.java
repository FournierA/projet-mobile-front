package com.example.projetimagemobile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ImagesApiResponse {

    @SerializedName("images")
    @Expose
    ArrayList<Images> images = new ArrayList<>();

    public ArrayList<Images> getImages() {
        return images;
    }

    public class Images {
        @SerializedName("id")
        @Expose
        private int id;

        @SerializedName("file")
        @Expose
        private String file;

        public int getId() {
            return id;
        }

        public String getFile() {
            return file;
        }
    }

}
