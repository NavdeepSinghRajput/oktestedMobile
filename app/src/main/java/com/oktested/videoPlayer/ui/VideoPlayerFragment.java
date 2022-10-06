package com.oktested.videoPlayer.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.AlignmentSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.mediarouter.app.MediaRouteButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.player.VideoAdPlayer;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.cast.CastPlayer;
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.common.images.WebImage;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oktested.BuildConfig;
import com.oktested.R;
import com.oktested.core.model.FavouriteResponse;
import com.oktested.core.model.UnFavouriteResponse;
import com.oktested.dashboard.model.GetUserDataresponse;
import com.oktested.dashboard.model.GetUserResponse;
import com.oktested.entity.AdSettingDataResponse;
import com.oktested.entity.DataItem;
import com.oktested.entity.EventTrackingResponse;
import com.oktested.entity.EventTrackingUpdateResponse;
import com.oktested.entity.VideoEventTrackingRequest;
import com.oktested.favourite.ui.FavouriteFragment;
import com.oktested.reportProblem.model.ReportProblemResponse;
import com.oktested.showDetail.ui.ShowDetailActivity;
import com.oktested.termsPrivacy.ui.TermsPrivacyActivity;
import com.oktested.utils.AnalyticsHelper;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.ApplicationSelectorReceiver;
import com.oktested.utils.DataHolder;
import com.oktested.utils.EndlessParentScrollListener;
import com.oktested.utils.Helper;
import com.oktested.utils.MyBannerAd;
import com.oktested.utils.MyInterstitialAd;
import com.oktested.utils.emojiutils.Direction;
import com.oktested.utils.emojiutils.ZeroGravityAnimation;
import com.oktested.videoPlayer.adapter.CastCrewAdapter;
import com.oktested.videoPlayer.adapter.CommentsAdapter;
import com.oktested.videoPlayer.adapter.ReplyAdapter;
import com.oktested.videoPlayer.adapter.ReportIssueAdapter;
import com.oktested.videoPlayer.adapter.VideoQualityAdapter;
import com.oktested.videoPlayer.model.CommentDeleteResponse;
import com.oktested.videoPlayer.model.CommentReactionResponse;
import com.oktested.videoPlayer.model.CommentReplyDataResponse;
import com.oktested.videoPlayer.model.CommentReplyPostResponse;
import com.oktested.videoPlayer.model.CommentReplyResponse;
import com.oktested.videoPlayer.model.CommentReportResponse;
import com.oktested.videoPlayer.model.FollowResponse;
import com.oktested.videoPlayer.model.ReactionResponse;
import com.oktested.videoPlayer.model.ReplyDeleteResponse;
import com.oktested.videoPlayer.model.ReplyReactionResponse;
import com.oktested.videoPlayer.model.ReplyReportResponse;
import com.oktested.videoPlayer.model.ReportIssueResponse;
import com.oktested.videoPlayer.model.UnFollowResponse;
import com.oktested.videoPlayer.model.VideoCommentDataResponse;
import com.oktested.videoPlayer.model.VideoCommentPostResponse;
import com.oktested.videoPlayer.model.VideoCommentResponse;
import com.oktested.videoPlayer.presenter.VideoPlayerPresenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VideoPlayerFragment extends Fragment implements VideoPlayerView, View.OnClickListener, VideoPlayerActivity.ScreenRotation {

    private TextView commentCountTV, noCommentTV;
    private ImageView filterIV, sendCommentIV, sendReplyIV;
    private RelativeLayout videoPlayerRL, parentRL, backwardRL, forwardRL, seekBarRL, playPauseRL;
    private CheckBox likeCB;
    private NestedScrollView nestedScrollView;
    private RecyclerView commentRV, replyRV;
    private BottomSheetDialog settingDialog;
    private EditText commentET, replyET;
    private Activity context;
    private SpinKitView spinKitView, bottomProgress, replyBottomProgress;
    private ArrayList<DataItem> dataItemList;
    private VideoPlayerPresenter videoPlayerPresenter;
    private CommentsAdapter commentsAdapter;
    private ReplyAdapter replyAdapter;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private SubtitleView subtitleView;
    private MediaRouteButton mediaRouteButton;
    private CastContext castContext;
    private CastPlayer castPlayer;
    private PlayerControlView controls;
    private ImaAdsLoader imaAdsLoader;
    private int mResumeWindow, pageNo, replyPageNo;
    private long mResumePosition, videoDurationInMilliSeconds;
    private boolean videoFirstOpen = true, dataLoaded = true, freshData = true, replyDataLoaded = true, replyFreshData = true;
    private int dataItemPosition, playerHeight, verticalPlayerHeight;
    private DefaultTrackSelector trackSelector;
    private ArrayList<VideoCommentDataResponse> videoCommentData = new ArrayList<>();
    private String likeString = "", commentType = "new", replyUserId = "", replyUserName = "";
    private int commentCount = 0, commentPosition = 0, replyPosition = 0, screenHeight = 0;
    private BottomSheetDialog replyDialog;
    private VideoCommentDataResponse commentDataResponse;
    private boolean isFullScreen = false, isCastDeviceAvailable = false, isInterstitialAdClosed = true, viaNotification = false, notificationCommentExistInList = false;
    private JsonObject jsonObjects;
    private String eventId = "", cityName, stateName, countryName, speedStatus = "Normal";
    private boolean isFive = true, isTen = true, isTwentyFive = true, isFifty = true, isFiftyPercent = true, isSeventyFive = true, isNinetyFive = true, isThirtySecond = true;
    private long twentyFivePercent, twentySixPercent, fiftyPercent, fiftyOnePercent, seventyFivePercent,
            seventySixPercent, ninetyFivePercent, ninetySixPercent;
    private Handler mVideoPositionTrackingHandler;
    private Runnable mVideoPositionTrackingRunnable;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<String> videoIssuesAL = new ArrayList<>();
    private DefaultDataSourceFactory defaultDataSourceFactory;
    private MediaSource extractorMediaSource;
    private AdsMediaSource mediaSourceWithAds;
    private boolean isVideoTrackingCalled, isImaAd = false, isImaAdRunning = false, isPreRollAd, isMidRollAd, isPostRollAd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (dataItemList != null && dataItemList.size() > 0) {
            if (Helper.isContainValue(dataItemList.get(dataItemPosition).aspectRatio) && dataItemList.get(dataItemPosition).aspectRatio.equalsIgnoreCase("9:16")) {
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return inflater.inflate(R.layout.vertical_video_player_item, container, false);
            } else {
                return inflater.inflate(R.layout.horizontal_video_player_item, container, false);
            }
        }
        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mVideoPositionTrackingHandler = new Handler();
        mVideoPositionTrackingRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    // If player exists and playing then set video position to tracking implementation.
                    if (player != null && player.isPlaying()) {
                        if (Helper.isContainValue(eventId)) {
                            callVideoTrackingUpdateApi(player.getCurrentPosition());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("TrackingFailed", "Video position tracking failed.");
                }

             /*   if (dataItemList != null && dataItemList.size() > 0) {
                    if (!(Helper.isContainValue(dataItemList.get(dataItemPosition).aspectRatio) && dataItemList.get(dataItemPosition).aspectRatio.equalsIgnoreCase("9:16"))) {
                        if (Helper.isRotationOn(context)) {
                            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        } else {
                            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                        }
                    }
                }*/

                mVideoPositionTrackingHandler.postDelayed(this, AppConstants.VIDEO_POSITION_TRACKING_POLL_TIME_MS);
            }
        };

        // Trigger GC to clean up prev player.
        // In regular Java vm this is not advised but for Android we need this.
        System.gc();

        inUi(view);
        getVideoComments();
        getVideoReportIssue();
        if (dataItemList != null && dataItemList.size() > 0) {
            initExoPlayer(dataItemList.get(dataItemPosition).altContent, dataItemList.get(dataItemPosition).aspectRatio);
        }

        if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.MIDDLE_BANNER_AD)) {
            loadMiddleAd(view, DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.MIDDLE_BANNER_AD));
        }
        if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.INTERSTITIAL_AD)) {
            loadInterstitialAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.INTERSTITIAL_AD));
        }
    }

    public static Fragment newInstance(Context context, ArrayList<DataItem> dataItemList, int position, CastContext castContext, VideoCommentDataResponse videoCommentDataResponse) {
        VideoPlayerFragment videoPlayerFragment = new VideoPlayerFragment();
        videoPlayerFragment.context = (Activity) context;
        videoPlayerFragment.dataItemList = dataItemList;
        videoPlayerFragment.dataItemPosition = position;
        videoPlayerFragment.castContext = castContext;
        if (videoCommentDataResponse != null) {
            videoPlayerFragment.viaNotification = true;
            videoPlayerFragment.commentDataResponse = videoCommentDataResponse;
        }
        return videoPlayerFragment;
    }

    private void inUi(View view) {
        videoPlayerPresenter = new VideoPlayerPresenter(context, this);
        controls = view.findViewById(R.id.controls);
        subtitleView = view.findViewById(R.id.subtitle);
        playerView = view.findViewById(R.id.playerView);
        parentRL = view.findViewById(R.id.parentRL);
        videoPlayerRL = view.findViewById(R.id.videoPlayerRL);
        videoPlayerRL.setEnabled(false);
        spinKitView = view.findViewById(R.id.spinKit);
        bottomProgress = view.findViewById(R.id.bottomProgress);
        TextView titleTV = view.findViewById(R.id.titleTV);
        TextView showNameTV = view.findViewById(R.id.showNameTV);
        TextView communityTV = view.findViewById(R.id.communityTV);
        TextView guidelineTV = view.findViewById(R.id.guidelineTV);
        RelativeLayout resizeRL = controls.findViewById(R.id.resizeRL);
        RelativeLayout verticalResizeRL = controls.findViewById(R.id.verticalResizeRL);
        forwardRL = controls.findViewById(R.id.forwardRL);
        backwardRL = controls.findViewById(R.id.backwardRL);
        playPauseRL = controls.findViewById(R.id.playPauseRL);
        seekBarRL = controls.findViewById(R.id.seekBarRL);
        LinearLayout playerOptionsLL = view.findViewById(R.id.playerOptionsLL);
        ImageView infoIV = view.findViewById(R.id.infoIV);
        ImageView shareIV = view.findViewById(R.id.shareIV);
        ImageView settingIV = view.findViewById(R.id.settingIV);
        ImageView reactionIV = view.findViewById(R.id.reactionIV);
        likeCB = view.findViewById(R.id.likeCB);
        RelativeLayout showRL = view.findViewById(R.id.showRL);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        mediaRouteButton = view.findViewById(R.id.media_route_button);

        noCommentTV = view.findViewById(R.id.noCommentTV);
        commentCountTV = view.findViewById(R.id.commentCountTV);
        sendCommentIV = view.findViewById(R.id.sendCommentIV);
        filterIV = view.findViewById(R.id.filterIV);
        commentET = view.findViewById(R.id.commentET);
        commentRV = view.findViewById(R.id.commentRV);
        commentRV.setNestedScrollingEnabled(false);

        videoPlayerRL.setOnClickListener(this);
        resizeRL.setOnClickListener(this);
        verticalResizeRL.setOnClickListener(this);
        infoIV.setOnClickListener(this);
        shareIV.setOnClickListener(this);
        settingIV.setOnClickListener(this);
        reactionIV.setOnClickListener(this);
        likeCB.setOnClickListener(this);
        sendCommentIV.setOnClickListener(this);
        filterIV.setOnClickListener(this);
        showRL.setOnClickListener(this);
        communityTV.setOnClickListener(this);
        guidelineTV.setOnClickListener(this);

        if (dataItemList.get(dataItemPosition).aspectRatio.equalsIgnoreCase("9:16")) {
            verticalResizeRL.setVisibility(View.VISIBLE);
        } else {
            resizeRL.setVisibility(View.VISIBLE);
        }

        if (dataItemList.get(dataItemPosition).castCrew.size() > 0) {
            playerOptionsLL.setWeightSum(5);
            infoIV.setVisibility(View.VISIBLE);
        } else {
            playerOptionsLL.setWeightSum(4);
            infoIV.setVisibility(View.GONE);
        }
        titleTV.setText(dataItemList.get(dataItemPosition).title);
        showNameTV.setText(dataItemList.get(dataItemPosition).show.topic);

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

        setLikeIcon(DataHolder.getInstance().getUserDataresponse);
        setCommentsAdapter(videoCommentData);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationClient.getLastLocation().addOnSuccessListener(context, location -> {
            if (location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                getLocationFromLatLong(latitude, longitude);
            }
        });

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
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
    }

    private void getLocationFromLatLong(double latitude, double longitude) {
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
        List<Address> address = null;
        try {
            address = geoCoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (address != null && address.size() > 0) {
            cityName = address.get(0).getLocality();
            stateName = address.get(0).getAdminArea();
            countryName = address.get(0).getCountryName();
        }
    }

    private void setLikeIcon(GetUserDataresponse response) {
        if (response != null && response.favourite != null && response.favourite.size() > 0) {
            for (int i = 0; i < response.favourite.size(); i++) {
                if (response.favourite.get(i).equalsIgnoreCase(dataItemList.get(dataItemPosition).id)) {
                    likeCB.setChecked(true);
                    break;
                }
            }
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
                    videoPlayerPresenter.postCommentReaction(dataItemList.get(dataItemPosition).id, commentId, type);
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
                    videoPlayerPresenter.reportComment(commentId, dataItemList.get(dataItemPosition).id);
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
                                videoPlayerPresenter.getVideoCommentsPagination(dataItemList.get(dataItemPosition).id, String.valueOf(pageNo));
                            } else {
                                videoPlayerPresenter.getTopVideoCommentsPagination(dataItemList.get(dataItemPosition).id, String.valueOf(pageNo));
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
                    videoPlayerPresenter.postReplyReaction(dataItemList.get(dataItemPosition).id, replyId, type, parent_id);
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
                    videoPlayerPresenter.reportReply(parentId, dataItemList.get(dataItemPosition).id, replyId);
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
                            videoPlayerPresenter.getCommentReplyPagination(dataItemList.get(dataItemPosition).id, String.valueOf(replyPageNo), commentDataResponse.id);
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
                videoPlayerPresenter.deleteReply(replyId);
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
                videoPlayerPresenter.deleteComment(commentId);
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
            PopupMenu popup = new PopupMenu(context, sideMenuRL);
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

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getTitle().toString().equalsIgnoreCase("Delete")) {
                        if (Helper.isNetworkAvailable(context)) {
                            commentPosition = position;
                            showDeleteCommentDialog(commentDataResponse.id, true);
                        } else {
                            Toast.makeText(context, context.getString(R.string.please_check_internet_connection), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (Helper.isNetworkAvailable(context)) {
                            videoPlayerPresenter.reportComment(commentDataResponse.id, dataItemList.get(dataItemPosition).id);
                        } else {
                            Toast.makeText(context, context.getString(R.string.please_check_internet_connection), Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                }
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
                    videoPlayerPresenter.postCommentReaction(dataItemList.get(dataItemPosition).id, commentDataResponse.id, "rm");
                } else {
                    commentDataResponse.setIs_reacted(true);
                    commentDataResponse.setTotal_reactions(commentDataResponse.total_reactions + 1);

                    commentLikeTV.setText((commentDataResponse.total_reactions) + "");
                    commentLikeIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_comment_like_heart));

                    if (position != -1) {
                        commentsAdapter.refreshCommentAfterReactionFromReply();
                    }
                    videoPlayerPresenter.postCommentReaction(dataItemList.get(dataItemPosition).id, commentDataResponse.id, "up");
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
            videoPlayerPresenter.getCommentReply(dataItemList.get(dataItemPosition).id, commentDataResponse.id);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void getVideoComments() {
        if (Helper.isNetworkAvailable(context)) {
            freshData = true;
            if (!commentType.equalsIgnoreCase("top")) {
                videoPlayerPresenter.getVideoComments(dataItemList.get(dataItemPosition).id);
            } else {
                videoPlayerPresenter.getTopVideoComments(dataItemList.get(dataItemPosition).id);
            }
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void getVideoReportIssue() {
        if (Helper.isNetworkAvailable(context)) {
            videoPlayerPresenter.getVideoReportIssue();
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void callUserDataApi() {
        if (Helper.isNetworkAvailable(context)) {
            videoPlayerPresenter.callUserData();
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void sendCommentReply(String reply, String replyTo) {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            sendReplyIV.setEnabled(false);
            videoPlayerPresenter.postCommentReply(dataItemList.get(dataItemPosition).id, dataItemList.get(dataItemPosition).slug, reply, commentDataResponse.id, replyTo);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void initExoPlayer(String videoUrl, String aspectRatio) {
        trackSelector = new DefaultTrackSelector(context);
        if (!AppPreferences.getInstance(context).getPreferencesString(AppConstants.VIDEO_HEIGHT).equalsIgnoreCase("Auto")) {
            trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSize(Integer.parseInt(AppPreferences.getInstance(context).getPreferencesString(AppConstants.VIDEO_WIDTH)), Integer.parseInt(AppPreferences.getInstance(context).getPreferencesString(AppConstants.VIDEO_HEIGHT))));
        }
        player = new SimpleExoPlayer.Builder(context).setTrackSelector(trackSelector).build();
        // player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        controls.setPlayer(player);
        playerView.setPlayer(player);

        // For set the visibility of caption on video player
        if (AppConstants.CAPTION_STATUS.equalsIgnoreCase("On")) {
            playerView.getSubtitleView().setVisibility(View.VISIBLE);
        } else {
            playerView.getSubtitleView().setVisibility(View.GONE);
        }

        //this is used to load from http stack or local devices this is default for both
        String userAgent = null;
        if (context != null) {
            userAgent = Util.getUserAgent(context, getResources().getString(R.string.app_name));
        }

        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
        defaultDataSourceFactory = new DefaultDataSourceFactory(context, null, httpDataSourceFactory);
        Uri uri = Uri.parse(videoUrl);

        if (dataItemList.get(dataItemPosition).srt != null && dataItemList.get(dataItemPosition).srt.size() > 0 && Helper.isContainValue(dataItemList.get(dataItemPosition).srt.get(0).srtfile)) {
            extractorMediaSource = buildCaptionMediaSource(uri, defaultDataSourceFactory);
        } else {
            extractorMediaSource = buildMediaSource(uri, defaultDataSourceFactory);
        }

        // For Ima Ads
        if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.PRE_ROLL_IMA_AD)) {
            loadPreRollImaAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.PRE_ROLL_IMA_AD));
        } else if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.MID_ROLL_IMA_AD)) {
            loadMidRollImaAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.MID_ROLL_IMA_AD));
        } else if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.POST_ROLL_IMA_AD)) {
            loadPostRollImaAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.POST_ROLL_IMA_AD));
        }

        if (!isPreRollAd) {
            player.prepare(extractorMediaSource);
        }
        player.setPlayWhenReady(true);

        //to resume content
     /*   boolean haveStartPosition = mResumeWindow != C.INDEX_UNSET;
        if (haveStartPosition) {
            player.seekTo(mResumeWindow, mResumePosition);
        }

        player.prepare(extractorMediaSource, !haveStartPosition, false);
        player.setPlayWhenReady(!player.getPlayWhenReady());*/

   /*     player.addTextOutput(new TextRenderer.Output() {
            @Override
            public void onCues(List<Cue> cues) {
                subtitleView.onCues(cues);
            }
        });*/

        // For Cast Functionality
        CastButtonFactory.setUpMediaRouteButton(context.getApplicationContext(), mediaRouteButton);
        if (castContext.getCastState() != CastState.NO_DEVICES_AVAILABLE) {
            //         mediaRouteButton.setVisibility(View.VISIBLE);
            isCastDeviceAvailable = true;
        }

        castContext.addCastStateListener(new CastStateListener() {
            @Override
            public void onCastStateChanged(int state) {
                if (state == CastState.NO_DEVICES_AVAILABLE) {
                    //          mediaRouteButton.setVisibility(View.GONE);
                    isCastDeviceAvailable = false;
                } else {
                    if (mediaRouteButton.getVisibility() == View.GONE) {
                        //            mediaRouteButton.setVisibility(View.VISIBLE);
                        isCastDeviceAvailable = true;
                    }
                }
            }
        });
        loadDataInCastPlayer(uri);

        // For player height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if (aspectRatio.equalsIgnoreCase("9:16")) {
            screenHeight = displayMetrics.heightPixels - 50;
            verticalPlayerHeight = displayMetrics.heightPixels - 300;

            playerView.getLayoutParams().height = verticalPlayerHeight;
            playerView.requestLayout();
            controls.getLayoutParams().height = verticalPlayerHeight;
            controls.requestLayout();
            // playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        } else {
            if (displayMetrics.widthPixels < displayMetrics.heightPixels) {
                double widthRatio = Helper.pxFromDp(context, Float.parseFloat("5.5"));
                double heightRatio = Helper.pxFromDp(context, Float.parseFloat("3"));
                playerHeight = (int) ((double) displayMetrics.widthPixels * (heightRatio / widthRatio));
            } else {
                double widthRatio = Helper.pxFromDp(context, Float.parseFloat("3"));
                double heightRatio = Helper.pxFromDp(context, Float.parseFloat("5.5"));
                playerHeight = (int) ((double) displayMetrics.heightPixels * (widthRatio / heightRatio));
            }

            int orientation = context.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                playerView.getLayoutParams().height = playerHeight;
                playerView.requestLayout();
                controls.getLayoutParams().height = playerHeight;
                controls.requestLayout();
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                playerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                playerView.requestLayout();
                controls.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                controls.requestLayout();
            }
        }

        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady) {
                    context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else {
                    context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }

              /*  if (playbackState == Player.STATE_IDLE) {
                    mResumeWindow = player.getCurrentWindowIndex();
                    mResumePosition = Math.max(0, player.getContentPosition());
                }*/

                if (playbackState == Player.STATE_BUFFERING) {
                    showLoader();
                } else if (playbackState == Player.STATE_READY) {
                    hideLoader();
                    videoPlayerRL.setEnabled(true);

                    if (isImaAd) {
                        isImaAd = false;
                        controls.show();
                    }

                    if (videoFirstOpen) {
                        playerView.setVisibility(View.VISIBLE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                        videoFirstOpen = false;
                        if (!isImaAdRunning) {
                            isVideoTrackingCalled = true;
                            callVideoTrackingApi();
                        }
                    }

                    if (!isImaAdRunning) {
                        if (!isVideoTrackingCalled) {
                            isVideoTrackingCalled = true;
                            callVideoTrackingApi();
                        }
                    }
                } else if (playbackState == Player.STATE_ENDED) {
                    controls.show();
                    if (isCastDeviceAvailable) {
                        mediaRouteButton.setVisibility(View.VISIBLE);
                    }
                    if (isPostRollAd) {
                        player.prepare(mediaSourceWithAds);
                    }
                }
            }
        });
    }

    private void loadDataInCastPlayer(Uri uri) {
        MediaQueueItem mediaItem = getMediaQueueItems(uri);
        castPlayer = new CastPlayer(castContext);
        if (castPlayer.isCastSessionAvailable()) {
            castPlayer.removeItem(0);
            castPlayer.loadItem(mediaItem, 0);
        }

        castPlayer.setSessionAvailabilityListener(new SessionAvailabilityListener() {
            @Override
            public void onCastSessionAvailable() {
                if (castPlayer != null) {
                    if (player != null) {
                        mResumePosition = Math.max(0, player.getContentPosition());
                    }
                    // castPlayer.removeItem(0);
                    castPlayer.loadItem(mediaItem, mResumePosition);
                    if (AppConstants.IS_VIDEO_CASTED) {
                        AppConstants.IS_VIDEO_CASTED = false;
                        AnalyticsHelper.getInstance(context).trackCastVideoFcmEvent(dataItemList.get(dataItemPosition).id);
                    }
                }
            }

            @Override
            public void onCastSessionUnavailable() {

            }
        });
    }

    @NonNull
    private MediaQueueItem getMediaQueueItems(Uri uri) {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, dataItemList.get(dataItemPosition).title);
        String url = "";
        if (Helper.isContainValue(dataItemList.get(dataItemPosition).video_thumbnail)) {
            url = dataItemList.get(dataItemPosition).video_thumbnail;
        } else if (Helper.isContainValue(dataItemList.get(dataItemPosition).featureImg)) {
            url = dataItemList.get(dataItemPosition).featureImg;
        }
        if (Helper.isContainValue(url)) {
            movieMetadata.addImage(new WebImage(Uri.parse(url)));
        }
        MediaInfo mediaInfo = new MediaInfo.Builder(uri.toString())
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(MimeTypes.VIDEO_UNKNOWN)
                .setMetadata(movieMetadata).build();

        //array of media sources
        return new MediaQueueItem.Builder(mediaInfo).build();
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

    private MediaSource buildCaptionMediaSource(Uri uri, DefaultDataSourceFactory defaultDataSourceFactory) {
        HlsMediaSource mediaSource = new HlsMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri);

        // For subtitles
        Format textFormat = Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP,
                Format.NO_VALUE, dataItemList.get(dataItemPosition).srt.get(0).language);

        Uri uriSubtitle = Uri.parse(dataItemList.get(dataItemPosition).srt.get(0).srtfile);
        MediaSource subtitleSource = new SingleSampleMediaSource(uriSubtitle, defaultDataSourceFactory, textFormat, C.TIME_UNSET);
        return new MergingMediaSource(mediaSource, subtitleSource);
    }

/*    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(context, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(context, "portrait", Toast.LENGTH_SHORT).show();
        }
    }*/

    /*   @Override
    public void onDestroy() {
        super.onDestroy();
        if (playerView != null) {
            Objects.requireNonNull(playerView.getOverlayFrameLayout()).removeAllViews();
            playerView = null;
        }
    }*/

  /*  @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            pauseExoPlayer();
            if (controlDispatcher != null && player != null) {
                controlDispatcher.dispatchSetPlayWhenReady(player, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

 /*   @Override
    public void onStart() {
        super.onStart();
        resumeExoPlayer();
    }*/

    @Override
    public void onStop() {
        pauseExoPlayer();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (videoPlayerPresenter != null) {
            videoPlayerPresenter.onDestroyedView();
        }
        releaseExoPlayer();
        super.onDestroy();
    }

    private void pauseExoPlayer() {
        if (playerView != null && player != null) {
            player.setPlayWhenReady(false);
        }
        /* if (playerView != null && player != null) {
            mVideoPositionTrackingHandler.removeCallbacks(mVideoPositionTrackingRunnable);
            mResumeWindow = player.getCurrentWindowIndex();
            mResumePosition = Math.max(0, player.getContentPosition());
            player.setPlayWhenReady(!player.getPlayWhenReady());
            player.release();
            player = null;
        }*/
    }

    private void releaseExoPlayer() {
        if (playerView != null && player != null) {
            player.release();
            player = null;
        }
    }

    private void resumeExoPlayer() {
        if (isInterstitialAdClosed) {
            if (playerView != null && player != null) {
                player.setPlayWhenReady(true);
            }
           /* if (dataItemList != null && dataItemList.size() > 0) {
                initExoPlayer(dataItemList.get(dataItemPosition).altContent, dataItemList.get(dataItemPosition).aspectRatio);
            }
            if (mVideoPositionTrackingHandler != null) {
                mVideoPositionTrackingHandler.post(mVideoPositionTrackingRunnable);
            }*/
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.infoIV:
                showCastCrewDialog();
                break;

            case R.id.reactionIV:
                showReactionDialog();
                break;

            case R.id.showRL:
                Intent intent = new Intent(context, ShowDetailActivity.class);
                intent.putExtra("slug", dataItemList.get(dataItemPosition).show.topicDisplay.topicSlug);
                intent.putExtra("sw_more", dataItemList.get(dataItemPosition).sw_more);
                context.startActivity(intent);
                break;

            case R.id.resizeRL:
                int orientation = context.getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    playerView.getLayoutParams().height = playerHeight;
                    playerView.requestLayout();
                    controls.getLayoutParams().height = playerHeight;
                    controls.requestLayout();
            /*        new Handler().postDelayed(() -> {
                        controls.getLayoutParams().height = playerView.getHeight();
                        controls.requestLayout();
                    }, 400);
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);*/
                } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    playerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                    playerView.requestLayout();
                    controls.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                    controls.requestLayout();
                    //   playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                }
                break;

            case R.id.verticalResizeRL:
                if (isFullScreen) {
                    isFullScreen = false;
                    playerView.getLayoutParams().height = verticalPlayerHeight;
                    playerView.requestLayout();
                    controls.getLayoutParams().height = verticalPlayerHeight;
                    controls.requestLayout();
                    //  playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                } else {
                    isFullScreen = true;
                    playerView.getLayoutParams().height = screenHeight;
                    playerView.requestLayout();
                    controls.getLayoutParams().height = screenHeight;
                    controls.requestLayout();
                    // playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                }
                break;

            case R.id.shareIV:
                String videoUrl = context.getString(R.string.share_url) + dataItemList.get(dataItemPosition).id;
                String shareUrl = context.getString(R.string.string_download);
                shareUrl = videoUrl + "\n\n" + shareUrl + "\n\n" + context.getString(R.string.common_play_store_url);
                shareData(shareUrl);
                break;

            case R.id.likeCB:
                if (likeCB.isChecked()) {
                    callVideoFavouriteApi(false);
                } else {
                    callVideoFavouriteApi(true);
                }
                break;

            case R.id.settingIV:
                showSettingsDialog();
                break;

            case R.id.sendCommentIV:
                if (Helper.isContainValue(commentET.getText().toString().trim())) {
                    sendComment();
                }
                break;

            case R.id.filterIV:
                showFilterPopUp();
                break;

            case R.id.videoPlayerRL:
                if (controls.isVisible()) {
                    controls.hide();
                    mediaRouteButton.setVisibility(View.GONE);
                } else {
                    controls.show();
                    if (isCastDeviceAvailable) {
                        mediaRouteButton.setVisibility(View.VISIBLE);
                    }
                    new Handler().postDelayed(() -> {
                        controls.hide();
                        mediaRouteButton.setVisibility(View.GONE);
                    }, 5000);
                }
                break;

            case R.id.guidelineTV:
            case R.id.communityTV:
                startActivity(new Intent(context, TermsPrivacyActivity.class).putExtra("from", "community"));
                break;

            default:
                break;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            try {
                context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                controls.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                controls.requestLayout();
                playerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                playerView.requestLayout();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //   playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            try {
                context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (controls != null) {
                controls.getLayoutParams().height = playerHeight;
                controls.requestLayout();
            }
 /*           new Handler().postDelayed(() -> {
                if (controls != null) {
                    controls.getLayoutParams().height = playerView.getHeight();
                    controls.requestLayout();
                }
            }, 400);*/
            if (playerView != null) {
                playerView.getLayoutParams().height = playerHeight;
                playerView.requestLayout();
                //  playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            }
        }
    }

    private void showReactionDialog() {
        View view = getLayoutInflater().inflate(R.layout.reaction_dialog, null);
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(view);
        dialog.show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }

        ImageView bombIV = dialog.findViewById(R.id.bombIV);
        ImageView wavingHandIV = dialog.findViewById(R.id.wavingHandIV);
        ImageView fireIV = dialog.findViewById(R.id.fireIV);
        ImageView tearOfJoyIV = dialog.findViewById(R.id.tearOfJoyIV);

        bombIV.setOnClickListener(view1 -> {
            postReactionApi(0, "bomb");
            dialog.cancel();
        });

        wavingHandIV.setOnClickListener(view12 -> {
            postReactionApi(1, "wavinghand");
            dialog.cancel();
        });

        fireIV.setOnClickListener(view13 -> {
            postReactionApi(2, "fire");
            dialog.cancel();
        });

        tearOfJoyIV.setOnClickListener(view14 -> {
            postReactionApi(3, "tearofjoy");
            dialog.cancel();
        });
    }

    private void postReactionApi(int position, String type) {
        if (Helper.isNetworkAvailable(context)) {
            videoPlayerPresenter.callReactionApi(dataItemList.get(dataItemPosition).id, type);
            callAnimation(position);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void callAnimation(int pos) {
        switch (pos) {
            case 0:
                for (int i = 0; i < 15; i++) {
                    flyEmoticon(R.drawable.ic_vector_bomb);
                }
                break;
            case 1:
                for (int i = 0; i < 15; i++) {
                    flyEmoticon(R.drawable.ic_vector_waving_hand);
                }
                break;
            case 2:
                for (int i = 0; i < 15; i++) {
                    flyEmoticon(R.drawable.ic_vector_fire);
                }
                break;
            case 3:
                for (int i = 0; i < 15; i++) {
                    flyEmoticon(R.drawable.ic_vector_tear_of_joy);
                }
                break;
        }
    }

    private void flyEmoticon(final int resId) {
        ZeroGravityAnimation animation = new ZeroGravityAnimation();
        animation.setCount(1);
        animation.setScalingFactor(1f);
        animation.setOriginationDirection(Direction.BOTTOM);
        animation.setDestinationDirection(Direction.TOP);
        animation.setDuration(3000);
        animation.setImage(resId);
        animation.play(context, parentRL);
    }

    private void callVideoFavouriteApi(boolean isFavourite) {
        if (Helper.isNetworkAvailable(context)) {
            if (isFavourite) {
                videoPlayerPresenter.callUnFavouriteApi(dataItemList.get(dataItemPosition).id);
            } else {
                videoPlayerPresenter.callFavouriteApi(dataItemList.get(dataItemPosition).id);
            }
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
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

    private void sendComment() {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            sendCommentIV.setEnabled(false);
            videoPlayerPresenter.postUserComment(dataItemList.get(dataItemPosition).id, dataItemList.get(dataItemPosition).slug, commentET.getText().toString().trim());
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void showCastCrewDialog() {
        View view = getLayoutInflater().inflate(R.layout.cast_crew_dialog, null);
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(view);
        dialog.show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }

        TextView titleTV = dialog.findViewById(R.id.titleTV);
        TextView descriptionTV = dialog.findViewById(R.id.descriptionTV);

        if (titleTV != null) {
            titleTV.setText(dataItemList.get(dataItemPosition).title);
        }
        if (descriptionTV != null) {
            descriptionTV.setText(dataItemList.get(dataItemPosition).shHeading);
        }

        RecyclerView castCrewRV = dialog.findViewById(R.id.castCrewRV);
        if (castCrewRV != null) {
            castCrewRV.setHasFixedSize(true);
            castCrewRV.setLayoutManager(new LinearLayoutManager(context));
            castCrewRV.setItemAnimator(new DefaultItemAnimator());

            CastCrewAdapter castCrewAdapter = new CastCrewAdapter(context, dataItemList.get(dataItemPosition).castCrew, DataHolder.getInstance().getUserDataresponse, new CastCrewAdapter.FollowActor() {
                @Override
                public void postFollowActor(String userName, String type) {
                    if (Helper.isNetworkAvailable(context)) {
                        if (type.equalsIgnoreCase("Follow")) {
                            videoPlayerPresenter.callFollowApi(userName);
                        } else {
                            videoPlayerPresenter.callUnFollowApi(userName);
                        }
                    } else {
                        showMessage(getString(R.string.please_check_internet_connection));
                    }
                }
            });
            castCrewRV.setAdapter(castCrewAdapter);
        }

        /*if (dataItemList.get(dataItemPosition).castCrew != null && dataItemList.get(dataItemPosition).castCrew.size() < 4) {
            Objects.requireNonNull(alertDialog.getWindow()).setLayout(400, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            Objects.requireNonNull(alertDialog.getWindow()).setLayout(400, 500);
        }*/
    }

    private void shareData(String message) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
            String shareMessage = "Let me recommend you this video :\n\n";
            shareMessage = shareMessage + message + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                Intent receiver = new Intent(context, ApplicationSelectorReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT);
                Intent chooser = Intent.createChooser(shareIntent, "Share via...", pendingIntent.getIntentSender());
                startActivity(chooser);
            } else {
                startActivity(Intent.createChooser(shareIntent, "Share via..."));
                AnalyticsHelper.getInstance(context).trackShareVideoFcmEvent("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSettingsDialog() {
        View view = getLayoutInflater().inflate(R.layout.setting_bottom_sheet_dialog, null);
        settingDialog = new BottomSheetDialog(context);
        settingDialog.setContentView(view);
        settingDialog.show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(settingDialog);
        }

        setCaptionLabel();
        setVideoQualityLabel();
        setPlaybackSpeedLabel();
        setReportProblemLabel();
    }

    private void setCaptionLabel() {
        LinearLayout captionsLL = settingDialog.findViewById(R.id.captionsLL);
        if (dataItemList.get(dataItemPosition).srt != null && dataItemList.get(dataItemPosition).srt.size() > 0 && Helper.isContainValue(dataItemList.get(dataItemPosition).srt.get(0).srtfile)) {
            captionsLL.setVisibility(View.VISIBLE);

            TextView captionTV = settingDialog.findViewById(R.id.captionTV);
            captionTV.setText(AppConstants.CAPTION_STATUS);
        } else {
            captionsLL.setVisibility(View.GONE);
        }

        captionsLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingDialog.cancel();
                View captionView = getLayoutInflater().inflate(R.layout.captions_bottom_sheet_dialog, null);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                bottomSheetDialog.setContentView(captionView);
                bottomSheetDialog.show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    setWhiteNavigationBar(bottomSheetDialog);
                }

                RadioButton onRB = captionView.findViewById(R.id.onRB);
                RadioButton offRB = captionView.findViewById(R.id.offRB);

                if (AppConstants.CAPTION_STATUS.equalsIgnoreCase("On")) {
                    onRB.setChecked(true);
                } else {
                    offRB.setChecked(true);
                }

                onRB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.cancel();
                        AppConstants.CAPTION_STATUS = "On";
                        playerView.getSubtitleView().setVisibility(View.VISIBLE);

                        AnalyticsHelper.getInstance(context).trackCaptionFcmEvent("on");
                    }
                });

                offRB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.cancel();
                        AppConstants.CAPTION_STATUS = "Off";
                        playerView.getSubtitleView().setVisibility(View.GONE);

                        AnalyticsHelper.getInstance(context).trackCaptionFcmEvent("off");
                    }
                });
            }
        });
    }

    private void setVideoQualityLabel() {
        TextView qualityTV = settingDialog.findViewById(R.id.qualityTV);
        if (Helper.isContainValue(AppPreferences.getInstance(context).getPreferencesString(AppConstants.VIDEO_HEIGHT))) {
            if (!AppPreferences.getInstance(context).getPreferencesString(AppConstants.VIDEO_HEIGHT).equalsIgnoreCase("Auto")) {
                if (Integer.parseInt(AppPreferences.getInstance(context).getPreferencesString(AppConstants.VIDEO_HEIGHT)) > 700) {
                    qualityTV.setText(Html.fromHtml(AppPreferences.getInstance(context).getPreferencesString(AppConstants.VIDEO_HEIGHT) + "p <b>HD</b>"));
                } else {
                    qualityTV.setText(AppPreferences.getInstance(context).getPreferencesString(AppConstants.VIDEO_HEIGHT) + "p");
                }
            } else {
                qualityTV.setText("Auto");
            }
        }

        LinearLayout videoQualityLL = settingDialog.findViewById(R.id.videoQualityLL);
        if (videoQualityLL != null) {
            videoQualityLL.setOnClickListener(view1 -> {
                MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                if (mappedTrackInfo != null) {
                    TrackGroupArray trackGroups = null;
                    for (int i = 0; i < mappedTrackInfo.length; i++) {
                        trackGroups = mappedTrackInfo.getTrackGroups(i);
                        if (trackGroups.length != 0) {
                            if (player.getRendererType(i) == C.TRACK_TYPE_VIDEO) {
                                break;
                            }
                        }
                    }

                    if (trackGroups != null) {
                        ArrayList<Integer> videoHeight = new ArrayList<>();
                        ArrayList<String> videoHeightAL = new ArrayList<>();

                        ArrayList<Integer> videoWidth = new ArrayList<>();
                        ArrayList<String> videoWidthAL = new ArrayList<>();
                        for (int j = 0; j < trackGroups.length; j++) {
                            for (int k = 0; k < trackGroups.get(j).length; k++) {
                                if (!videoHeight.contains(trackGroups.get(j).getFormat(k).height)) {
                                    videoHeight.add(trackGroups.get(j).getFormat(k).height);
                                }
                                if (!videoWidth.contains(trackGroups.get(j).getFormat(k).width)) {
                                    videoWidth.add(trackGroups.get(j).getFormat(k).width);
                                }
                            }
                        }

                        Collections.sort(videoHeight, Collections.reverseOrder());
                        Collections.sort(videoWidth, Collections.reverseOrder());
                        for (int l = 0; l < videoHeight.size(); l++) {
                            videoHeightAL.add(String.valueOf(videoHeight.get(l)));
                        }
                        for (int l = 0; l < videoWidth.size(); l++) {
                            videoWidthAL.add(String.valueOf(videoWidth.get(l)));
                        }
                        videoHeightAL.add(0, "Auto");
                        videoWidthAL.add(0, "Auto");
                        setVideoQualityDialog(videoHeightAL, videoWidthAL);
                    }
                }
            });
        }
    }

    private void setVideoQualityDialog(ArrayList<String> videoHeightAL, ArrayList<String> videoWidthAL) {
        View view = getLayoutInflater().inflate(R.layout.video_quality_bottom_sheet_dialog, null);
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(view);
        dialog.show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }

        RecyclerView videoHeightRV = dialog.findViewById(R.id.videoHeightRV);
        if (videoHeightRV != null) {
            videoHeightRV.setHasFixedSize(true);
            videoHeightRV.setLayoutManager(new LinearLayoutManager(context));
            videoHeightRV.setItemAnimator(new DefaultItemAnimator());

            VideoQualityAdapter videoQualityAdapter = new VideoQualityAdapter(context, videoHeightAL, videoWidthAL, true, new VideoQualityAdapter.VideoQuality() {
                @Override
                public void setVideoQuality() {
                    dialog.cancel();
                    settingDialog.cancel();
                    if (!AppPreferences.getInstance(context).getPreferencesString(AppConstants.VIDEO_HEIGHT).equalsIgnoreCase("Auto")) {
                        trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSize(Integer.parseInt(AppPreferences.getInstance(context).getPreferencesString(AppConstants.VIDEO_WIDTH)), Integer.parseInt(AppPreferences.getInstance(context).getPreferencesString(AppConstants.VIDEO_HEIGHT))));
                    } else {
                        trackSelector.setParameters(trackSelector.buildUponParameters().clearVideoSizeConstraints());
                    }
                }
            });
            videoHeightRV.setAdapter(videoQualityAdapter);
        }
    }

    private void setPlaybackSpeedLabel() {
        LinearLayout playbackSpeedLL = settingDialog.findViewById(R.id.playbackSpeedLL);
        TextView speedTV = settingDialog.findViewById(R.id.speedTV);
        speedTV.setText(speedStatus);

        playbackSpeedLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingDialog.cancel();
                View playbackSpeedView = getLayoutInflater().inflate(R.layout.playback_speed_bottom_sheet_dialog, null);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                bottomSheetDialog.setContentView(playbackSpeedView);
                bottomSheetDialog.show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    setWhiteNavigationBar(bottomSheetDialog);
                }

                RadioButton pointTwentyFiveRB = playbackSpeedView.findViewById(R.id.pointTwentyFiveRB);
                RadioButton pointFiveRB = playbackSpeedView.findViewById(R.id.pointFiveRB);
                RadioButton pointSeventyFiveRB = playbackSpeedView.findViewById(R.id.pointSeventyFiveRB);
                RadioButton normalRB = playbackSpeedView.findViewById(R.id.normalRB);
                RadioButton onePointTwentyFiveRB = playbackSpeedView.findViewById(R.id.onePointTwentyFiveRB);
                RadioButton onePointFiveRB = playbackSpeedView.findViewById(R.id.onePointFiveRB);
                RadioButton onePointSeventyFiveRB = playbackSpeedView.findViewById(R.id.onePointSeventyFiveRB);
                RadioButton TwoRB = playbackSpeedView.findViewById(R.id.TwoRB);

                if (speedStatus.equalsIgnoreCase("0.25x")) {
                    pointTwentyFiveRB.setChecked(true);
                } else if (speedStatus.equalsIgnoreCase("0.5x")) {
                    pointFiveRB.setChecked(true);
                } else if (speedStatus.equalsIgnoreCase("0.75x")) {
                    pointSeventyFiveRB.setChecked(true);
                } else if (speedStatus.equalsIgnoreCase("Normal")) {
                    normalRB.setChecked(true);
                } else if (speedStatus.equalsIgnoreCase("1.25x")) {
                    onePointTwentyFiveRB.setChecked(true);
                } else if (speedStatus.equalsIgnoreCase("1.5x")) {
                    onePointFiveRB.setChecked(true);
                } else if (speedStatus.equalsIgnoreCase("1.75x")) {
                    onePointSeventyFiveRB.setChecked(true);
                } else if (speedStatus.equalsIgnoreCase("2x")) {
                    TwoRB.setChecked(true);
                }

                pointTwentyFiveRB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.cancel();
                        player.setPlaybackParameters(new PlaybackParameters(0.25f));
                        speedStatus = "0.25x";
                    }
                });

                pointFiveRB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.cancel();
                        player.setPlaybackParameters(new PlaybackParameters(0.5f));
                        speedStatus = "0.5x";
                    }
                });

                pointSeventyFiveRB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.cancel();
                        player.setPlaybackParameters(new PlaybackParameters(0.75f));
                        speedStatus = "0.75x";
                    }
                });

                normalRB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.cancel();
                        player.setPlaybackParameters(new PlaybackParameters(1f));
                        speedStatus = "Normal";
                    }
                });

                onePointTwentyFiveRB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.cancel();
                        player.setPlaybackParameters(new PlaybackParameters(1.25f));
                        speedStatus = "1.25x";
                    }
                });

                onePointFiveRB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.cancel();
                        player.setPlaybackParameters(new PlaybackParameters(1.5f));
                        speedStatus = "1.5x";
                    }
                });

                onePointSeventyFiveRB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.cancel();
                        player.setPlaybackParameters(new PlaybackParameters(1.75f));
                        speedStatus = "1.75x";
                    }
                });

                TwoRB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.cancel();
                        player.setPlaybackParameters(new PlaybackParameters(2f));
                        speedStatus = "2x";
                    }
                });
            }
        });
    }

    private void setReportProblemLabel() {
        LinearLayout reportIssueLL = settingDialog.findViewById(R.id.reportIssueLL);
        if (videoIssuesAL != null && videoIssuesAL.size() > 0) {
            reportIssueLL.setVisibility(View.VISIBLE);
        } else {
            reportIssueLL.setVisibility(View.GONE);
        }

        reportIssueLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View reportIssueView = getLayoutInflater().inflate(R.layout.report_issue_bottom_sheet_dialog, null);
                BottomSheetDialog dialog = new BottomSheetDialog(context);
                dialog.setContentView(reportIssueView);
                dialog.show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    setWhiteNavigationBar(dialog);
                }

                RecyclerView reportIssueRV = dialog.findViewById(R.id.reportIssueRV);
                if (reportIssueRV != null) {
                    reportIssueRV.setHasFixedSize(true);
                    reportIssueRV.setLayoutManager(new LinearLayoutManager(context));
                    reportIssueRV.setItemAnimator(new DefaultItemAnimator());

                    ReportIssueAdapter reportIssueAdapter = new ReportIssueAdapter(context, videoIssuesAL, new ReportIssueAdapter.ReportIssue() {
                        @Override
                        public void reportVideoIssue(String videoIssue) {
                            settingDialog.cancel();
                            dialog.cancel();
                            if (Helper.isNetworkAvailable(context)) {
                                videoPlayerPresenter.postReportIssueApi(videoIssue, dataItemList.get(dataItemPosition).id);
                            } else {
                                showMessage(getString(R.string.please_check_internet_connection));
                            }
                        }
                    });
                    reportIssueRV.setAdapter(reportIssueAdapter);
                }
            }
        });
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

    @Override
    public void setReactionResponse(ReactionResponse reactionResponse) {

    }

    @Override
    public void setUnFavResponse(UnFavouriteResponse unFavouriteResponse) {
        if (unFavouriteResponse != null) {
            likeString = "video";
            callUserDataApi();
        }
    }

    @Override
    public void setFavResponse(FavouriteResponse favouriteResponse) {
        if (favouriteResponse != null) {
            likeString = "video";
            callUserDataApi();
        }
    }

    @Override
    public void setFollowResponse(FollowResponse followResponse) {
        if (followResponse != null) {
            likeString = "anchor";
            callUserDataApi();
        }
    }

    @Override
    public void setUnFollowResponse(UnFollowResponse unFollowResponse) {
        if (unFollowResponse != null) {
            likeString = "anchor";
            callUserDataApi();
        }
    }

    @Override
    public void getUserData(GetUserResponse getUserResponse) {
        if (getUserResponse != null && getUserResponse.data != null) {
            DataHolder.getInstance().getUserDataresponse = getUserResponse.data;

            if (likeString.equalsIgnoreCase("video")) {
                if (getUserResponse.data.favourite != null && getUserResponse.data.favourite.size() > 0) {
                    FavouriteFragment.callVideoListApi(getUserResponse.data.favourite);
                } else {
                    FavouriteFragment.hideVideosView();
                }
            } else if (likeString.equalsIgnoreCase("anchor")) {
                if (getUserResponse.data.follow != null && getUserResponse.data.follow.size() > 0) {
                    FavouriteFragment.callAnchorListApi(getUserResponse.data.follow);
                } else {
                    FavouriteFragment.hideAnchorView();
                }
            }

            if (getUserResponse.data.follow != null && !(getUserResponse.data.follow.size() > 0) && getUserResponse.data.favourite != null && !(getUserResponse.data.favourite.size() > 0) && getUserResponse.data.favourite_shows != null && !(getUserResponse.data.favourite_shows.size() > 0)) {
                FavouriteFragment.showNoDataView();
            }
        }
    }

    @Override
    public void setReportIssueResponse(ReportIssueResponse reportIssueResponse) {
        if (reportIssueResponse != null && reportIssueResponse.videoIssues != null && reportIssueResponse.videoIssues.size() > 0) {
            videoIssuesAL = reportIssueResponse.videoIssues;
        }
    }

    @Override
    public void setReportIssuePostResponse(ReportProblemResponse reportProblemResponse) {
        if (reportProblemResponse != null) {
            if (Helper.isContainValue(reportProblemResponse.message)) {
                showMessage(reportProblemResponse.message);
            }
        }
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
                commentCountTV.setText(videoCommentResponse.total_coments + " Comment");
            } else {
                commentCountTV.setText(videoCommentResponse.total_coments + " Comments");
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
    public void setEventTrackingUpdateResponse(EventTrackingUpdateResponse eventTrackingUpdateResponse) {
        if (eventTrackingUpdateResponse != null) {
            Log.d("success", "success");
        }
    }

    @Override
    public void setEventTrackingResponse(EventTrackingResponse eventTrackingResponse) {
        if (eventTrackingResponse != null) {
            eventId = eventTrackingResponse.event_id;
            if (eventTrackingResponse.video_id != null) {
                eventId = eventTrackingResponse.video_id;
            }
            if (mVideoPositionTrackingHandler != null) {
                mVideoPositionTrackingHandler.post(mVideoPositionTrackingRunnable);
            }
        }
    }

    @Override
    public void setPlayerControlDimension() {
        try {
            if (dataItemList != null && dataItemList.size() > 0 && dataItemList.get(dataItemPosition).aspectRatio.equalsIgnoreCase("9:16")) {
                controls.getLayoutParams().height = verticalPlayerHeight;
                controls.requestLayout();
                //  playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            } else {
                playerView.getLayoutParams().height = playerHeight;
                playerView.requestLayout();
                controls.getLayoutParams().height = playerHeight;
                controls.requestLayout();
                //  playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callVideoTrackingApi() {
        videoDurationInMilliSeconds = player.getDuration();
        long videoDurationInSeconds = TimeUnit.MILLISECONDS.toSeconds(videoDurationInMilliSeconds);
        twentyFivePercent = videoDurationInMilliSeconds * 25 / 100;
        twentySixPercent = videoDurationInMilliSeconds * 26 / 100;
        fiftyPercent = videoDurationInMilliSeconds * 50 / 100;
        fiftyOnePercent = videoDurationInMilliSeconds * 51 / 100;
        seventyFivePercent = videoDurationInMilliSeconds * 75 / 100;
        seventySixPercent = videoDurationInMilliSeconds * 76 / 100;
        ninetyFivePercent = videoDurationInMilliSeconds * 95 / 100;
        ninetySixPercent = videoDurationInMilliSeconds * 96 / 100;

        Calendar calendar = Calendar.getInstance();

        ArrayList<String> mediaTag = new ArrayList<>();
        mediaTag.addAll(dataItemList.get(dataItemPosition).tags);
        VideoEventTrackingRequest videoEventTrackingRequest = new VideoEventTrackingRequest();
        videoEventTrackingRequest.city = cityName;
        videoEventTrackingRequest.autoplay = "true";
        videoEventTrackingRequest.state = stateName;
        videoEventTrackingRequest.country = countryName;
        videoEventTrackingRequest.date = Helper.getCurrentDates();
        videoEventTrackingRequest.duration = String.valueOf(videoDurationInSeconds);
        videoEventTrackingRequest.event = "play";
        videoEventTrackingRequest.quartile = "0";
        videoEventTrackingRequest.watchDuration = "0";
        videoEventTrackingRequest.hours = String.valueOf(calendar.get(Calendar.HOUR));
        videoEventTrackingRequest.minutes = String.valueOf(calendar.get(Calendar.MINUTE));
        videoEventTrackingRequest.mediaTags = mediaTag;
        videoEventTrackingRequest.image = dataItemList.get(dataItemPosition).featureImg;
        videoEventTrackingRequest.path = dataItemList.get(dataItemPosition).altContent;
        videoEventTrackingRequest.playerUID = AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID);
        videoEventTrackingRequest.title = dataItemList.get(dataItemPosition).title;
        videoEventTrackingRequest.videoId = dataItemList.get(dataItemPosition).id;
        videoEventTrackingRequest.videoPlatform = "APP";
        videoEventTrackingRequest.pageDomain = Build.VERSION.SDK;
        videoEventTrackingRequest.os = "Android";
        videoEventTrackingRequest.deviceType = "oktested";
        videoEventTrackingRequest.osVersion = BuildConfig.VERSION_NAME;
        videoEventTrackingRequest.browser = Build.BRAND;
        videoEventTrackingRequest.browserVersion = Build.MODEL;
        videoEventTrackingRequest.sw_cookie = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(videoEventTrackingRequest);
            JsonParser parser = new JsonParser();
            jsonObjects = parser.parse(jsonStr).getAsJsonObject();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (Helper.isNetworkAvailable(context)) {
            videoPlayerPresenter.callVideoTrackingApi(jsonObjects);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void callVideoTrackingUpdateApi(long duration) {
        if (duration >= 5000 && duration <= 6000) {
            if (isFive) {
                isFive = false;
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("quartile", "5");
                jsonObject.addProperty("watchDuration", "5");
                jsonObject.addProperty("id", eventId);
                if (Helper.isNetworkAvailable(context)) {
                    videoPlayerPresenter.callVideoTrackingUpdateApi(jsonObject);
                }
            }
        } else if (duration >= 10000 && duration <= 11000) {
            if (isTen) {
                isTen = false;
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("quartile", "10");
                jsonObject.addProperty("watchDuration", "10");
                jsonObject.addProperty("id", eventId);
                if (Helper.isNetworkAvailable(context)) {
                    videoPlayerPresenter.callVideoTrackingUpdateApi(jsonObject);
                }
            }
        } else if (duration >= 30000 && isThirtySecond) {
            isThirtySecond = false;
            AnalyticsHelper.getInstance(context).trackPlayVideoFcmEvent(dataItemList.get(dataItemPosition).id);
        } else if (duration >= twentyFivePercent && duration <= twentySixPercent && twentyFivePercent != 0L && twentySixPercent != 0L) {
            if (isTwentyFive) {
                isTwentyFive = false;
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("quartile", "25");
                jsonObject.addProperty("watchDuration", TimeUnit.MILLISECONDS.toSeconds(duration));
                jsonObject.addProperty("id", eventId);
                if (Helper.isNetworkAvailable(context)) {
                    videoPlayerPresenter.callVideoTrackingUpdateApi(jsonObject);
                }
            }
        } else if (duration >= fiftyPercent && fiftyPercent != 0L && duration <= fiftyOnePercent && fiftyOnePercent != 0L) {
            if (isFifty) {
                isFifty = false;
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("quartile", "50");
                jsonObject.addProperty("watchDuration", TimeUnit.MILLISECONDS.toSeconds(duration));
                jsonObject.addProperty("id", eventId);
                if (Helper.isNetworkAvailable(context)) {
                    videoPlayerPresenter.callVideoTrackingUpdateApi(jsonObject);
                }
            }
        } else if (duration >= fiftyPercent && fiftyPercent != 0L && isFiftyPercent && isMidRollAd) {
            isFiftyPercent = false;

            mResumeWindow = player.getCurrentWindowIndex();
            mResumePosition = Math.max(0, player.getContentPosition());
            player.prepare(mediaSourceWithAds);
        } else if (duration >= seventyFivePercent && seventyFivePercent != 0L && duration <= seventySixPercent && seventySixPercent != 0L) {
            if (isSeventyFive) {
                isSeventyFive = false;
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("quartile", "75");
                jsonObject.addProperty("watchDuration", TimeUnit.MILLISECONDS.toSeconds(duration));
                jsonObject.addProperty("id", eventId);
                if (Helper.isNetworkAvailable(context)) {
                    videoPlayerPresenter.callVideoTrackingUpdateApi(jsonObject);
                }
            }
        } else if (duration >= ninetyFivePercent && ninetyFivePercent != 0L && duration <= ninetySixPercent && ninetySixPercent != 0L) {
            if (isNinetyFive) {
                isNinetyFive = false;
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("quartile", "100");
                jsonObject.addProperty("watchDuration", TimeUnit.MILLISECONDS.toSeconds(duration));
                jsonObject.addProperty("id", eventId);
                if (Helper.isNetworkAvailable(context)) {
                    videoPlayerPresenter.callVideoTrackingUpdateApi(jsonObject);
                }
            }
        }
    }

    private void loadMiddleAd(View view, AdSettingDataResponse.AdSettingValue adSettingValue) {
        if (adSettingValue != null && adSettingValue.status) {
            RelativeLayout middleAdView = view.findViewById(R.id.middleAdView);
            MyBannerAd.getInstance().getAd(context, middleAdView, adSettingValue.is_mid_ad, adSettingValue.ad_code);
        }
    }

    private void loadInterstitialAd(AdSettingDataResponse.AdSettingValue adSettingValue) {
       /* int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {*/
        if (adSettingValue != null && adSettingValue.status) {
            if (AppConstants.INTERSTITIAL_AD_COUNT == AppConstants.INTERSTITIAL_INCREASE_AD_COUNT) {
                AppConstants.INTERSTITIAL_INCREASE_AD_COUNT = AppConstants.INTERSTITIAL_INCREASE_AD_COUNT + adSettingValue.count;
                AppConstants.INTERSTITIAL_AD_COUNT++;

                // Show interstitial ad
                PublisherInterstitialAd interstitialAd = MyInterstitialAd.getInstance().getAd();
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isInterstitialAdClosed = false;
                            pauseExoPlayer();
                        }
                    }, 400);
                }

                MyInterstitialAd.getInstance().setInterface(new MyInterstitialAd.OnAdClosed() {
                    @Override
                    public void onInterstitialAdClosed() {
                        MyInterstitialAd.getInstance().loadInterstitialAd(context, adSettingValue.ad_code);

                        isInterstitialAdClosed = true;
                        resumeExoPlayer();
                    }
                });
            } else {
                if (AppConstants.INTERSTITIAL_AD_COUNT == 1) {
                    MyInterstitialAd.getInstance().loadInterstitialAd(context, adSettingValue.ad_code);
                }
                AppConstants.INTERSTITIAL_AD_COUNT++;
                //      }
            }
        }
    }

    private void loadPreRollImaAd(AdSettingDataResponse.AdSettingValue adSettingValue) {
        if (adSettingValue != null && adSettingValue.status) {
            isPreRollAd = true;

            imaAdsLoader = new ImaAdsLoader(context, Uri.parse(adSettingValue.ad_code));
            imaAdsLoader.setPlayer(player);
            mediaSourceWithAds = new AdsMediaSource(extractorMediaSource, defaultDataSourceFactory, imaAdsLoader, playerView);
            player.prepare(mediaSourceWithAds);

            imaAdsLoader.getAdsLoader().addAdErrorListener(new AdErrorEvent.AdErrorListener() {
                @Override
                public void onAdError(AdErrorEvent adErrorEvent) {
                    if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.MID_ROLL_IMA_AD)) {
                        loadMidRollImaAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.MID_ROLL_IMA_AD));
                    } else if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.POST_ROLL_IMA_AD)) {
                        loadPostRollImaAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.POST_ROLL_IMA_AD));
                    }
                }
            });

            imaAdsLoader.addCallback(new VideoAdPlayer.VideoAdPlayerCallback() {
                @Override
                public void onPlay() {
                    isImaAdRunning = true;
                    isImaAd = true;
                    forwardRL.setVisibility(View.GONE);
                    backwardRL.setVisibility(View.GONE);
                    playPauseRL.setVisibility(View.GONE);
                    seekBarRL.setVisibility(View.GONE);
                }

                @Override
                public void onVolumeChanged(int i) {
                }

                @Override
                public void onPause() {
                    playPauseRL.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoaded() {
                }

                @Override
                public void onResume() {
                    playPauseRL.setVisibility(View.GONE);
                }

                @Override
                public void onEnded() {
                    isImaAdRunning = false;
                    controls.hide();
                    playPauseRL.setVisibility(View.VISIBLE);
                    forwardRL.setVisibility(View.VISIBLE);
                    backwardRL.setVisibility(View.VISIBLE);
                    seekBarRL.setVisibility(View.VISIBLE);

                    if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.MID_ROLL_IMA_AD)) {
                        loadMidRollImaAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.MID_ROLL_IMA_AD));
                    } else if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.POST_ROLL_IMA_AD)) {
                        loadPostRollImaAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.POST_ROLL_IMA_AD));
                    }
                }

                @Override
                public void onError() {
                }

                @Override
                public void onBuffering() {
                }
            });
        } else {
            if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.MID_ROLL_IMA_AD)) {
                loadMidRollImaAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.MID_ROLL_IMA_AD));
            } else if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.POST_ROLL_IMA_AD)) {
                loadPostRollImaAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.POST_ROLL_IMA_AD));
            }
        }
    }

    private void loadMidRollImaAd(AdSettingDataResponse.AdSettingValue adSettingValue) {
        if (adSettingValue != null && adSettingValue.status) {
            isMidRollAd = true;

            imaAdsLoader = new ImaAdsLoader(context, Uri.parse(adSettingValue.ad_code));
            imaAdsLoader.setPlayer(player);
            mediaSourceWithAds = new AdsMediaSource(extractorMediaSource, defaultDataSourceFactory, imaAdsLoader, playerView);

            imaAdsLoader.getAdsLoader().addAdErrorListener(new AdErrorEvent.AdErrorListener() {
                @Override
                public void onAdError(AdErrorEvent adErrorEvent) {
                    if (player != null) {
                        player.seekTo(mResumeWindow, mResumePosition);
                    }
                    if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.POST_ROLL_IMA_AD)) {
                        loadPostRollImaAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.POST_ROLL_IMA_AD));
                    }
                }
            });
            imaAdsLoader.addCallback(new VideoAdPlayer.VideoAdPlayerCallback() {
                @Override
                public void onPlay() {
                    isImaAd = true;
                    forwardRL.setVisibility(View.GONE);
                    backwardRL.setVisibility(View.GONE);
                    playPauseRL.setVisibility(View.GONE);
                    seekBarRL.setVisibility(View.GONE);
                }

                @Override
                public void onVolumeChanged(int i) {
                }

                @Override
                public void onPause() {
                    playPauseRL.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoaded() {
                }

                @Override
                public void onResume() {
                    playPauseRL.setVisibility(View.GONE);
                }

                @Override
                public void onEnded() {
                    controls.hide();
                    playPauseRL.setVisibility(View.VISIBLE);
                    forwardRL.setVisibility(View.VISIBLE);
                    backwardRL.setVisibility(View.VISIBLE);
                    seekBarRL.setVisibility(View.VISIBLE);

                    player.seekTo(mResumeWindow, mResumePosition);

                    if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.POST_ROLL_IMA_AD)) {
                        loadPostRollImaAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.POST_ROLL_IMA_AD));
                    }
                }

                @Override
                public void onError() {
                }

                @Override
                public void onBuffering() {
                }
            });
        } else {
            if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.POST_ROLL_IMA_AD)) {
                loadPostRollImaAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.POST_ROLL_IMA_AD));
            }
        }
    }

    private void loadPostRollImaAd(AdSettingDataResponse.AdSettingValue adSettingValue) {
        if (adSettingValue != null && adSettingValue.status) {
            isPostRollAd = true;

            imaAdsLoader = new ImaAdsLoader(context, Uri.parse(adSettingValue.ad_code));
            imaAdsLoader.setPlayer(player);
            mediaSourceWithAds = new AdsMediaSource(extractorMediaSource, defaultDataSourceFactory, imaAdsLoader, playerView);

            imaAdsLoader.getAdsLoader().addAdErrorListener(new AdErrorEvent.AdErrorListener() {
                @Override
                public void onAdError(AdErrorEvent adErrorEvent) {
                    controls.show();
                    if (player != null) {
                        player.setPlayWhenReady(false);
                        player.seekTo(0, player.getDuration());
                    }
                }
            });
            imaAdsLoader.addCallback(new VideoAdPlayer.VideoAdPlayerCallback() {
                @Override
                public void onPlay() {
                    isImaAd = true;
                    forwardRL.setVisibility(View.GONE);
                    backwardRL.setVisibility(View.GONE);
                    playPauseRL.setVisibility(View.GONE);
                    seekBarRL.setVisibility(View.GONE);
                }

                @Override
                public void onVolumeChanged(int i) {
                }

                @Override
                public void onPause() {
                    playPauseRL.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoaded() {
                }

                @Override
                public void onResume() {
                    playPauseRL.setVisibility(View.GONE);
                }

                @Override
                public void onEnded() {
                    playPauseRL.setVisibility(View.VISIBLE);
                    forwardRL.setVisibility(View.VISIBLE);
                    backwardRL.setVisibility(View.VISIBLE);
                    seekBarRL.setVisibility(View.VISIBLE);

                    controls.show();
                    player.setPlayWhenReady(false);
                    player.seekTo(0, player.getDuration());
                }

                @Override
                public void onError() {
                }

                @Override
                public void onBuffering() {
                }
            });
        }
    }
}