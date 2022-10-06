package com.oktested.favourite.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oktested.R;
import com.oktested.dashboard.model.GetUserDataresponse;
import com.oktested.favourite.adapter.FavouriteAnchorAdapter;
import com.oktested.favourite.adapter.FavouriteShowAdapter;
import com.oktested.favourite.adapter.FavouriteVideoAdapter;
import com.oktested.favourite.presenter.FavouritePresenter;
import com.oktested.home.model.AnchorsResponse;
import com.oktested.home.model.ShowsResponse;
import com.oktested.home.model.VideoListResponse;
import com.oktested.utils.DataHolder;

import java.util.ArrayList;

public class FavouriteFragment extends Fragment implements FavouriteView, View.OnClickListener {

    private Context context;
    private static FavouritePresenter favouritePresenter;
    private static TextView favVideoTV;
    private static TextView favShowTV;
    private static TextView favAnchorTV;
    private static View videoView;
    private static View showView;
    private static RecyclerView favVideoRV;
    private static RecyclerView favShowRV;
    private static RecyclerView favAnchorRV;
    private RelativeLayout favVideoMoreRL, favShowMoreRL, favAnchorMoreRL;
    private static LinearLayout noDataLL;
    private FavouriteVideoAdapter favouriteVideoAdapter;
    private FavouriteShowAdapter favouriteShowAdapter;
    private FavouriteAnchorAdapter favouriteAnchorAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_favourite, container, false);

        inUi(root_view);
        getUserData(DataHolder.getInstance().getUserDataresponse);
        return root_view;
    }

    private void inUi(View root_view) {
        favouritePresenter = new FavouritePresenter(context, this);

        favVideoRV = root_view.findViewById(R.id.favVideoRV);
        favVideoRV.setHasFixedSize(true);
        favVideoRV.setLayoutManager(new GridLayoutManager(context, 3));
        favVideoRV.setItemAnimator(new DefaultItemAnimator());

        favShowRV = root_view.findViewById(R.id.favShowRV);
        favShowRV.setHasFixedSize(true);
        favShowRV.setLayoutManager(new GridLayoutManager(context, 3));
        favShowRV.setItemAnimator(new DefaultItemAnimator());

        favAnchorRV = root_view.findViewById(R.id.favAnchorRV);
        favAnchorRV.setHasFixedSize(true);
        favAnchorRV.setLayoutManager(new GridLayoutManager(context, 3));
        favAnchorRV.setItemAnimator(new DefaultItemAnimator());

        favVideoTV = root_view.findViewById(R.id.favVideoTV);
        favShowTV = root_view.findViewById(R.id.favShowTV);
        favAnchorTV = root_view.findViewById(R.id.favAnchorTV);
        videoView = root_view.findViewById(R.id.videoView);
        showView = root_view.findViewById(R.id.showView);

        noDataLL = root_view.findViewById(R.id.noDataLL);
        favVideoMoreRL = root_view.findViewById(R.id.favVideoMoreRL);
        favShowMoreRL = root_view.findViewById(R.id.favShowMoreRL);
        favAnchorMoreRL = root_view.findViewById(R.id.favAnchorMoreRL);

        favVideoMoreRL.setOnClickListener(this);
        favShowMoreRL.setOnClickListener(this);
        favAnchorMoreRL.setOnClickListener(this);
    }

    @Override
    public void showLoader() {

    }

    @Override
    public void hideLoader() {

    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void getUserData(GetUserDataresponse getUserResponse) {
        if (getUserResponse != null) {
            if (getUserResponse.favourite != null && getUserResponse.favourite.size() > 0) {
                callVideoListApi(getUserResponse.favourite);
            }
            if (getUserResponse.favourite_shows != null && getUserResponse.favourite_shows.size() > 0) {
                callShowsListApi(getUserResponse.favourite_shows);
            }
            if (getUserResponse.follow != null && getUserResponse.follow.size() > 0) {
                callAnchorListApi(getUserResponse.follow);
            }
        }
    }

    public static void callVideoListApi(ArrayList<String> favourite) {
        if (favouritePresenter != null && favourite != null) {
            favouritePresenter.callFavouriteVideoListApi(favourite);
        }
    }

    public static void callAnchorListApi(ArrayList<String> follow) {
        if (favouritePresenter != null && follow != null) {
            favouritePresenter.callFavouriteAnchorsListApi(follow);
        }
    }

    public static void callShowsListApi(ArrayList<String> favourite_shows) {
        if (favouritePresenter != null && favourite_shows != null) {
            favouritePresenter.callFavouriteShowsListApi(favourite_shows);
        }
    }

    @Override
    public void getFavouriteVideos(VideoListResponse videoListResponse) {
        if (videoListResponse != null && videoListResponse.data != null && videoListResponse.data.size() > 0) {
            favouriteVideoAdapter = new FavouriteVideoAdapter(context, videoListResponse.data);
            favVideoRV.setAdapter(favouriteVideoAdapter);

            noDataLL.setVisibility(View.GONE);
            favVideoTV.setVisibility(View.VISIBLE);
            favVideoRV.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.VISIBLE);
            if (videoListResponse.data.size() > 6) {
                favVideoMoreRL.setVisibility(View.VISIBLE);
            } else {
                favVideoMoreRL.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void getFavouriteShows(ShowsResponse favouriteShowsResponse) {
        if (favouriteShowsResponse != null && favouriteShowsResponse.data != null && favouriteShowsResponse.data.size() > 0) {
            favouriteShowAdapter = new FavouriteShowAdapter(context, favouriteShowsResponse.data);
            favShowRV.setAdapter(favouriteShowAdapter);

            noDataLL.setVisibility(View.GONE);
            favShowTV.setVisibility(View.VISIBLE);
            favShowRV.setVisibility(View.VISIBLE);
            showView.setVisibility(View.VISIBLE);
            if (favouriteShowsResponse.data.size() > 6) {
                favShowMoreRL.setVisibility(View.VISIBLE);
            } else {
                favShowMoreRL.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void getFavouriteAnchors(AnchorsResponse anchorDetailResponse) {
        if (anchorDetailResponse != null && anchorDetailResponse.data != null && anchorDetailResponse.data.size() > 0) {
            favouriteAnchorAdapter = new FavouriteAnchorAdapter(context, anchorDetailResponse.data);
            favAnchorRV.setAdapter(favouriteAnchorAdapter);

            noDataLL.setVisibility(View.GONE);
            favAnchorTV.setVisibility(View.VISIBLE);
            favAnchorRV.setVisibility(View.VISIBLE);
            if (anchorDetailResponse.data.size() > 6) {
                favAnchorMoreRL.setVisibility(View.VISIBLE);
            } else {
                favAnchorMoreRL.setVisibility(View.GONE);
            }
        }
    }

    public static void hideVideosView() {
        try {
            favVideoTV.setVisibility(View.GONE);
            favVideoRV.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideShowsView() {
        favShowTV.setVisibility(View.GONE);
        favShowRV.setVisibility(View.GONE);
        showView.setVisibility(View.GONE);
    }

    public static void hideAnchorView() {
        favAnchorTV.setVisibility(View.GONE);
        favAnchorRV.setVisibility(View.GONE);
    }

    public static void showNoDataView() {
        if (noDataLL != null) {
            noDataLL.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        if (favouritePresenter != null) {
            favouritePresenter.onDestroyedView();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.favVideoMoreRL:
                favVideoMoreRL.setVisibility(View.GONE);
                favouriteVideoAdapter.setFavouriteVideoData(true);
                break;

            case R.id.favShowMoreRL:
                favShowMoreRL.setVisibility(View.GONE);
                favouriteShowAdapter.setFavouriteShowData(true);
                break;

            case R.id.favAnchorMoreRL:
                favAnchorMoreRL.setVisibility(View.GONE);
                favouriteAnchorAdapter.setFavouriteAnchorData(true);
                break;

            default:
                break;
        }
    }
}