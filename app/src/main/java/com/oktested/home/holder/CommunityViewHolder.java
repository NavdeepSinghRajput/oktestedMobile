package com.oktested.home.holder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.oktested.R;
import com.oktested.utils.AppConstants;
import com.oktested.utils.HeightWrappingViewPager;
import com.oktested.utils.ScaleImageView;
import com.rd.PageIndicatorView;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommunityViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView communityImage;
    public TextView communityName, postDateTV, descriptionTV, postLikeTV, postShareTV, postCommentTV, audioNameTV, audioCategoryTV, linkTV;
    public ScaleImageView postIV, thumbIV;
    public RelativeLayout upperRL, audioRL, resizeRL, playerRL;
    public HeightWrappingViewPager carouselVP;
    public PageIndicatorView pageIndicatorView;
    public RecyclerView optionsRV;
    public ImageButton playIB;
    public DefaultTimeBar exoSeekBar;
    public PlayerView playerView;
    public PlayerControlView audioControls, videoControls;

    public CommunityViewHolder(@NonNull View itemView, int viewType) {
        super(itemView);
        switch (viewType) {
            case AppConstants.POST_IMAGE:
                communityImage = itemView.findViewById(R.id.communityImage);
                postIV = itemView.findViewById(R.id.postIV);
                communityName = itemView.findViewById(R.id.communityName);
                postDateTV = itemView.findViewById(R.id.postDateTV);
                descriptionTV = itemView.findViewById(R.id.descriptionTV);
                postLikeTV = itemView.findViewById(R.id.postLikeTV);
                postShareTV = itemView.findViewById(R.id.postShareTV);
                postCommentTV = itemView.findViewById(R.id.postCommentTV);

            case AppConstants.POST_TEXT:
                communityImage = itemView.findViewById(R.id.communityImage);
                communityName = itemView.findViewById(R.id.communityName);
                postDateTV = itemView.findViewById(R.id.postDateTV);
                descriptionTV = itemView.findViewById(R.id.descriptionTV);
                postLikeTV = itemView.findViewById(R.id.postLikeTV);
                postShareTV = itemView.findViewById(R.id.postShareTV);
                postCommentTV = itemView.findViewById(R.id.postCommentTV);

            case AppConstants.POST_VIDEO:
                communityImage = itemView.findViewById(R.id.communityImage);
                communityName = itemView.findViewById(R.id.communityName);
                postDateTV = itemView.findViewById(R.id.postDateTV);
                descriptionTV = itemView.findViewById(R.id.descriptionTV);
                postLikeTV = itemView.findViewById(R.id.postLikeTV);
                postShareTV = itemView.findViewById(R.id.postShareTV);
                postCommentTV = itemView.findViewById(R.id.postCommentTV);

                playerRL = itemView.findViewById(R.id.playerRL);
                playerView = itemView.findViewById(R.id.playerView);
                videoControls = itemView.findViewById(R.id.videoControls);
                if (videoControls != null) {
                    resizeRL = videoControls.findViewById(R.id.relative);
                    resizeRL.setVisibility(View.GONE);
                    playIB = videoControls.findViewById(R.id.exo_play);
                    exoSeekBar = videoControls.findViewById(R.id.exo_progress);
                }

            case AppConstants.POST_AUDIO:
                communityImage = itemView.findViewById(R.id.communityImage);
                thumbIV = itemView.findViewById(R.id.thumbIV);
                communityName = itemView.findViewById(R.id.communityName);
                postDateTV = itemView.findViewById(R.id.postDateTV);
                descriptionTV = itemView.findViewById(R.id.descriptionTV);
                postLikeTV = itemView.findViewById(R.id.postLikeTV);
                postShareTV = itemView.findViewById(R.id.postShareTV);
                postCommentTV = itemView.findViewById(R.id.postCommentTV);

                playerRL = itemView.findViewById(R.id.playerRL);
                audioRL = itemView.findViewById(R.id.audioRL);
                playerView = itemView.findViewById(R.id.playerView);
                audioControls = itemView.findViewById(R.id.audioControls);
                if (audioControls != null) {
                    playIB = audioControls.findViewById(R.id.exo_play);
                    exoSeekBar = audioControls.findViewById(R.id.exo_progress);
                    audioNameTV = audioControls.findViewById(R.id.audioNameTV);
                    audioCategoryTV = audioControls.findViewById(R.id.audioCategoryTV);
                }

            case AppConstants.POST_LINK:
                communityImage = itemView.findViewById(R.id.communityImage);
                postIV = itemView.findViewById(R.id.postIV);
                upperRL = itemView.findViewById(R.id.upperRL);
                communityName = itemView.findViewById(R.id.communityName);
                postDateTV = itemView.findViewById(R.id.postDateTV);
                descriptionTV = itemView.findViewById(R.id.descriptionTV);
                linkTV = itemView.findViewById(R.id.linkTV);
                postLikeTV = itemView.findViewById(R.id.postLikeTV);
                postShareTV = itemView.findViewById(R.id.postShareTV);
                postCommentTV = itemView.findViewById(R.id.postCommentTV);

            case AppConstants.POST_CAROUSEL:
                communityImage = itemView.findViewById(R.id.communityImage);
                communityName = itemView.findViewById(R.id.communityName);
                postDateTV = itemView.findViewById(R.id.postDateTV);
                descriptionTV = itemView.findViewById(R.id.descriptionTV);
                postLikeTV = itemView.findViewById(R.id.postLikeTV);
                postShareTV = itemView.findViewById(R.id.postShareTV);
                postCommentTV = itemView.findViewById(R.id.postCommentTV);
                carouselVP = itemView.findViewById(R.id.carouselVP);
                pageIndicatorView = itemView.findViewById(R.id.pageIndicatorView);

            case AppConstants.POST_POLL:
                communityImage = itemView.findViewById(R.id.communityImage);
                postIV = itemView.findViewById(R.id.postIV);
                communityName = itemView.findViewById(R.id.communityName);
                postDateTV = itemView.findViewById(R.id.postDateTV);
                descriptionTV = itemView.findViewById(R.id.descriptionTV);
                postLikeTV = itemView.findViewById(R.id.postLikeTV);
                postShareTV = itemView.findViewById(R.id.postShareTV);
                postCommentTV = itemView.findViewById(R.id.postCommentTV);
                optionsRV = itemView.findViewById(R.id.optionsRV);
        }
    }
}