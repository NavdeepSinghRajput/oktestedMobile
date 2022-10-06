package com.oktested.quizDetail.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.oktested.R;
import com.oktested.allQuiz.adapter.AllQuizAdapter;
import com.oktested.allQuiz.presenter.AllQuizPresenter;
import com.oktested.allQuiz.ui.AllQuizView;
import com.oktested.home.model.CategoryDetailQuizResponse;
import com.oktested.home.model.CategoryQuizResponse;
import com.oktested.home.model.LatestQuizResponse;
import com.oktested.home.model.TrendingQuizResponse;
import com.oktested.quizProfile.model.RecentPlayedQuizResponse;
import com.oktested.utils.Helper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuizCategoryDetailActivity extends AppCompatActivity implements AllQuizView {

    private RecyclerView categoryQuizRecycler;
    private TextView categoryDesc;
    private TextView categoryTitle;
    private RelativeLayout parentRL;
    private Context context;
    private CircleImageView quizCategoryPic;
    private boolean dataLoaded = true;
    private String offset = "0";
    private AllQuizPresenter allQuizPresenter;
    private AllQuizAdapter allQuizAdapter;
    private SpinKitView spinKitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_category_detail);
        context = this;
        inUi();

        String categorySlug = getIntent().getStringExtra("categorySlug");

        categoryQuizRecycler.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getBaseContext(), 3);
        categoryQuizRecycler.setLayoutManager(mLayoutManager);
        categoryQuizRecycler.setItemAnimator(new DefaultItemAnimator());
        categoryQuizRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // only when scrolling up
                    final int visibleThreshold = 4;
                    int lastItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = mLayoutManager.getItemCount();
                    if (currentTotalCount <= lastItem + visibleThreshold && dataLoaded && Helper.isContainValue(offset) && !offset.equalsIgnoreCase("-1")) {
                        if (Helper.isNetworkAvailable(getBaseContext())) {
                            dataLoaded = false;
                            allQuizPresenter.callCategoryDetailQuizApi(categorySlug, offset);
                        }
                    }
                }
            }
        });
        allQuizAdapter = new AllQuizAdapter(getBaseContext(), new ArrayList<>());
        categoryQuizRecycler.setAdapter(allQuizAdapter);

        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            allQuizPresenter.callCategoryDetailQuizApi(categorySlug, offset);
        }
    }

    private void inUi() {
        allQuizPresenter = new AllQuizPresenter(getBaseContext(), this);

        spinKitView = findViewById(R.id.spinKit);
        categoryQuizRecycler = findViewById(R.id.categoryQuizRecycler);
        categoryDesc = findViewById(R.id.categoryDesc);
        categoryTitle = findViewById(R.id.categoryTitle);
        quizCategoryPic = findViewById(R.id.quizCategoryPic);
        parentRL = findViewById(R.id.parentRL);
    }

    @Override
    public void setTrendingQuizResponse(TrendingQuizResponse trendingQuizResponse) {

    }

    @Override
    public void setLatestQuizResponse(LatestQuizResponse latestQuizResponse) {

    }

    @Override
    public void setCategoryQuizResponse(CategoryQuizResponse categoryQuizResponse) {

    }

    @Override
    public void setCategoryDetailQuizResponse(CategoryDetailQuizResponse categoryDetailQuizResponse) {
        hideLoader();
        if (categoryDetailQuizResponse != null && Helper.isContainValue(categoryDetailQuizResponse.status) && categoryDetailQuizResponse.status.equalsIgnoreCase("1")) {
            dataLoaded = true;
            offset = categoryDetailQuizResponse.next_offset;
            if (categoryDetailQuizResponse.cat_data != null) {
                parentRL.setVisibility(View.VISIBLE);
                if (Helper.isContainValue(categoryDetailQuizResponse.cat_data.onexone_img)) {
                    Glide.with(getBaseContext())
                            .load(categoryDetailQuizResponse.cat_data.onexone_img)
                            .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .into(quizCategoryPic);
                } else if (Helper.isContainValue(categoryDetailQuizResponse.cat_data.category_feature_img)) {
                    Glide.with(getBaseContext())
                            .load(categoryDetailQuizResponse.cat_data.category_feature_img)
                            .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .into(quizCategoryPic);
                } else {
                    quizCategoryPic.setImageDrawable(getResources().getDrawable(R.drawable.placeholder));
                }

                if (Helper.isContainValue(categoryDetailQuizResponse.cat_data.category_display)) {
                    categoryTitle.setText(categoryDetailQuizResponse.cat_data.category_display);
                }

                if (Helper.isContainValue(categoryDetailQuizResponse.cat_data.category_desc)) {
                    categoryDesc.setText(categoryDetailQuizResponse.cat_data.category_desc);
                }
            }

            if (categoryDetailQuizResponse.data != null && categoryDetailQuizResponse.data.size() > 0) {
                allQuizAdapter.setMoreVideosData(categoryDetailQuizResponse.data);
            }
        }
    }

    @Override
    public void setRecentPlayedQuizResponse(RecentPlayedQuizResponse recentPlayedQuizResponse) {

    }

    @Override
    public void showLoader() {
        spinKitView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        spinKitView.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}