package com.oktested.community.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.AlignmentSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.oktested.R;
import com.oktested.activities.WebViewActivity;
import com.oktested.community.model.PollSubmitResponse;
import com.oktested.community.model.PostDetailResponse;
import com.oktested.community.model.PostLogResponse;
import com.oktested.community.presenter.PostDetailPresenter;
import com.oktested.home.adapter.CarouselPostAdapter;
import com.oktested.home.adapter.PollOptionsAdapter;
import com.oktested.home.model.CommunityPostDataResponse;
import com.oktested.home.model.PostReactionResponse;
import com.oktested.home.model.PostShareResponse;
import com.oktested.termsPrivacy.ui.TermsPrivacyActivity;
import com.oktested.utils.AppConstants;
import com.oktested.utils.CommunityApplicationSelectorReceiver;
import com.oktested.utils.EndlessParentScrollListener;
import com.oktested.utils.HeightWrappingViewPager;
import com.oktested.utils.Helper;
import com.oktested.videoPlayer.adapter.CommentsAdapter;
import com.oktested.videoPlayer.adapter.ReplyAdapter;
import com.oktested.videoPlayer.model.CommentDeleteResponse;
import com.oktested.videoPlayer.model.CommentReactionResponse;
import com.oktested.videoPlayer.model.CommentReplyDataResponse;
import com.oktested.videoPlayer.model.CommentReplyPostResponse;
import com.oktested.videoPlayer.model.CommentReplyResponse;
import com.oktested.videoPlayer.model.CommentReportResponse;
import com.oktested.videoPlayer.model.ReplyDeleteResponse;
import com.oktested.videoPlayer.model.ReplyReactionResponse;
import com.oktested.videoPlayer.model.ReplyReportResponse;
import com.oktested.videoPlayer.model.SingleCommentResponse;
import com.oktested.videoPlayer.model.VideoCommentDataResponse;
import com.oktested.videoPlayer.model.VideoCommentPostResponse;
import com.oktested.videoPlayer.model.VideoCommentResponse;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity implements View.OnClickListener, PostDetailView {

    private Activity context;
    private CircleImageView communityImage;
    private TextView communityName, postDateTV, descriptionTV, postLikeTV, commentCountTV, postShareTV, noCommentTV, audioNameTV, audioCategoryTV, typeTV, linkTV;
    private ImageView postIV, sendCommentIV, filterIV, sendReplyIV;
    private RecyclerView commentRV, replyRV;
    private NestedScrollView nestedScrollView;
    private EditText commentET, replyET;
    private RelativeLayout videoPlayerRL, audioRL;
    private SpinKitView spinKitView, bottomProgress, replyBottomProgress;
    private PostDetailPresenter postDetailPresenter;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private PlayerControlView controls, audioControls;
    private CommunityPostDataResponse postDataResponse;
    private int pageNo, replyPageNo, commentCount = 0, commentPosition = 0, replyPosition = 0, itemPosition;
    private boolean videoFirstOpen = true, dataLoaded = true, freshData = true, replyDataLoaded = true, replyFreshData = true, dataChanges = false, viaNotification = false, notificationCommentExistInList = false;
    private ArrayList<VideoCommentDataResponse> videoCommentData = new ArrayList<>();
    private CommentsAdapter commentsAdapter;
    private ReplyAdapter replyAdapter;
    private BottomSheetDialog replyDialog;
    private VideoCommentDataResponse commentDataResponse;
    private String commentType = "new", replyUserId = "", replyUserName = "", selectedOption, notificationPostId, notificationCommentId;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HeightWrappingViewPager carouselVP;
    private PageIndicatorView pageIndicatorView;
    private RecyclerView optionsRV;
    private PollOptionsAdapter pollOptionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // Trigger GC to clean up prev player.
        // In regular Java vm this is not advised but for Android we need this.
        System.gc();
        inUi();

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("postId")) {
                notificationPostId = getIntent().getExtras().getString("postId");
                if (getIntent().getExtras().containsKey("commentId")) {
                    notificationCommentId = getIntent().getExtras().getString("commentId");
                }
                getPostDetail(notificationPostId);
            } else {
                postDataResponse = (CommunityPostDataResponse) getIntent().getSerializableExtra("postData");
                itemPosition = getIntent().getExtras().getInt("position");
                setBasicData();
                getVideoComments();
                sendPostLog();
                LocalBroadcastManager.getInstance(context).registerReceiver(shareCountReceiver, new IntentFilter(AppConstants.POST_SHARE_COUNT_UPDATE));
            }
        }
    }

    private void inUi() {
        context = this;
        postDetailPresenter = new PostDetailPresenter(context, this);

        LinearLayout backLL = findViewById(R.id.backLL);
        communityImage = findViewById(R.id.communityImage);
        postIV = findViewById(R.id.postIV);
        communityName = findViewById(R.id.communityName);
        postDateTV = findViewById(R.id.postDateTV);
        descriptionTV = findViewById(R.id.descriptionTV);
        postLikeTV = findViewById(R.id.postLikeTV);
        postShareTV = findViewById(R.id.postShareTV);
        commentCountTV = findViewById(R.id.commentCountTV);
        noCommentTV = findViewById(R.id.noCommentTV);
        typeTV = findViewById(R.id.typeTV);
        linkTV = findViewById(R.id.linkTV);
        TextView communityTV = findViewById(R.id.communityTV);
        TextView guidelineTV = findViewById(R.id.guidelineTV);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        spinKitView = findViewById(R.id.spinKit);
        bottomProgress = findViewById(R.id.bottomProgress);

        commentET = findViewById(R.id.commentET);
        sendCommentIV = findViewById(R.id.sendCommentIV);
        noCommentTV = findViewById(R.id.noCommentTV);
        filterIV = findViewById(R.id.filterIV);
        commentRV = findViewById(R.id.commentRV);
        commentRV.setNestedScrollingEnabled(false);

        audioRL = findViewById(R.id.audioRL);
        videoPlayerRL = findViewById(R.id.videoPlayerRL);
        videoPlayerRL.setEnabled(false);
        controls = findViewById(R.id.controls);
        playerView = findViewById(R.id.playerView);
        RelativeLayout relative = controls.findViewById(R.id.relative);
        relative.setVisibility(View.GONE);

        audioControls = findViewById(R.id.audioControls);
        audioNameTV = audioControls.findViewById(R.id.audioNameTV);
        audioCategoryTV = audioControls.findViewById(R.id.audioCategoryTV);

        carouselVP = findViewById(R.id.carouselVP);
        pageIndicatorView = findViewById(R.id.pageIndicatorView);
        optionsRV = findViewById(R.id.optionsRV);

        videoPlayerRL.setOnClickListener(this);
        backLL.setOnClickListener(this);
        sendCommentIV.setOnClickListener(this);
        postLikeTV.setOnClickListener(this);
        postShareTV.setOnClickListener(this);
        sendCommentIV.setOnClickListener(this);
        filterIV.setOnClickListener(this);
        communityTV.setOnClickListener(this);
        guidelineTV.setOnClickListener(this);

        commentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Helper.isContainValue(commentET.getText().toString())) {
                    sendCommentIV.setVisibility(View.VISIBLE);
                } else {
                    sendCommentIV.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Helper.isNetworkAvailable(context)) {
                    swipeRefreshLayout.setRefreshing(false);
                    getVideoComments();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    showMessage(getString(R.string.please_check_internet_connection));
                }
            }
        });

        setCommentsAdapter(videoCommentData);
    }

    private void setBasicData() {
        if (postDataResponse != null) {
            if (Helper.isContainValue(postDataResponse.user_picture)) {
                Glide.with(context)
                        .load(postDataResponse.user_picture)
                        .placeholder(R.drawable.community_placeholder)
                        .into(communityImage);
            } else {
                communityImage.setImageDrawable(context.getResources().getDrawable(R.drawable.community_placeholder));
            }

            if (Helper.isContainValue(postDataResponse.user_name)) {
                communityName.setText(postDataResponse.user_name);
            } else {
                communityName.setText("Ok Tested");
            }

            if (Helper.isContainValue(postDataResponse.description)) {
                descriptionTV.setVisibility(View.VISIBLE);
                descriptionTV.setText(postDataResponse.description);
            } else {
                descriptionTV.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(postDataResponse.created)) {
                setPostDate(postDateTV, postDataResponse.created);
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.comment_count))) {
                if (postDataResponse.comment_count < 2) {
                    commentCountTV.setText(postDataResponse.comment_count + " comment");
                } else {
                    commentCountTV.setText(postDataResponse.comment_count + " comments");
                }
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.share_count))) {
                postShareTV.setText(postDataResponse.share_count + "");
            }

            if (Helper.isContainValue(String.valueOf(postDataResponse.total_reactions))) {
                postLikeTV.setText(postDataResponse.total_reactions + "");
            }

            // For set the post reaction
            if (Helper.isContainValue(postDataResponse.is_reacted) && postDataResponse.is_reacted.equalsIgnoreCase("up")) {
                postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_like_heart, 0, 0, 0);
            } else {
                postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_heart, 0, 0, 0);
            }

            if ((postDataResponse.type.equalsIgnoreCase(AppConstants.POST_IMAGE_LAYOUT) || postDataResponse.type.equalsIgnoreCase(AppConstants.POST_LINK_LAYOUT)) && Helper.isContainValue(postDataResponse.media)) {
                postIV.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(postDataResponse.media)
                        .placeholder(R.drawable.placeholder)
                        .into(postIV);
            }

            if (postDataResponse.type.equalsIgnoreCase(AppConstants.POST_VIDEO_LAYOUT) || postDataResponse.type.equalsIgnoreCase(AppConstants.POST_AUDIO_LAYOUT)) {
                nestedScrollView.setVisibility(View.GONE);
                showLoader();
                initExoPlayer();
            } else {
                nestedScrollView.setVisibility(View.VISIBLE);
            }

            if (postDataResponse.type.equalsIgnoreCase(AppConstants.POST_IMAGE_LAYOUT)) {
                typeTV.setText(" posted an image");
            }

            if (postDataResponse.type.equalsIgnoreCase(AppConstants.POST_TEXT_LAYOUT)) {
                typeTV.setText(" says");
            }

            if (postDataResponse.type.equalsIgnoreCase(AppConstants.POST_VIDEO_LAYOUT)) {
                typeTV.setText(" posted a video");
                playerView.setVisibility(View.VISIBLE);
                controls.setVisibility(View.VISIBLE);
            }

            if (postDataResponse.type.equalsIgnoreCase(AppConstants.POST_LINK_LAYOUT) && Helper.isContainValue(postDataResponse.link)) {
                typeTV.setText(" posted a link");
                if (Helper.isContainValue(postDataResponse.title)) {
                    linkTV.setVisibility(View.VISIBLE);
                    linkTV.setText(postDataResponse.title);
                }

                linkTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context, WebViewActivity.class).putExtra("link", postDataResponse.link));
                    }
                });

                postIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context, WebViewActivity.class).putExtra("link", postDataResponse.link));
                    }
                });
            }

            if (postDataResponse.type.equalsIgnoreCase(AppConstants.POST_AUDIO_LAYOUT)) {
                typeTV.setText(" posted an audio");
                audioRL.setVisibility(View.VISIBLE);

                if (Helper.isContainValue(postDataResponse.title)) {
                    audioNameTV.setText(postDataResponse.title);
                }

                if (Helper.isContainValue(postDataResponse.subtitle)) {
                    audioCategoryTV.setText(postDataResponse.subtitle);
                }

                if (Helper.isContainValue(postDataResponse.thumbnail_image)) {
                    postIV.setVisibility(View.VISIBLE);
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
                                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) audioRL.getLayoutParams();
                                    layoutParams.setMargins(30, resource.getIntrinsicHeight() - 80, 30, 0);
                                    audioRL.requestLayout();
                                    return false;
                                }
                            })
                            .into(postIV);
                }
            }

            if (postDataResponse.type.equalsIgnoreCase(AppConstants.POST_CAROUSEL_LAYOUT) && postDataResponse.album != null && postDataResponse.album.size() > 0) {
                typeTV.setText(" posted an album");
                carouselVP.setVisibility(View.VISIBLE);
                pageIndicatorView.setVisibility(View.VISIBLE);
                pageIndicatorView.setPadding(4);
                pageIndicatorView.setRadius(3);
                CarouselPostAdapter carouselPostAdapter = new CarouselPostAdapter(context, postDataResponse.album);
                carouselVP.setAdapter(carouselPostAdapter);
            }

            if (postDataResponse.type.equalsIgnoreCase(AppConstants.POST_POLL_LAYOUT)) {
                typeTV.setText(" posted a poll");
                if (Helper.isContainValue(postDataResponse.media)) {
                    postIV.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(postDataResponse.media)
                            .placeholder(R.drawable.placeholder)
                            .into(postIV);
                }

                if (postDataResponse.choices != null && postDataResponse.choices.size() > 0) {
                    optionsRV.setVisibility(View.VISIBLE);
                    optionsRV.setHasFixedSize(true);
                    optionsRV.setLayoutManager(new LinearLayoutManager(context));
                    optionsRV.setItemAnimator(new DefaultItemAnimator());
                    pollOptionsAdapter = new PollOptionsAdapter(context, postDataResponse, new PollOptionsAdapter.Poll() {
                        @Override
                        public void submitPoll(String option) {
                            selectedOption = option;
                            postDetailPresenter.postPoll(postDataResponse.id, postDataResponse.community_id, selectedOption);
                        }
                    });
                    optionsRV.setAdapter(pollOptionsAdapter);
                }
            }
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

    private void getPostDetail(String postId) {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            postDetailPresenter.getPostDetailData(postId);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void getVideoComments() {
        if (Helper.isNetworkAvailable(context)) {
            freshData = true;
            if (!commentType.equalsIgnoreCase("top")) {
                postDetailPresenter.getVideoComments(postDataResponse.id);
            } else {
                postDetailPresenter.getTopVideoComments(postDataResponse.id);
            }
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void sendPostLog() {
        if (Helper.isNetworkAvailable(context)) {
            postDetailPresenter.sendPostLogData(postDataResponse.id, postDataResponse.community_id);
        }
    }

    private void setCommentsAdapter(ArrayList<VideoCommentDataResponse> videoCommentDataAL) {
        commentRV.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        commentRV.setLayoutManager(mLayoutManager);
        commentRV.setItemAnimator(new DefaultItemAnimator());

        commentsAdapter = new CommentsAdapter(context, videoCommentDataAL, new CommentsAdapter.Reaction() {
            @Override
            public void setReaction(String type, String commentId) {
                if (Helper.isNetworkAvailable(context)) {
                    postDetailPresenter.postCommentReaction(postDataResponse.id, commentId, type);
                }
            }
        }, new CommentsAdapter.CommentDelete() {
            @Override
            public void deleteComment(String commentId, int position) {
                commentPosition = position;
                showDeleteCommentDialog(commentId, false);
            }
        }, new CommentsAdapter.ReplySection() {
            @Override
            public void setReplySection(VideoCommentDataResponse dataResponse, int position) {
                commentDataResponse = dataResponse;
                showReplyBottomSheetDialog(position);
            }
        }, new CommentsAdapter.CommentReport() {
            @Override
            public void reportComment(String commentId) {
                if (Helper.isNetworkAvailable(context)) {
                    postDetailPresenter.reportComment(commentId, postDataResponse.id, postDataResponse.community_id);
                } else {
                    showMessage(getString(R.string.please_check_internet_connection));
                }
            }
        }
        );
        commentRV.setAdapter(commentsAdapter);

        nestedScrollView.setOnScrollChangeListener(new EndlessParentScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (videoCommentData != null && videoCommentData.size() >= 9) {
                    if (dataLoaded && Helper.isContainValue(String.valueOf(pageNo)) && pageNo != -1) {
                        if (Helper.isNetworkAvailable(context)) {
                            dataLoaded = false;
                            bottomProgress.setVisibility(View.VISIBLE);
                            if (!commentType.equalsIgnoreCase("top")) {
                                postDetailPresenter.getVideoCommentsPagination(postDataResponse.id, String.valueOf(pageNo));
                            } else {
                                postDetailPresenter.getTopVideoCommentsPagination(postDataResponse.id, String.valueOf(pageNo));
                            }
                        }
                    }
                }
            }
        });
    }

    private void setReplyAdapter(ArrayList<CommentReplyDataResponse> replyDataResponses) {
        replyRV.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        replyRV.setLayoutManager(mLayoutManager);
        replyRV.setItemAnimator(new DefaultItemAnimator());

        replyAdapter = new ReplyAdapter(context, replyDataResponses, new ReplyAdapter.ReplyDelete() {
            @Override
            public void replyDelete(String replyId, int position) {
                replyPosition = position;
                showDeleteReplyDialog(replyId);
            }
        }, new ReplyAdapter.ReplyReaction() {
            @Override
            public void setReplyReaction(String type, String replyId, String parent_id) {
                if (Helper.isNetworkAvailable(context)) {
                    postDetailPresenter.postReplyReaction(postDataResponse.id, replyId, type, parent_id);
                }
            }
        }, new ReplyAdapter.ReplyComment() {
            @Override
            public void setReplyComment(String userid, String userName) {
                replyUserId = userid;
                replyUserName = userName;
                replyET.requestFocus();

                userName = "<b>" + userName + "</b>";
                replyET.setText(Html.fromHtml(userName) + " ");

                replyET.setSelection(replyET.getText().length());
                try {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(replyET, InputMethodManager.SHOW_IMPLICIT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new ReplyAdapter.ReplyReport() {
            @Override
            public void reportReply(String replyId, String parentId) {
                if (Helper.isNetworkAvailable(context)) {
                    postDetailPresenter.reportReply(parentId, postDataResponse.id, replyId, postDataResponse.community_id);
                } else {
                    showMessage(getString(R.string.please_check_internet_connection));
                }
            }
        });
        replyRV.setAdapter(replyAdapter);

        replyRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // only when scrolling up
                    final int visibleThreshold = 4;
                    int lastItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = mLayoutManager.getItemCount();
                    if (currentTotalCount <= lastItem + visibleThreshold && replyDataLoaded && Helper.isContainValue(String.valueOf(replyPageNo)) && replyPageNo != -1) {
                        if (Helper.isNetworkAvailable(context)) {
                            replyBottomProgress.setVisibility(View.VISIBLE);
                            replyDataLoaded = false;
                            postDetailPresenter.getCommentReplyPagination(postDataResponse.id, String.valueOf(replyPageNo), commentDataResponse.id);
                        }
                    }
                }
            }
        });
    }

    private void showDeleteReplyDialog(String replyId) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.delete_reply_dialog, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);

        TextView noTV = dialogView.findViewById(R.id.noTV);
        TextView yesTV = dialogView.findViewById(R.id.yesTV);

        noTV.setOnClickListener(view -> alertDialog.cancel());

        yesTV.setOnClickListener(view -> {
            alertDialog.cancel();
            if (Helper.isNetworkAvailable(context)) {
                postDetailPresenter.deleteReply(replyId);
            } else {
                showMessage(getString(R.string.please_check_internet_connection));
            }
        });
    }

    private void showDeleteCommentDialog(String commentId, boolean isReplyDialog) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.delete_comment_dialog, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);

        TextView noTV = dialogView.findViewById(R.id.noTV);
        TextView yesTV = dialogView.findViewById(R.id.yesTV);

        noTV.setOnClickListener(view -> alertDialog.cancel());

        yesTV.setOnClickListener(view -> {
            alertDialog.cancel();
            if (Helper.isNetworkAvailable(context)) {
                if (isReplyDialog) {
                    replyDialog.cancel();
                }
                postDetailPresenter.deleteComment(commentId);
            } else {
                showMessage(getString(R.string.please_check_internet_connection));
            }
        });
    }

    private void showReplyBottomSheetDialog(int position) {
        View view = getLayoutInflater().inflate(R.layout.comment_reply_bottom_sheet_dialog, null);
        replyDialog = new BottomSheetDialog(context);
        replyDialog.setContentView(view);
        replyDialog.show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(replyDialog);
        }

        replyBottomProgress = replyDialog.findViewById(R.id.replyBottomProgress);
        replyRV = replyDialog.findViewById(R.id.replyRV);
        replyET = replyDialog.findViewById(R.id.replyET);
        sendReplyIV = replyDialog.findViewById(R.id.sendReplyIV);
        ImageView userIV = replyDialog.findViewById(R.id.userIV);
        ImageView badgeIV = replyDialog.findViewById(R.id.badgeIV);
        ImageView commentLikeIV = replyDialog.findViewById(R.id.commentLikeIV);
        RelativeLayout sideMenuRL = replyDialog.findViewById(R.id.sideMenuRL);
        LinearLayout commentReactionLL = replyDialog.findViewById(R.id.commentReactionLL);
        TextView userNameTV = replyDialog.findViewById(R.id.userNameTV);
        TextView commentTV = replyDialog.findViewById(R.id.commentTV);
        TextView replyCountTV = replyDialog.findViewById(R.id.replyCountTV);
        TextView commentLikeTV = replyDialog.findViewById(R.id.commentLikeTV);

        // For set the user image
        if (Helper.isContainValue(commentDataResponse.user.picture)) {
            Glide.with(context)
                    .load(commentDataResponse.user.picture)
                    .centerCrop()
                    .placeholder(R.drawable.ic_vector_default_profile)
                    .into(userIV);
        } else {
            userIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_default_profile));
        }

        // For the user name on comment
        if (Helper.isContainValue(commentDataResponse.user.display_name)) {
            userNameTV.setText(commentDataResponse.user.display_name);
        } else {
            if (Helper.isContainValue(commentDataResponse.user.name)) {
                userNameTV.setText(commentDataResponse.user.name);
            } else {
                if (Helper.isContainValue(commentDataResponse.userid)) {
                    userNameTV.setText("user" + commentDataResponse.userid.substring(0, 10) + "...");
                }
            }
        }

        // For set the verified anchor badge
        if (Helper.isContainValue(String.valueOf(commentDataResponse.user.okt_verified)) && commentDataResponse.user.okt_verified == 1) {
            badgeIV.setVisibility(View.VISIBLE);
        } else {
            badgeIV.setVisibility(View.GONE);
        }

        // For set the user comment
        if (Helper.isContainValue(commentDataResponse.comment)) {
            commentTV.setText(commentDataResponse.comment);
        }

        // For set the comment reply count
        if (Helper.isContainValue(String.valueOf(commentDataResponse.total_replies)) && commentDataResponse.total_replies > 0) {
            getCommentReply();
        }

        // For set the comment reaction
        if (commentDataResponse.is_reacted) {
            commentLikeIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_comment_like_heart));
        } else {
            commentLikeIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_comment_heart));
        }

        // For set the reaction count
        if (Helper.isContainValue(String.valueOf(commentDataResponse.total_reactions))) {
            commentLikeTV.setText(commentDataResponse.total_reactions + "");
        }

        // Click listener of side menu option
        sideMenuRL.setOnClickListener(view1 -> {
            androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(context, sideMenuRL);
            popup.getMenuInflater().inflate(R.menu.comment_menu, popup.getMenu());

            MenuItem deleteItem = popup.getMenu().findItem(R.id.delete);
            MenuItem reportItem = popup.getMenu().findItem(R.id.report);

            if (commentDataResponse.is_admin) {
                SpannableString deleteString = new SpannableString("Delete");
                deleteString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, deleteString.length(), 0);
                deleteItem.setTitle(deleteString);

                SpannableString reportString = new SpannableString("Report");
                reportString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, reportString.length(), 0);
                reportItem.setTitle(reportString);
            } else {
                if (commentDataResponse.is_editable) {
                    reportItem.setVisible(false);
                    SpannableString deleteString = new SpannableString("Delete");
                    deleteString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, deleteString.length(), 0);
                    deleteItem.setTitle(deleteString);
                } else {
                    deleteItem.setVisible(false);
                    SpannableString reportString = new SpannableString("Report");
                    reportString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, reportString.length(), 0);
                    reportItem.setTitle(reportString);
                }
            }

            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().toString().equalsIgnoreCase("Delete")) {
                    if (Helper.isNetworkAvailable(context)) {
                        commentPosition = position;
                        showDeleteCommentDialog(commentDataResponse.id, true);
                    } else {
                        Toast.makeText(context, context.getString(R.string.please_check_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Helper.isNetworkAvailable(context)) {
                        postDetailPresenter.reportComment(commentDataResponse.id, postDataResponse.id, postDataResponse.community_id);
                    } else {
                        Toast.makeText(context, context.getString(R.string.please_check_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            });
            popup.show();
        });

        // Click listener of reaction
        commentReactionLL.setOnClickListener(view12 -> {
            if (Helper.isNetworkAvailable(context)) {
                if (commentDataResponse.is_reacted) {
                    commentDataResponse.setIs_reacted(false);
                    commentDataResponse.setTotal_reactions(commentDataResponse.total_reactions - 1);

                    commentLikeTV.setText((commentDataResponse.total_reactions) + "");
                    commentLikeIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_comment_heart));

                    if (position != -1) {
                        commentsAdapter.refreshCommentAfterReactionFromReply();
                    }
                    postDetailPresenter.postCommentReaction(postDataResponse.id, commentDataResponse.id, "rm");
                } else {
                    commentDataResponse.setIs_reacted(true);
                    commentDataResponse.setTotal_reactions(commentDataResponse.total_reactions + 1);

                    commentLikeTV.setText((commentDataResponse.total_reactions) + "");
                    commentLikeIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_comment_like_heart));

                    if (position != -1) {
                        commentsAdapter.refreshCommentAfterReactionFromReply();
                    }
                    postDetailPresenter.postCommentReaction(postDataResponse.id, commentDataResponse.id, "up");
                }
            } else {
                Toast.makeText(context, context.getString(R.string.please_check_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });

        sendReplyIV.setOnClickListener(view13 -> {
            String replyComment;
            if (Helper.isContainValue(replyET.getText().toString().trim())) {
                if (Helper.isContainValue(replyUserName) && replyET.getText().toString().trim().contains(replyUserName)) {
                    replyComment = replyET.getText().toString().trim().replace(replyUserName, "");
                    replyComment = replyComment.trim();
                    if (Helper.isContainValue(replyComment)) {
                        sendCommentReply(replyComment, replyUserId);
                    }
                } else {
                    sendCommentReply(replyET.getText().toString().trim(), commentDataResponse.userid);
                }
            }
        });
    }

    private void getCommentReply() {
        if (Helper.isNetworkAvailable(context)) {
            replyFreshData = true;
            postDetailPresenter.getCommentReply(postDataResponse.id, commentDataResponse.id);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void sendCommentReply(String reply, String replyTo) {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            sendReplyIV.setEnabled(false);
            postDetailPresenter.postCommentReply(postDataResponse.id, reply, commentDataResponse.id, replyTo, postDataResponse.community_id);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backLL:
                finish();
                clickBackButton();
                break;

            case R.id.videoPlayerRL:
                if (controls.isVisible()) {
                    controls.hide();
                } else {
                    controls.show();
                    new Handler().postDelayed(() -> {
                        controls.hide();
                    }, 5000);
                }
                break;

            case R.id.sendCommentIV:
                if (Helper.isContainValue(commentET.getText().toString().trim())) {
                    sendComment();
                }
                break;

            case R.id.postLikeTV:
                if (Helper.isNetworkAvailable(context)) {
                    dataChanges = true;
                    if (Helper.isContainValue(postDataResponse.is_reacted) && postDataResponse.is_reacted.equalsIgnoreCase("up")) {
                        postDataResponse.setIs_reacted("down");
                        postDataResponse.setTotal_reactions(postDataResponse.total_reactions - 1);

                        postLikeTV.setText((postDataResponse.total_reactions) + "");
                        postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_heart, 0, 0, 0);

                        postDetailPresenter.sendCommunityPostReaction("rm", postDataResponse.id);
                    } else {
                        postDataResponse.setIs_reacted("up");
                        postDataResponse.setTotal_reactions(postDataResponse.total_reactions + 1);

                        postLikeTV.setText((postDataResponse.total_reactions) + "");
                        postLikeTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_comment_like_heart, 0, 0, 0);

                        postDetailPresenter.sendCommunityPostReaction("up", postDataResponse.id);
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.please_check_internet_connection), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.postShareTV:
                String videoUrl = context.getString(R.string.post_share_url) + postDataResponse.id;
                String shareUrl = context.getString(R.string.string_download);
                shareUrl = videoUrl + "\n\n" + shareUrl + "\n\n" + context.getString(R.string.common_play_store_url);
                shareData(shareUrl);
                break;

            case R.id.filterIV:
                showFilterPopUp();
                break;

            case R.id.guidelineTV:
            case R.id.communityTV:
                startActivity(new Intent(context, TermsPrivacyActivity.class).putExtra("from", "community"));
                break;

            default:
                break;
        }
    }

    private void sendComment() {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            sendCommentIV.setEnabled(false);
            postDetailPresenter.postUserComment(postDataResponse.id, commentET.getText().toString().trim(), postDataResponse.community_id);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setWhiteNavigationBar(@NonNull Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            GradientDrawable dimDrawable = new GradientDrawable();
            // ...customize your dim effect here

            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(Color.WHITE);

            Drawable[] layers = {dimDrawable, navigationBarDrawable};

            LayerDrawable windowBackground = new LayerDrawable(layers);
            windowBackground.setLayerInsetTop(1, metrics.heightPixels);

            window.setBackgroundDrawable(windowBackground);
        }
    }

    private void shareData(String message) {
        try {
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
                receiver.putExtra("position", itemPosition);
                receiver.putExtra("from", "post");
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
    protected void onDestroy() {
        super.onDestroy();
        postDetailPresenter.onDestroyedView();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(shareCountReceiver);
        if (postDataResponse != null && (postDataResponse.type.equalsIgnoreCase(AppConstants.POST_VIDEO_LAYOUT) || postDataResponse.type.equalsIgnoreCase(AppConstants.POST_AUDIO_LAYOUT))) {
            releaseExoPlayer();
        }
    }

 /*   @Override
    protected void onStart() {
        super.onStart();
        if (postDataResponse != null && (postDataResponse.type.equalsIgnoreCase(AppConstants.POST_VIDEO_LAYOUT) || postDataResponse.type.equalsIgnoreCase(AppConstants.POST_AUDIO_LAYOUT))) {
            resumeExoPlayer();
        }
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        if (postDataResponse != null && (postDataResponse.type.equalsIgnoreCase(AppConstants.POST_VIDEO_LAYOUT) || postDataResponse.type.equalsIgnoreCase(AppConstants.POST_AUDIO_LAYOUT))) {
            pauseExoPlayer();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        clickBackButton();
    }

    private void clickBackButton() {
        if (postDataResponse != null && !dataChanges) {
            if (Helper.isContainValue(String.valueOf(postDataResponse.comment_count)) && commentCount != postDataResponse.comment_count) {
                dataChanges = true;
            }

            if (player != null && !dataChanges) {
                if (postDataResponse.getPlaybackPosition() != player.getContentPosition()) {
                    dataChanges = true;
                    postDataResponse.setPlaybackPosition(player.getContentPosition());
                }
            }
        }

        if (postDataResponse != null && dataChanges) {
            postDataResponse.setPlayerAlreadyAdded(false);
            postDataResponse.setComment_count(commentCount);
            Intent intent = new Intent(AppConstants.COMMUNITY_POST_UPDATE);
            intent.putExtra("postData", postDataResponse);
            intent.putExtra("position", itemPosition);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private void resumeExoPlayer() {
        if (playerView != null && player != null && !videoFirstOpen) {
            player.setPlayWhenReady(true);
        }
    }

    private void pauseExoPlayer() {
        if (playerView != null && player != null) {
            player.setPlayWhenReady(false);
        }
    }

    private void releaseExoPlayer() {
        if (playerView != null && player != null) {
            player.release();
            player = null;
        }
    }

    private void initExoPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(context);
        controls.setPlayer(player);
        audioControls.setPlayer(player);
        playerView.setPlayer(player);

        //this is used to load from http stack or local devices this is default for both
        String userAgent = null;
        if (context != null) {
            userAgent = Util.getUserAgent(context, getResources().getString(R.string.app_name));
        }

        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(context, null, httpDataSourceFactory);
        Uri uri = Uri.parse(postDataResponse.media);

        MediaSource extractorMediaSource = buildMediaSource(uri, defaultDataSourceFactory);
        player.setPlayWhenReady(false);
        player.prepare(extractorMediaSource, true, false);
        player.seekTo(0, postDataResponse.getPlaybackPosition());

        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }

                if (playbackState == Player.STATE_BUFFERING) {
                    showLoader();
                } else if (playbackState == Player.STATE_READY) {
                    hideLoader();
                    videoPlayerRL.setEnabled(true);

                    if (videoFirstOpen) {
                        nestedScrollView.setVisibility(View.VISIBLE);
                        videoFirstOpen = false;

                        if (postDataResponse.dimension != null && Helper.isContainValue(String.valueOf(postDataResponse.dimension.height))) {
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                            int seventyFivePercentHeight = (int) (0.75 * displayMetrics.heightPixels);
                            if (postDataResponse.dimension.height <= seventyFivePercentHeight) {
                                playerView.getLayoutParams().height = postDataResponse.dimension.height;
                                controls.getLayoutParams().height = postDataResponse.dimension.height;
                            } else {
                                playerView.getLayoutParams().height = seventyFivePercentHeight;
                                controls.getLayoutParams().height = seventyFivePercentHeight;
                            }

                            playerView.requestLayout();
                            controls.requestLayout();
                        }
                    }
                } else if (playbackState == Player.STATE_ENDED) {
                    hideLoader();
                    controls.show();
                }
            }
        });
    }

    private MediaSource buildMediaSource(Uri uri, DefaultDataSourceFactory defaultDataSourceFactory) {
        int type = Util.inferContentType(uri);
        switch (type) {
     /*       case C.TYPE_SS:
                return new SsMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);*/
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri);

            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private void showFilterPopUp() {
        PopupMenu popup = new PopupMenu(context, filterIV);
        popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());

        MenuItem newItem = popup.getMenu().findItem(R.id.newComment);
        MenuItem topItem = popup.getMenu().findItem(R.id.topComment);
        SpannableString newString = new SpannableString("New Comments");
        SpannableString topString = new SpannableString("Top Comments");

        newString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, newString.length(), 0);
        topString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, topString.length(), 0);

        newItem.setTitle(newString);
        topItem.setTitle(topString);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.newComment) {
                    if (!commentType.equalsIgnoreCase("new")) {
                        commentType = "new";
                        ArrayList<VideoCommentDataResponse> commentDataResponses = new ArrayList<>();
                        setCommentsAdapter(commentDataResponses);
                        getVideoComments();
                    }
                } else if (item.getItemId() == R.id.topComment) {
                    if (!commentType.equalsIgnoreCase("top")) {
                        commentType = "top";
                        ArrayList<VideoCommentDataResponse> commentDataResponses = new ArrayList<>();
                        setCommentsAdapter(commentDataResponses);
                        getVideoComments();
                    }
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void setVideoCommentResponse(VideoCommentResponse videoCommentResponse) {
        bottomProgress.setVisibility(View.GONE);
        if (videoCommentResponse != null && Helper.isContainValue(String.valueOf(videoCommentResponse.total_coments))) {
            commentCount = videoCommentResponse.total_coments;
            if (videoCommentResponse.total_coments > 0) {
                noCommentTV.setVisibility(View.GONE);
            }

            if (videoCommentResponse.total_coments < 2) {
                commentCountTV.setText(videoCommentResponse.total_coments + " comment");
            } else {
                commentCountTV.setText(videoCommentResponse.total_coments + " comments");
            }

            if (videoCommentResponse.data != null && videoCommentResponse.data.size() > 0) {
                pageNo = videoCommentResponse.next_page;
                dataLoaded = true;
                videoCommentData = videoCommentResponse.data;
                if (freshData) {
                    freshData = false;
                    commentsAdapter.setCommentSectionData(videoCommentResponse.data);
                } else {
                    commentsAdapter.setCommentSectionDataOnScroll(videoCommentResponse.data);
                }

                if (viaNotification) {
                    viaNotification = false;
                    for (int i = 0; i < videoCommentResponse.data.size(); i++) {
                        if (videoCommentResponse.data.get(i).id.equalsIgnoreCase(commentDataResponse.id)) {
                            notificationCommentExistInList = true;
                            commentDataResponse = videoCommentResponse.data.get(i);
                            showReplyBottomSheetDialog(i);
                            break;
                        }
                    }

                    if (!notificationCommentExistInList) {
                        showReplyBottomSheetDialog(-1);
                    }
                }
            }
            if (videoCommentResponse.total_coments > 1) {
                filterIV.setVisibility(View.VISIBLE);
            }
        } else {
            filterIV.setVisibility(View.GONE);
        }
    }

    @Override
    public void setVideoCommentPostResponse(VideoCommentPostResponse videoCommentPostResponse) {
        if (videoCommentPostResponse != null && Helper.isContainValue(videoCommentPostResponse.status) && videoCommentPostResponse.status.equalsIgnoreCase("success")) {
            sendCommentIV.setEnabled(true);
            commentET.setText("");
            Helper.hideKeyboard(context);
            ArrayList<VideoCommentDataResponse> commentDataResponses = new ArrayList<>();
            setCommentsAdapter(commentDataResponses);
            getVideoComments();
        }
    }

    @Override
    public void setCommentReactionResponse(CommentReactionResponse commentReactionResponse) {

    }

    @Override
    public void setCommentDeleteResponse(CommentDeleteResponse commentDeleteResponse) {
        if (commentDeleteResponse != null && Helper.isContainValue(commentDeleteResponse.status) && commentDeleteResponse.status.equalsIgnoreCase("success")) {
            if (Helper.isContainValue(commentDeleteResponse.message)) {
                showMessage(commentDeleteResponse.message);
            }

            if (commentPosition != -1) {
                commentsAdapter.refreshCommentAfterDelete(commentPosition);
                if (commentCount > 2) {
                    commentCount = commentCount - 1;
                    commentCountTV.setText(commentCount + " Comments");
                } else if (commentCount > 0) {
                    commentCount = commentCount - 1;
                    commentCountTV.setText(commentCount + " Comment");
                }

                if (commentCount == 0) {
                    noCommentTV.setVisibility(View.VISIBLE);
                }

                if (commentCount < 2) {
                    filterIV.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void setCommentReportResponse(CommentReportResponse commentReportResponse) {
        if (commentReportResponse != null && Helper.isContainValue(commentReportResponse.status) && commentReportResponse.status.equalsIgnoreCase("success")) {
            if (Helper.isContainValue(commentReportResponse.message)) {
                showMessage(commentReportResponse.message);
            }
        }
    }

    @Override
    public void setCommentReplyResponse(CommentReplyResponse commentReplyResponse) {
        replyBottomProgress.setVisibility(View.GONE);
        if (commentReplyResponse != null && commentReplyResponse.data != null && commentReplyResponse.data.size() > 0) {
            replyPageNo = commentReplyResponse.next_page;
            replyDataLoaded = true;
            if (replyFreshData) {
                replyFreshData = false;

                setReplyAdapter(commentReplyResponse.data);
            } else {
                replyAdapter.setReplySectionDataOnScroll(commentReplyResponse.data);
            }
        }
    }

    @Override
    public void setCommentReplyPostResponse(CommentReplyPostResponse commentReplyPostResponse) {
        if (commentReplyPostResponse != null && Helper.isContainValue(commentReplyPostResponse.status) && commentReplyPostResponse.status.equalsIgnoreCase("success")) {
            sendReplyIV.setEnabled(true);
            replyET.setText("");
            commentDataResponse.setTotal_replies(commentDataResponse.total_replies + 1);
            commentsAdapter.refreshCommentAfterReactionFromReply();
            getCommentReply();
        }
    }

    @Override
    public void setReplyReactionResponse(ReplyReactionResponse replyReactionResponse) {

    }

    @Override
    public void setReplyDeleteResponse(ReplyDeleteResponse replyDeleteResponse) {
        if (replyDeleteResponse != null && Helper.isContainValue(replyDeleteResponse.status) && replyDeleteResponse.status.equalsIgnoreCase("success")) {
            commentDataResponse.setTotal_replies(commentDataResponse.total_replies - 1);
            commentsAdapter.refreshCommentAfterReactionFromReply();
            replyAdapter.refreshReplyAfterDelete(replyPosition);
            if (Helper.isContainValue(replyDeleteResponse.message)) {
                showMessage(replyDeleteResponse.message);
            }
        }
    }

    @Override
    public void setReplyReportResponse(ReplyReportResponse replyReportResponse) {
        if (replyReportResponse != null && Helper.isContainValue(replyReportResponse.status) && replyReportResponse.status.equalsIgnoreCase("success")) {
            if (Helper.isContainValue(replyReportResponse.message)) {
                showMessage(replyReportResponse.message);
            }
        }
    }

    @Override
    public void setPostDetailResponse(PostDetailResponse postDetailResponse) {
        if (postDetailResponse != null && Helper.isContainValue(postDetailResponse.status) && postDetailResponse.status.equalsIgnoreCase("success") && postDetailResponse.data != null) {
            postDataResponse = postDetailResponse.data;
            if (Helper.isContainValue(notificationCommentId)) {
                callCommentDetailApi(notificationCommentId, notificationPostId);
            } else {
                setBasicData();
                getVideoComments();
                sendPostLog();
                LocalBroadcastManager.getInstance(context).registerReceiver(shareCountReceiver, new IntentFilter(AppConstants.POST_SHARE_COUNT_UPDATE));
            }
        }
    }

    public void callCommentDetailApi(String commentId, String postId) {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            postDetailPresenter.getCommentDetailedData(commentId, postId);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    @Override
    public void setPostReactionResponse(PostReactionResponse postReactionResponse) {

    }

    @Override
    public void setPostShareResponse(PostShareResponse postShareResponse) {
        if (postShareResponse != null && Helper.isContainValue(postShareResponse.status) && postShareResponse.status.equalsIgnoreCase("success")) {
            postDataResponse.setShare_count(postDataResponse.share_count + 1);
            postShareTV.setText(postDataResponse.share_count + "");
            dataChanges = true;
        }
    }

    @Override
    public void setPollSubmitResponse(PollSubmitResponse pollSubmitResponse) {
        if (pollSubmitResponse != null && pollSubmitResponse.data != null && pollSubmitResponse.data.choices != null && pollSubmitResponse.data.choices.size() > 0 && Helper.isContainValue(pollSubmitResponse.status) && pollSubmitResponse.status.equalsIgnoreCase("success")) {
            if (Helper.isContainValue(pollSubmitResponse.message)) {
                showMessage(pollSubmitResponse.message);
            }

            postDataResponse.is_polled = true;
            postDataResponse.selected_poll_option = selectedOption;
            postDataResponse.choices.clear();
            postDataResponse.choices.addAll(pollSubmitResponse.data.choices);
            pollOptionsAdapter.setPollResult(postDataResponse);
        }
    }

    @Override
    public void setPostLogResponse(PostLogResponse postLogResponse) {

    }

    @Override
    public void getCommentDetailData(SingleCommentResponse singleCommentResponse) {
        if (singleCommentResponse != null && Helper.isContainValue(singleCommentResponse.status) && singleCommentResponse.status.equalsIgnoreCase("success")) {
            if (singleCommentResponse.data != null) {
                viaNotification = true;
                commentDataResponse = singleCommentResponse.data;
                setBasicData();
                getVideoComments();
                sendPostLog();
                LocalBroadcastManager.getInstance(context).registerReceiver(shareCountReceiver, new IntentFilter(AppConstants.POST_SHARE_COUNT_UPDATE));
            }
        }
    }

    private BroadcastReceiver shareCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null) {
                String postId = intent.getExtras().getString("postId");
                String appName = intent.getExtras().getString("appName");
                String communityId = intent.getExtras().getString("communityId");
                if (Helper.isNetworkAvailable(context)) {
                    postDetailPresenter.sendCommunityPostShareCount(postId, appName, communityId);
                }
            }
        }
    };
}