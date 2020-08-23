package com.laioffer.tinnews.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.model.NewsResponse;
import com.laioffer.tinnews.repository.NewsRepository;

public class HomeViewModel extends ViewModel {
    private final NewsRepository repository;
    private final MutableLiveData<String> countryInput = new MutableLiveData<>(); // input -> pipe -> output
    private final MutableLiveData<Article> favoriteArticleInput = new MutableLiveData<>();

    public HomeViewModel(NewsRepository newsRepository) {
        this.repository = newsRepository;
    }

    // set API
    public void setCountryInput(String country) {
        // input
        countryInput.setValue(country);
    }

    public void setFavoriteArticleInput(Article article) {
        favoriteArticleInput.setValue(article);
    }

    // Observe API
    public LiveData<NewsResponse> getTopHeadlines() {
        // countryInput有结果时，
        return Transformations.switchMap(countryInput, repository::getTopHeadlines);
        // repository::getTopHeadlines ==>
        //      new Function<String, LiveData<NewsResponse>>(){
        //      @Override
        //      public LiveData<NewsResponse> apply(String output){
        //          return repository.getTopHeadlines(output);
        //      }
        // ==> lambda: (output) ->  return repository.getTopHeadlines(output)
        // ==> lambda with reference:  repository::getTopHeadlines
    }

    public LiveData<Boolean> onFavorite() {
        return Transformations.switchMap(favoriteArticleInput, repository::favoriteArticle);
    }

    public void onCancel() {
        repository.onCancel();
    }

}
