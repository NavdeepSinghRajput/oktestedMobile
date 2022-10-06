package com.oktested.videoCall.ui.layout;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.oktested.videoCall.model.ChannelDetailResponse;
import com.oktested.videoCall.model.MemberInfoKeyValueData;
import com.oktested.videoCall.model.QuestionWiseResultKeyValueData;
import com.oktested.videoCall.propeller.UserStatusData;
import com.oktested.videoCall.propeller.VideoInfoData;
import com.oktested.videoCall.propeller.ui.RecyclerItemClickListener;

import java.util.HashMap;

public class GridVideoViewContainer extends RecyclerView {

    private GridVideoViewContainerAdapter mGridVideoViewContainerAdapter;
    private boolean quiz = false;

    public GridVideoViewContainer(Context context) {
        super(context);
    }

    public GridVideoViewContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GridVideoViewContainer(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setItemEventHandler(RecyclerItemClickListener.OnItemClickListener listener) {
        this.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), listener));
    }

    private boolean initAdapter(Activity activity, int localUid, HashMap<Integer, SurfaceView> uids) {
        if (mGridVideoViewContainerAdapter == null) {
            mGridVideoViewContainerAdapter = new GridVideoViewContainerAdapter(activity, localUid, uids);
            mGridVideoViewContainerAdapter.setHasStableIds(true);
            return true;
        }
        return false;
    }

    public void initViewContainer(Activity activity, int localUid, HashMap<Integer, SurfaceView> uids, boolean isLandscape, boolean quizSelected) {
        boolean newCreated = initAdapter(activity, localUid, uids);

        if (!newCreated) {
            mGridVideoViewContainerAdapter.setLocalUid(localUid);
            mGridVideoViewContainerAdapter.customizedInit(uids, true);
        }

        this.setAdapter(mGridVideoViewContainerAdapter);
        int orientation = isLandscape ? RecyclerView.HORIZONTAL : RecyclerView.VERTICAL;

        int count = uids.size();
        if (!quizSelected) {
            if (count <= 2) { // only local full view or or with one peer
                this.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext(), orientation, true));
            } else {
                int itemSpanCount = getNearestSqrt(count);
                this.setLayoutManager(new GridLayoutManager(activity.getApplicationContext(), itemSpanCount, orientation, true));
            }
            mGridVideoViewContainerAdapter.notifyDataSetChanged();
            mGridVideoViewContainerAdapter.notifyUiAfterQuizResult();
        } else {
            FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(activity.getApplicationContext());
            flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
            flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
            flexboxLayoutManager.setAlignItems(AlignItems.CENTER);
            this.setLayoutManager(flexboxLayoutManager);
         //   this.setLayoutManager(new GridLayoutManager(activity.getApplicationContext(), 3, orientation, false));
            mGridVideoViewContainerAdapter.notifyUiAfterPickQuiz();
        }
    }

    private int getNearestSqrt(int n) {
        return (int) Math.sqrt(n);
    }

    public void notifyUiChanged(HashMap<Integer, SurfaceView> uids, int localUid, HashMap<Integer, Integer> status, HashMap<Integer, Integer> volume) {
        if (mGridVideoViewContainerAdapter == null) {
            return;
        }
        mGridVideoViewContainerAdapter.notifyUiChanged(uids, localUid, status, volume);
    }

    public void addVideoInfo(int uid, VideoInfoData video) {
        if (mGridVideoViewContainerAdapter == null) {
            return;
        }
        mGridVideoViewContainerAdapter.addVideoInfo(uid, video);
    }

    public void cleanVideoInfo() {
        if (mGridVideoViewContainerAdapter == null) {
            return;
        }
        mGridVideoViewContainerAdapter.cleanVideoInfo();
    }

    public UserStatusData getItem(int position) {
        return mGridVideoViewContainerAdapter.getItem(position);
    }

    public  void notifyVideoGrid(boolean allAnswerSubmit, boolean displayScore, HashMap<Integer, QuestionWiseResultKeyValueData> dataHashMap) {
        mGridVideoViewContainerAdapter.notifyVideoGridAfterAllPlayed(allAnswerSubmit, displayScore, dataHashMap);
    }

    public void notifyMemberInfo(ChannelDetailResponse.ChannelDetailResponseData data, HashMap<Integer, MemberInfoKeyValueData> memberInfoKeyValueDataHashMap){
        mGridVideoViewContainerAdapter.notifyMemberInfoOnView(data, memberInfoKeyValueDataHashMap);
    }
}