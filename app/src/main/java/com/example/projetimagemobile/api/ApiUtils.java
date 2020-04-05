package com.example.projetimagemobile.api;

public class ApiUtils {

    private ApiUtils() {}

    public final static String API_BASE_URL = "http://10.0.2.2:8000";

    public static ImagesApi getAPIService() {
        return RetrofitClient.getClient(API_BASE_URL).create(ImagesApi.class);
    }

}