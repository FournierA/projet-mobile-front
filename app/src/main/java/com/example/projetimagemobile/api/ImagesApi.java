package com.example.projetimagemobile.api;

import com.example.projetimagemobile.model.ImageApiBody;
import com.example.projetimagemobile.model.ImagesApiResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ImagesApi {

        @GET("/api/images/")
        Call<ImagesApiResponse> getImagesList(@Query("format") String format);

        @Multipart
        @POST("/api/images/")
        Call<ImageApiBody> postImage(@Part MultipartBody.Part file);

}
