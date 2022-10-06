package com.oktested.videoPlayer.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.android.gms.cast.framework.CastContext;
import com.oktested.entity.DataItem;
import com.oktested.videoPlayer.model.VideoCommentDataResponse;
import com.oktested.videoPlayer.ui.VideoPlayerFragment;

import java.util.ArrayList;

public class VideoPlayerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<DataItem> dataItemList;
    private Context context;
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private int currentItem;
    private CastContext castContext;
    private VideoCommentDataResponse videoCommentDataResponse;

    public VideoPlayerAdapter(@NonNull FragmentManager fm, int behavior, ArrayList<DataItem> dataItemList, Context context, int currentItem, CastContext castContext, VideoCommentDataResponse videoCommentDataResponse) {
        super(fm, behavior);
        this.context = context;
        this.dataItemList = dataItemList;
        this.currentItem = currentItem;
        this.castContext = castContext;
        this.videoCommentDataResponse = videoCommentDataResponse;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return VideoPlayerFragment.newInstance(context, dataItemList, currentItem, castContext, videoCommentDataResponse);
    }

    @Override
    public int getCount() {
        return dataItemList.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }
}