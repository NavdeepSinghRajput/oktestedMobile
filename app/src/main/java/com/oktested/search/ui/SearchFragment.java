package com.oktested.search.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.oktested.R;
import com.oktested.entity.DataItem;
import com.oktested.home.model.SearchQuizResponse;
import com.oktested.home.model.VideoListResponse;
import com.oktested.pickQuiz.adapter.SearchQuizDataAdapter;
import com.oktested.quizDetail.ui.QuizDetailActivity;
import com.oktested.search.adapter.SearchHistoryAdapter;
import com.oktested.search.adapter.SearchResultAdapter;
import com.oktested.search.model.SearchHistoryResponse;
import com.oktested.search.presenter.SearchPresenter;
import com.oktested.utils.DataHolder;
import com.oktested.utils.Helper;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements SearchView {

    private Context context;
    private SearchPresenter searchPresenter;
    private SpinKitView spinKitView;
    private EditText searchET, searchQuizET;
    private TextView searchTitleTV;
    private RecyclerView searchSuggestionRV, searchResultRV, searchQuizRV;
    private boolean dataLoaded = true, quizdataLoaded = true, firstTime = true;
    private String offset = "", oldSearchString = "", oldQuizSearchString = "";
    private SearchResultAdapter searchResultAdapter;

    private RadioGroup video_quiz_radioGroup;
    private RadioButton searchQuiz, searchVideo;
    SearchQuizDataAdapter searchQuizDataAdapter;
    String searchpage = "1";
    private RelativeLayout searchRL, quizSeacrhRL;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_search, container, false);

        searchPresenter = new SearchPresenter(context, this);
        spinKitView = root_view.findViewById(R.id.spinKit);
        quizSeacrhRL = root_view.findViewById(R.id.quizSeacrhRL);
        searchRL = root_view.findViewById(R.id.searchRL);
        searchET = root_view.findViewById(R.id.searchET);
        searchQuizET = root_view.findViewById(R.id.searchQuizET);
        searchTitleTV = root_view.findViewById(R.id.searchTitleTV);
        video_quiz_radioGroup = (RadioGroup) root_view.findViewById(R.id.video_quiz_radioGroup);
        searchVideo = (RadioButton) root_view.findViewById(R.id.searchVideo);
        searchQuiz = (RadioButton) root_view.findViewById(R.id.searchQuiz);
        LinearLayout searchType = (LinearLayout) root_view.findViewById(R.id.searchType);

        if (DataHolder.getInstance().getUserDataresponse.is_quiz_enable) {
            searchType.setVisibility(View.VISIBLE);
        } else {
            searchType.setVisibility(View.GONE);
        }

        ImageView submitIV = root_view.findViewById(R.id.submitIV);
        submitIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchET.getText().toString().trim().length() > 2 && !oldSearchString.equalsIgnoreCase(searchET.getText().toString().trim())) {
                    callUserSearchApi(searchET.getText().toString().trim());
                }
            }
        });

        ImageView submitQuizIV = root_view.findViewById(R.id.submitQuizIV);
        submitQuizIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchQuizET.getText().toString().trim().length() > 2 && !oldQuizSearchString.equalsIgnoreCase(searchQuizET.getText().toString().trim())) {
                    callQuizUserSearchApi(searchQuizET.getText().toString().trim());
                }
            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchResultRV.setVisibility(View.GONE);
                searchTitleTV.setVisibility(View.GONE);
                searchSuggestionRV.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchQuizET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchQuizRV.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (searchET.getText().toString().trim().length() > 2 && !oldSearchString.equalsIgnoreCase(searchET.getText().toString().trim())) {
                        callUserSearchApi(searchET.getText().toString().trim());
                    }
                    return true;
                }
                return false;
            }
        });

        searchQuizET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (searchQuizET.getText().toString().trim().length() > 2 && !oldQuizSearchString.equalsIgnoreCase(searchQuizET.getText().toString().trim())) {
                        callQuizUserSearchApi(searchET.getText().toString().trim());
                    }
                    return true;
                }
                return false;
            }
        });

        searchSuggestionRV = root_view.findViewById(R.id.searchSuggestionRV);
        searchSuggestionRV.setHasFixedSize(true);
        searchSuggestionRV.setLayoutManager(new LinearLayoutManager(context));
        searchSuggestionRV.setItemAnimator(new DefaultItemAnimator());

        GridLayoutManager mLayoutManager1 = new GridLayoutManager(getContext(), 3);
        searchQuizRV = root_view.findViewById(R.id.searchQuizRV);
        searchQuizRV.setHasFixedSize(true);
        searchQuizRV.setLayoutManager(mLayoutManager1);
        searchQuizRV.setItemAnimator(new DefaultItemAnimator());

        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
        searchResultRV = root_view.findViewById(R.id.searchResultRV);
        searchResultRV.setHasFixedSize(true);
        searchResultRV.setLayoutManager(mLayoutManager);
        searchResultRV.setItemAnimator(new DefaultItemAnimator());

        ArrayList<DataItem> dataItemList = new ArrayList<>();
        searchResultAdapter = new SearchResultAdapter(context, dataItemList);
        searchResultRV.setAdapter(searchResultAdapter);

        searchResultRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            searchPresenter.getUserSearchPagingResult(searchET.getText().toString().trim(), offset);
                        }
                    }
                }
            }
        });

        searchQuizDataAdapter = new SearchQuizDataAdapter(context, new ArrayList<>(), new SearchQuizDataAdapter.QuizSelected() {
            @Override
            public void quizSelectedDetail(String articleId) {
                Intent intent = new Intent(context, QuizDetailActivity.class);
                intent.putExtra("articleId", articleId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        searchQuizRV.setAdapter(searchQuizDataAdapter);
        searchQuizRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // only when scrolling up
                    final int visibleThreshold = 4;
                    int lastItem = mLayoutManager1.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = mLayoutManager1.getItemCount();
                    if (currentTotalCount <= lastItem + visibleThreshold && quizdataLoaded && Helper.isContainValue(searchpage) && !searchpage.equalsIgnoreCase("-1")) {
                        if (Helper.isNetworkAvailable(context)) {
                            quizdataLoaded = false;
                            searchPresenter.callQuizSearchApi(oldQuizSearchString, searchpage);
                        }
                    }
                }
            }
        });
        callSearchHistoryApi();
        video_quiz_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int selectedId) {
                if (selectedId == searchVideo.getId()) {
                    searchRL.setVisibility(View.VISIBLE);
                    quizSeacrhRL.setVisibility(View.GONE);
                } else {
                    searchRL.setVisibility(View.GONE);
                    quizSeacrhRL.setVisibility(View.VISIBLE);

                }
            }
        });
        searchVideo.setChecked(true);
        return root_view;
    }

    private void callSearchHistoryApi() {
        if (Helper.isNetworkAvailable(context)) {
            searchPresenter.getSearchHistory();
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void callUserSearchApi(String searchString) {
        Helper.hideKeyboard((Activity) context);
        if (Helper.isNetworkAvailable(context)) {
            if (!oldSearchString.equalsIgnoreCase(searchString)) {
                firstTime = true;
                showLoader();
                searchPresenter.getUserSearchResult(searchString);
            } else {
                searchResultRV.setVisibility(View.VISIBLE);
                searchTitleTV.setVisibility(View.VISIBLE);
                searchSuggestionRV.setVisibility(View.GONE);
            }
            oldSearchString = searchString;
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void callQuizUserSearchApi(String searchString) {
        Helper.hideKeyboard((Activity) context);
        if (Helper.isNetworkAvailable(context)) {
            if (!oldQuizSearchString.equalsIgnoreCase(searchString)) {
                showLoader();
                searchpage = "1";
                searchPresenter.callQuizSearchApi(searchString, searchpage);
            }
            oldQuizSearchString = searchString;
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
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

    @Override
    public void onDestroy() {
        if (searchPresenter != null) {
            searchPresenter.onDestroyedView();
        }
        super.onDestroy();
    }

    @Override
    public void setSearchHistoryResponse(SearchHistoryResponse searchHistoryResponse) {
        if (searchHistoryResponse != null && searchHistoryResponse.data != null && searchHistoryResponse.data.size() > 0) {
            SearchHistoryAdapter searchHistoryAdapter = new SearchHistoryAdapter(context, searchHistoryResponse.data, new SearchHistoryAdapter.SearchHistoryData() {
                @Override
                public void setSearchHistory(String searchHistory) {
                    searchET.setText(searchHistory);
                }
            }, new SearchHistoryAdapter.UserSearch() {
                @Override
                public void callUserSearchFromAdapter(String searchHistory) {
                    if (searchHistory.trim().length() > 2) {
                        searchET.setText(searchHistory);
                        callUserSearchApi(searchHistory.trim());
                    }
                }
            });
            searchSuggestionRV.setAdapter(searchHistoryAdapter);
        }
    }

    @Override
    public void setUserSearchResponse(VideoListResponse videoListResponse) {
        if (videoListResponse != null && videoListResponse.data != null && videoListResponse.data.size() > 0) {
            dataLoaded = true;
            offset = videoListResponse.next_offset;

            if (firstTime) {
                firstTime = false;
                searchResultAdapter.setMoreVideosDataFirstTime(videoListResponse.data);
                searchPresenter.postUserSearchData(searchET.getText().toString().trim());
            } else {
                searchResultAdapter.setMoreVideosData(videoListResponse.data);
            }
            searchSuggestionRV.setVisibility(View.GONE);
            searchResultRV.setVisibility(View.VISIBLE);

            searchTitleTV.setVisibility(View.VISIBLE);
            searchTitleTV.setText("Videos related to \"" + searchET.getText().toString().trim() + "\"");
        }
    }

    @Override
    public void setSearchQuizResponse(SearchQuizResponse searchQuizResponse) {
        if (searchQuizResponse != null && searchQuizResponse.data != null && searchQuizResponse.data.size() > 0) {
            if (searchpage.equalsIgnoreCase("1")) {
                searchQuizDataAdapter.clearAll();
            }
            searchQuizRV.setVisibility(View.VISIBLE);
            searchQuizDataAdapter.setMoreVideosData(searchQuizResponse.data);
            searchpage = searchQuizResponse.next_page;
            quizdataLoaded = true;
        }
        hideLoader();
    }
}