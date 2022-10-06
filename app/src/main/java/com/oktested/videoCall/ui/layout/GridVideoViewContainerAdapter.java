package com.oktested.videoCall.ui.layout;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.oktested.utils.Helper;
import com.oktested.videoCall.model.ChannelDetailResponse;
import com.oktested.videoCall.model.MemberInfoKeyValueData;
import com.oktested.videoCall.model.QuestionWiseResultKeyValueData;
import com.oktested.videoCall.model.QuestionWiseResultResponse;
import com.oktested.videoCall.propeller.UserStatusData;

import java.util.ArrayList;
import java.util.HashMap;

public class GridVideoViewContainerAdapter extends VideoViewAdapter {

    public boolean quizSelected = false;
    private Context context;
    private int viewWidth, viewHeight;

    public GridVideoViewContainerAdapter(Activity activity, int localUid, HashMap<Integer, SurfaceView> uids) {
        super(activity, localUid, uids);
        this.context = activity;
        //    log.debug("GridVideoViewContainerAdapter " + (mLocalUid & 0xFFFFFFFFL));
    }

    @Override
    protected void customizedInit(HashMap<Integer, SurfaceView> uids, boolean force) {
        VideoViewAdapterUtil.composeDataItem1(mUsers, uids, mLocalUid); // local uid

        if (force || mItemWidth == 0 || mItemHeight == 0) {
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(outMetrics);

            int count = uids.size();
            int DividerX = 1;
            int DividerY = 1;

            if (count == 2) {
                DividerY = 2;
            } else if (count >= 3) {
                DividerX = getNearestSqrt(count);
                DividerY = (int) Math.ceil(count * 1.f / DividerX);
            }

            int width = outMetrics.widthPixels;
            int height = outMetrics.heightPixels;

            if (!quizSelected) {
                if (width > height) {
                    mItemWidth = width / DividerY;
                    mItemHeight = height / DividerX;
                } else {
                    mItemWidth = width / DividerX;
                    mItemHeight = height / DividerY;
                }
                viewWidth = mItemWidth;
                viewHeight = mItemHeight;
            } else {
                mItemWidth = (int) Helper.pxFromDp(context, 80);
                mItemHeight = (int) Helper.pxFromDp(context, 80);
            }
        }
    }

    private int getNearestSqrt(int n) {
        return (int) Math.sqrt(n);
    }

    @Override
    public void notifyUiChanged(HashMap<Integer, SurfaceView> uids, int localUid, HashMap<Integer, Integer> status, HashMap<Integer, Integer> volume) {
        setLocalUid(localUid);
        VideoViewAdapterUtil.composeDataItem(mUsers, uids, localUid, status, volume, mVideoInfo);
        notifyDataSetChanged();
      /*  if (DEBUG) {
            log.debug("notifyUiChanged " + (mLocalUid & 0xFFFFFFFFL) + " " + (localUid & 0xFFFFFFFFL) + " " + uids + " " + status + " " + volume);
        }*/
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public UserStatusData getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        UserStatusData user = mUsers.get(position);
        SurfaceView view = user.mView;
        if (view == null) {
            throw new NullPointerException("SurfaceView destroyed for user " + (user.mUid & 0xFFFFFFFFL) + " " + user.mStatus + " " + user.mVolume);
        }
        return (String.valueOf(user.mUid) + System.identityHashCode(view)).hashCode();
    }

    public void notifyUiAfterPickQuiz() {
        quizSelected = true;
        mItemWidth = (int) Helper.pxFromDp(context, 80);
        mItemHeight = (int) Helper.pxFromDp(context, 80);
        startQuizPlay(true);
    }

    public void notifyUiAfterQuizResult() {
        quizSelected = false;
        mItemWidth = viewWidth;
        mItemHeight = viewHeight;
        startQuizPlay(false);
    }

    public void notifyVideoGridAfterAllPlayed(boolean allAnswerSubmit, boolean displayScore, HashMap<Integer, QuestionWiseResultKeyValueData> dataHashMap) {
        displayCorrectWrongAnswer(allAnswerSubmit, displayScore, dataHashMap);
    }

    public void notifyMemberInfoOnView(ChannelDetailResponse.ChannelDetailResponseData data, HashMap<Integer, MemberInfoKeyValueData> memberInfoKeyValueDataHashMap) {
        notifyMembersInfo(data, memberInfoKeyValueDataHashMap);
    }
}