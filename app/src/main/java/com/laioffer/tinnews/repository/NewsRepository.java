package com.laioffer.tinnews.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.laioffer.tinnews.TinNewsApplication;
import com.laioffer.tinnews.database.AppDatabase;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.model.NewsResponse;
import com.laioffer.tinnews.network.NewsApi;
import com.laioffer.tinnews.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {
    private final NewsApi newsApi;
    private final AppDatabase database;
    private AsyncTask asyncTask;

    public NewsRepository(Context context){
        newsApi = RetrofitClient.newInstance(context).create(NewsApi.class);
        // DataBase Access
        database = TinNewsApplication.getDatabase();
    }

    // Implement get getTopHeadlines API
    public LiveData<NewsResponse> getTopHeadlines(String country){
        // topHeadlinesLiveData 向外传输数据
        MutableLiveData<NewsResponse> topHeadlinesLiveData = new MutableLiveData<>();
        newsApi.getTopHeadlines(country)
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                        if (response.isSuccessful()){
                            topHeadlinesLiveData.setValue(response.body());
                        } else {
                            topHeadlinesLiveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<NewsResponse> call, Throwable t) {
                        topHeadlinesLiveData.setValue(null);
                    }
                });
        return topHeadlinesLiveData;
    }

    // Implement searchNews API
    public LiveData<NewsResponse> searchNews(String query){
        MutableLiveData<NewsResponse> everyThingLiveData = new MutableLiveData<>();
        newsApi.getEverything(query, 40)
                .enqueue(
                        new Callback<NewsResponse>() {
                            @Override
                            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                                if (response.isSuccessful()) {
                                    everyThingLiveData.setValue(response.body());
                                } else {
                                    everyThingLiveData.setValue(null);
                                }
                            }

                            @Override
                            public void onFailure(Call<NewsResponse> call, Throwable t) {
                                    everyThingLiveData.setValue(null);
                            }
                        }
                );
        return everyThingLiveData;
    }

    public LiveData<Boolean> favoriteArticle(Article article) {
        MutableLiveData<Boolean> isSuccessLiveData = new MutableLiveData<>();
        // AsyncTask for database execution
        // It enables proper and easy use of the UI thread
        // It allows us to perform background operations and publish results on the UI thread
        // without having to manipulate threads and/or handlers.
        asyncTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            // doInBackground(Params...), invoked on the background thread immediately after onPreExecute() finishes executing.
            // to perform background computation that can take a long time.
            // The parameters of the asynchronous task are passed to this step.
            protected Boolean doInBackground(Void... voids) {
                try {
                    database.dao().saveArticle(article);
                } catch (Exception e) {
                    Log.e("test", e.getMessage());
                    return false;
                }
                return true;
            }

            @Override
            // onPostExecute(Result) invoked on the UI thread after the background computation finishes.
            // The result of the background computation is passed to this step as a parameter.
            protected void onPostExecute(Boolean isSuccess) {
                article.favorite = isSuccess;
                isSuccessLiveData.setValue(isSuccess);
            }
        }.execute();
        return  isSuccessLiveData;
    }

    // Add Save Page’s Room Database’s operation
    public LiveData<List<Article>> getAllSavedArticles(){
        return database.dao().getAllArticles();
    }

    public void deleteSavedArticle(Article article) {
        AsyncTask.execute(
                () -> database.dao().deleteArticle(article)
        );
    }

    // to prevent potential memory leak
    public void onCancel() {
        if (asyncTask != null) {
            // when kill the app, need to cancel the asyncTask avoid memory leak
            asyncTask.cancel(true);
        }
    }

}
