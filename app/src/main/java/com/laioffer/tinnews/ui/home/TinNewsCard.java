package com.laioffer.tinnews.ui.home;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.laioffer.tinnews.model.Article;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;

import com.laioffer.tinnews.R;
import com.squareup.picasso.Picasso;

// @layout is used to bind the layout with this class
// to bind the card item view and its operations on layout(tin_news_card.xml)
@Layout(R.layout.tin_news_card)
public class TinNewsCard {
    // @View is used to bind the views in a layout we want to refer to
    @View(R.id.news_image)
    private ImageView image;

    @View(R.id.news_title)
    private TextView newsTitle;

    @View(R.id.news_description)
    private TextView newsDescription;

    private final Article article;
    private final OnSwipeListener onSwipeListener;

    public TinNewsCard(Article news, OnSwipeListener onSwipeListener) {
        this.article = news;
        this.onSwipeListener = onSwipeListener;

    }

    // @Resolve annotation binds a method to be executed when the view is ready to be used.
    // Any operation we want to perform on view references should be written
    // in a method and annotated with this.
    @Resolve
    private void onResolved() {
        // for image fetching
        if (article.urlToImage == null || article.urlToImage.isEmpty()){
            image.setImageResource(R.drawable.ic_empty_image);
        } else {
            // to handle image request
            Picasso.get().load(article.urlToImage).into(image);
        }

        newsTitle.setText(article.title);
        newsDescription.setText(article.description);
    }

    // @SwipeOut calls the annotated method when the card has been rejected.
    @SwipeOut
    private void onSwipedOut() {
        Log.d("EVENT", "onSwipedOut");
        onSwipeListener.onDisLike(article);
    }

    @SwipeCancelState
    private void onSwipeCancelState() {
        Log.d("EVENT", "onSwipeCancelState");
    }

    // @SwipeIn calls the annotated method when the card has been accepted.
    @SwipeIn
    private void onSwipeIn() {
        article.favorite = true;
        Log.d("EVENT", "onSwipedIn");
        onSwipeListener.onLike(article);
    }

    // TinNewCard triggers callback when SwipeEvent happens
    interface OnSwipeListener {
        void onLike(Article article);
        void onDisLike(Article article);
    }
}
