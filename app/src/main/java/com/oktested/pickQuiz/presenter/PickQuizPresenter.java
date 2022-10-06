package com.oktested.pickQuiz.presenter;

import android.content.Context;

import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.home.model.CategoryDetailQuizResponse;
import com.oktested.home.model.CategoryQuizResponse;
import com.oktested.home.model.LatestQuizResponse;
import com.oktested.home.model.SearchQuizResponse;
import com.oktested.home.model.TrendingQuizResponse;
import com.oktested.pickQuiz.ui.PickQuizView;
import com.oktested.utils.Helper;

import okhttp3.Request;

public class PickQuizPresenter extends NetworkHandler {

    private Context context;
    private PickQuizView pickQuizView;

    public PickQuizPresenter(Context context, PickQuizView pickQuizView) {
        super(context, pickQuizView);
        this.context = context;
        this.pickQuizView = pickQuizView;
    }

    public void callCategoryListApi(String categoryOffset) {
        NetworkManager.getOkTestedQuizApi().getQuizCategory(categoryOffset).enqueue(this);
    }

    public void callTrendingQuizApi(String section_type, String offsets) {
        NetworkManager.getOkTestedQuizApi().getTrendingQuiz(section_type, offsets).enqueue(this);
    }

    public void callLatestQuizApi(String offset) {
        NetworkManager.getOkTestedQuizApi().getLatestQuiz(offset).enqueue(this);
    }

    public void callCategoryDetailQuizApi(String category_slug, String offset) {
        NetworkManager.getOkTestedQuizApi().getQuizCategoryDetail(category_slug, offset).enqueue(this);
    }

    public void callQuizSearchApi(String searchString, String page) {
        NetworkManager.getOkTestedQuizApi().getSearchedQuiz(searchString, page).enqueue(this);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (pickQuizView != null) {
            pickQuizView.hideLoader();
            pickQuizView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (pickQuizView != null) {
            pickQuizView.hideLoader();
            if (Helper.isContainValue(message)) {
                pickQuizView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (pickQuizView != null) {
            if(response instanceof CategoryQuizResponse){
                CategoryQuizResponse categoryQuizResponse = (CategoryQuizResponse) response;
                pickQuizView.setQuizViewResponse(categoryQuizResponse);
            } else if (response instanceof TrendingQuizResponse) {
                TrendingQuizResponse tredingQuizResponse = (TrendingQuizResponse) response;
                pickQuizView.setTrendingQuizResponse(tredingQuizResponse);
            } else if (response instanceof LatestQuizResponse) {
                LatestQuizResponse latestQuizResponse = (LatestQuizResponse) response;
                pickQuizView.setLatestQuizResponse(latestQuizResponse);
            }else if (response instanceof CategoryDetailQuizResponse) {
                CategoryDetailQuizResponse categoryQuizResponse = (CategoryDetailQuizResponse) response;
                pickQuizView.setCategoryDetailQuizResponse(categoryQuizResponse);
            }else if (response instanceof SearchQuizResponse) {
                SearchQuizResponse searchQuizResponse = (SearchQuizResponse) response;
                pickQuizView.setSearchQuizResponse(searchQuizResponse);
            }
            pickQuizView.hideLoader();
        }
        return true;
    }

    public void onDestroyedView() {
        pickQuizView = null;
    }
}