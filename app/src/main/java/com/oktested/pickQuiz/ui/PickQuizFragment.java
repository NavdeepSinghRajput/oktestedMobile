package com.oktested.pickQuiz.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.tabs.TabLayout;
import com.oktested.R;
import com.oktested.home.model.CategoryDetailQuizResponse;
import com.oktested.home.model.CategoryQuizResponse;
import com.oktested.home.model.LatestQuizResponse;
import com.oktested.home.model.SearchQuizResponse;
import com.oktested.home.model.TrendingQuizResponse;
import com.oktested.pickQuiz.adapter.CategoryQuizDataAdapter;
import com.oktested.pickQuiz.adapter.PickQuizCategoryAdapter;
import com.oktested.pickQuiz.adapter.SearchQuizDataAdapter;
import com.oktested.pickQuiz.presenter.PickQuizPresenter;
import com.oktested.utils.AppConstants;
import com.oktested.utils.Helper;

import java.util.ArrayList;

public class PickQuizFragment extends Fragment implements PickQuizView {

    private Context context;
    private TabLayout tabLayout;
    private PickQuizPresenter pickQuizPresenter;
    private RecyclerView quizPickSectionRV, quizSearchRV;
    private RecyclerView categoryTypeRV;
    private ImageView searchView;
    PickQuizCategoryAdapter pickQuizCategoryAdapter;
    String categoryOffset = "0";
    private boolean dataLoaded = true, catagoryDataLoaded = true,searchdataLoaded=true;
    String offset = "0";
    CategoryQuizDataAdapter categoryQuizDataAdapter;
    SearchQuizDataAdapter searchQuizDataAdapter;
    LinearLayout searchLL;
    SpinKitView spinKit;
    String type = "", slug = "";
    EditText searchEditText;
    String searchpage = "0";
    private static HandleQuizLoading handleQuizLoading;
    private TextView searchText;
    private boolean searchFound=true;

    public static PickQuizFragment newInstance(HandleQuizLoading handleQuizLoadings) {
        PickQuizFragment fragment = new PickQuizFragment();
        handleQuizLoading = handleQuizLoadings;
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_pick_quiz, container, false);

        pickQuizPresenter = new PickQuizPresenter(getContext(), this);
        quizPickSectionRV = (RecyclerView) root_view.findViewById(R.id.quizPickSectionRV);
        quizSearchRV = (RecyclerView) root_view.findViewById(R.id.quizSearchRV);
        spinKit = (SpinKitView) root_view.findViewById(R.id.spinKit);
        searchLL = (LinearLayout) root_view.findViewById(R.id.searchLL);
        searchView = (ImageView) root_view.findViewById(R.id.searchView);
        searchEditText = (EditText) root_view.findViewById(R.id.searchEditText);
        searchText = (TextView) root_view.findViewById(R.id.searchText);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchEditText.getText().toString().length() > 0) {
                    showLoader();
                    searchpage = "1";
                    pickQuizPresenter.callQuizSearchApi(searchEditText.getText().toString(), searchpage);
                    pickQuizCategoryAdapter.clickable = false;
                } else {
                    showMessage("Enter search text");
                }
            }
        });

        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
        quizPickSectionRV.setNestedScrollingEnabled(false);
        quizPickSectionRV.setHasFixedSize(true);
        quizPickSectionRV.setLayoutManager(mLayoutManager);
        quizPickSectionRV.setItemAnimator(new DefaultItemAnimator());
        quizPickSectionRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            pickQuizPresenter.callCategoryListApi(categoryOffset);

                        }
                    }
                }
            }
        });

        GridLayoutManager mSearchLayoutManager = new GridLayoutManager(getContext(), 3);
        quizSearchRV.setNestedScrollingEnabled(false);
        quizSearchRV.setHasFixedSize(true);
        quizSearchRV.setLayoutManager(mSearchLayoutManager);
        quizSearchRV.setItemAnimator(new DefaultItemAnimator());
        quizSearchRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // only when scrolling up
                    final int visibleThreshold = 4;
                    int lastItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = mLayoutManager.getItemCount();
                    if (currentTotalCount <= lastItem + visibleThreshold && searchdataLoaded && Helper.isContainValue(offset) && !offset.equalsIgnoreCase("-1")) {
                        if (Helper.isNetworkAvailable(context)) {
                            searchdataLoaded = false;
                            pickQuizPresenter.callQuizSearchApi(searchEditText.getText().toString(), searchpage);
                        }
                    }
                }
            }
        });

        categoryQuizDataAdapter = new CategoryQuizDataAdapter(context, new ArrayList<>(), new CategoryQuizDataAdapter.QuizSelected() {
            @Override
            public void quizSelectedDetail(String articleId) {
                handleQuizLoading.loadingQuizDetailFragment(articleId);
            }
        });

        searchQuizDataAdapter = new SearchQuizDataAdapter(context, new ArrayList<>(), new SearchQuizDataAdapter.QuizSelected() {
            @Override
            public void quizSelectedDetail(String articleId) {
                handleQuizLoading.loadingQuizDetailFragment(articleId);
            }
        });
        quizPickSectionRV.setAdapter(categoryQuizDataAdapter);
        quizSearchRV.setAdapter(searchQuizDataAdapter);

        settingHorizontalRV(root_view);
        showLoader();
        return root_view;
    }

    private void settingHorizontalRV(View view) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        categoryTypeRV = (RecyclerView) view.findViewById(R.id.categoryTypeRV);
        categoryTypeRV.setHasFixedSize(true);
        categoryTypeRV.setLayoutManager(linearLayoutManager);
        categoryTypeRV.setItemAnimator(new DefaultItemAnimator());
        categoryTypeRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // only when scrolling up
                    final int visibleThreshold = 4;
                    int lastItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = linearLayoutManager.getItemCount();
                    if (currentTotalCount <= lastItem + visibleThreshold && catagoryDataLoaded && Helper.isContainValue(categoryOffset) && !categoryOffset.equalsIgnoreCase("-1")) {
                        if (Helper.isNetworkAvailable(context)) {
                            catagoryDataLoaded = false;
                            pickQuizPresenter.callCategoryListApi(categoryOffset);

                        }
                    }
                }
            }
        });
        pickQuizCategoryAdapter = new PickQuizCategoryAdapter(context, new ArrayList<>(), new PickQuizCategoryAdapter.CategorySelected() {
            @Override
            public void loadCategoryData(String types, String slugs) {
                offset = "0";
                categoryQuizDataAdapter.clearAll();
                type = types;
                slug = slugs;
                settingDetailsSelected();

            }
        });
        categoryTypeRV.setAdapter(pickQuizCategoryAdapter);
        pickQuizPresenter.callCategoryListApi(categoryOffset);
    }

    private void settingDetailsSelected() {
        quizPickSectionRV.setVisibility(View.GONE);
        quizSearchRV.setVisibility(View.GONE);
        searchText.setVisibility(View.GONE);
        if (type.equalsIgnoreCase("Search")) {
            quizPickSectionRV.setVisibility(View.GONE);
            quizSearchRV.setVisibility(View.VISIBLE);
            searchLL.setVisibility(View.VISIBLE);
            if(searchFound){
                searchText.setVisibility(View.GONE);
            }else{
                searchText.setVisibility(View.VISIBLE);
            }pickQuizCategoryAdapter.clickable = true;
        } else if (type.equalsIgnoreCase("Latest")) {
            showLoader();
            searchLL.setVisibility(View.GONE);
            pickQuizPresenter.callLatestQuizApi(offset);
        } else if (type.equalsIgnoreCase("Trending")) {
            showLoader();
            searchLL.setVisibility(View.GONE);
            pickQuizPresenter.callTrendingQuizApi(AppConstants.SECTION_QUIZ_TRENDING_LAYOUT, offset);
        } else {
            showLoader();
            searchLL.setVisibility(View.GONE);
            pickQuizPresenter.callCategoryDetailQuizApi(slug, offset);
        }
    }

    @Override
    public void showLoader() {
        spinKit.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        spinKit.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setQuizViewResponse(CategoryQuizResponse categoryQuizResponse) {
        if (categoryQuizResponse != null && categoryQuizResponse.data != null && categoryQuizResponse.data.size() > 0) {
            if (categoryOffset.equalsIgnoreCase("0")) {
                ArrayList<CategoryQuizResponse.CategoryQuizModel> categoryQuizModels = new ArrayList<>();
                CategoryQuizResponse.CategoryQuizModel categoryQuizModel1 = new CategoryQuizResponse.CategoryQuizModel();
                categoryQuizModel1.category_display = "Search";
                categoryQuizModel1.category_slug = "";
                CategoryQuizResponse.CategoryQuizModel categoryQuizModel2 = new CategoryQuizResponse.CategoryQuizModel();
                categoryQuizModel2.category_display = "Latest";
                categoryQuizModel2.category_slug = "";
                categoryQuizModel2.selected = true;
                CategoryQuizResponse.CategoryQuizModel categoryQuizModel3 = new CategoryQuizResponse.CategoryQuizModel();
                categoryQuizModel3.category_display = "Trending";
                categoryQuizModel3.category_slug = "";
                categoryQuizModels.add(categoryQuizModel1);
                categoryQuizModels.add(categoryQuizModel2);
                categoryQuizModels.add(categoryQuizModel3);
                categoryQuizModels.addAll(categoryQuizResponse.data);
                pickQuizCategoryAdapter.notifyItem(categoryQuizModels);
            } else {
                pickQuizCategoryAdapter.notifyItem(categoryQuizResponse.data);
            }
            categoryOffset = categoryQuizResponse.next_offset;
            catagoryDataLoaded = true;
        }
    }

    @Override
    public void setTrendingQuizResponse(TrendingQuizResponse trendingQuizResponse) {
        quizPickSectionRV.setVisibility(View.VISIBLE);
        quizSearchRV.setVisibility(View.GONE);
        if (trendingQuizResponse != null && trendingQuizResponse.data != null && trendingQuizResponse.data.size() > 0) {
            categoryQuizDataAdapter.setMoreVideosData(trendingQuizResponse.data);
            offset = trendingQuizResponse.next_offset;
            dataLoaded = true;
            pickQuizCategoryAdapter.clickable = true;
        }
    }

    @Override
    public void setLatestQuizResponse(LatestQuizResponse latestQuizResponse) {
        quizPickSectionRV.setVisibility(View.VISIBLE);
        quizSearchRV.setVisibility(View.GONE);
        if (latestQuizResponse != null && latestQuizResponse.data != null && latestQuizResponse.data.size() > 0) {
            categoryQuizDataAdapter.setMoreVideosData(latestQuizResponse.data);
            offset = latestQuizResponse.next_offset;
            dataLoaded = true;
            pickQuizCategoryAdapter.clickable = true;
        }
    }

    @Override
    public void setCategoryDetailQuizResponse(CategoryDetailQuizResponse categoryDetailQuizResponse) {
        quizPickSectionRV.setVisibility(View.VISIBLE);
        quizSearchRV.setVisibility(View.GONE);
        if (categoryDetailQuizResponse != null && categoryDetailQuizResponse.data != null && categoryDetailQuizResponse.data.size() > 0) {
            categoryQuizDataAdapter.setMoreVideosData(categoryDetailQuizResponse.data);
            offset = categoryDetailQuizResponse.next_offset;
            dataLoaded = true;
            pickQuizCategoryAdapter.clickable = true;
        }
    }

    @Override
    public void setSearchQuizResponse(SearchQuizResponse searchQuizResponse) {
         pickQuizCategoryAdapter.clickable = true;
        if (searchpage.equalsIgnoreCase("1")) {
            searchQuizDataAdapter.clearAll();
        }
        if (searchQuizResponse != null && searchQuizResponse.data != null && searchQuizResponse.data.size() > 0) {
            if (searchQuizResponse.data.size() > 0) {
                searchQuizDataAdapter.setMoreVideosData(searchQuizResponse.data);
                quizPickSectionRV.setVisibility(View.GONE);
                quizSearchRV.setVisibility(View.VISIBLE);
                searchpage = searchQuizResponse.next_page;
                searchText.setVisibility(View.GONE);
                searchFound =true;
                searchdataLoaded=true;
            }else{
                searchFound =false;
                quizPickSectionRV.setVisibility(View.GONE);
                quizSearchRV.setVisibility(View.GONE);
                searchText.setVisibility(View.VISIBLE);
            }
        } else {
            searchFound =false;
            quizPickSectionRV.setVisibility(View.GONE);
            quizSearchRV.setVisibility(View.GONE);
            searchText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        pickQuizPresenter.onDestroyedView();
        super.onDestroyView();
    }
}