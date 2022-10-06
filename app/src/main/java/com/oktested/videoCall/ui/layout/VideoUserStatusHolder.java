package com.oktested.videoCall.ui.layout;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.oktested.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoUserStatusHolder extends RecyclerView.ViewHolder {

    public final RelativeLayout mMaskView, answerRL, userAfterRL;
    public final ImageView mAvatar, answerIV;
    public final ImageView mIndicator, userAfterIV;
    public final CircleImageView userIV;
    public final LinearLayout mVideoInfo, readyLL, userLL;
    public final TextView mMetaData, scoreTV, userNameTV, readyTV, removeTV, nameTV;
    public final RadioButton readyRB;
    public final CardView videoCardView;

    public VideoUserStatusHolder(View v) {
        super(v);
        mMaskView = (RelativeLayout) v.findViewById(R.id.user_control_mask);
        answerRL = (RelativeLayout) v.findViewById(R.id.answerRL);
        userAfterRL = (RelativeLayout) v.findViewById(R.id.userAfterRL);
        mAvatar = (ImageView) v.findViewById(R.id.default_avatar);
        mIndicator = (ImageView) v.findViewById(R.id.indicator);
        answerIV = (ImageView) v.findViewById(R.id.answerIV);
        mVideoInfo = (LinearLayout) v.findViewById(R.id.video_info_container);
        mMetaData = (TextView) v.findViewById(R.id.video_info_metadata);
        scoreTV = (TextView) v.findViewById(R.id.scoreTV);
        videoCardView = (CardView) v.findViewById(R.id.videoCardView);

        userNameTV = (TextView) v.findViewById(R.id.userNameTV);
        readyTV = (TextView) v.findViewById(R.id.readyTV);
        removeTV = (TextView) v.findViewById(R.id.removeTV);
        readyRB = (RadioButton) v.findViewById(R.id.readyRB);
        readyLL = (LinearLayout) v.findViewById(R.id.readyLL);

        userAfterIV = (ImageView) v.findViewById(R.id.userAfterIV);
        userLL = (LinearLayout) v.findViewById(R.id.userLL);
        nameTV = (TextView) v.findViewById(R.id.nameTV);
        userIV = (CircleImageView) v.findViewById(R.id.userIV);
    }
}