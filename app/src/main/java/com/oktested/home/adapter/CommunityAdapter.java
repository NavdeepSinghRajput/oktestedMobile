package com.oktested.home.adapter;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.oktested.R;
import com.oktested.activities.WebViewActivity;
import com.oktested.community.ui.PostDetailActivity;
import com.oktested.home.holder.CommunityViewHolder;
import com.oktested.home.model.CommunityPostDataResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.CommunityApplicationSelectorReceiver;
import com.oktested.utils.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityViewHolder> {

    private Context context;
    private ArrayList<CommunityPostDataResponse> postDataResponseAL;
    private PostReaction postReaction;
    private PollSubmit pollSubmit;
    private SwipeLayout swipeLayout;
    private HashMap<Integer, SimpleExoPlayer> playerHashMap = new HashMap<>();

    public CommunityAdapter(Context context, ArrayList<CommunityPostDataResponse> postDataResponseAL, PostReaction postReaction, PollSubmit pollSubmit, SwipeLayout swipeLayout) {
        this.context = context;
        this.postDataResponseAL = postDataResponseAL;
        this.postReaction = postReaction;
        this.pollSubmit = pollSubmit;
        this.swipeLayout = swipeLayout;
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case AppConstants.POST_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_post_layout, parent, false);
                return new CommunityViewHolder(view, viewType);

            case AppConstants.POST_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_post_layout, parent, false);
                return new CommunityViewHolder(view, viewType);

            case AppConstants.POST_VIDEO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_post_layout, parent, false);
                return new CommunityViewHolder(view, viewType);

            case AppConstants.POST_AUDIO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_post_layout, parent, false);
                return new CommunityViewHolder(view, viewType);

            case AppConstants.POST_LINK:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.link_post_layout, parent, false);
                return new CommunityViewHolder(view, viewType);

            case AppConstants.POST_CAROUSEL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carousel_post_layout, parent, false);
                return new CommunityViewHolder(view, viewType);

            case AppConstants.POST_POLL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.poll_post_layout, parent, false);
                return new CommunityViewHolder(view, viewType);

            case AppConstants.SECTION_UNKNOWN:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.unknown_layout, parent, false);
                return new CommunityViewHolder(view, viewType);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        CommunityPostDataResponse postDataResponse = postDataResponseAL.get(position);
        if (postDataResponse != null) {
            switch (postDataResponse.type) {
                case AppConstants.POST_IMAGE_LAYOUT:
                    loadImagePost(holder, postDataResponse, position);
                    break;

                case AppConstants.POST_TEXT_LAYOUT:
                    loadTextPost(holder, postDataResponse, position);
                    break;

                case AppConstants.POST_VIDEO_LAYOUT:
                    loadVideoPost(holder, postDataResponse, position);
                    break;

                case AppConstants.POST_AUDIO_LAYOUT:
                    loadAudioPost(holder, postDataResponse, position);
                    break;

                case AppConstants.POST_LINK_LAYOUT:
                    loadLinkPost(holder, postDataResponse, position);
                    break;

                case AppConstants.POST_CAROUSEL_LAYOUT:
                    loadCarouselPost(holder, postDataResponse, position);
                    break;

                case AppConstants.POST_POLL_LAYOUT:
                    loadPollPost(holder, postDataResponse, position);
                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (postDataResponseAL.get(position).type) {
            case AppConstants.POST_IMAGE_LAYOUT:
                return AppConstants.POST_IMAGE;

            case AppConstants.POST_TEXT_LAYOUT:
                return AppConstants.POST_TEXT;

            case AppConstants.POST_VIDEO_LAYOUT:
                return AppConstants.POST_VIDEO;

            case AppConstants.POST_AUDIO_LAYOUT:
                return AppConstants.POST_AUDIO;

            case AppConstants.POST_LINK_LAYOUT:
                return AppConstants.POST_LINK;

            case AppConstants.POST_CAROUSEL_LAYOUT:
                return AppConstants.POST_CAROUSEL;

            case AppConstants.POST_POLL_LAYOUT:
                return AppConstants.POST_POLL;

            default:
                return AppConstants.SECTION_UNKNOWN;
        }
    }

    @Override
    public int getItemCount() {
        return postDataResponseAL.size();
    }

    public void setMorePostsData(ArrayList<CommunityPostDataResponse> postDataResponseAL) {
        int startPosition = this.postDataResponseAL.size();
        this.postDataResponseAL.addAll(postDataResponseAL);
        notifyItemRangeInserted(startPosition, postDataResponseAL.size());
    }

    public void setFreshData(ArrayList<CommunityPostDataResponse> postDataResponseAL) {
        this.postDataResponseAL.clear();
        this.playerHashMap.clear();
        this.postDataResponseAL.addAll(postDataResponseAL);
        notifyDataSetChanged();
    }

    public void updateShareCount(int position) {
        postDataResponseAL.get(position).setShare_count(postDataResponseAL.get(position).share_count + 1);
        postDataResponseAL.get(position).setPlayerAlreadyAdded(false);
        if ((postDataResponseAL.get(position).type.equalsIgnoreCase(AppConstants.POST_AUDIO_LAYOUT) || postDataResponseAL.get(position).type.equalsIgnoreCase(AppConstants.POST_VIDEO_LAYOUT)) && playerHashMap.get(position) != null) {
            postDataResponseAL.get(position).setPlaybackPosition(Math.max(0, playerHashMap.get(position).getContentPosition()));
        }
        notifyItemChanged(position);
    }

    public void updateItemChanges(CommunityPostDataResponse postDataResponse, int position) {
        postDataResponseAL.set(position, postDataResponse);
        notifyItemChanged(position);
    }

    private void loadImagePost(CommunityViewHolder holder, CommunityPostDataResponse postDataResponse, int position) {
        if (holder != null && postDataResponse != null) {
            if (Helper.isContainValue(postDataResponse.user_picture)) {
                Glide.with(context)
                        .load(postDataResponse.user_picture)
                        .placeholder(R.drawable.community_placeholder)
                        .into(holder.communityImage);
            } else {
                holder.communityImage.setImageDrawable(context.getResources().getDrawable(R.drawable.community_placeholder));
            }

            if (Helper.isContainValue(postDataResponse.media)) {
                holder.postIV.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(postDataResponse.media)
                        .placeholder(R.drawable.placeholder)
                        .into(holder.postIV);
            } else {
                holder.postIV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(postDataResponse.user_name)) {
                holder.communityName.setText(postDataResponse.user_name);
            } else {
                holder.communityName.setText("Ok Tested");
            }

            if (Helper.isContainValue(postDataResponse.description)) {
                holder.descriptionTV.setVisibility(View.VISIBLE);
                holder.descriptionTV.setText(postDataResponse.description);
            } else {
                holder.descriptionTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(postDataResponse.created)) {
                holder.postDateTV.setVisibility(View.VISIBLE);
                setPostDate(holder.postDateTV, postDataResponse.created);
            } else {
                holder.postDateTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.comment_count))) {
                holder.postCommentTV.setVisibility(View.VISIBLE);
                if (postDataResponse.comment_count == 0) {
                    holder.postCommentTV.setText("Be the first to comment");
                } else if (postDataResponse.comment_count == 1) {
                    holder.postCommentTV.setText("View 1 comment");
                } else {
                    holder.postCommentTV.setText("View all " + postDataResponse.comment_count + " comments");
                }
            } else {
                holder.postCommentTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.share_count))) {
                holder.postShareTV.setVisibility(View.VISIBLE);
                holder.postShareTV.setText(postDataResponse.share_count + "");
            } else {
                holder.postShareTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.total_reactions))) {
                holder.postLikeTV.setVisibility(View.VISIBLE);
                holder.postLikeTV.setText(postDataResponse.total_reactions + "");
            } else {
                holder.postLikeTV.setVisibility(View.GONE);
            }

            // For set the post reaction
            if (Helper.isContainValue(postDataResponse.is_reacted) && postDataResponse.is_reacted.equalsIgnoreCase("up")) {
                holder.postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_like_heart, 0, 0, 0);
            } else {
                holder.postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_heart, 0, 0, 0);
            }

            setOnItemClick(holder, holder.postCommentTV, postDataResponse, position);
            setOnItemClick(holder, holder.postShareTV, postDataResponse, position);
            setOnItemClick(holder, holder.postLikeTV, postDataResponse, position);
        }
    }

    private void loadVideoPost(CommunityViewHolder holder, CommunityPostDataResponse postDataResponse, int position) {
        if (holder != null && postDataResponse != null) {
            if (Helper.isContainValue(postDataResponse.user_picture)) {
                Glide.with(context)
                        .load(postDataResponse.user_picture)
                        .placeholder(R.drawable.community_placeholder)
                        .into(holder.communityImage);
            } else {
                holder.communityImage.setImageDrawable(context.getResources().getDrawable(R.drawable.community_placeholder));
            }

            if (Helper.isContainValue(postDataResponse.user_name)) {
                holder.communityName.setText(postDataResponse.user_name);
            } else {
                holder.communityName.setText("Ok Tested");
            }

            if (Helper.isContainValue(postDataResponse.description)) {
                holder.descriptionTV.setVisibility(View.VISIBLE);
                holder.descriptionTV.setText(postDataResponse.description);
            } else {
                holder.descriptionTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(postDataResponse.created)) {
                holder.postDateTV.setVisibility(View.VISIBLE);
                setPostDate(holder.postDateTV, postDataResponse.created);
            } else {
                holder.postDateTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.comment_count))) {
                holder.postCommentTV.setVisibility(View.VISIBLE);
                if (postDataResponse.comment_count == 0) {
                    holder.postCommentTV.setText("Be the first to comment");
                } else if (postDataResponse.comment_count == 1) {
                    holder.postCommentTV.setText("View 1 comment");
                } else {
                    holder.postCommentTV.setText("View all " + postDataResponse.comment_count + " comments");
                }
            } else {
                holder.postCommentTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.share_count))) {
                holder.postShareTV.setVisibility(View.VISIBLE);
                holder.postShareTV.setText(postDataResponse.share_count + "");
            } else {
                holder.postShareTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.total_reactions))) {
                holder.postLikeTV.setVisibility(View.VISIBLE);
                holder.postLikeTV.setText(postDataResponse.total_reactions + "");
            } else {
                holder.postLikeTV.setVisibility(View.GONE);
            }

            // For set the post reaction
            if (Helper.isContainValue(postDataResponse.is_reacted) && postDataResponse.is_reacted.equalsIgnoreCase("up")) {
                holder.postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_like_heart, 0, 0, 0);
            } else {
                holder.postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_heart, 0, 0, 0);
            }

            if (Helper.isContainValue(postDataResponse.media)) {
                holder.playerRL.setVisibility(View.VISIBLE);
                if (!postDataResponse.isPlayerAlreadyAdded()) {
                    initVideoPlayer(holder, postDataResponse.media, position);
                    postDataResponseAL.get(position).setPlayerAlreadyAdded(true);
                }

                holder.videoControls.setPlayer(playerHashMap.get(position));
                holder.playerView.setPlayer(playerHashMap.get(position));

                if (postDataResponse.dimension != null && Helper.isContainValue(String.valueOf(postDataResponse.dimension.height))) {
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int seventyFivePercentHeight = (int) (0.75 * displayMetrics.heightPixels);
                    if (postDataResponse.dimension.height <= seventyFivePercentHeight) {
                        holder.playerView.getLayoutParams().height = postDataResponse.dimension.height;
                        holder.videoControls.getLayoutParams().height = postDataResponse.dimension.height;
                    } else {
                        holder.playerView.getLayoutParams().height = seventyFivePercentHeight;
                        holder.videoControls.getLayoutParams().height = seventyFivePercentHeight;
                    }

                    holder.playerView.requestLayout();
                    holder.videoControls.requestLayout();
                }
            } else {
                holder.playerRL.setVisibility(View.GONE);
            }

            setOnItemClick(holder, holder.postCommentTV, postDataResponse, position);
            setOnItemClick(holder, holder.postShareTV, postDataResponse, position);
            setOnItemClick(holder, holder.postLikeTV, postDataResponse, position);
            setOnItemClick(holder, holder.playIB, postDataResponse, position);
            setOnItemClick(holder, holder.playerRL, postDataResponse, position);
        }
    }

    private void loadTextPost(CommunityViewHolder holder, CommunityPostDataResponse postDataResponse, int position) {
        if (holder != null && postDataResponse != null) {
            if (Helper.isContainValue(postDataResponse.user_picture)) {
                Glide.with(context)
                        .load(postDataResponse.user_picture)
                        .placeholder(R.drawable.community_placeholder)
                        .into(holder.communityImage);
            } else {
                holder.communityImage.setImageDrawable(context.getResources().getDrawable(R.drawable.community_placeholder));
            }

            if (Helper.isContainValue(postDataResponse.user_name)) {
                holder.communityName.setText(postDataResponse.user_name);
            } else {
                holder.communityName.setText("Ok Tested");
            }

            if (Helper.isContainValue(postDataResponse.description)) {
                holder.descriptionTV.setVisibility(View.VISIBLE);
                holder.descriptionTV.setText(postDataResponse.description);
            } else {
                holder.descriptionTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(postDataResponse.created)) {
                holder.postDateTV.setVisibility(View.VISIBLE);
                setPostDate(holder.postDateTV, postDataResponse.created);
            } else {
                holder.postDateTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.comment_count))) {
                holder.postCommentTV.setVisibility(View.VISIBLE);
                if (postDataResponse.comment_count == 0) {
                    holder.postCommentTV.setText("Be the first to comment");
                } else if (postDataResponse.comment_count == 1) {
                    holder.postCommentTV.setText("View 1 comment");
                } else {
                    holder.postCommentTV.setText("View all " + postDataResponse.comment_count + " comments");
                }
            } else {
                holder.postCommentTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.share_count))) {
                holder.postShareTV.setVisibility(View.VISIBLE);
                holder.postShareTV.setText(postDataResponse.share_count + "");
            } else {
                holder.postShareTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.total_reactions))) {
                holder.postLikeTV.setVisibility(View.VISIBLE);
                holder.postLikeTV.setText(postDataResponse.total_reactions + "");
            } else {
                holder.postLikeTV.setVisibility(View.GONE);
            }

            // For set the post reaction
            if (Helper.isContainValue(postDataResponse.is_reacted) && postDataResponse.is_reacted.equalsIgnoreCase("up")) {
                holder.postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_like_heart, 0, 0, 0);
            } else {
                holder.postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_heart, 0, 0, 0);
            }

            setOnItemClick(holder, holder.postCommentTV, postDataResponse, position);
            setOnItemClick(holder, holder.postShareTV, postDataResponse, position);
            setOnItemClick(holder, holder.postLikeTV, postDataResponse, position);
        }
    }

    private void loadAudioPost(CommunityViewHolder holder, CommunityPostDataResponse postDataResponse, int position) {
        if (holder != null && postDataResponse != null) {
            if (Helper.isContainValue(postDataResponse.user_picture)) {
                Glide.with(context)
                        .load(postDataResponse.user_picture)
                        .placeholder(R.drawable.community_placeholder)
                        .into(holder.communityImage);
            } else {
                holder.communityImage.setImageDrawable(context.getResources().getDrawable(R.drawable.community_placeholder));
            }

            if (Helper.isContainValue(postDataResponse.thumbnail_image)) {
                holder.thumbIV.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(postDataResponse.thumbnail_image)
                        .placeholder(R.drawable.placeholder)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.audioRL.getLayoutParams();
                                layoutParams.setMargins(30, resource.getIntrinsicHeight() - 80, 30, 0);
                                holder.audioRL.requestLayout();
                                return false;
                            }
                        })
                        .into(holder.thumbIV);
            } else {
                holder.thumbIV.setVisibility(View.GONE);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.audioRL.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, 0);
                holder.audioRL.requestLayout();
            }

            if (Helper.isContainValue(postDataResponse.user_name)) {
                holder.communityName.setText(postDataResponse.user_name);
            } else {
                holder.communityName.setText("Ok Tested");
            }

            if (Helper.isContainValue(postDataResponse.description)) {
                holder.descriptionTV.setVisibility(View.VISIBLE);
                holder.descriptionTV.setText(postDataResponse.description);
            } else {
                holder.descriptionTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(postDataResponse.created)) {
                holder.postDateTV.setVisibility(View.VISIBLE);
                setPostDate(holder.postDateTV, postDataResponse.created);
            } else {
                holder.postDateTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(postDataResponse.title)) {
                holder.audioNameTV.setVisibility(View.VISIBLE);
                holder.audioNameTV.setText(postDataResponse.title);
            } else {
                holder.audioNameTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(postDataResponse.subtitle)) {
                holder.audioCategoryTV.setVisibility(View.VISIBLE);
                holder.audioCategoryTV.setText(postDataResponse.subtitle);
            } else {
                holder.audioCategoryTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.comment_count))) {
                holder.postCommentTV.setVisibility(View.VISIBLE);
                if (postDataResponse.comment_count == 0) {
                    holder.postCommentTV.setText("Be the first to comment");
                } else if (postDataResponse.comment_count == 1) {
                    holder.postCommentTV.setText("View 1 comment");
                } else {
                    holder.postCommentTV.setText("View all " + postDataResponse.comment_count + " comments");
                }
            } else {
                holder.postCommentTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.share_count))) {
                holder.postShareTV.setVisibility(View.VISIBLE);
                holder.postShareTV.setText(postDataResponse.share_count + "");
            } else {
                holder.postShareTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.total_reactions))) {
                holder.postLikeTV.setVisibility(View.VISIBLE);
                holder.postLikeTV.setText(postDataResponse.total_reactions + "");
            } else {
                holder.postLikeTV.setVisibility(View.GONE);
            }

            // For set the post reaction
            if (Helper.isContainValue(postDataResponse.is_reacted) && postDataResponse.is_reacted.equalsIgnoreCase("up")) {
                holder.postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_like_heart, 0, 0, 0);
            } else {
                holder.postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_heart, 0, 0, 0);
            }

            if (Helper.isContainValue(postDataResponse.media)) {
                holder.playerRL.setVisibility(View.VISIBLE);
                if (!postDataResponse.isPlayerAlreadyAdded()) {
                    initAudioPlayer(holder, postDataResponse.media, position);
                    postDataResponseAL.get(position).setPlayerAlreadyAdded(true);
                }
                holder.audioControls.setPlayer(playerHashMap.get(position));
            } else {
                holder.playerRL.setVisibility(View.GONE);
            }

            setOnItemClick(holder, holder.postCommentTV, postDataResponse, position);
            setOnItemClick(holder, holder.postShareTV, postDataResponse, position);
            setOnItemClick(holder, holder.postLikeTV, postDataResponse, position);
            setOnItemClick(holder, holder.playIB, postDataResponse, position);
        }
    }

    private void loadLinkPost(CommunityViewHolder holder, CommunityPostDataResponse postDataResponse, int position) {
        if (holder != null && postDataResponse != null) {
            if (Helper.isContainValue(postDataResponse.user_picture)) {
                Glide.with(context)
                        .load(postDataResponse.user_picture)
                        .placeholder(R.drawable.community_placeholder)
                        .into(holder.communityImage);
            } else {
                holder.communityImage.setImageDrawable(context.getResources().getDrawable(R.drawable.community_placeholder));
            }

            if (Helper.isContainValue(postDataResponse.user_name)) {
                holder.communityName.setText(postDataResponse.user_name);
            } else {
                holder.communityName.setText("Ok Tested");
            }

            if (Helper.isContainValue(postDataResponse.description)) {
                holder.descriptionTV.setVisibility(View.VISIBLE);
                holder.descriptionTV.setText(postDataResponse.description);
            } else {
                holder.descriptionTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(postDataResponse.created)) {
                holder.postDateTV.setVisibility(View.VISIBLE);
                setPostDate(holder.postDateTV, postDataResponse.created);
            } else {
                holder.postDateTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.comment_count))) {
                holder.postCommentTV.setVisibility(View.VISIBLE);
                if (postDataResponse.comment_count == 0) {
                    holder.postCommentTV.setText("Be the first to comment");
                } else if (postDataResponse.comment_count == 1) {
                    holder.postCommentTV.setText("View 1 comment");
                } else {
                    holder.postCommentTV.setText("View all " + postDataResponse.comment_count + " comments");
                }
            } else {
                holder.postCommentTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.share_count))) {
                holder.postShareTV.setVisibility(View.VISIBLE);
                holder.postShareTV.setText(postDataResponse.share_count + "");
            } else {
                holder.postShareTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.total_reactions))) {
                holder.postLikeTV.setVisibility(View.VISIBLE);
                holder.postLikeTV.setText(postDataResponse.total_reactions + "");
            } else {
                holder.postLikeTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(postDataResponse.media)) {
                holder.postIV.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(postDataResponse.media)
                        .placeholder(R.drawable.placeholder)
                        .into(holder.postIV);
            } else {
                holder.postIV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(postDataResponse.title)) {
                holder.linkTV.setVisibility(View.VISIBLE);
                holder.linkTV.setText(postDataResponse.title);
            } else {
                holder.linkTV.setVisibility(View.GONE);
            }

            // For set the post reaction
            if (Helper.isContainValue(postDataResponse.is_reacted) && postDataResponse.is_reacted.equalsIgnoreCase("up")) {
                holder.postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_like_heart, 0, 0, 0);
            } else {
                holder.postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_heart, 0, 0, 0);
            }

            setOnItemClick(holder, holder.postCommentTV, postDataResponse, position);
            setOnItemClick(holder, holder.postShareTV, postDataResponse, position);
            setOnItemClick(holder, holder.postLikeTV, postDataResponse, position);
            setOnItemClick(holder, holder.upperRL, postDataResponse, position);
        }
    }

    private void loadCarouselPost(CommunityViewHolder holder, CommunityPostDataResponse postDataResponse, int position) {
        if (holder != null && postDataResponse != null) {
            if (Helper.isContainValue(postDataResponse.user_picture)) {
                Glide.with(context)
                        .load(postDataResponse.user_picture)
                        .placeholder(R.drawable.community_placeholder)
                        .into(holder.communityImage);
            } else {
                holder.communityImage.setImageDrawable(context.getResources().getDrawable(R.drawable.community_placeholder));
            }

            if (Helper.isContainValue(postDataResponse.user_name)) {
                holder.communityName.setText(postDataResponse.user_name);
            } else {
                holder.communityName.setText("Ok Tested");
            }

            if (Helper.isContainValue(postDataResponse.description)) {
                holder.descriptionTV.setVisibility(View.VISIBLE);
                holder.descriptionTV.setText(postDataResponse.description);
            } else {
                holder.descriptionTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(postDataResponse.created)) {
                holder.postDateTV.setVisibility(View.VISIBLE);
                setPostDate(holder.postDateTV, postDataResponse.created);
            } else {
                holder.postDateTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.comment_count))) {
                holder.postCommentTV.setVisibility(View.VISIBLE);
                if (postDataResponse.comment_count == 0) {
                    holder.postCommentTV.setText("Be the first to comment");
                } else if (postDataResponse.comment_count == 1) {
                    holder.postCommentTV.setText("View 1 comment");
                } else {
                    holder.postCommentTV.setText("View all " + postDataResponse.comment_count + " comments");
                }
            } else {
                holder.postCommentTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.share_count))) {
                holder.postShareTV.setVisibility(View.VISIBLE);
                holder.postShareTV.setText(postDataResponse.share_count + "");
            } else {
                holder.postShareTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.total_reactions))) {
                holder.postLikeTV.setVisibility(View.VISIBLE);
                holder.postLikeTV.setText(postDataResponse.total_reactions + "");
            } else {
                holder.postLikeTV.setVisibility(View.GONE);
            }

            if (postDataResponse.album != null && postDataResponse.album.size() > 0) {
                holder.carouselVP.setVisibility(View.VISIBLE);
                holder.pageIndicatorView.setVisibility(View.VISIBLE);
                holder.pageIndicatorView.setPadding(4);
                holder.pageIndicatorView.setRadius(3);
                CarouselPostAdapter carouselPostAdapter = new CarouselPostAdapter(context, postDataResponse.album);
                holder.carouselVP.setAdapter(carouselPostAdapter);
            } else {
                holder.carouselVP.setVisibility(View.GONE);
                holder.pageIndicatorView.setVisibility(View.GONE);
            }

            // For set the post reaction
            if (Helper.isContainValue(postDataResponse.is_reacted) && postDataResponse.is_reacted.equalsIgnoreCase("up")) {
                holder.postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_like_heart, 0, 0, 0);
            } else {
                holder.postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_heart, 0, 0, 0);
            }

            setOnItemClick(holder, holder.postCommentTV, postDataResponse, position);
            setOnItemClick(holder, holder.postShareTV, postDataResponse, position);
            setOnItemClick(holder, holder.postLikeTV, postDataResponse, position);
        }
    }

    private void loadPollPost(CommunityViewHolder holder, CommunityPostDataResponse postDataResponse, int position) {
        if (holder != null && postDataResponse != null) {
            if (Helper.isContainValue(postDataResponse.user_picture)) {
                Glide.with(context)
                        .load(postDataResponse.user_picture)
                        .placeholder(R.drawable.community_placeholder)
                        .into(holder.communityImage);
            } else {
                holder.communityImage.setImageDrawable(context.getResources().getDrawable(R.drawable.community_placeholder));
            }

            if (Helper.isContainValue(postDataResponse.media)) {
                holder.postIV.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(postDataResponse.media)
                        .placeholder(R.drawable.placeholder)
                        .into(holder.postIV);
            } else {
                holder.postIV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(postDataResponse.user_name)) {
                holder.communityName.setText(postDataResponse.user_name);
            } else {
                holder.communityName.setText("Ok Tested");
            }

            if (Helper.isContainValue(postDataResponse.description)) {
                holder.descriptionTV.setVisibility(View.VISIBLE);
                holder.descriptionTV.setText(postDataResponse.description);
            } else {
                holder.descriptionTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(postDataResponse.created)) {
                holder.postDateTV.setVisibility(View.VISIBLE);
                setPostDate(holder.postDateTV, postDataResponse.created);
            } else {
                holder.postDateTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.comment_count))) {
                holder.postCommentTV.setVisibility(View.VISIBLE);
                if (postDataResponse.comment_count == 0) {
                    holder.postCommentTV.setText("Be the first to comment");
                } else if (postDataResponse.comment_count == 1) {
                    holder.postCommentTV.setText("View 1 comment");
                } else {
                    holder.postCommentTV.setText("View all " + postDataResponse.comment_count + " comments");
                }
            } else {
                holder.postCommentTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.share_count))) {
                holder.postShareTV.setVisibility(View.VISIBLE);
                holder.postShareTV.setText(postDataResponse.share_count + "");
            } else {
                holder.postShareTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.total_reactions))) {
                holder.postLikeTV.setVisibility(View.VISIBLE);
                holder.postLikeTV.setText(postDataResponse.total_reactions + "");
            } else {
                holder.postLikeTV.setVisibility(View.GONE);
            }

            if (postDataResponse.choices != null && postDataResponse.choices.size() > 0) {
                holder.optionsRV.setVisibility(View.VISIBLE);
                holder.optionsRV.setHasFixedSize(true);
                holder.optionsRV.setLayoutManager(new LinearLayoutManager(context));
                holder.optionsRV.setItemAnimator(new DefaultItemAnimator());
                PollOptionsAdapter pollOptionsAdapter = new PollOptionsAdapter(context, postDataResponse, new PollOptionsAdapter.Poll() {
                    @Override
                    public void submitPoll(String selectedOption) {
                        pollSubmit.callPollSubmit(selectedOption, postDataResponse, position);
                    }
                });
                holder.optionsRV.setAdapter(pollOptionsAdapter);
            } else {
                holder.optionsRV.setVisibility(View.GONE);
            }

            // For set the post reaction
            if (Helper.isContainValue(postDataResponse.is_reacted) && postDataResponse.is_reacted.equalsIgnoreCase("up")) {
                holder.postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_like_heart, 0, 0, 0);
            } else {
                holder.postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_heart, 0, 0, 0);
            }

            setOnItemClick(holder, holder.postCommentTV, postDataResponse, position);
            setOnItemClick(holder, holder.postShareTV, postDataResponse, position);
            setOnItemClick(holder, holder.postLikeTV, postDataResponse, position);
        }
    }

    private void setPostDate(TextView postDateTV, String createdDate) {
        try {
            if (createdDate.contains("T")) {
                createdDate = createdDate.replace("T", " ");
            }
            long postTime = Helper.getTimeInMillisecondsFromDate(createdDate);
            long currentTime = System.currentTimeMillis();
            long diffInDays = Helper.getDateDiff(postTime, currentTime, TimeUnit.DAYS);
            if (diffInDays == 1) {
                postDateTV.setText("1 day ago");
            } else if (diffInDays > 1) {
                postDateTV.setText(diffInDays + " days ago");
            } else if (diffInDays == 0) {
                long diffInHour = Helper.getDateDiff(postTime, currentTime, TimeUnit.HOURS);
                if (diffInHour == 1) {
                    postDateTV.setText("1 hour ago");
                } else if (diffInHour > 1) {
                    postDateTV.setText(diffInHour + " hours ago");
                } else if (diffInHour == 0) {
                    long diffInMinute = Helper.getDateDiff(postTime, currentTime, TimeUnit.MINUTES);
                    if (diffInMinute > 1) {
                        postDateTV.setText(diffInMinute + " minutes ago");
                    } else {
                        postDateTV.setText("1 minute ago");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initAudioPlayer(CommunityViewHolder holder, String media, int position) {
        playerHashMap.put(position, ExoPlayerFactory.newSimpleInstance(context));
        holder.playerView.setPlayer(playerHashMap.get(position));

        String userAgent = null;
        if (context != null) {
            userAgent = Util.getUserAgent(context, context.getResources().getString(R.string.app_name));
        }

        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(context, null, httpDataSourceFactory);
        Uri uri = Uri.parse(media);

        MediaSource extractorMediaSource = buildMediaSource(uri, defaultDataSourceFactory);

        playerHashMap.get(position).setPlayWhenReady(false);
        playerHashMap.get(position).prepare(extractorMediaSource, true, false);
        playerHashMap.get(position).seekTo(postDataResponseAL.get(position).getPlaybackPosition());

        playerHashMap.get(position).addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady) {
                    ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else {
                    ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            }
        });

        holder.exoSeekBar.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(@NonNull TimeBar timeBar, long position) {
                swipeLayout.setSwipeLayoutEnable(false);
            }

            @Override
            public void onScrubMove(@NonNull TimeBar timeBar, long position) {

            }

            @Override
            public void onScrubStop(@NonNull TimeBar timeBar, long position, boolean canceled) {
                swipeLayout.setSwipeLayoutEnable(true);
            }
        });
    }

    private void initVideoPlayer(CommunityViewHolder holder, String media, int position) {
        playerHashMap.put(position, ExoPlayerFactory.newSimpleInstance(context));

        String userAgent = null;
        if (context != null) {
            userAgent = Util.getUserAgent(context, context.getResources().getString(R.string.app_name));
        }

        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(context, null, httpDataSourceFactory);
        Uri uri = Uri.parse(media);

        MediaSource extractorMediaSource = buildMediaSource(uri, defaultDataSourceFactory);

        playerHashMap.get(position).setPlayWhenReady(false);
        playerHashMap.get(position).prepare(extractorMediaSource, true, false);
        playerHashMap.get(position).seekTo(postDataResponseAL.get(position).getPlaybackPosition());

        playerHashMap.get(position).addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady) {
                    ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else {
                    ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            }
        });

        holder.exoSeekBar.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(@NonNull TimeBar timeBar, long position) {
                swipeLayout.setSwipeLayoutEnable(false);
            }

            @Override
            public void onScrubMove(@NonNull TimeBar timeBar, long position) {

            }

            @Override
            public void onScrubStop(@NonNull TimeBar timeBar, long position, boolean canceled) {
                swipeLayout.setSwipeLayoutEnable(true);
            }
        });
    }

    private MediaSource buildMediaSource(Uri uri, DefaultDataSourceFactory defaultDataSourceFactory) {
        int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private void setOnItemClick(CommunityViewHolder holder, View itemView, CommunityPostDataResponse postDataResponse, int position) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.exo_play:
                        playOnlyCurrentPlayer(position);
                        break;

                    case R.id.playerRL:
                        if (holder.videoControls.isVisible()) {
                            holder.videoControls.hide();
                        } else {
                            holder.videoControls.show();
                        }
                        break;

                    case R.id.upperRL:
                        context.startActivity(new Intent(context, WebViewActivity.class).putExtra("link", postDataResponse.link));
                        break;

                    case R.id.postCommentTV:
                        if ((postDataResponse.type.equalsIgnoreCase(AppConstants.POST_AUDIO_LAYOUT) || postDataResponse.type.equalsIgnoreCase(AppConstants.POST_VIDEO_LAYOUT)) && playerHashMap.get(position) != null) {
                            postDataResponse.setPlaybackPosition(Math.max(0, playerHashMap.get(position).getContentPosition()));
                        }
                        context.startActivity(new Intent(context, PostDetailActivity.class).putExtra("postData", postDataResponse).putExtra("position", position));
                        break;

                    case R.id.postShareTV:
                        String videoUrl = context.getString(R.string.post_share_url) + postDataResponse.id;
                        String shareUrl = context.getString(R.string.string_download);
                        shareUrl = videoUrl + "\n\n" + shareUrl + "\n\n" + context.getString(R.string.common_play_store_url);
                        shareData(shareUrl, postDataResponse, position);
                        break;

                    case R.id.postLikeTV:
                        if (Helper.isNetworkAvailable(context)) {
                            if (Helper.isContainValue(postDataResponse.is_reacted) && postDataResponse.is_reacted.equalsIgnoreCase("up")) {
                                postDataResponse.setIs_reacted("down");
                                postDataResponse.setTotal_reactions(postDataResponse.total_reactions - 1);

                                holder.postLikeTV.setText((postDataResponse.total_reactions) + "");
                                holder.postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_heart, 0, 0, 0);

                                postReaction.setPostReaction("rm", postDataResponse.id);
                            } else {
                                postDataResponse.setIs_reacted("up");
                                postDataResponse.setTotal_reactions(postDataResponse.total_reactions + 1);

                                holder.postLikeTV.setText((postDataResponse.total_reactions) + "");
                                holder.postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_like_heart, 0, 0, 0);

                                postReaction.setPostReaction("up", postDataResponse.id);
                            }
                        } else {
                            Toast.makeText(context, context.getString(R.string.please_check_internet_connection), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void shareData(String message, CommunityPostDataResponse postDataResponse, int position) {
        try {
            if ((postDataResponse.type.equalsIgnoreCase(AppConstants.POST_AUDIO_LAYOUT) || postDataResponse.type.equalsIgnoreCase(AppConstants.POST_VIDEO_LAYOUT)) && playerHashMap.get(position) != null && playerHashMap.get(position).isPlaying()) {
                playerHashMap.get(position).setPlayWhenReady(false);
            }

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
            String shareMessage = "Let me recommend you this post :\n\n";
            shareMessage = shareMessage + message + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                Intent receiver = new Intent(context, CommunityApplicationSelectorReceiver.class);
                receiver.putExtra("postId", postDataResponse.id);
                receiver.putExtra("communityId", postDataResponse.community_id);
                receiver.putExtra("position", position);
                receiver.putExtra("from", "list");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT);
                Intent chooser = Intent.createChooser(shareIntent, "Share via...", pendingIntent.getIntentSender());
                context.startActivity(chooser);
            } else {
                context.startActivity(Intent.createChooser(shareIntent, "Share via..."));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface PostReaction {
        void setPostReaction(String type, String postId);
    }

    public interface PollSubmit {
        void callPollSubmit(String option, CommunityPostDataResponse postDataResponse, int position);
    }

    public interface SwipeLayout {
        void setSwipeLayoutEnable(boolean isEnable);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull CommunityViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        int position = holder.getAdapterPosition();
        if (position >= 0 && (postDataResponseAL.get(position).type.equalsIgnoreCase(AppConstants.POST_AUDIO_LAYOUT) || postDataResponseAL.get(position).type.equalsIgnoreCase(AppConstants.POST_VIDEO_LAYOUT)) && playerHashMap.get(position) != null && playerHashMap.get(position).isPlaying()) {
            playerHashMap.get(position).setPlayWhenReady(false);
        }
    }

    public void playOnlyCurrentPlayer(int position) {
        for (int i = 0; i < postDataResponseAL.size(); i++) {
            if (position != i && (postDataResponseAL.get(i).type.equalsIgnoreCase(AppConstants.POST_AUDIO_LAYOUT) || postDataResponseAL.get(i).type.equalsIgnoreCase(AppConstants.POST_VIDEO_LAYOUT)) && playerHashMap.get(i) != null && playerHashMap.get(i).isPlaying()) {
                playerHashMap.get(i).setPlayWhenReady(false);
            }
        }
        playerHashMap.get(position).setPlayWhenReady(true);
        if (playerHashMap.get(position).getContentPosition() >= playerHashMap.get(position).getDuration()) {
            playerHashMap.get(position).seekTo(0);
        }
    }

    public void pauseExoPlayer() {
        for (int i = 0; i < postDataResponseAL.size(); i++) {
            if ((postDataResponseAL.get(i).type.equalsIgnoreCase(AppConstants.POST_AUDIO_LAYOUT) || postDataResponseAL.get(i).type.equalsIgnoreCase(AppConstants.POST_VIDEO_LAYOUT)) && playerHashMap.get(i) != null && playerHashMap.get(i).isPlaying()) {
                playerHashMap.get(i).setPlayWhenReady(false);
            }
        }
    }

    public void releaseExoPlayer() {
        for (int i = 0; i < postDataResponseAL.size(); i++) {
            if ((postDataResponseAL.get(i).type.equalsIgnoreCase(AppConstants.POST_AUDIO_LAYOUT) || postDataResponseAL.get(i).type.equalsIgnoreCase(AppConstants.POST_VIDEO_LAYOUT)) && playerHashMap.get(i) != null) {
                playerHashMap.get(i).release();
            }
        }
    }
}