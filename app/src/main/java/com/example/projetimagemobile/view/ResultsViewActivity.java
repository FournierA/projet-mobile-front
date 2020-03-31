package com.example.projetimagemobile.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;

import com.example.projetimagemobile.R;
import com.example.projetimagemobile.api.ApiUtils;
import com.example.projetimagemobile.api.ImagesApi;
import com.example.projetimagemobile.model.ImageApiBody;
import com.example.projetimagemobile.model.ImagesApiResponse;
import com.google.gson.Gson;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ResultsViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ImagesApi mAPIService;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_view);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);

        filePath = getIntent().getExtras().getString("filePath","/");
        Log.d("LOG_TAG", filePath);

        mAPIService = ApiUtils.getAPIService();
        postPicture();

    }

    public void postPicture() {
        File file = new File(filePath);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData(
                "file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));


        mAPIService.postImage(body).enqueue(new Callback<ImageApiBody>() {
            @Override
            public void onResponse(Call<ImageApiBody> call, Response<ImageApiBody> response) {
                if(response.isSuccessful()) {
                    Log.d("LOG_TAG", "Success Response : " + response.body());
                    getPictures();
                } else {
                    Log.d("LOG_TAG", "Failure Response : " + response.raw().toString());
                }
            }
            @Override
            public void onFailure(Call<ImageApiBody> call, Throwable t) {
                Log.d("LOG_TAG", "URL : " + call.request().toString());
                Log.d("LOG_TAG", "Error :  " + t.getMessage());
            }
        });
    }

    public void getPictures() {
        mAPIService.getImagesList("json").enqueue(new Callback<ImagesApiResponse>() {
            @Override
            public void onResponse(Call<ImagesApiResponse> call, Response<ImagesApiResponse> response) {
                if(response.isSuccessful()) {
                    ImagesApiResponse images = response.body();
                    DataAdapter dataAdapter = new DataAdapter(getApplicationContext(), images);
                    recyclerView.setAdapter(dataAdapter);

                    String json = new Gson().toJson(images);
                    Log.d("LOG_TAG", "Object to String : " +  json);
                } else {
                    Log.d("LOG_TAG", "Failure Response : " + response.raw().toString());
                }
            }
            @Override
            public void onFailure(Call<ImagesApiResponse> call, Throwable t) {
                Log.d("LOG_TAG", "URL : " + call.request().toString());
                Log.d("LOG_TAG", "Error :  " + t.getMessage());
            }
        });
    }



}
