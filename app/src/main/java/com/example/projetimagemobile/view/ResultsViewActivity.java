package com.example.projetimagemobile.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.projetimagemobile.R;
import com.example.projetimagemobile.api.ApiUtils;
import com.example.projetimagemobile.api.ImagesApi;
import com.example.projetimagemobile.model.ImageApiBody;
import com.example.projetimagemobile.model.ImagesApiResponse;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultsViewActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ImagesApi mAPIService;
    private String filePath;
    private List<ImagesApiResponse> dataArrayList;
    private RecyclerView.SmoothScroller smoothScroller;
    private ProgressBar loadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_view);

        relativeLayout = findViewById(R.id.resultLayout);
        relativeLayout.getBackground().setAlpha(128);
        loadProgress = findViewById(R.id.progressBar);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView = findViewById(R.id.recyclerView);

        smoothScroller = new LinearSmoothScroller(getApplicationContext()) {
            @Override protected int getHorizontalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        SnapHelper snapHelper = new PagerSnapHelper();
        recyclerView.setLayoutManager(linearLayoutManager);
        snapHelper.attachToRecyclerView(recyclerView);

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
                    Toast.makeText(ResultsViewActivity.this, "Chargement en cours...", Toast.LENGTH_SHORT).show();
                    loadProgress.setVisibility(View.GONE);
                    getPictures();
                } else {
                    Log.d("LOG_TAG", "Failure Response : " + response.raw().toString());
                }
            }
            @Override
            public void onFailure(Call<ImageApiBody> call, Throwable t) {
                Log.d("LOG_TAG", "URL : " + call.request().toString());
                Log.d("LOG_TAG", "POST Error onFailure:  " + t.getMessage());
                relativeLayout.setBackground(null);
                loadProgress.setVisibility(View.GONE);
                Toast.makeText(ResultsViewActivity.this, "Une erreur est survenue - Temps de réponse dépassé", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getPictures() {
        dataArrayList = new ArrayList<>();
        loadProgress.setVisibility(View.VISIBLE);
        Toast.makeText(ResultsViewActivity.this, "Récupération des images en cours...", Toast.LENGTH_LONG).show();
        mAPIService.getImagesList("json").enqueue(new Callback<List<ImagesApiResponse>>() {
            @Override
            public void onResponse(Call<List<ImagesApiResponse>> call, Response<List<ImagesApiResponse>> response) {
                if(response.isSuccessful()) {
                    dataArrayList = response.body();
                    DataAdapter dataAdapter = new DataAdapter(getApplicationContext(), dataArrayList);
                    recyclerView.setAdapter(dataAdapter);
                    relativeLayout.setBackground(null);
                    loadProgress.setVisibility(View.GONE);

                    String json = new Gson().toJson(dataArrayList);
                    Log.d("LOG_TAG", "Object to String : " +  json);
                } else {
                    Log.d("LOG_TAG", "Failure Response : " + response.raw().toString());
                    relativeLayout.setBackground(null);
                    loadProgress.setVisibility(View.GONE);
                    Toast.makeText(ResultsViewActivity.this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<ImagesApiResponse>> call, Throwable t) {
                Log.d("LOG_TAG", "URL : " + call.request().toString());
                Log.d("LOG_TAG", "GET Error onFailure :  " + t.getMessage());
                relativeLayout.setBackground(null);
                loadProgress.setVisibility(View.GONE);
                Toast.makeText(ResultsViewActivity.this, "Une erreur est survenue - Temps de réponse dépassé", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void goNext(View v) {
        View positionView = recyclerView.getChildAt(0);
        try {
            if (!smoothScroller.isRunning()) {
                smoothScroller.setTargetPosition(recyclerView.getChildAdapterPosition(positionView) + 1);
                linearLayoutManager.startSmoothScroll(smoothScroller);
                recyclerView.getChildAt(0).setSelected(true);
            }
        } catch (Exception e) {
            Log.d("LOG_TAG", e.getLocalizedMessage());
            Toast.makeText(this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();

        }
    }

    public void goBack(View v) {
        View positionView = recyclerView.getChildAt(0);
        try {
            if (!smoothScroller.isRunning()) {
                smoothScroller.setTargetPosition(recyclerView.getChildAdapterPosition(positionView) - 1);
                linearLayoutManager.startSmoothScroll(smoothScroller);
                recyclerView.setSelected(true);
            }
        } catch (Exception e) {
            Log.d("LOG_TAG", e.getLocalizedMessage());
            Toast.makeText(this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
        }
    }

}
