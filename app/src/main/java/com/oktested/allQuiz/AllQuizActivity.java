package com.oktested.allQuiz;

import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oktested.R;
import com.oktested.allQuiz.adapter.AllCategoryQuizAdapter;
import com.oktested.allQuiz.adapter.AllQuizAdapter;
import com.oktested.allQuiz.presenter.AllQuizPresenter;
import com.oktested.allQuiz.ui.AllQuizView;
import com.oktested.home.model.CategoryDetailQuizResponse;
import com.oktested.home.model.CategoryQuizResponse;
import com.oktested.home.model.CategoryQuizResponse.CategoryQuizModel;
import com.oktested.home.model.LatestQuizResponse;
import com.oktested.home.model.QuizListModel;
import com.oktested.home.model.TrendingQuizResponse;
import com.oktested.quizProfile.model.RecentPlayedQuizResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.Helper;

import java.util.ArrayList;

public class AllQuizActivity extends AppCompatActivity implements AllQuizView {

    private AllQuizPresenter allQuizPresenter;
    private AllQuizAdapter allQuizAdapter;
    private String screenName, offset = "0", slug;
    private boolean dataLoaded = true, firstTime = true;
    private Context context;
    private AllCategoryQuizAdapter allCategoryQuizAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_quiz);

        context = this;
        allQuizPresenter = new AllQuizPresenter(context, this);

        TextView titleTV = findViewById(R.id.titleTV);
        titleTV.setText(getIntent().getExtras().getString("sectionTitle"));
        screenName = getIntent().getExtras().getString("screenName");
        offset = getIntent().getExtras().getString("offset");

        RecyclerView allVideoRV = findViewById(R.id.allVideoRV);
        allVideoRV.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(context, 3);
        allVideoRV.setLayoutManager(mLayoutManager);
        allVideoRV.setItemAnimator(new DefaultItemAnimator());

        if (getIntent().getExtras().getString("type").equalsIgnoreCase("categories")) {
            ArrayList<CategoryQuizModel> dataItemList = getIntent().getParcelableArrayListExtra("dataItemList");
            allCategoryQuizAdapter = new AllCategoryQuizAdapter(context, dataItemList);
            allVideoRV.setAdapter(allCategoryQuizAdapter);
        } else {
            ArrayList<QuizListModel> dataItemList = getIntent().getParcelableArrayListExtra("dataItemList");
            if (dataItemList != null && dataItemList.size() > 0 && Helper.isContainValue(dataItemList.get(0).slug)) {
                slug = dataItemList.get(0).cat_display.get(0).category_slug;
            }
            allQuizAdapter = new AllQuizAdapter(AllQuizActivity.this, dataItemList);
            allVideoRV.setAdapter(allQuizAdapter);
        }

        LinearLayout backLL = findViewById(R.id.backLL);
        backLL.setOnClickListener(view -> finish());

        allVideoRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // only when scrolling up
                    final int visibleThreshold = 4;
                    int lastItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = mLayoutManager.getItemCount();
                    if (currentTotalCount <= lastItem + visibleThreshold && dataLoaded && Helper.isContainValue(offset) && !offset.equalsIgnoreCase("-1")) {
                        if (Helper.isNetworkAvailable(context)) {
                            dataLoaded = false;
                            if (screenName.equalsIgnoreCase(AppConstants.SECTION_QUIZ_LATEST_LAYOUT)) {
                                allQuizPresenter.callLatestQuizApi(offset);
                            } else if (screenName.equalsIgnoreCase(AppConstants.SECTION_QUIZ_TRENDING_LAYOUT)) {
                                allQuizPresenter.callTrendingQuizApi(screenName, offset);
                            } else if (screenName.equalsIgnoreCase(AppConstants.SECTION_QUIZ_CATEGORY_DETAILS_LAYOUT)) {
                                allQuizPresenter.callCategoryDetailQuizApi(slug, offset);
                            } else if (screenName.equalsIgnoreCase(AppConstants.SECTION_QUIZ_CATEGORY_LAYOUT)) {
                                allQuizPresenter.callCategoryQuizApi(offset);
                            } else if (screenName.equalsIgnoreCase("recentPlayed")) {
                                allQuizPresenter.callRecentPlayedQuizApi(offset);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void setTrendingQuizResponse(TrendingQuizResponse trendingQuizResponse) {
        if (trendingQuizResponse != null && trendingQuizResponse.data != null && trendingQuizResponse.data.size() > 0) {
            dataLoaded = true;
            offset = trendingQuizResponse.next_offset;
            allQuizAdapter.setMoreVideosData(trendingQuizResponse.data);
        }
    }

    @Override
    public void setRecentPlayedQuizResponse(RecentPlayedQuizResponse recentPlayedQuizResponse) {
        if (recentPlayedQuizResponse != null && recentPlayedQuizResponse.data != null && recentPlayedQuizResponse.data.size() > 0) {
            dataLoaded = true;
            offset = recentPlayedQuizResponse.next_offset;
            allQuizAdapter.setMoreVideosData(recentPlayedQuizResponse.data);
        }
    }

    @Override
    public void setLatestQuizResponse(LatestQuizResponse latestQuizResponse) {
        if (latestQuizResponse != null && latestQuizResponse.data != null && latestQuizResponse.data.size() > 0) {
            dataLoaded = true;
            offset = latestQuizResponse.next_offset;
            allQuizAdapter.setMoreVideosData(latestQuizResponse.data);
        }
    }

    @Override
    public void setCategoryQuizResponse(CategoryQuizResponse categoryQuizResponse) {
        if (categoryQuizResponse != null && categoryQuizResponse.data != null && categoryQuizResponse.data.size() > 0) {
            dataLoaded = true;
            offset = categoryQuizResponse.next_offset;
            allCategoryQuizAdapter.setMoreVideosData(categoryQuizResponse.data);
        }
    }

    @Override
    public void setCategoryDetailQuizResponse(CategoryDetailQuizResponse categoryDetailQuizResponse) {
        if (categoryDetailQuizResponse != null && categoryDetailQuizResponse.data != null && categoryDetailQuizResponse.data.size() > 0) {
            dataLoaded = true;
            offset = categoryDetailQuizResponse.next_offset;
            allQuizAdapter.setMoreVideosData(categoryDetailQuizResponse.data);
        }
    }

    @Override
    public void showLoader() {

    }

    @Override
    public void hideLoader() {

    }

    @Override
    public void showMessage(String message) {

    }
}