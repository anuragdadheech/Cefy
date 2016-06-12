package com.cefy.cefy.network;

import com.cefy.cefy.models.LinkedInUser;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Anurag
 */
public class LinkedInService {
    private CefyAPI cefyAPI = null;

    public LinkedInService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.linkedin.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        cefyAPI = retrofit.create(CefyAPI.class);
    }

    public void loadLinkedInProfile(String token, final Callback<LinkedInUser> callback){
        Call<LinkedInUser> call = cefyAPI.loadLinkedInProfile(token);
        call.enqueue(new Callback<LinkedInUser>() {
            @Override
            public void onResponse(Call<LinkedInUser> call, Response<LinkedInUser> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<LinkedInUser> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }
}
