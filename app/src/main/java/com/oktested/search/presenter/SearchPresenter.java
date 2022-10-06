package com.oktested.search.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.home.model.SearchQuizResponse;
import com.oktested.home.model.VideoListResponse;
import com.oktested.search.model.SearchHistoryResponse;
import com.oktested.search.ui.SearchView;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

import java.util.HashMap;

import okhttp3.Request;

public class SearchPresenter extends NetworkHandler {

    private Context context;
    private SearchView searchView;

    public SearchPresenter(Context context, SearchView searchView) {
        super(context, searchView);
        this.context = context;
        this.searchView = searchView;
    }

    public void getSearchHistory() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        String id = AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID);
        NetworkManager.getApi().getUserHistoryData(headerMap, id).enqueue(this);
    }

    public void getUserSearchResult(String searchString) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getScoopWhoopApi().getUserSearchResult(headerMap, searchString).enqueue(this);
    }

    public void getUserSearchPagingResult(String searchString, String offset) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getScoopWhoopApi().getUserSearchPagingResult(headerMap, searchString, offset).enqueue(this);
    }

    public void postUserSearchData(String searchString) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userid", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("query_str", searchString);
        NetworkManager.getApi().storeSearchData(headerMap, jsonObject).enqueue(this);
    }

    public void callQuizSearchApi(String searchString, String page) {
        NetworkManager.getOkTestedQuizApi().getSearchedQuiz(searchString, page).enqueue(this);
    }
    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (searchView != null) {
            searchView.hideLoader();
            if (Helper.isContainValue(message)) {
                searchView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (searchView != null) {
            searchView.hideLoader();
            searchView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (searchView != null) {
            if (response instanceof SearchHistoryResponse){
                SearchHistoryResponse searchHistoryResponse = (SearchHistoryResponse) response;
                searchView.setSearchHistoryResponse(searchHistoryResponse);
            } else if (response instanceof VideoListResponse) {
                searchView.hideLoader();
                VideoListResponse videoListResponse = (VideoListResponse) response;
                searchView.setUserSearchResponse(videoListResponse);
            }else if (response instanceof SearchQuizResponse) {
                SearchQuizResponse searchQuizResponse = (SearchQuizResponse) response;
                searchView.setSearchQuizResponse(searchQuizResponse);
            }
        }
        return true;
    }

    public void onDestroyedView() {
        searchView = null;
    }
}