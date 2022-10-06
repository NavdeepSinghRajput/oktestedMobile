package com.oktested.videoCall.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.oktested.R;
import com.oktested.home.adapter.ViewAllFriendAdapter;
import com.oktested.home.model.FriendListResponse;
import com.oktested.home.model.Questions;
import com.oktested.home.model.QuizListModel;
import com.oktested.pickQuiz.ui.PickQuizActivity;
import com.oktested.playSolo.adapter.QuizOptionsAdapter;
import com.oktested.playSolo.model.InsertQuizQuestionResponse;
import com.oktested.playSolo.model.QuizStartResponse;
import com.oktested.quizDetail.model.InviteFriendToRoomResponse;
import com.oktested.quizDetail.model.QuizDetailResponse;
import com.oktested.quizResult.adapter.GroupQuizResultAdapter;
import com.oktested.quizResult.model.GroupQuizResultResponse;
import com.oktested.quizResult.ui.ShareGroupQuizResult;
import com.oktested.utils.AppConstants;
import com.oktested.utils.EndlessParentScrollListener;
import com.oktested.utils.Helper;
import com.oktested.videoCall.adapter.RecommendQuizAdapter;
import com.oktested.videoCall.model.AGEventHandler;
import com.oktested.videoCall.model.AcceptSuggestedQuizResponse;
import com.oktested.videoCall.model.CameraStatusResponse;
import com.oktested.videoCall.model.ChannelDetailResponse;
import com.oktested.videoCall.model.ConstantApp;
import com.oktested.videoCall.model.DuringCallEventHandler;
import com.oktested.videoCall.model.LeaveRoomResponse;
import com.oktested.videoCall.model.MemberInfoKeyValueData;
import com.oktested.videoCall.model.QuestionWiseResultKeyValueData;
import com.oktested.videoCall.model.QuestionWiseResultResponse;
import com.oktested.videoCall.model.RecommendedQuizResponse;
import com.oktested.videoCall.model.RemoveParticipantResponse;
import com.oktested.videoCall.presenter.CallPresenter;
import com.oktested.videoCall.propeller.Constant;
import com.oktested.videoCall.propeller.UserStatusData;
import com.oktested.videoCall.propeller.VideoInfoData;
import com.oktested.videoCall.propeller.ui.RecyclerItemClickListener;
import com.oktested.videoCall.ui.layout.GridVideoViewContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class CallActivity extends BaseActivity implements DuringCallEventHandler, CallView, View.OnClickListener {

    private Context context;
    public static final int LAYOUT_TYPE_DEFAULT = 0;
    public static final int LAYOUT_TYPE_SMALL = 1;

    // should only be modified under UI thread
    private final HashMap<Integer, SurfaceView> mUidsList = new HashMap<>(); // uid = 0 || uid == EngineConfig.mUid

    private GridVideoViewContainer mGridVideoViewContainer;
    private boolean mIsLandscape = false, isAudioEnable = true, isVideoEnable = true, isVideoRenderingHappenFirstTime = false;
    private volatile boolean mVideoMuted = false;
    private volatile boolean mAudioMuted = false;
    public int mLayoutType = LAYOUT_TYPE_DEFAULT;
    private volatile int mAudioRouting = Constants.AUDIO_ROUTE_DEFAULT;
    private LinearLayout videoControlsLL, indicatorLL, childLL;
    private RelativeLayout videoControlsRL, beforeRL, parentRL, videoViewRL, timerRL;
    private NestedScrollView afterSV;
    private ProgressBar timerProgressBar;
    private TextView pickQuizTV, startQuizTV, questionTV, timerTV, acceptTV;
    private CardView questionCV, questionImageCV;
    private boolean menuSelected = false, quizSelected = false, isAdmin, currentAnswerDisplay, dataInserted, fromSelectQuiz = false, isAllReady = false;
    private int position = 0, pos = 0, userChannelId, maxScore = 0;
    private CallPresenter callPresenter;
    private ArrayList<Questions> questionsAL = new ArrayList<>();
    private ArrayList<Questions> loadingQuestionsAL = new ArrayList<>();
    private String quizId, attemptedTime, channelName, adminName = "";
    private ObjectAnimator animation;
    private ImageView questionIV, loadingIV, voiceMuteIV, videoMuteIV, addFriendIV;
    private BroadcastReceiver mReceiver, quizSuggestionReceiver, quizSelectReceiver, readyStatusReceiver, enableCameraReceiver, leaveChannelReceiver, shiftAdminReceiver, rejectInvitationReceiver;
    private QuizOptionsAdapter quizOptionsAdapter;
    private AlertDialog quizSuggestionDialog;
    private RecyclerView optionsRV;

    // For add more friends bottom sheet
    private BroadcastReceiver friendReceiver;
    private RecyclerView viewFriendRoomList;
    private BottomSheetBehavior friendBottomSheetBehavior;
    private ViewAllFriendAdapter viewAllFriendAdapter;
    private boolean friendDataLoaded = true, refreshingData = true, hitApiAgain = false;
    private int friendsPage = 1;
    private String friendsType = "online", friendId;

    // For Result Overlay
    private ArrayList<QuizListModel> dataItemList = new ArrayList<>();
    private GroupQuizResultResponse quizResultResponse;
    private SpinKitView spinKitView;
    private LinearLayout parentLL, inflatedLL;
    private RelativeLayout oneRL, twoRL, resultRL, moreQuizRL;
    private TextView winnerTV;
    private ImageView backIV, shareIV;
    private String offset = "0";
    private RecommendQuizAdapter recommendQuizAdapter;
    private boolean dataLoaded = true;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private DefaultDataSourceFactory dataSourceFactory;

    // For building quiz
    private LinearLayout buildQuizLL;
    private ImageView buildQuizIV;
    private TextView buildTitleTV, buildMessageTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        inUi();

        viewAllFriendBottomSheetSetup();
        friendReceiverRegister();
        startQuizReceiverRegister();
        quizSuggestionReceiverRegister();
        quizSelectReceiverRegister();
        readyStatusReceiverRegister();
        enableCameraReceiverRegister();
        leaveChannelReceiver();
        shiftAdminReceiver();
        rejectInvitationReceiver();
        LocalBroadcastManager.getInstance(context).registerReceiver(quizSelectFromPickDialogReceiver, new IntentFilter(AppConstants.QUIZ_SELECTED));
        LocalBroadcastManager.getInstance(context).registerReceiver(removeParticipantReceiver, new IntentFilter(AppConstants.REMOVE_PARTICIPANT));
        LocalBroadcastManager.getInstance(context).registerReceiver(readyUnreadyReceiver, new IntentFilter(AppConstants.READY_UNREADY));

        if (Helper.isNetworkAvailable(context)) {
            callPresenter.callFriendListApi(friendsType, friendsPage);
        }
    }

    private void inUi() {
        context = this;
        callPresenter = new CallPresenter(context, this);

        quizId = getIntent().getExtras().getString("quizId");
        userChannelId = getIntent().getExtras().getInt("userChannelId");
        isAdmin = getIntent().getExtras().getBoolean("isAdmin");
        isAudioEnable = getIntent().getExtras().getBoolean("isAudioEnable");
        isVideoEnable = getIntent().getExtras().getBoolean("isVideoEnable");

        videoControlsLL = findViewById(R.id.videoControlsLL);
        videoControlsRL = findViewById(R.id.videoControlsRL);
        pickQuizTV = findViewById(R.id.pickQuizTV);
        startQuizTV = findViewById(R.id.startQuizTV);
        beforeRL = findViewById(R.id.beforeRL);
        afterSV = findViewById(R.id.afterSV);

        addFriendIV = findViewById(R.id.addFriendIV);
        voiceMuteIV = findViewById(R.id.voiceMuteIV);
        videoMuteIV = findViewById(R.id.videoMuteIV);
        videoViewRL = findViewById(R.id.videoViewRL);
        parentRL = findViewById(R.id.parentRL);
        timerRL = findViewById(R.id.timerRL);
        childLL = findViewById(R.id.childLL);
        indicatorLL = findViewById(R.id.indicatorLL);
        questionCV = findViewById(R.id.questionCV);
        questionImageCV = findViewById(R.id.questionImageCV);
        questionIV = findViewById(R.id.questionIV);
        loadingIV = findViewById(R.id.loadingIV);
        questionTV = findViewById(R.id.questionTV);
        timerTV = findViewById(R.id.timerTV);
        timerProgressBar = findViewById(R.id.timerProgressBar);
        optionsRV = findViewById(R.id.optionsRV);

        playerView = findViewById(R.id.playerView);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        player = new SimpleExoPlayer.Builder(context).build();
        playerView.setPlayer(player);

        videoControlsRL.setOnClickListener(this);
        pickQuizTV.setOnClickListener(this);
        startQuizTV.setOnClickListener(this);
        voiceMuteIV.setOnClickListener(this);
        videoMuteIV.setOnClickListener(this);
        addFriendIV.setOnClickListener(this);

        // For Result Overlay
        inflatedLL = findViewById(R.id.inflatedLL);
        resultRL = findViewById(R.id.resultRL);
        spinKitView = findViewById(R.id.spinKit);
        parentLL = findViewById(R.id.parentLL);
        backIV = findViewById(R.id.backIV);
        shareIV = findViewById(R.id.shareIV);
        winnerTV = findViewById(R.id.winnerTV);
        oneRL = findViewById(R.id.oneRL);
        twoRL = findViewById(R.id.twoRL);
        moreQuizRL = findViewById(R.id.moreQuizRL);

        buildQuizLL = findViewById(R.id.buildQuizLL);
        buildQuizIV = findViewById(R.id.buildQuizIV);
        buildTitleTV = findViewById(R.id.buildTitleTV);
        buildMessageTV = findViewById(R.id.buildMessageTV);

        NestedScrollView moreQuizNSV = findViewById(R.id.moreQuizNSV);
        RecyclerView moreQuizRV = findViewById(R.id.moreQuizRV);
        moreQuizRV.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(context, 3);
        moreQuizRV.setLayoutManager(mLayoutManager);
        moreQuizRV.setItemAnimator(new DefaultItemAnimator());
        recommendQuizAdapter = new RecommendQuizAdapter(context, new ArrayList<>(), new RecommendQuizAdapter.Quiz() {
            @Override
            public void recommendQuiz(String articleId) {
                Intent intent = new Intent(context, PickQuizActivity.class);
                intent.putExtra("articleId", articleId);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("channelName", channelName);
                startActivity(intent);
            }
        });
        moreQuizRV.setAdapter(recommendQuizAdapter);

        moreQuizNSV.setOnScrollChangeListener(new EndlessParentScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (dataItemList != null && dataItemList.size() >= 9) {
                    if (dataLoaded && Helper.isContainValue(offset) && !offset.equalsIgnoreCase("-1")) {
                        if (Helper.isNetworkAvailable(context)) {
                            dataLoaded = false;
                            callPresenter.callRecommendedQuizApi(quizId, offset);
                        }
                    }
                }
            }
        });

        backIV.setOnClickListener(this);
        shareIV.setOnClickListener(this);

        if (isAdmin) {
            addFriendIV.setVisibility(View.VISIBLE);
            startQuizTV.setVisibility(View.VISIBLE);
            startQuizTV.setEnabled(false);
            if (Helper.isContainValue(quizId)) {
                pickQuizTV.setText("Change quiz");
            } else {
                pickQuizTV.setText("Pick a quiz");
            }
        } else {
            startQuizTV.setVisibility(View.GONE);
            addFriendIV.setVisibility(View.GONE);
            if (Helper.isContainValue(quizId)) {
                pickQuizTV.setText("Change quiz");
            } else {
                pickQuizTV.setText("Suggest a quiz");
            }
        }
    }

    private void viewAllFriendBottomSheetSetup() {
        ConstraintLayout viewAllFrdBottomSheet = findViewById(R.id.viewAllFrdBottomSheet);
        viewFriendRoomList = findViewById(R.id.viewFriendRoomList);
        viewFriendRoomList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        viewFriendRoomList.setLayoutManager(linearLayoutManager);
        viewFriendRoomList.setItemAnimator(new DefaultItemAnimator());

        friendBottomSheetBehavior = BottomSheetBehavior.from(viewAllFrdBottomSheet);
        friendBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        friendBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset >= 0.8) {

                }
            }
        });

        ArrayList<FriendListResponse.FriendListModel> friendListModels = new ArrayList<>();
        viewAllFriendAdapter = new ViewAllFriendAdapter(context, friendListModels, new ViewAllFriendAdapter.Room() {
            @Override
            public void createRoom(String id) {
                friendId = id;
                if (Helper.isNetworkAvailable(context)) {
                    callPresenter.inviteFriendToRoomApi(quizId, friendId, channelName);
                }
            }

            @Override
            public void removeFriend(String friendId, int position) {

            }
        }, false);
        viewFriendRoomList.setAdapter(viewAllFriendAdapter);
        viewFriendRoomList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // only when scrolling up
                    final int visibleThreshold = 4;
                    int lastItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = linearLayoutManager.getItemCount();
                    if (currentTotalCount <= lastItem + visibleThreshold && friendDataLoaded && friendsPage != -1) {
                        if (Helper.isNetworkAvailable(context)) {
                            friendDataLoaded = false;
                            callPresenter.callFriendListApi(friendsType, friendsPage);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void setFriendListResponse(FriendListResponse friendListResponse) {
        if (friendListResponse != null && friendListResponse.data != null && friendListResponse.data.size() > 0) {
            viewAllFriendAdapter.clearAll();
            friendDataLoaded = true;
            friendsPage = friendListResponse.next_page;
            friendsType = friendListResponse.type;
            viewAllFriendAdapter.notifyItem(friendListResponse.data);
            if (hitApiAgain) {
                hitApiAgain = false;
                friendDataLoaded = true;
                friendsType = "online";
                friendsPage = 1;
                callPresenter.callFriendListApi(friendsType, friendsPage);
            } else {
                hitApiAgain = false;
                refreshingData = true;
            }
        }
    }

    @Override
    public void setInviteFriendToRoomResponse(InviteFriendToRoomResponse inviteFriendToRoomResponse) {
        if (inviteFriendToRoomResponse != null && Helper.isContainValue(inviteFriendToRoomResponse.status)) {
            if (inviteFriendToRoomResponse.status.equalsIgnoreCase("1")) {
                if (Helper.isContainValue(inviteFriendToRoomResponse.notification_msg)) {
                    showMessage(inviteFriendToRoomResponse.notification_msg);
                }

            } else {
                if (Helper.isContainValue(inviteFriendToRoomResponse.err)) {
                    showMessage(inviteFriendToRoomResponse.err);
                }
            }
        }
    }

    private void friendReceiverRegister() {
        friendReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                friendDataLoaded = true;
                friendsType = "online";
                friendsPage = 1;
                if (refreshingData) {
                    callPresenter.callFriendListApi(friendsType, friendsPage);
                    refreshingData = false;
                } else {
                    hitApiAgain = true;
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "user.refresh");
        context.registerReceiver(friendReceiver, intentFilter);
    }

    private void startQuizReceiverRegister() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pos = 0;
                loadingQuestionsAL.clear();
                for (int i = 0; i < questionsAL.size(); i++) {
                    if (questionsAL.get(i).question != null && Helper.isContainValue(questionsAL.get(i).question.type)) {
                        if (questionsAL.get(i).question.type.equalsIgnoreCase("text_image")) {
                            if (Helper.isContainValue(questionsAL.get(i).question.image)) {
                                loadingQuestionsAL.add(questionsAL.get(i));
                            }
                        } else if (questionsAL.get(i).question.type.equalsIgnoreCase("image")) {
                            if (Helper.isContainValue(questionsAL.get(i).question.image)) {
                                loadingQuestionsAL.add(questionsAL.get(i));
                            }
                        }
                    }
                }

                if (loadingQuestionsAL != null && loadingQuestionsAL.size() > 0) {
                    buildingQuizDialog();
                    loadImages();
                } else {
                    playStartVideo();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "start.quiz");
        context.registerReceiver(mReceiver, intentFilter);
    }

    private void quizSuggestionReceiverRegister() {
        quizSuggestionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() != null) {
                    String quizFeatureImage = intent.getExtras().getString("quizFeatureImage");
                    String quizTitle = intent.getExtras().getString("quizTitle");
                    String participantName = intent.getExtras().getString("participantName");
                    String participantId = intent.getExtras().getString("participantId");
                    String quizId = intent.getExtras().getString("quizId");
                    String quizCategory = intent.getExtras().getString("quizCategory");

                    displayQuizSuggestionDialog(quizFeatureImage, quizTitle, participantName, quizId, quizCategory, participantId);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "participant.suggestion");
        context.registerReceiver(quizSuggestionReceiver, intentFilter);
    }

    private void quizSelectReceiverRegister() {
        quizSelectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() != null) {
                    adminName = intent.getExtras().getString("adminName");
                    String id = intent.getExtras().getString("quizId");

                    if (Helper.isContainValue(id)) {
                        quizId = id;
                        fromSelectQuiz = true;
                        callPresenter.callQuizDetailApi(quizId, channelName);
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "admin.select");
        context.registerReceiver(quizSelectReceiver, intentFilter);
    }

    private void readyStatusReceiverRegister() {
        readyStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Helper.isNetworkAvailable(context)) {
                    callPresenter.getChannelDetailApi(channelName);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "ready.status");
        context.registerReceiver(readyStatusReceiver, intentFilter);
    }

    private void enableCameraReceiverRegister() {
        enableCameraReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Helper.isNetworkAvailable(context)) {
                    callPresenter.getChannelDetailApi(channelName);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "enable.camera");
        context.registerReceiver(enableCameraReceiver, intentFilter);
    }

    private void leaveChannelReceiver() {
        leaveChannelReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showMessage("You have been removed from the room by the host");
                finish();
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "remove.participant");
        context.registerReceiver(leaveChannelReceiver, intentFilter);
    }

    private void shiftAdminReceiver() {
        shiftAdminReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() != null) {
                    String name = intent.getExtras().getString("adminName");
                    if (Helper.isContainValue(name)) {
                        shiftAdminControlDialog(name);
                    }

                    isAdmin = true;
                    addFriendIV.setVisibility(View.VISIBLE);
                    startQuizTV.setVisibility(View.VISIBLE);
                    startQuizTV.setEnabled(false);

                    if (Helper.isContainValue(quizId)) {
                        pickQuizTV.setText("Change quiz");
                    } else {
                        pickQuizTV.setText("Pick a quiz");
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "shift.admin");
        context.registerReceiver(shiftAdminReceiver, intentFilter);
    }

    private void rejectInvitationReceiver() {
        rejectInvitationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() != null) {
                    String name = intent.getExtras().getString("participantName");
                    if (Helper.isContainValue(name)) {
                        showMessage("Your invitation have been rejected by " + name);
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "reject.invitation");
        context.registerReceiver(rejectInvitationReceiver, intentFilter);
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
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
        }
        if (friendReceiver != null) {
            context.unregisterReceiver(friendReceiver);
        }
        if (quizSuggestionReceiver != null) {
            context.unregisterReceiver(quizSuggestionReceiver);
        }
        if (quizSelectReceiver != null) {
            context.unregisterReceiver(quizSelectReceiver);
        }
        if (readyStatusReceiver != null) {
            context.unregisterReceiver(readyStatusReceiver);
        }
        if (enableCameraReceiver != null) {
            context.unregisterReceiver(enableCameraReceiver);
        }
        if (leaveChannelReceiver != null) {
            context.unregisterReceiver(leaveChannelReceiver);
        }
        if (shiftAdminReceiver != null) {
            context.unregisterReceiver(shiftAdminReceiver);
        }
        if (rejectInvitationReceiver != null) {
            context.unregisterReceiver(rejectInvitationReceiver);
        }
        if (quizSelectFromPickDialogReceiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(quizSelectFromPickDialogReceiver);
        }
        if (removeParticipantReceiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(removeParticipantReceiver);
        }
        if (readyUnreadyReceiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(readyUnreadyReceiver);
        }
        callPresenter.onDestroyedView();

        if (playerView != null && player != null) {
            player.release();
            player = null;
        }
        super.onDestroy();
    }

    @Override
    protected void initUIAndEvent() {
        addEventHandler(this);
        channelName = getIntent().getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME);
        String encryptionKey = getIntent().getStringExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY);
        String accessToken = getIntent().getStringExtra(ConstantApp.ACTION_KEY_ACCESS_TOKEN);

        String encryptionMode = getResources().getStringArray(R.array.encryption_mode_values)[vSettings().mEncryptionModeIndex];
        doConfigEngine(encryptionKey, encryptionMode);

        mGridVideoViewContainer = findViewById(R.id.grid_video_view_container);
        mGridVideoViewContainer.setItemEventHandler(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //  onBigVideoViewClicked(view, position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

            @Override
            public void onItemDoubleClick(View view, int position) {
                // onBigVideoViewDoubleClicked(view, position);
            }
        });

        SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
        preview(true, surfaceV, userChannelId);
        surfaceV.setZOrderOnTop(false);
        surfaceV.setZOrderMediaOverlay(false);

        mUidsList.put(userChannelId, surfaceV); // get first surface view
        mGridVideoViewContainer.initViewContainer(this, userChannelId, mUidsList, mIsLandscape, quizSelected); // first is now full view

        joinChannel(channelName, userChannelId, accessToken);
        optional();

        if (!isAudioEnable) {
            onVoiceMuteClicked();
        }

        if (!isVideoEnable) {
            videoMuteIV.setSelected(true);
            onVideoMuteClicked();
            //  new Handler().postDelayed(this::onVideoMuteClicked, 100);
        }

        if (Helper.isContainValue(quizId)) {
            callPresenter.callQuizDetailApi(quizId, channelName);
        }
        if (Helper.isNetworkAvailable(context)) {
            callPresenter.getChannelDetailApi(channelName);
        }
    }

    @Override
    public void setQuizDetailResponse(QuizDetailResponse quizDetailResponse) {
        if (quizDetailResponse != null && quizDetailResponse.data != null && quizDetailResponse.data.post_json_content != null) {
            if (quizDetailResponse.data.post_json_content.questions.size() != 0) {
                questionsAL = quizDetailResponse.data.post_json_content.questions;

                if (isAdmin && isVideoRenderingHappenFirstTime) {
                    if (isAllReady && Helper.isContainValue(quizId)) {
                        startQuizTV.setEnabled(true);
                        startQuizTV.setTextColor(context.getResources().getColor(R.color.colorAccent));
                        startQuizTV.setBackground(context.getResources().getDrawable(R.drawable.rounded_pink_bgcolor));
                    } else {
                        startQuizTV.setEnabled(false);
                        startQuizTV.setTextColor(context.getResources().getColor(R.color.colorLightGrey));
                        startQuizTV.setBackground(context.getResources().getDrawable(R.drawable.start_bg));
                    }
                }

                if (fromSelectQuiz) {
                    fromSelectQuiz = false;
                    String title = quizDetailResponse.data.title;
                    String featureImage = quizDetailResponse.data.feature_img_1x1;
                    String categoryName = quizDetailResponse.data.cat_display.get(0).category_display;
                    displayQuizSelectDialog(title, featureImage, categoryName);
                }
            }
        }
    }

    private void doConfigEngine(String encryptionKey, String encryptionMode) {
        VideoEncoderConfiguration.VideoDimensions videoDimension = ConstantApp.VIDEO_DIMENSIONS[getVideoEncResolutionIndex()];
        VideoEncoderConfiguration.FRAME_RATE videoFps = ConstantApp.VIDEO_FPS[getVideoEncFpsIndex()];
        configEngine(videoDimension, videoFps, encryptionKey, encryptionMode);
    }

    private int getVideoEncResolutionIndex() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int videoEncResolutionIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_RESOLUTION, ConstantApp.DEFAULT_VIDEO_ENC_RESOLUTION_IDX);
        if (videoEncResolutionIndex > ConstantApp.VIDEO_DIMENSIONS.length - 1) {
            videoEncResolutionIndex = ConstantApp.DEFAULT_VIDEO_ENC_RESOLUTION_IDX;

            // save the new value
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_RESOLUTION, videoEncResolutionIndex);
            editor.apply();
        }
        return videoEncResolutionIndex;
    }

    private int getVideoEncFpsIndex() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int videoEncFpsIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_FPS, ConstantApp.DEFAULT_VIDEO_ENC_FPS_IDX);
        if (videoEncFpsIndex > ConstantApp.VIDEO_FPS.length - 1) {
            videoEncFpsIndex = ConstantApp.DEFAULT_VIDEO_ENC_FPS_IDX;

            // save the new value
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_FPS, videoEncFpsIndex);
            editor.apply();
        }
        return videoEncFpsIndex;
    }

    private void optional() {
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    public void onSwitchCameraClicked(View view) {
        RtcEngine rtcEngine = rtcEngine();
        // Switches between front and rear cameras.
        rtcEngine.switchCamera();
    }

    public void onSwitchSpeakerClicked(View view) {
        RtcEngine rtcEngine = rtcEngine();
        /*
          Enables/Disables the audio playback route to the speakerphone.
          This method sets whether the audio is routed to the speakerphone or earpiece.
          After calling this method, the SDK returns the onAudioRouteChanged callback
          to indicate the changes.
         */
        rtcEngine.setEnableSpeakerphone(mAudioRouting != Constants.AUDIO_ROUTE_SPEAKERPHONE);
    }

    @Override
    protected void deInitUIAndEvent() {
        doLeaveChannel();
        removeEventHandler(this);
        mUidsList.clear();
    }

    private void doLeaveChannel() {
        leaveChannel(config().mChannel);
        preview(false, null, userChannelId);
    }

    public void onHangupClicked(View view) {
        finish();
    }

    public void onVideoMuteClicked() {
     /*   if (mUidsList.size() == 0) {
            return;
        }
        SurfaceView surfaceV = getLocalView();
        ViewParent parent;
        if (surfaceV == null || (parent = surfaceV.getParent()) == null) {
            return;
        }*/

        if (!isVideoEnable) {
            //  doHideTargetView(userChannelId, true);
            videoMuteIV.setColorFilter(ContextCompat.getColor(context, R.color.colorButtonDisable));
        } else {
            //  doHideTargetView(userChannelId, false);
            videoMuteIV.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
        }
    }

/*    public void onVideoMuteClicked() {
        if (mUidsList.size() == 0) {
            return;
        }
        SurfaceView surfaceV = getLocalView();
        ViewParent parent;
        if (surfaceV == null || (parent = surfaceV.getParent()) == null) {
            return;
        }

        RtcEngine rtcEngine = rtcEngine();
        mVideoMuted = !mVideoMuted;

        if (mVideoMuted) {
            rtcEngine.muteLocalVideoStream(true);
        } else {
            rtcEngine.muteLocalVideoStream(false);
        }

        if (mVideoMuted) {
            videoMuteIV.setColorFilter(ContextCompat.getColor(context, R.color.colorButtonDisable));
        } else {
            videoMuteIV.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
        }
        hideLocalView(mVideoMuted);
    }*/

    private SurfaceView getLocalView() {
        for (HashMap.Entry<Integer, SurfaceView> entry : mUidsList.entrySet()) {
            if (entry.getKey() == 0 || entry.getKey() == config().mUid) {
                return entry.getValue();
            }
        }
        return null;
    }

   /* private void hideLocalView(boolean hide) {
        int uid = config().mUid;
        doHideTargetView(uid, hide);
    }*/

    private void doHideTargetView(int targetUid, boolean hide) {
        HashMap<Integer, Integer> status = new HashMap<>();
        status.put(targetUid, hide ? UserStatusData.VIDEO_MUTED : UserStatusData.DEFAULT_STATUS);
        if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
            mGridVideoViewContainer.notifyUiChanged(mUidsList, targetUid, status, null);
        } else if (mLayoutType == LAYOUT_TYPE_SMALL) {
            UserStatusData bigBgUser = mGridVideoViewContainer.getItem(0);
            if (bigBgUser.mUid == targetUid) { // big background is target view
                mGridVideoViewContainer.notifyUiChanged(mUidsList, targetUid, status, null);
            }
         /*   else { // find target view in small video view list
                mSmallVideoViewAdapter.notifyUiChanged(mUidsList, bigBgUser.mUid, status, null);
            }*/
        }
    }

    public void onVoiceMuteClicked() {
        if (mUidsList.size() == 0) {
            return;
        }

        RtcEngine rtcEngine = rtcEngine();
        rtcEngine.muteLocalAudioStream(mAudioMuted = !mAudioMuted);
        // ImageView iv = (ImageView) view;
        if (mAudioMuted) {
            voiceMuteIV.setColorFilter(ContextCompat.getColor(context, R.color.colorButtonDisable));
        } else {
            voiceMuteIV.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
        }
        //   iv.setImageResource(mAudioMuted ? R.drawable.btn_microphone_off : R.drawable.btn_microphone);
    }

    @Override
    public void onUserJoined(int uid) {
        Log.d("hello", "onUserJoined");
        doRenderRemoteUi(uid);
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        Log.d("hello", "onFirstRemoteVideoDecoded");
    }

    private void doRenderRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                if (mUidsList.containsKey(uid)) {
                    return;
                }

                /*
                  Creates the video renderer view.
                  CreateRendererView returns the SurfaceView type. The operation and layout of the
                  view are managed by the app, and the Agora SDK renders the view provided by the
                  app. The video display view must be created using this method instead of
                  directly calling SurfaceView.
                 */
                SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
                mUidsList.put(uid, surfaceV);

                boolean useDefaultLayout = mLayoutType == LAYOUT_TYPE_DEFAULT;

                surfaceV.setZOrderOnTop(true);
                surfaceV.setZOrderMediaOverlay(true);

                /*
                  Initializes the video view of a remote user.
                  This method initializes the video view of a remote stream on the local device. It affects only the video view that the local user sees.
                  Call this method to bind the remote video stream to a video view and to set the rendering and mirror modes of the video view.
                 */
                rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));

                if (useDefaultLayout) {
                    switchToDefaultVideoView();
                }

                /*else {
                    int bigBgUid = mSmallVideoViewAdapter == null ? uid : mSmallVideoViewAdapter.getExceptedUid();
                    switchToSmallVideoView(bigBgUid);
                }*/
            }
        });
    }

    private void switchToDefaultVideoView() {
        mGridVideoViewContainer.initViewContainer(this, config().mUid, mUidsList, mIsLandscape, quizSelected);
        mLayoutType = LAYOUT_TYPE_DEFAULT;
        boolean setRemoteUserPriorityFlag = false;
        int sizeLimit = mUidsList.size();
        if (sizeLimit > ConstantApp.MAX_PEER_COUNT + 1) {
            sizeLimit = ConstantApp.MAX_PEER_COUNT + 1;
        }
        for (int i = 0; i < sizeLimit; i++) {
            int uid = mGridVideoViewContainer.getItem(i).mUid;
            if (config().mUid != uid) {
                if (!setRemoteUserPriorityFlag) {
                    setRemoteUserPriorityFlag = true;
                    rtcEngine().setRemoteUserPriority(uid, Constants.USER_PRIORITY_HIGH);
                } else {
                    rtcEngine().setRemoteUserPriority(uid, Constants.USER_PRIORITY_NORMAL);
                }
            }
        }

        if (Helper.isNetworkAvailable(context)) {
            callPresenter.getChannelDetailApi(channelName);
        }

    /*    if (!isVideoEnable) {
            videoDisableRL.setVisibility(View.VISIBLE);

            new Handler().postDelayed(() -> {
                doHideTargetView(userChannelId, true);
                rtcEngine().muteLocalVideoStream(true);
            }, 500);

            new Handler().postDelayed(() -> {
                videoDisableRL.setVisibility(View.GONE);
            }, 1200);
        }*/
        isVideoRenderingHappenFirstTime = true;
    }

    @Override
    public void setChannelDetailResponse(ChannelDetailResponse channelDetailResponse) {
        if (channelDetailResponse != null && Helper.isContainValue(channelDetailResponse.status) && channelDetailResponse.status.equalsIgnoreCase("1") && channelDetailResponse.data != null) {
            if (channelDetailResponse.data.members_info != null && channelDetailResponse.data.members_info.size() > 0) {
                HashMap<Integer, MemberInfoKeyValueData> memberInfoKeyValueDataHashMap = new HashMap<>();
                for (int i = 0; i < channelDetailResponse.data.members_info.size(); i++) {
                    MemberInfoKeyValueData memberInfoKeyValueData = new MemberInfoKeyValueData();
                    memberInfoKeyValueData.is_ready = channelDetailResponse.data.members_info.get(i).is_ready;
                    memberInfoKeyValueData.name = channelDetailResponse.data.members_info.get(i).name;
                    memberInfoKeyValueData.uid = channelDetailResponse.data.members_info.get(i).uid;
                    memberInfoKeyValueData.user_id = channelDetailResponse.data.members_info.get(i).user_id;
                    memberInfoKeyValueData.profile_pic = channelDetailResponse.data.members_info.get(i).profile_pic;
                    memberInfoKeyValueData.is_channel_admin = channelDetailResponse.data.members_info.get(i).is_channel_admin;
                    memberInfoKeyValueData.is_camera_enable = channelDetailResponse.data.members_info.get(i).is_camera_enable;
                    memberInfoKeyValueDataHashMap.put(channelDetailResponse.data.members_info.get(i).uid, memberInfoKeyValueData);
                }
                mGridVideoViewContainer.notifyMemberInfo(channelDetailResponse.data, memberInfoKeyValueDataHashMap);
            }

            if (isAdmin && isVideoRenderingHappenFirstTime) {
                isAllReady = channelDetailResponse.data.is_all_ready;
                if (channelDetailResponse.data.is_all_ready && Helper.isContainValue(quizId)) {
                    startQuizTV.setEnabled(true);
                    startQuizTV.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    startQuizTV.setBackground(context.getResources().getDrawable(R.drawable.rounded_pink_bgcolor));
                } else {
                    startQuizTV.setEnabled(false);
                    startQuizTV.setTextColor(context.getResources().getColor(R.color.colorLightGrey));
                    startQuizTV.setBackground(context.getResources().getDrawable(R.drawable.start_bg));
                }
            }
        }
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.d("hello", "onJoinChannelSuccess");
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        Log.d("hello", "onUserOffline");
        doRemoveRemoteUi(uid);
    }

    private void doRemoveRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                Object target = mUidsList.remove(uid);
                if (target == null) {
                    return;
                }

                int bigBgUid = -1;
              /*  if (mSmallVideoViewAdapter != null) {
                    bigBgUid = mSmallVideoViewAdapter.getExceptedUid();
                }*/

                if (mLayoutType == LAYOUT_TYPE_DEFAULT || uid == bigBgUid) {
                    switchToDefaultVideoView();
                }
                /*else {
                    switchToSmallVideoView(bigBgUid);
                }*/
            }
        });
    }

    @Override
    public void onExtraCallback(int type, Object... data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                doHandleExtraCallback(type, data);
            }
        });
    }

    private void doHandleExtraCallback(int type, Object... data) {
        int peerUid;
        boolean muted;

        switch (type) {
            case AGEventHandler.EVENT_TYPE_ON_USER_AUDIO_MUTED:
                peerUid = (Integer) data[0];
                muted = (boolean) data[1];

                if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
                    HashMap<Integer, Integer> status = new HashMap<>();
                    status.put(peerUid, muted ? UserStatusData.AUDIO_MUTED : UserStatusData.DEFAULT_STATUS);
                    mGridVideoViewContainer.notifyUiChanged(mUidsList, config().mUid, status, null);
                }
                break;

            case AGEventHandler.EVENT_TYPE_ON_USER_VIDEO_MUTED:
                peerUid = (Integer) data[0];
                muted = (boolean) data[1];
                doHideTargetView(peerUid, muted);
                break;

            case AGEventHandler.EVENT_TYPE_ON_USER_VIDEO_STATS:
                IRtcEngineEventHandler.RemoteVideoStats stats = (IRtcEngineEventHandler.RemoteVideoStats) data[0];

                if (Constant.SHOW_VIDEO_INFO) {
                    if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
                        mGridVideoViewContainer.addVideoInfo(stats.uid, new VideoInfoData(stats.width, stats.height, stats.delay, stats.rendererOutputFrameRate, stats.receivedBitrate));
                        int uid = config().mUid;
                        int profileIndex = getVideoEncResolutionIndex();
                        String resolution = getResources().getStringArray(R.array.string_array_resolutions)[profileIndex];
                        String fps = getResources().getStringArray(R.array.string_array_frame_rate)[profileIndex];

                        String[] rwh = resolution.split("x");
                        int width = Integer.valueOf(rwh[0]);
                        int height = Integer.valueOf(rwh[1]);

                        mGridVideoViewContainer.addVideoInfo(uid, new VideoInfoData(width > height ? width : height,
                                width > height ? height : width,
                                0, Integer.valueOf(fps), Integer.valueOf(0)));
                    }
                } else {
                    mGridVideoViewContainer.cleanVideoInfo();
                }
                break;

            case AGEventHandler.EVENT_TYPE_ON_SPEAKER_STATS:
                IRtcEngineEventHandler.AudioVolumeInfo[] infos = (IRtcEngineEventHandler.AudioVolumeInfo[]) data[0];

                if (infos.length == 1 && infos[0].uid == 0) { // local guy, ignore it
                    break;
                }

                if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
                    HashMap<Integer, Integer> volume = new HashMap<>();

                    for (IRtcEngineEventHandler.AudioVolumeInfo each : infos) {
                        peerUid = each.uid;
                        int peerVolume = each.volume;

                        if (peerUid == 0) {
                            continue;
                        }
                        volume.put(peerUid, peerVolume);
                    }
                    mGridVideoViewContainer.notifyUiChanged(mUidsList, config().mUid, null, volume);
                }
                break;

  /*          case AGEventHandler.EVENT_TYPE_ON_APP_ERROR:
                int subType = (int) data[0];
                if (subType == ConstantApp.AppError.NO_CONNECTION_ERROR) {
                    String msg = getString(R.string.msg_connection_error);
                    notifyMessageChanged(new Message(new User(0, null), msg));
                    showLongToast(msg);
                }
                break;

            case AGEventHandler.EVENT_TYPE_ON_DATA_CHANNEL_MSG:
                peerUid = (Integer) data[0];
                final byte[] content = (byte[]) data[1];
                notifyMessageChanged(new Message(new User(peerUid, String.valueOf(peerUid)), new String(content)));
                break;

            case AGEventHandler.EVENT_TYPE_ON_AGORA_MEDIA_ERROR: {
                int error = (int) data[0];
                String description = (String) data[1];
                notifyMessageChanged(new Message(new User(0, null), error + " " + description));
                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_AUDIO_ROUTE_CHANGED:
                notifyHeadsetPlugged((int) data[0]);
                break;*/
        }
    }

/*    public void notifyHeadsetPlugged(final int routing) {
        mAudioRouting = routing;
        ImageView iv = (ImageView) findViewById(R.id.switch_speaker_id);
        if (mAudioRouting == Constants.AUDIO_ROUTE_SPEAKERPHONE) {
            iv.setImageResource(R.drawable.btn_speaker);
        } else {
            iv.setImageResource(R.drawable.btn_speaker_off);
        }
    }*/

    @Override
    public void onBackPressed() {
        showDialogOnBackPress();
    }

    private void showDialogOnBackPress() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.leave_room_dialog, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);

        TextView messageTV = dialogView.findViewById(R.id.messageTV);
        TextView yesTV = dialogView.findViewById(R.id.yesTV);
        TextView cancelTV = dialogView.findViewById(R.id.cancelTV);

        messageTV.setText("Are you sure you want to leave the room ?");

        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        yesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callLeaveRoomApi();
                if (animation != null) {
                    animation.removeAllListeners();
                }
                alertDialog.dismiss();
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.videoControlsRL:
                if (menuSelected) {
                    menuSelected = false;
                    videoControlsLL.setVisibility(View.GONE);
                    videoControlsRL.setBackground(getResources().getDrawable(R.drawable.video_unselect_menu_bg));
                } else {
                    menuSelected = true;
                    videoControlsLL.setVisibility(View.VISIBLE);
                    videoControlsRL.setBackground(getResources().getDrawable(R.drawable.video_select_menu_bg));
                }
                break;

            case R.id.addFriendIV:
                menuSelected = false;
                videoControlsLL.setVisibility(View.GONE);
                videoControlsRL.setBackground(getResources().getDrawable(R.drawable.video_unselect_menu_bg));

                friendBottomSheetBehaviour();
                break;

            case R.id.startQuizTV:
                callPlayQuiz();
                break;

            case R.id.pickQuizTV:
                Intent intent = new Intent(context, PickQuizActivity.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("channelName", channelName);
                startActivity(intent);
                break;

            case R.id.voiceMuteIV:
                onVoiceMuteClicked();
                break;

            case R.id.videoMuteIV:
                //   if (!isVideoRenderingHappenFirstTime) {
                videoMuteIV.setEnabled(false);
                if (videoMuteIV.isSelected()) {
                    videoMuteIV.setSelected(false);
                    isVideoEnable = true;
                    onVideoMuteClicked();
                    if (Helper.isNetworkAvailable(context)) {
                        if (mUidsList.size() > 1) {
                            callPresenter.changeCameraStatusMultipleUserApi(channelName, "enable");
                        } else {
                            callPresenter.changeCameraStatusSingleUserApi(channelName, "enable");
                        }
                    }
                } else {
                    videoMuteIV.setSelected(true);
                    isVideoEnable = false;
                    onVideoMuteClicked();
                    if (Helper.isNetworkAvailable(context)) {
                        if (mUidsList.size() > 1) {
                            callPresenter.changeCameraStatusMultipleUserApi(channelName, "disable");
                        } else {
                            callPresenter.changeCameraStatusSingleUserApi(channelName, "disable");
                        }
                    }
                }
               /* } else {
                    if (videoMuteIV.isSelected()) {
                        videoMuteIV.setSelected(false);
                        isVideoEnable = true;
                        onVideoMuteClicked();
                        rtcEngine().muteLocalVideoStream(false);
                    } else {
                        videoMuteIV.setSelected(true);
                        isVideoEnable = false;
                        onVideoMuteClicked();
                        rtcEngine().muteLocalVideoStream(true);
                    }
                }*/
                break;

            case R.id.backIV:
                resultRL.setVisibility(View.GONE);
                break;

            case R.id.shareIV:
                if (checkSelfPermission()) {
                    new ShareGroupQuizResult(context, inflatedLL, quizResultResponse, quizId, maxScore);
                }
                break;

            default:
                break;
        }
    }

    private boolean checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new ShareGroupQuizResult(context, inflatedLL, quizResultResponse, quizId, maxScore);
            } else {
                showAlertDialogForDenyPermission();
            }
        }
    }

    private void showAlertDialogForDenyPermission() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        AlertDialog alertDialog = dialog.create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Permission Denied");
        alertDialog.setMessage("Without this permission the app is unable to share result with your friends, so please allow the permission");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void friendBottomSheetBehaviour() {
        if (friendBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            friendBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            friendBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    private void callPlayQuiz() {
        if (Helper.isNetworkAvailable(context)) {
            startQuizTV.setEnabled(false);
            callPresenter.callPlayQuizApi(quizId, channelName);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void callInsertQuizDataApi(String answer, String choiceId) {
        if (Helper.isNetworkAvailable(context)) {
            callPresenter.insertQuizDataApi(quizId, questionsAL.get(position).question.id, choiceId, answer, String.valueOf(questionsAL.get(position).score), attemptedTime, channelName);
        }
    }

    private void callLeaveRoomApi() {
        if (Helper.isNetworkAvailable(context)) {
            callPresenter.leaveRoomApi(channelName);
        }
    }

    private void callQuestionWiseResultApi() {
        if (Helper.isNetworkAvailable(context)) {
            callPresenter.callQuestionWiseResult(channelName, quizId, questionsAL.get(position).question.id);
        }
    }

    @Override
    public void setPlayGroupResponse(QuizStartResponse quizStartResponse) {
        startQuizTV.setEnabled(true);
        if (quizStartResponse != null && Helper.isContainValue(quizStartResponse.status)) {
            if (quizStartResponse.status.equalsIgnoreCase("1")) {
                pos = 0;
                loadingQuestionsAL.clear();
                for (int i = 0; i < questionsAL.size(); i++) {
                    if (questionsAL.get(i).question != null && Helper.isContainValue(questionsAL.get(i).question.type)) {
                        if (questionsAL.get(i).question.type.equalsIgnoreCase("text_image")) {
                            if (Helper.isContainValue(questionsAL.get(i).question.image)) {
                                loadingQuestionsAL.add(questionsAL.get(i));
                            }
                        } else if (questionsAL.get(i).question.type.equalsIgnoreCase("image")) {
                            if (Helper.isContainValue(questionsAL.get(i).question.image)) {
                                loadingQuestionsAL.add(questionsAL.get(i));
                            }
                        }
                    }
                }

                if (loadingQuestionsAL != null && loadingQuestionsAL.size() > 0) {
                    buildingQuizDialog();
                    loadImages();
                } else {
                    playStartVideo();
                }
            } else {
                if (Helper.isContainValue(quizStartResponse.err)) {
                    showMessage(quizStartResponse.err);
                }
            }
        }
    }

    @Override
    public void insertQuizDataResponse(InsertQuizQuestionResponse insertQuizQuestionResponse) {
        if (insertQuizQuestionResponse != null && Helper.isContainValue(insertQuizQuestionResponse.status) && insertQuizQuestionResponse.status.equalsIgnoreCase("1")) {
            if ((Integer.parseInt(attemptedTime) < (questionsAL.get(position).timer - 4)) || currentAnswerDisplay) {
                callQuestionWiseResultApi();
            }
        }
    }

    @Override
    public void setLeaveRoomResponse(LeaveRoomResponse leaveRoomResponse) {

    }

    @Override
    public void setQuestionWiseResultResponse(QuestionWiseResultResponse questionWiseResultResponse) {
        if (questionWiseResultResponse != null && Helper.isContainValue(questionWiseResultResponse.status) && questionWiseResultResponse.status.equalsIgnoreCase("1")) {
            if (!currentAnswerDisplay) {
                if (questionWiseResultResponse.all_played) {
                    if (questionWiseResultResponse.data != null && questionWiseResultResponse.data.size() > 0) {
                        currentAnswerDisplay = true;
                        quizOptionsAdapter.notifyAdapter();

                        HashMap<Integer, QuestionWiseResultKeyValueData> hashMap = new HashMap<>();
                        for (int i = 0; i < questionWiseResultResponse.data.size(); i++) {
                            QuestionWiseResultKeyValueData keyValueData = new QuestionWiseResultKeyValueData();
                            keyValueData.current_qus_status = questionWiseResultResponse.data.get(i).current_qus_status;
                            keyValueData.total_score = questionWiseResultResponse.data.get(i).total_score;
                            keyValueData.status = questionWiseResultResponse.data.get(i).status;

                            hashMap.put(questionWiseResultResponse.data.get(i).user_channel_id, keyValueData);
                        }

                        // Play sound bases on correct and wrong answer
                        QuestionWiseResultKeyValueData keyValueData = hashMap.get(userChannelId);
                        if (keyValueData != null) {
                            if (keyValueData.current_qus_status.equalsIgnoreCase("correct")) {
                                playCorrectAnswerSound();
                            } else {
                                playWrongAnswerSound();
                            }
                        }

                        // Display red-green square boxes
                        mGridVideoViewContainer.notifyVideoGrid(true, false, hashMap);

                        new Handler().postDelayed(() -> {
                            // Display score in pink boxes
                            mGridVideoViewContainer.notifyVideoGrid(true, true, hashMap);
                        }, 2000);

                        new Handler().postDelayed(() -> {
                            if (animation != null) {
                                animation.removeAllListeners();
                            }

                            if (position < questionsAL.size() - 1) {
                                // Set the previous indicator value based on correct or wrong answer
                                QuestionWiseResultKeyValueData valueData = hashMap.get(userChannelId);
                                if (valueData != null) {
                                    if (valueData.current_qus_status.equalsIgnoreCase("correct")) {
                                        indicatorLL.getChildAt(position).setBackground(context.getResources().getDrawable(R.drawable.correct_indicator));
                                    } else {
                                        indicatorLL.getChildAt(position).setBackground(context.getResources().getDrawable(R.drawable.wrong_indicator));
                                    }
                                }

                                position++;
                                setQuizQuestionUi();

                                // hide red-green box and score both
                                mGridVideoViewContainer.notifyVideoGrid(false, false, hashMap);
                            } else {
                                // Open Result
                                if (animation != null) {
                                    animation.removeAllListeners();
                                }

                                afterSV.setVisibility(View.GONE);
                                childLL.removeView(videoViewRL);
                                parentRL.addView(videoViewRL);
                                parentRL.addView(beforeRL);
                                parentRL.addView(resultRL);
                                mGridVideoViewContainer.setPadding(0, 0, 0, 0);
                                quizSelected = false;
                                mGridVideoViewContainer.initViewContainer(CallActivity.this, config().mUid, mUidsList, mIsLandscape, quizSelected);
                                mGridVideoViewContainer.notifyVideoGrid(false, false, hashMap);

                              /*  rtcEngine().muteLocalVideoStream(false);
                                if (!isVideoEnable) {
                                    videoDisableRL.setVisibility(View.VISIBLE);

                                    new Handler().postDelayed(() -> {
                                        doHideTargetView(userChannelId, true);
                                        rtcEngine().muteLocalVideoStream(true);
                                    }, 500);

                                    new Handler().postDelayed(() -> {
                                        videoDisableRL.setVisibility(View.GONE);
                                    }, 1000);
                                }*/

                                resultRL.setVisibility(View.VISIBLE);
                                pickQuizTV.setVisibility(View.VISIBLE);
                                if (isAdmin) {
                                    pickQuizTV.setText("Pick a quiz");
                                } else {
                                    pickQuizTV.setText("Suggest a quiz");
                                }

                                if (isAdmin) {
                                    startQuizTV.setEnabled(false);
                                    startQuizTV.setTextColor(context.getResources().getColor(R.color.colorLightGrey));
                                    startQuizTV.setBackground(context.getResources().getDrawable(R.drawable.start_bg));
                                }

                                callGroupQuizResult();
                            }
                        }, 4000);
                    }
                } else {
                    if (Integer.parseInt(attemptedTime) < (questionsAL.get(position).timer - 4)) {
                        callQuestionWiseResultApi();
                    }
                }
            } else {
                if (questionWiseResultResponse.data != null && questionWiseResultResponse.data.size() > 0) {
                    quizOptionsAdapter.notifyAdapter();

                    HashMap<Integer, QuestionWiseResultKeyValueData> hashMap = new HashMap<>();
                    for (int i = 0; i < questionWiseResultResponse.data.size(); i++) {
                        QuestionWiseResultKeyValueData keyValueData = new QuestionWiseResultKeyValueData();
                        keyValueData.current_qus_status = questionWiseResultResponse.data.get(i).current_qus_status;
                        keyValueData.total_score = questionWiseResultResponse.data.get(i).total_score;
                        keyValueData.status = questionWiseResultResponse.data.get(i).status;

                        hashMap.put(questionWiseResultResponse.data.get(i).user_channel_id, keyValueData);
                    }

                    // Play sound bases on correct and wrong answer
                    QuestionWiseResultKeyValueData keyValueData = hashMap.get(userChannelId);
                    if (keyValueData != null) {
                        if (keyValueData.current_qus_status.equalsIgnoreCase("correct")) {
                            playCorrectAnswerSound();
                        } else {
                            playWrongAnswerSound();
                        }
                    }

                    // Display red-green square boxes
                    mGridVideoViewContainer.notifyVideoGrid(true, false, hashMap);

                    new Handler().postDelayed(() -> {
                        // Display score in pink boxes
                        mGridVideoViewContainer.notifyVideoGrid(true, true, hashMap);
                    }, 2000);

                    new Handler().postDelayed(() -> {
                        if (animation != null) {
                            animation.removeAllListeners();
                        }

                        if (position < questionsAL.size() - 1) {
                            // Set the previous indicator value based on correct or wrong answer
                            QuestionWiseResultKeyValueData valueData = hashMap.get(userChannelId);
                            if (valueData != null) {
                                if (valueData.current_qus_status.equalsIgnoreCase("correct")) {
                                    indicatorLL.getChildAt(position).setBackground(context.getResources().getDrawable(R.drawable.correct_indicator));
                                } else {
                                    indicatorLL.getChildAt(position).setBackground(context.getResources().getDrawable(R.drawable.wrong_indicator));
                                }
                            }

                            position++;
                            setQuizQuestionUi();

                            // hide red-green box and score both
                            mGridVideoViewContainer.notifyVideoGrid(false, false, hashMap);
                        } else {
                            // Open Result
                            if (animation != null) {
                                animation.removeAllListeners();
                            }

                            afterSV.setVisibility(View.GONE);
                            childLL.removeView(videoViewRL);
                            parentRL.addView(videoViewRL);
                            parentRL.addView(beforeRL);
                            parentRL.addView(resultRL);
                            mGridVideoViewContainer.setPadding(0, 0, 0, 0);
                            quizSelected = false;
                            mGridVideoViewContainer.initViewContainer(CallActivity.this, config().mUid, mUidsList, mIsLandscape, quizSelected);
                            mGridVideoViewContainer.notifyVideoGrid(false, false, hashMap);

                           /* rtcEngine().muteLocalVideoStream(false);
                            if (!isVideoEnable) {
                                videoDisableRL.setVisibility(View.VISIBLE);

                                new Handler().postDelayed(() -> {
                                    doHideTargetView(userChannelId, true);
                                    rtcEngine().muteLocalVideoStream(true);
                                }, 500);

                                new Handler().postDelayed(() -> {
                                    videoDisableRL.setVisibility(View.GONE);
                                }, 1000);
                            }*/

                            resultRL.setVisibility(View.VISIBLE);
                            pickQuizTV.setVisibility(View.VISIBLE);
                            if (isAdmin) {
                                pickQuizTV.setText("Pick a quiz");
                            } else {
                                pickQuizTV.setText("Suggest a quiz");
                            }

                            if (isAdmin) {
                                startQuizTV.setEnabled(false);
                                startQuizTV.setTextColor(context.getResources().getColor(R.color.colorLightGrey));
                                startQuizTV.setBackground(context.getResources().getDrawable(R.drawable.start_bg));
                            }

                            callGroupQuizResult();
                        }
                    }, 4000);
                }
            }
        }
    }

    private void callGroupQuizResult() {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            callPresenter.callGroupQuizResultApi(quizId, channelName);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void setQuizQuestionUi() {
        if (questionsAL.get(position).question != null && Helper.isContainValue(questionsAL.get(position).question.type)) {
            if (questionsAL.get(position).question.type.equalsIgnoreCase("text")) {
                questionImageCV.setVisibility(View.GONE);
                questionCV.setVisibility(View.VISIBLE);

                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) questionCV.getLayoutParams();
                layoutParams.setMargins((int) Helper.pxFromDp(context, 30), 0, (int) Helper.pxFromDp(context, 30), 0);
                questionCV.requestLayout();

                childLL.setVisibility(View.VISIBLE);
                if (Helper.isContainValue(questionsAL.get(position).question.text)) {
                    questionTV.setText(Html.fromHtml(questionsAL.get(position).question.text));
                }
                maxScore = maxScore + questionsAL.get(position).score;

                setQuizDisplayAnimation();
                setOptionsAdapter();
            } else if (questionsAL.get(position).question.type.equalsIgnoreCase("text_image")) {
                questionImageCV.setVisibility(View.VISIBLE);
                questionCV.setVisibility(View.VISIBLE);

                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) questionCV.getLayoutParams();
                layoutParams.setMargins((int) Helper.pxFromDp(context, 30), -50, (int) Helper.pxFromDp(context, 30), 0);
                questionCV.requestLayout();

                childLL.setVisibility(View.VISIBLE);
                if (Helper.isContainValue(questionsAL.get(position).question.text)) {
                    questionTV.setText(Html.fromHtml(questionsAL.get(position).question.text));
                }
                maxScore = maxScore + questionsAL.get(position).score;

                if (Helper.isContainValue(questionsAL.get(position).question.image)) {
                    Glide.with(context)
                            .load(questionsAL.get(position).question.image)
                            .placeholder(R.drawable.quiz_placeholder)
                            .into(questionIV);
                }

                setQuizDisplayAnimation();
                setOptionsAdapter();
            } else if (questionsAL.get(position).question.type.equalsIgnoreCase("image")) {
                questionImageCV.setVisibility(View.VISIBLE);
                questionCV.setVisibility(View.GONE);

                childLL.setVisibility(View.VISIBLE);
                if (Helper.isContainValue(questionsAL.get(position).question.image)) {
                    Glide.with(context)
                            .load(questionsAL.get(position).question.image)
                            .placeholder(R.drawable.quiz_placeholder)
                            .into(questionIV);
                }
                maxScore = maxScore + questionsAL.get(position).score;

                setQuizDisplayAnimation();
                setOptionsAdapter();
            }
        }
    }

    private void setOptionsAdapter() {
        currentAnswerDisplay = false;
        dataInserted = false;

        optionsRV.setHasFixedSize(true);
        optionsRV.setLayoutManager(new LinearLayoutManager(context));
        optionsRV.setItemAnimator(new DefaultItemAnimator());
        quizOptionsAdapter = new QuizOptionsAdapter(context, questionsAL.get(position).choices, questionsAL.get(position).correct_ans, false, new QuizOptionsAdapter.Quiz() {
            @Override
            public void submitQuiz(boolean isCorrect, String answer, String choiceId, boolean optionSelect) {
                dataInserted = true;
                callInsertQuizDataApi(answer, choiceId);
                if (optionSelect) {
                    playOptionSelectSound();
                }
            }
        });
        optionsRV.setAdapter(quizOptionsAdapter);

        // Move the indicator position one by one
        indicatorLL.getChildAt(position).setBackground(context.getResources().getDrawable(R.drawable.select_indicator));

        if (Helper.isContainValue(String.valueOf(questionsAL.get(position).timer))) {
            timerTV.setText(questionsAL.get(position).timer + "");

            animation = ObjectAnimator.ofInt(timerProgressBar, "progress", 0, 100);
            animation.setDuration(questionsAL.get(position).timer * 1000);
            animation.setInterpolator(new LinearInterpolator());
            animation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (!dataInserted) {
                        quizOptionsAdapter.timerComplete(false, -1);
                    } else {
                        callQuestionWiseResultApi();
                    }
                    currentAnswerDisplay = true;
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });

            ValueAnimator animator = ValueAnimator.ofInt(questionsAL.get(position).timer, 0);
            animator.setDuration(questionsAL.get(position).timer * 1000);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    timerTV.setText(animation.getAnimatedValue().toString());
                    if (questionsAL.get(position).timer == (int) animation.getAnimatedValue()) {
                        attemptedTime = String.valueOf(questionsAL.get(position).timer - ((int) animation.getAnimatedValue() - 1));
                    } else {
                        attemptedTime = String.valueOf(questionsAL.get(position).timer - (int) animation.getAnimatedValue());
                    }
                }
            });

            animator.start();
            animation.start();
        }
    }

    @Override
    public void setGroupQuizResultResponse(GroupQuizResultResponse groupQuizResultResponse) {
        if (groupQuizResultResponse != null && Helper.isContainValue(groupQuizResultResponse.status)) {
            if (groupQuizResultResponse.status.equalsIgnoreCase("1")) {
                if (groupQuizResultResponse.data != null && groupQuizResultResponse.data.size() > 0) {
                    quizResultResponse = groupQuizResultResponse;
                    parentLL.setVisibility(View.VISIBLE);
                    playResultDisplaySound();

                    if (groupQuizResultResponse.data.size() == 1) {
                        winnerTV.setText("WINNER");

                        // visible only one relative layout
                        twoRL.setVisibility(View.GONE);
                        setOneWinnerLayout(groupQuizResultResponse.data);
                    } else if (groupQuizResultResponse.data.size() == 2) {
                        if (Helper.isContainValue(String.valueOf(groupQuizResultResponse.winner_count))) {
                            if (groupQuizResultResponse.winner_count == 1) {
                                winnerTV.setText("WINNER");

                                // visible only one relative layout and adapter
                                twoRL.setVisibility(View.GONE);
                                setOneWinnerLayout(groupQuizResultResponse.data);

                                ArrayList<GroupQuizResultResponse.GroupQuizResultDataResponse> dataAL = new ArrayList<>();
                                dataAL.add(groupQuizResultResponse.data.get(1));
                                setAdapter(dataAL);
                            } else {
                                winnerTV.setText("WINNERS");

                                // visible both one and two relative layout
                                setOneWinnerLayout(groupQuizResultResponse.data);
                                setTwoWinnerLayout(groupQuizResultResponse.data);
                            }
                        }
                    } else {
                        if (Helper.isContainValue(String.valueOf(groupQuizResultResponse.winner_count))) {
                            if (groupQuizResultResponse.winner_count == 1) {
                                winnerTV.setText("WINNER");

                                // visible only one relative layout and adapter
                                twoRL.setVisibility(View.GONE);
                                setOneWinnerLayout(groupQuizResultResponse.data);

                                ArrayList<GroupQuizResultResponse.GroupQuizResultDataResponse> dataAL = new ArrayList<>();
                                for (int i = 1; i < groupQuizResultResponse.data.size(); i++) {
                                    dataAL.add(groupQuizResultResponse.data.get(i));
                                }
                                setAdapter(dataAL);
                            } else if (groupQuizResultResponse.winner_count == 2) {
                                winnerTV.setText("WINNERS");

                                // visible both one and two relative layout and adapter too
                                setOneWinnerLayout(groupQuizResultResponse.data);
                                setTwoWinnerLayout(groupQuizResultResponse.data);

                                ArrayList<GroupQuizResultResponse.GroupQuizResultDataResponse> dataAL = new ArrayList<>();
                                for (int i = 2; i < groupQuizResultResponse.data.size(); i++) {
                                    dataAL.add(groupQuizResultResponse.data.get(i));
                                }
                                setAdapter(dataAL);
                            } else {
                                winnerTV.setVisibility(View.GONE);

                                oneRL.setVisibility(View.GONE);
                                twoRL.setVisibility(View.GONE);
                                setAdapter(groupQuizResultResponse.data);
                            }
                        }
                    }

                    if (Helper.isNetworkAvailable(context)) {
                        offset = "0";
                        recommendQuizAdapter.clearAll();
                        callPresenter.callRecommendedQuizApi(quizId, offset);
                    }
                }
            } else {
                if (Helper.isContainValue(groupQuizResultResponse.err)) {
                    showMessage(groupQuizResultResponse.err);
                }
            }
        }
    }

    @Override
    public void setRecommendedQuizResponse(RecommendedQuizResponse recommendedQuizResponse) {
        if (recommendedQuizResponse != null && recommendedQuizResponse.data != null && recommendedQuizResponse.data.size() > 0) {
            moreQuizRL.setVisibility(View.VISIBLE);
            dataLoaded = true;
            dataItemList.clear();
            dataItemList = recommendedQuizResponse.data;
            recommendQuizAdapter.setMoreVideosData(dataItemList);
            offset = recommendedQuizResponse.next_offset;
        }
    }

    private void setOneWinnerLayout(ArrayList<GroupQuizResultResponse.GroupQuizResultDataResponse> dataAL) {
        TextView oneNameTV = findViewById(R.id.oneNameTV);
        ImageView oneIV = findViewById(R.id.oneIV);
        TextView oneRankTV = findViewById(R.id.oneRankTV);
        RatingBar oneRatingBar = findViewById(R.id.oneRatingBar);
        TextView oneScoreTV = findViewById(R.id.oneScoreTV);
        TextView oneTimeTV = findViewById(R.id.oneTimeTV);

        if (Helper.isContainValue(dataAL.get(0).user_pic)) {
            Glide.with(context)
                    .load(dataAL.get(0).user_pic)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(oneIV);
        }

        if (Helper.isContainValue(String.valueOf(dataAL.get(0).rank))) {
            oneRankTV.setText(dataAL.get(0).rank + "");
        }

        if (Helper.isContainValue(String.valueOf(dataAL.get(0).user_name))) {
            oneNameTV.setText(dataAL.get(0).user_name);
        }

        if (Helper.isContainValue(String.valueOf(dataAL.get(0).avg_time))) {
            oneTimeTV.setText(dataAL.get(0).avg_time + " sec/que");
        }

        if (Helper.isContainValue(String.valueOf(dataAL.get(0).total_score))) {
            oneScoreTV.setText(dataAL.get(0).total_score + "/" + maxScore);
            oneRatingBar.setNumStars(maxScore);
            oneRatingBar.setMax(maxScore);
            oneRatingBar.setRating(dataAL.get(0).total_score);
        }
    }

    private void setTwoWinnerLayout(ArrayList<GroupQuizResultResponse.GroupQuizResultDataResponse> dataAL) {
        TextView twoNameTV = findViewById(R.id.twoNameTV);
        ImageView twoIV = findViewById(R.id.twoIV);
        TextView twoRankTV = findViewById(R.id.twoRankTV);
        RatingBar twoRatingBar = findViewById(R.id.twoRatingBar);
        TextView twoScoreTV = findViewById(R.id.twoScoreTV);
        TextView twoTimeTV = findViewById(R.id.twoTimeTV);

        if (Helper.isContainValue(dataAL.get(1).user_pic)) {
            Glide.with(context)
                    .load(dataAL.get(1).user_pic)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(twoIV);
        }

        if (Helper.isContainValue(String.valueOf(dataAL.get(1).rank))) {
            twoRankTV.setText(dataAL.get(1).rank + "");
        }

        if (Helper.isContainValue(String.valueOf(dataAL.get(1).user_name))) {
            twoNameTV.setText(dataAL.get(1).user_name);
        }

        if (Helper.isContainValue(String.valueOf(dataAL.get(1).avg_time))) {
            twoTimeTV.setText(dataAL.get(1).avg_time + " sec/que");
        }

        if (Helper.isContainValue(String.valueOf(dataAL.get(1).total_score))) {
            twoScoreTV.setText(dataAL.get(1).total_score + "/" + maxScore);
            twoRatingBar.setNumStars(maxScore);
            twoRatingBar.setMax(maxScore);
            twoRatingBar.setRating(dataAL.get(1).total_score);
        }
    }

    private void setAdapter(ArrayList<GroupQuizResultResponse.GroupQuizResultDataResponse> dataAL) {
        RecyclerView resultRV = findViewById(R.id.resultRV);
        resultRV.setNestedScrollingEnabled(false);
        resultRV.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        resultRV.setLayoutManager(mLayoutManager);
        resultRV.setItemAnimator(new DefaultItemAnimator());

        GroupQuizResultAdapter groupQuizResultAdapter = new GroupQuizResultAdapter(context, dataAL, maxScore);
        resultRV.setAdapter(groupQuizResultAdapter);
    }

    private void displayQuizSuggestionDialog(String quizFeatureImage, String quizTitle, String participantName, String suggestedQuizId, String quizCategory, String participantId) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.quiz_suggestion_dialog, null);
        dialogBuilder.setView(dialogView);
        quizSuggestionDialog = dialogBuilder.create();
        quizSuggestionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        quizSuggestionDialog.show();
        quizSuggestionDialog.setCancelable(false);

        TextView quizCategoryTV = dialogView.findViewById(R.id.quizCategoryTV);
        ImageView quizIV = dialogView.findViewById(R.id.quizIV);
        TextView quizTitleTV = dialogView.findViewById(R.id.quizTitleTV);
        TextView participantNameTV = dialogView.findViewById(R.id.participantNameTV);
        acceptTV = dialogView.findViewById(R.id.acceptTV);
        TextView pickAnotherQuizTV = dialogView.findViewById(R.id.pickAnotherQuizTV);

        if (Helper.isContainValue(quizTitle)) {
            quizTitleTV.setText(quizTitle);
        }
        if (Helper.isContainValue(participantName)) {
            String text = "from <font color=#32FFCE>" + participantName + "</font>";
            participantNameTV.setText(Html.fromHtml(text));
        }
        if (Helper.isContainValue(quizCategory)) {
            quizCategoryTV.setText(quizCategory);
        }
        if (Helper.isContainValue(quizFeatureImage)) {
            Glide.with(context)
                    .load(quizFeatureImage)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(quizIV);
        }

        pickAnotherQuizTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quizSuggestionDialog.dismiss();
                Intent intent = new Intent(context, PickQuizActivity.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("channelName", channelName);
                startActivity(intent);
            }
        });

        acceptTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.isNetworkAvailable(context)) {
                    quizId = suggestedQuizId;
                    acceptTV.setEnabled(false);
                    callPresenter.callAcceptSuggestedQuizApi(suggestedQuizId, channelName, participantId);
                }
            }
        });
    }

    private void displayQuizSelectDialog(String title, String featureImage, String categoryName) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.quiz_select_dialog, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);

        TextView quizCategoryTV = dialogView.findViewById(R.id.quizCategoryTV);
        ImageView quizIV = dialogView.findViewById(R.id.quizIV);
        TextView quizTitleTV = dialogView.findViewById(R.id.quizTitleTV);
        TextView adminNameTV = dialogView.findViewById(R.id.adminNameTV);
        TextView okTV = dialogView.findViewById(R.id.okTV);

        if (Helper.isContainValue(adminName)) {
            String text = "<font color=#32FFCE>" + adminName + "</font> has picked";
            adminNameTV.setText(Html.fromHtml(text));
        }
        if (Helper.isContainValue(title)) {
            quizTitleTV.setText(title);
        }
        if (Helper.isContainValue(categoryName)) {
            quizCategoryTV.setText(categoryName);
        }
        if (Helper.isContainValue(featureImage)) {
            Glide.with(context)
                    .load(featureImage)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(quizIV);
        }

        okTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        new Handler().postDelayed(alertDialog::dismiss, 3000);
    }

    @Override
    public void setAcceptSuggestedQuizResponse(AcceptSuggestedQuizResponse acceptSuggestedQuizResponse) {
        acceptTV.setEnabled(true);
        if (acceptSuggestedQuizResponse != null && Helper.isContainValue(acceptSuggestedQuizResponse.status)) {
            if (acceptSuggestedQuizResponse.status.equalsIgnoreCase("1")) {
                if (Helper.isContainValue(acceptSuggestedQuizResponse.message)) {
                    showMessage(acceptSuggestedQuizResponse.message);
                }

                quizSuggestionDialog.dismiss();
                if (Helper.isContainValue(quizId)) {
                    callPresenter.callQuizDetailApi(quizId, channelName);
                }
            } else {
                if (Helper.isContainValue(acceptSuggestedQuizResponse.err)) {
                    showMessage(acceptSuggestedQuizResponse.err);
                }
            }
        }
    }

    private BroadcastReceiver quizSelectFromPickDialogReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null) {
                String id = intent.getExtras().getString("quizId");
                if (Helper.isContainValue(id)) {
                    quizId = id;
                    callPresenter.callQuizDetailApi(quizId, channelName);
                }
            }
        }
    };

    private BroadcastReceiver removeParticipantReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null) {
                String participantName = intent.getExtras().getString("participantName");
                String participantId = intent.getExtras().getString("participantId");

                displayRemoveParticipantDialog(participantName, participantId);
            }
        }
    };

    private BroadcastReceiver readyUnreadyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null) {
                String action = intent.getExtras().getString("action");
                if (Helper.isContainValue(action)) {
                    callPresenter.callReadyUnreadyApi(action, channelName);
                }
            }
        }
    };

    private void displayRemoveParticipantDialog(String participantName, String participantId) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.leave_room_dialog, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);

        TextView messageTV = dialogView.findViewById(R.id.messageTV);
        TextView yesTV = dialogView.findViewById(R.id.yesTV);
        TextView cancelTV = dialogView.findViewById(R.id.cancelTV);

        if (Helper.isContainValue(participantName)) {
            messageTV.setText("Are you sure you want to remove " + participantName + " from the room");
        }

        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        yesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                if (Helper.isContainValue(participantId)) {
                    callPresenter.callRemoveParticipantApi(participantId, channelName);
                }
            }
        });
    }

    @Override
    public void setRemoveParticipantResponse(RemoveParticipantResponse removeParticipantResponse) {
        if (removeParticipantResponse != null && Helper.isContainValue(removeParticipantResponse.status)) {
            if (removeParticipantResponse.status.equalsIgnoreCase("1")) {
                if (Helper.isContainValue(removeParticipantResponse.message)) {
                    showMessage(removeParticipantResponse.message);
                }
            } else {
                if (Helper.isContainValue(removeParticipantResponse.err)) {
                    showMessage(removeParticipantResponse.err);
                }
            }
        }
    }

    @Override
    public void setCameraStatusResponse(CameraStatusResponse cameraStatusResponse) {
        if (cameraStatusResponse != null && Helper.isContainValue(cameraStatusResponse.status)) {
            videoMuteIV.setEnabled(true);
            if (cameraStatusResponse.status.equalsIgnoreCase("1")) {
                if (Helper.isNetworkAvailable(context)) {
                    callPresenter.getChannelDetailApi(channelName);
                }
            } else {
                if (Helper.isContainValue(cameraStatusResponse.err)) {
                    showMessage(cameraStatusResponse.err);
                }
            }
        }
    }

    private void shiftAdminControlDialog(String name) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.shift_admin_dialog, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);

        TextView messageTV = dialogView.findViewById(R.id.messageTV);
        TextView okTV = dialogView.findViewById(R.id.okTV);

        if (Helper.isContainValue(name)) {
            messageTV.setText(name + " left the room\nYou are the new host");
        }

        okTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        new Handler().postDelayed(alertDialog::dismiss, 3000);
    }

    private void buildingQuizDialog() {
        buildQuizLL.setVisibility(View.VISIBLE);
        Random r = new Random();
        int number = r.nextInt(5 - 1) + 1;

        if (number == 1) {
            Glide.with(context)
                    .load(R.raw.building_quiz_one)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(buildQuizIV);

            buildTitleTV.setText("Okayyy...");
            buildMessageTV.setText("Your quiz is\nalmost ready");
        } else if (number == 2) {
            Glide.with(context)
                    .load(R.raw.building_quiz_two)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(buildQuizIV);

            buildTitleTV.setText("Take a moment");
            buildMessageTV.setText("We are building\nyour quiz");
        } else if (number == 3) {
            Glide.with(context)
                    .load(R.raw.building_quiz_three)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(buildQuizIV);

            buildTitleTV.setText("We hope you\nare excited");
            buildMessageTV.setText("Your quiz is\nalmost ready");
        } else {
            Glide.with(context)
                    .load(R.raw.building_quiz_four)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(buildQuizIV);

            buildTitleTV.setText("Get ready!");
            buildMessageTV.setText("We are building\nyour quiz");
        }
    }

    private void playStartVideo() {
        final boolean[] videoFinish = {false};
        playerView.setVisibility(View.VISIBLE);
        // Produces DataSource instances through which media data is loaded.
        dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getResources().getString(R.string.app_name)));

        // This is the MediaSource representing the media to be played.
        MediaSource firstSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(RawResourceDataSource.buildRawResourceUri(R.raw.start_quiz));

        // Prepare the player with the source.
        player.prepare(firstSource);
        player.setPlayWhenReady(true);

        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED && !videoFinish[0]) {
                    videoFinish[0] = true;
                    setLayoutAfterStartQuiz();
                }
            }
        });
    }

    private void setLayoutAfterStartQuiz() {
        afterSV.setVisibility(View.VISIBLE);
        indicatorLL.removeAllViews();
        position = 0;
        maxScore = 0;
        for (int i = 0; i < questionsAL.size(); i++) {
            TextView textView = new TextView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) Helper.pxFromDp(context, 15), (int) Helper.pxFromDp(context, 6));
            layoutParams.setMargins(0, 0, (int) Helper.pxFromDp(context, 3), 0);
            textView.setLayoutParams(layoutParams);
            textView.setBackground(context.getResources().getDrawable(R.drawable.unselect_indicator));
            indicatorLL.addView(textView);
        }
        setQuizQuestionUi();
        parentRL.removeView(videoViewRL);
        parentRL.removeView(beforeRL);
        parentRL.removeView(resultRL);

        childLL.addView(videoViewRL);
        mGridVideoViewContainer.setPadding((int) Helper.pxFromDp(context, 15), 0, (int) Helper.pxFromDp(context, 5), 0);
        quizSelected = true;
        mGridVideoViewContainer.initViewContainer(this, config().mUid, mUidsList, mIsLandscape, quizSelected);

        playerView.setVisibility(View.GONE);
      /*  rtcEngine().muteLocalVideoStream(false);
        if (!isVideoEnable) {
            new Handler().postDelayed(() -> {
                doHideTargetView(userChannelId, true);
                rtcEngine().muteLocalVideoStream(true);
                playerView.setVisibility(View.GONE);
            }, 500);
        } else {
            playerView.setVisibility(View.GONE);
        }*/
    }

    private void loadImages() {
        if (loadingQuestionsAL.get(pos).question != null && Helper.isContainValue(loadingQuestionsAL.get(pos).question.type)) {
            if (loadingQuestionsAL.get(pos).question.type.equalsIgnoreCase("text_image")) {
                if (Helper.isContainValue(loadingQuestionsAL.get(pos).question.image)) {
                    Glide.with(context)
                            .load(loadingQuestionsAL.get(pos).question.image)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    new Handler().postDelayed(() -> {
                                        pos = pos + 1;
                                        if (pos < loadingQuestionsAL.size()) {
                                            loadImages();
                                        } else {
                                            new Handler().postDelayed(() -> {
                                                buildQuizLL.setVisibility(View.GONE);
                                                playStartVideo();
                                            }, 1000);
                                        }
                                    }, 100);
                                    return false;
                                }
                            })
                            .into(loadingIV);
                }
            } else if (loadingQuestionsAL.get(pos).question.type.equalsIgnoreCase("image")) {
                if (Helper.isContainValue(loadingQuestionsAL.get(pos).question.image)) {
                    Glide.with(context)
                            .load(loadingQuestionsAL.get(pos).question.image)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    new Handler().postDelayed(() -> {
                                        pos = pos + 1;
                                        if (pos < loadingQuestionsAL.size()) {
                                            loadImages();
                                        } else {
                                            new Handler().postDelayed(() -> {
                                                buildQuizLL.setVisibility(View.GONE);
                                                playStartVideo();
                                            }, 1000);
                                        }
                                    }, 100);
                                    return false;
                                }
                            })
                            .into(loadingIV);
                }
            }
        }
    }

    private void setQuizDisplayAnimation() {
        YoYo.with(Techniques.ZoomInDown).duration(600).repeat(0).playOn(indicatorLL);
        YoYo.with(Techniques.ZoomInDown).duration(600).repeat(0).playOn(questionCV);
        YoYo.with(Techniques.ZoomInDown).duration(600).repeat(0).playOn(questionImageCV);

        YoYo.with(Techniques.ZoomInUp).duration(600).repeat(0).playOn(optionsRV);
        YoYo.with(Techniques.ZoomInUp).duration(600).repeat(0).playOn(timerRL);
        YoYo.with(Techniques.ZoomInUp).duration(600).repeat(0).playOn(videoViewRL);

        // Play quiz display music file
        MediaSource firstSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(RawResourceDataSource.buildRawResourceUri(R.raw.quiz_display_sound));
        player.prepare(firstSource);
        player.setPlayWhenReady(true);
    }

    private void playOptionSelectSound() {
        MediaSource firstSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(RawResourceDataSource.buildRawResourceUri(R.raw.option_select_sound));
        player.prepare(firstSource);
        player.setPlayWhenReady(true);
    }

    private void playWrongAnswerSound() {
        MediaSource firstSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(RawResourceDataSource.buildRawResourceUri(R.raw.wrong_answer_sound));
        player.prepare(firstSource);
        player.setPlayWhenReady(true);
    }

    private void playCorrectAnswerSound() {
        MediaSource firstSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(RawResourceDataSource.buildRawResourceUri(R.raw.correct_answer_sound));
        player.prepare(firstSource);
        player.setPlayWhenReady(true);
    }

    private void playResultDisplaySound() {
        YoYo.with(Techniques.BounceIn).duration(600).repeat(0).playOn(parentLL);

        MediaSource firstSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(RawResourceDataSource.buildRawResourceUri(R.raw.quiz_display_sound));
        player.prepare(firstSource);
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isVideoRenderingHappenFirstTime && isVideoEnable) {
            rtcEngine().disableVideo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isVideoRenderingHappenFirstTime && isVideoEnable) {
            rtcEngine().enableVideo();
        }
    }
}