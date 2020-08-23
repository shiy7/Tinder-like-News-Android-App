package com.laioffer.tinnews.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.laioffer.tinnews.R;
import com.laioffer.tinnews.databinding.FragmentHomeBinding;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.repository.NewsRepository;
import com.laioffer.tinnews.repository.NewsViewModelFactory;
import com.mindorks.placeholderview.SwipeDecor;

import static android.widget.Toast.LENGTH_SHORT;

public class HomeFragment extends Fragment implements TinNewsCard.OnSwipeListener {

    private HomeViewModel viewModel;
    private FragmentHomeBinding binding;

    // CallBack methods: onLike, onDisLike, onDestroyView
    // to pass the SwipeEvents in the TinNewsCard to  HomeFragment
    @Override
    public void onLike(Article news) {
        viewModel.setFavoriteArticleInput(news);
    }

    @Override
    public void onDisLike(Article news) {
        if (binding.swipeView.getChildCount() < 3) {
            viewModel.setCountryInput("us");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onCancel();
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false);

        // Call the static inflate() method included in the generated binding class. This creates an instance of the binding class for the fragment to use.
        // Get a reference to the root view by either calling the getRoot() method
        // Return the root view from the onCreateView() method to make it the active view on the screen.
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding
                .swipeView
                // getBuilder() method to modify the default swipeView configurations
                .getBuilder()
                // adding 3 cards in the display
                .setDisplayViewCount(3)
                .setSwipeDecor(
                        // SwipeDecor class is used to adjust the visual elements of the view.
                        new SwipeDecor()
                                .setPaddingTop(20)
                                .setRelativeScale(0.01f));
        binding.rejectBtn.setOnClickListener(v -> binding.swipeView.doSwipe(false));
        binding.acceptBtn.setOnClickListener(v -> binding.swipeView.doSwipe(true));

        NewsRepository repository = new NewsRepository(getContext());
        // to store viewModel
        viewModel = new ViewModelProvider(this, new NewsViewModelFactory(repository))
                .get(HomeViewModel.class);

        viewModel.setCountryInput("us");

        viewModel.getTopHeadlines()
                .observe(getViewLifecycleOwner(), newsResponse -> {
                    if (newsResponse != null) {
//                       Log.d("HomeFragment", newsResponse.toString());

                        // Create the TinNewsCard from Retrofit Response and add to the PlaceHolderView
                        for (Article article : newsResponse.articles) {
                            TinNewsCard tinNewsCard = new TinNewsCard(article, this);
                            binding.swipeView.addView(tinNewsCard);
                        }
                    }
                });

        // get result(isSuccess) from NewsRepository
        viewModel.onFavorite()
                .observe(getViewLifecycleOwner(), isSuccess -> {
                    if (isSuccess) {
                        Toast.makeText(getContext(), "Success", LENGTH_SHORT).show();
                    } else {
                        // liked before, then if saved again, DB will have the duplicate primary key (it's error for DB)
                        Toast.makeText(getContext(), "You might have liked before", LENGTH_SHORT).show();
                    }
                });
    }

}