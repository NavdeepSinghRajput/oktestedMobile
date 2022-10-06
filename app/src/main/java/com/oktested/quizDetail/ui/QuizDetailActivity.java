package com.oktested.quizDetail.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.oktested.R;
import com.oktested.home.adapter.AddNewFriendAdapter;
import com.oktested.home.adapter.QuizFriendAdapter;
import com.oktested.home.adapter.ViewAllFriendAdapter;
import com.oktested.home.async.ContactFetching;
import com.oktested.home.async.ContactFileInterface;
import com.oktested.home.model.AcceptFriendResponse;
import com.oktested.home.model.ContactFileLinkResponse;
import com.oktested.home.model.ContactFileUploadResponse;
import com.oktested.home.model.FriendListResponse;
import com.oktested.home.model.GenerateInviteLinkResponse;
import com.oktested.home.model.Questions;
import com.oktested.home.model.SendFriendRequestResponse;
import com.oktested.home.model.UnFriendResponse;
import com.oktested.home.model.UserContactListResponse;
import com.oktested.home.ui.FriendsActionInterface;
import com.oktested.playSolo.ui.PlaySoloActivity;
import com.oktested.quizDetail.model.InviteFriendToRoomResponse;
import com.oktested.quizDetail.model.QuizDetailResponse;
import com.oktested.quizDetail.presenter.QuizDetailPresenter;
import com.oktested.roomDatabase.AppDatabase;
import com.oktested.roomDatabase.ContactService;
import com.oktested.roomDatabase.model.ContactModel;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.DataHolder;
import com.oktested.utils.Helper;
import com.oktested.videoCall.model.CameraStatusResponse;
import com.oktested.videoCall.model.ConstantApp;
import com.oktested.videoCall.ui.CallActivity;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuizDetailActivity extends AppCompatActivity implements QuizDetailView, View.OnClickListener, FriendsActionInterface {

    private QuizDetailPresenter quizDetailPresenter;
    private ImageView quizCoverPic;
    private TextView quizCategory, quizTitle, questionLength, timePerQuestion, questionPoint;
    private RelativeLayout parentRL;
    private SpinKitView spinKitView;
    private Context context;
    private ArrayList<Questions> questionsAL;
    private String articleId, friendId, categorySlug = "";
    private RelativeLayout addFriendLayout;
    private LinearLayout quizFriendRoom;
    private QuizFriendAdapter quizFriendAdapter;
    private BottomSheetBehavior bottomSheetBehavior, contactBottomSheetBehavior, friendBottomSheetBehavior;
    private RecyclerView addNewFriendList;
    private AddNewFriendAdapter addNewFriendAdapter;
    private String offset = "0", friendsType = "online";
    private int friendsPage = 1;
    private boolean dataLoaded = true, friendDataLoaded = true, isVideoEnable = true, searchdataLoaded = true, isAudioEnable = true;
    private String searchPage = "1";
    private JSONArray jsonArray = new JSONArray();
    private RecyclerView viewFriendRoomList;
    private ViewAllFriendAdapter viewAllFriendAdapter;
    private TextView viewAllFriend;
    private BroadcastReceiver mReceiver;
    private String type = "add", searchType = "add", searchText = "";
    private boolean refresingData = true, hitApiAgain = false;
    private BottomSheetDialog contactBottomDialog, viewAllBottomDialog;
    private AlertDialog contactSyncAlert;
    //    private RecyclerView searchFriendList;
    private Boolean refreshcontactData = false;
    //    private SeacrhAddNewFriendAdapter seacrhAddNewFriendAdapter;
    private List<ContactModel> allContactList = new ArrayList<>();
    private AppDatabase database;
    private String agoraAccessToken, channelId;
    private int userChannelId;
    private boolean isChannelAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_detail);
        context = this;

        inUi();
        database = AppDatabase.getAppDatabase(context);
        setFriendAdapter();
        articleId = getIntent().getStringExtra("articleId");
        callQuizDetail(articleId);

        settingContactBottomSheet();
        settingViewAllFriendBottomSheet();

        receiverRegister();
    }

    private void receiverRegister() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                friendDataLoaded = true;
                friendsType = "online";
                friendsPage = 1;
        /*        if (refresingData) {
                    refresingData = false;*/
                quizDetailPresenter.callFriendListApi(friendsType, friendsPage);
          /*      } else {
                    hitApiAgain = true;
                }*/
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "user.refresh");
        context.registerReceiver(mReceiver, intentFilter);
    }

    private void sendLinkViaSma(String link, String mobileNumber) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("address", mobileNumber);
        sendIntent.putExtra("sms_body", "Play Quizzes, Watch Videos, Challenge Friends. Download OK Tested Now.\n" + link);
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivity(sendIntent);
    }

    private void inUi() {
        quizDetailPresenter = new QuizDetailPresenter(context, this);
        quizCoverPic = findViewById(R.id.quizCoverPic);
        quizCategory = findViewById(R.id.quizCategory);
        quizTitle = findViewById(R.id.quizTitle);
        TextView playSolo = findViewById(R.id.playSolo);
        questionLength = findViewById(R.id.questionLength);
        timePerQuestion = findViewById(R.id.timePerQuestion);
        questionPoint = findViewById(R.id.questionPoint);
        CircleImageView inviteFriends = findViewById(R.id.inviteFriends);
        parentRL = findViewById(R.id.parentRL);
        spinKitView = findViewById(R.id.spinKit);
        addFriendLayout = findViewById(R.id.addFriendLayout);
        quizFriendRoom = findViewById(R.id.quizFriendRoom);

        findViewById(R.id.viewAllFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAllFriendBottomPopup();
            }
        });
        findViewById(R.id.addFriends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendWaysBottomPopup();

            }
        });
        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendWaysBottomPopup();
            }
        });

        playSolo.setOnClickListener(this);

    }


    private void setFriendAdapter() {
        RecyclerView friendListRecyclerView = findViewById(R.id.friendListRecylerView);
        friendListRecyclerView.setHasFixedSize(true);
        friendListRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        friendListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        quizFriendAdapter = new QuizFriendAdapter(context, new ArrayList<>(), new QuizFriendAdapter.Room() {
            @Override
            public void createRoom(String id) {
                friendId = id;
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO) &&
                        checkSelfPermission(Manifest.permission.CAMERA, ConstantApp.PERMISSION_REQ_ID_CAMERA)) {
                    showVideoEnableDisableDialog();
                }
            }
        });
        friendListRecyclerView.setAdapter(quizFriendAdapter);
        quizDetailPresenter.callFriendListApi(friendsType, friendsPage);
    }

    private void callQuizDetail(String articleId) {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            quizDetailPresenter.callQuizDetailApi(articleId);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void inviteFriendToRoom(String quizId, String friendId) {
        if (Helper.isNetworkAvailable(context)) {
            quizDetailPresenter.inviteFriendToRoomApi(quizId, friendId);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    @Override
    public void setUploadContactResponse(ContactFileUploadResponse contactFileUploadResponse) {
        if (contactFileUploadResponse != null && Helper.isContainValue(contactFileUploadResponse.status)) {
            if (contactFileUploadResponse.status.equalsIgnoreCase("1")) {
                AppPreferences.getInstance(context).savePreferencesString(AppConstants.CONTACT_UPDATE, "syncing");
                Calendar calendar = Calendar.getInstance();
                long time = calendar.getTimeInMillis() + 600000;
                AppPreferences.getInstance(context).savePreferencesString(AppConstants.CONTACT_UPDATE_TIME, String.valueOf(time));

                if (Helper.isContainValue(contactFileUploadResponse.message)) {
                    showMessage(contactFileUploadResponse.message);
                }
            } else {
                if (Helper.isContainValue(contactFileUploadResponse.err)) {
                    showMessage(contactFileUploadResponse.err);
                }
            }
        }
        contactSyncAlert.dismiss();
    }

    @Override
    public void setFriendListResponse(FriendListResponse friendListResponse) {
        if (friendListResponse != null && friendListResponse.data != null && friendListResponse.data.size() > 0) {

            quizFriendAdapter.clearAll();
            viewAllFriendAdapter.clearAll();
            quizFriendAdapter.notifyItem(friendListResponse.data);
            addFriendLayout.setVisibility(View.GONE);
            quizFriendRoom.setVisibility(View.VISIBLE);
            friendDataLoaded = true;
            friendsPage = friendListResponse.next_page;
            friendsType = friendListResponse.type;
            viewAllFriendAdapter.notifyItem(friendListResponse.data);

            refresingData = true;
            if (hitApiAgain) {
                hitApiAgain = false;
                friendDataLoaded = true;
                friendsType = "online";
                friendsPage = 1;
                quizDetailPresenter.callFriendListApi(friendsType, friendsPage);
            } else {
                hitApiAgain = false;
            }
        } else {
            addFriendLayout.setVisibility(View.VISIBLE);
            quizFriendRoom.setVisibility(View.GONE);
        }

    }

    @Override
    public void setQuizDetailResponse(QuizDetailResponse quizDetailResponse) {
        if (quizDetailResponse != null && quizDetailResponse.data != null) {
            parentRL.setVisibility(View.VISIBLE);
            if (Helper.isContainValue(quizDetailResponse.data.feature_img_1x1)) {
                Glide.with(getBaseContext())
                        .load(quizDetailResponse.data.feature_img_1x1)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(quizCoverPic);
            } else {
                quizCoverPic.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.placeholder));
            }

            if (Helper.isContainValue(quizDetailResponse.data.cat_display.get(0).category_display)) {
                categorySlug = quizDetailResponse.data.cat_display.get(0).category_slug;
                quizCategory.setOnClickListener(this);
                quizCategory.setText(quizDetailResponse.data.cat_display.get(0).category_display + "  ");
            }

            if (Helper.isContainValue(quizDetailResponse.data.title)) {
                quizTitle.setText(quizDetailResponse.data.title);
            }

            if (quizDetailResponse.data.post_json_content.questions.size() != 0) {
                questionsAL = quizDetailResponse.data.post_json_content.questions;
                if (quizDetailResponse.data.post_json_content.questions.get(0).timer != 0) {
                    timePerQuestion.setText("Time: " + quizDetailResponse.data.post_json_content.questions.get(0).timer + "sec/question");
                }
                if (quizDetailResponse.data.post_json_content.questions.get(0).score != 0) {
                    questionPoint.setText("Points: " + quizDetailResponse.data.post_json_content.questions.get(0).score + "/question");
                }
            }

            if (quizDetailResponse.data.post_json_content.ques_length != 0) {
                questionLength.setText("Questions: " + quizDetailResponse.data.post_json_content.ques_length);
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

                if (inviteFriendToRoomResponse.channel_data != null) {
                    if (!isVideoEnable) {
                        Intent i = new Intent(context, CallActivity.class);
                        i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, "");
                        i.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, inviteFriendToRoomResponse.channel_data._id);
                        i.putExtra(ConstantApp.ACTION_KEY_ACCESS_TOKEN, inviteFriendToRoomResponse.channel_data.agora_access_token);
                        i.putExtra("isAdmin", inviteFriendToRoomResponse.channel_data.is_channel_admin);
                        i.putExtra("userChannelId", inviteFriendToRoomResponse.channel_data.user_channel_id);
                        i.putExtra("quizId", articleId);
                        i.putExtra("isAudioEnable", isAudioEnable);
                        i.putExtra("isVideoEnable", isVideoEnable);
                        startActivity(i);
                    } else {
                        if (Helper.isNetworkAvailable(context)) {
                            channelId = inviteFriendToRoomResponse.channel_data._id;
                            agoraAccessToken = inviteFriendToRoomResponse.channel_data.agora_access_token;
                            isChannelAdmin = inviteFriendToRoomResponse.channel_data.is_channel_admin;
                            userChannelId = inviteFriendToRoomResponse.channel_data.user_channel_id;

                            quizDetailPresenter.changeCameraStatusApi(inviteFriendToRoomResponse.channel_data._id, "enable");
                        }
                    }
                }
            } else {
                if (Helper.isContainValue(inviteFriendToRoomResponse.err)) {
                    showMessage(inviteFriendToRoomResponse.err);
                }
            }
        }
    }

    @Override
    public void setCameraStatusResponse(CameraStatusResponse cameraStatusResponse) {
        if (cameraStatusResponse != null && Helper.isContainValue(cameraStatusResponse.status)) {
            if (cameraStatusResponse.status.equalsIgnoreCase("1")) {
                Intent i = new Intent(context, CallActivity.class);
                i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, "");
                i.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, channelId);
                i.putExtra(ConstantApp.ACTION_KEY_ACCESS_TOKEN, agoraAccessToken);
                i.putExtra("isAdmin", isChannelAdmin);
                i.putExtra("userChannelId", userChannelId);
                i.putExtra("quizId", articleId);
                i.putExtra("isAudioEnable", isAudioEnable);
                i.putExtra("isVideoEnable", isVideoEnable);
                startActivity(i);
            } else {
                if (Helper.isContainValue(cameraStatusResponse.err)) {
                    showMessage(cameraStatusResponse.err);
                }
            }
        }
    }

    @Override
    public void setContactListResponse(UserContactListResponse userContactListResponse) {
        if (userContactListResponse != null && userContactListResponse.data != null && userContactListResponse.data.size() > 0) {
            dataLoaded = true;
            if (offset.equalsIgnoreCase("0")) {
                contactFriendPopup();
            }
            offset = userContactListResponse.next_offset;
//            addNewFriendAdapter.notifyItem(userContactListResponse.data);
            type = userContactListResponse.type;
            AppPreferences.getInstance(getBaseContext()).savePreferencesString(AppConstants.CONTACT_UPDATE, "true");
        }
    }

    @Override
    public void setSendRequestResponse(SendFriendRequestResponse sendFriendRequestResponse, int poistion, ContactModel contactModel) {
        if (sendFriendRequestResponse != null && Helper.isContainValue(sendFriendRequestResponse.status)) {
            if (sendFriendRequestResponse.status.equalsIgnoreCase("1")) {
                contactModel.setStatus("pending");
                addNewFriendAdapter.notifyItemPostion(poistion, contactModel);
                database.userDao().updateUserStatus(contactModel);
                showMessage(sendFriendRequestResponse.message);
            } else {
                if (Helper.isContainValue(sendFriendRequestResponse.err)) {
                    showMessage(sendFriendRequestResponse.err);
                }
            }
        }
    }

    @Override
    public void setInviteLinkResponse(GenerateInviteLinkResponse generateInviteLinkResponse, String mobileNumber) {
        if (generateInviteLinkResponse != null && Helper.isContainValue(generateInviteLinkResponse.status)) {
            if (generateInviteLinkResponse.status.equalsIgnoreCase("1")) {
                sendLinkViaSma(generateInviteLinkResponse.link, mobileNumber);
            } else {
                if (Helper.isContainValue(generateInviteLinkResponse.err)) {
                    showMessage(generateInviteLinkResponse.err);
                }
            }
        }
    }

    @Override
    public void setContactLinkResponse(ContactFileLinkResponse contactFileLinkResponse) {
        if (contactFileLinkResponse != null && contactFileLinkResponse.status.equalsIgnoreCase("1")) {
            context.startService(new Intent(context, ContactService.class).putExtra("filename", contactFileLinkResponse.filename));
        } else {
            showMessage(contactFileLinkResponse.err);
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
        quizDetailPresenter.onDestroyedView();
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
        /*    case R.id.inviteFriends:
//                bottomSheetBehaviour();
                break;*/

            case R.id.playSolo:
                intent = new Intent(context, PlaySoloActivity.class);
                intent.putParcelableArrayListExtra("questionsAL", questionsAL);
                intent.putExtra("quizId", articleId);
                startActivity(intent);
                finish();
                break;

            case R.id.quizCategory:
                intent = new Intent(context, QuizCategoryDetailActivity.class);
                intent.putExtra("categorySlug", categorySlug);
                context.startActivity(intent);
                break;

            default:
                break;
        }
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.CAMERA, ConstantApp.PERMISSION_REQ_ID_CAMERA);
                } else {
                    showAlertDialogForDenyPermission("Without this permission the app is unable to make video call to your friends, so please allow the permission");
                }
                break;
            }
            case ConstantApp.PERMISSION_REQ_ID_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showVideoEnableDisableDialog();
                } else {
                    showAlertDialogForDenyPermission("Without this permission the app is unable to make video call to your friends, so please allow the permission");
                }
                break;
            }
            case ConstantApp.PERMISSION_REQ_ID_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAllContacts();
                } else {
                    showAlertDialogForDenyPermission("Without this permission the app is unable to invite friends, so please allow the permission");
                }
                break;
            }
        }
    }

    private void getAllContacts() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(QuizDetailActivity.this);
        final View syncPopup = getLayoutInflater().inflate(R.layout.contact_sync_dialog, null);
        alertDialog.setView(syncPopup);
        ImageView contactSyncIV = (ImageView) syncPopup.findViewById(R.id.contactSyncIV);
        Glide.with(context)
                .load(R.raw.contact_sync)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(contactSyncIV);
        contactSyncAlert = alertDialog.create();
        contactSyncAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        contactSyncAlert.setCancelable(false);
        contactSyncAlert.show();

        new ContactFetching(QuizDetailActivity.this, new ContactFileInterface() {
            @Override
            public void uploadContacts(File file) {
                if (file != null) {
                    quizDetailPresenter.callContactUploadApi(file);
                } else {
                    showMessage("Error occurred while fetching contact");
                }
            }
        }).execute();
    }

    private void showAlertDialogForDenyPermission(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        AlertDialog alertDialog = dialog.create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Permission Denied");
        alertDialog.setMessage(message);
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

    private void settingContactBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.add_new_friendlist_bottomsheet, null);
        contactBottomDialog = new BottomSheetDialog(context, R.style.DialogStyle);
        contactBottomDialog.setContentView(view/*, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height-400)*/);
        contactBottomDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        contactBottomDialog.getBehavior().setHideable(true);
        contactBottomDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(contactBottomDialog);
        }
        ImageView syncContact = (ImageView) view.findViewById(R.id.syncContact);
        syncContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.READ_CONTACTS, ConstantApp.PERMISSION_REQ_ID_CONTACTS)) {
                    contactBottomDialog.cancel();
                    getAllContacts();
                }
            }
        });
        ImageView searchView = (ImageView) view.findViewById(R.id.searchView);
        EditText searchEditText = (EditText) view.findViewById(R.id.searchEditText);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchEditText.getText().toString().length() > 0) {
                    showLoader();
                    String searchText = searchEditText.getText().toString();
                    searchText = searchText + "%";
                    List<ContactModel> searchContactList = database.userDao().getSearchContact(searchText, "friend");
                    addNewFriendAdapter.clearAll();
                    addNewFriendAdapter.notifyItem(searchContactList);
                } else {
                    showMessage("Enter search text");
                }
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() >= 1) {
                    String searchText = s.toString() + "%";
                    List<ContactModel> searchContactList = database.userDao().getSearchContact(searchText, "friend");
                    addNewFriendAdapter.clearAll();
                    addNewFriendAdapter.notifyItem(searchContactList);
                } else {
                    gettingListContactDb();
                }
            }
        });

        addNewFriendList = (RecyclerView) view.findViewById(R.id.addNewFriendList);
        addNewFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        addNewFriendList.setLayoutManager(linearLayoutManager);
        addNewFriendList.setItemAnimator(new DefaultItemAnimator());
        addNewFriendAdapter = new AddNewFriendAdapter(context, this, new ArrayList<>());
        addNewFriendList.setAdapter(addNewFriendAdapter);

    }

    private void contactFriendPopup() {
        contactBottomDialog.show();
    }

    private void addFriendWaysBottomPopup() {
        View view = getLayoutInflater().inflate(R.layout.invite_friendoptions_list_bottomsheet, null);
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(view);
        dialog.show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }
        AppCompatTextView connectFacebook = (AppCompatTextView) view.findViewById(R.id.connectFacebook);
        AppCompatTextView connectGoogle = (AppCompatTextView) view.findViewById(R.id.connectGoogle);
        AppCompatTextView connectContatcs = (AppCompatTextView) view.findViewById(R.id.connectContatcs);
        AppCompatTextView connectNewFriends = (AppCompatTextView) view.findViewById(R.id.connectNewFriends);

        connectFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        connectGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        connectContatcs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppPreferences.getInstance(context).getPreferencesString(AppConstants.CONTACT_UPDATE).equalsIgnoreCase("true")) {
                    gettingListContactDb();
                    contactFriendPopup();

                } else if (AppPreferences.getInstance(context).getPreferencesString(AppConstants.CONTACT_UPDATE).equalsIgnoreCase("syncing")) {
                    String time = AppPreferences.getInstance(context).getPreferencesString(AppConstants.CONTACT_UPDATE_TIME);
                    if (Calendar.getInstance().getTimeInMillis() > Long.parseLong(time)) {
                        quizDetailPresenter.callContactFileLink();
                    } else {
                        showMessage("Fetching Contacts, Please Wait..");
                    }
                } else {
                    if (checkSelfPermission(Manifest.permission.READ_CONTACTS, ConstantApp.PERMISSION_REQ_ID_CONTACTS)) {
                        getAllContacts();
                    }
                }
            }
        });
        connectNewFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isContainValue(DataHolder.getInstance().getUserDataresponse.invite_link)) {
                    shareLink(DataHolder.getInstance().getUserDataresponse.invite_link);
                } else {
                    showMessage("No link found");
                }
            }
        });
    }

    private void gettingListContactDb() {
        List<ContactModel> allContactList = new ArrayList<>();
        List<ContactModel> addContactList = database.userDao().getAddContact("add");
        List<ContactModel> pendingfriendRequestContactList = database.userDao().getPendingFriendRequestContact("pendingfriendRequest");
        List<ContactModel> pendingContactList = database.userDao().getPendingContact("pending");
        List<ContactModel> inviteContactLList = database.userDao().getInviteContact("invite");
        if (addContactList.size() > 0) {
            allContactList.addAll(addContactList);
        }
        if (pendingfriendRequestContactList.size() > 0) {
            allContactList.addAll(pendingfriendRequestContactList);
        }
        if (pendingContactList.size() > 0) {
            allContactList.addAll(pendingContactList);
        }
        if (inviteContactLList.size() > 0) {
            allContactList.addAll(inviteContactLList);
        }
        addNewFriendAdapter.notifyItem(allContactList);
    }

    private void settingViewAllFriendBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.add_friend_room_bottomsheet, null);
        viewAllBottomDialog = new BottomSheetDialog(context);
        viewAllBottomDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(viewAllBottomDialog);
        }
        viewFriendRoomList = view.findViewById(R.id.viewFriendRoomList);
        viewFriendRoomList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false);
        viewFriendRoomList.setLayoutManager(linearLayoutManager);
        viewFriendRoomList.setItemAnimator(new DefaultItemAnimator());

        viewAllFriendAdapter = new ViewAllFriendAdapter(QuizDetailActivity.this, new ArrayList<>(), new ViewAllFriendAdapter.Room() {
            @Override
            public void createRoom(String id) {
                friendId = id;
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO) &&
                        checkSelfPermission(Manifest.permission.CAMERA, ConstantApp.PERMISSION_REQ_ID_CAMERA)) {
                    showVideoEnableDisableDialog();
                }
            }

            @Override
            public void removeFriend(String friendId, int position) {
                quizDetailPresenter.callUnfriendApi(friendId, position);
            }
        }, true);

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
                            quizDetailPresenter.callFriendListApi(friendsType, friendsPage);
                        }
                    }
                }
            }
        });
    }

    private void viewAllFriendBottomPopup() {
        viewAllBottomDialog.show();
    }


    @Override
    public void callAddRequestApi(int position, ContactModel contactModel) {
        quizDetailPresenter.callSendFriendApi(contactModel.id, contactModel.contact_no, "mobile_contact", position, contactModel);
    }

    @Override
    public void callInviteApi(String contactNumber) {
        if (Helper.isContainValue(DataHolder.getInstance().getUserDataresponse.invite_link)) {
            sendLinkViaSma(DataHolder.getInstance().getUserDataresponse.invite_link, contactNumber);
        } else {
            showMessage("No link found");
        }
//        quizDetailPresenter.callGenerateInviteLinkApi(contactNumber, "mobile_contact");
    }

    @Override
    public void callFriendAcceptApi(ContactModel contactModel, int postion) {
        quizDetailPresenter.callAcceptFriendRequestApi(contactModel, "mobile_contact", postion);

    }

    @Override
    public void setAcceptFriendResponse(AcceptFriendResponse acceptFriendResponse, int position, ContactModel contactModel) {
        if (acceptFriendResponse != null && acceptFriendResponse.status.equalsIgnoreCase("1")) {
            contactModel.setStatus("friend");
            addNewFriendAdapter.notifyRemoved(position);
            database.userDao().updateUserStatus(contactModel);
            showMessage(acceptFriendResponse.message);
            Intent intent = new Intent();
            intent.setAction(getString(R.string.app_name) + "user.refresh");
            context.sendBroadcast(intent);
        } else {
            if (Helper.isContainValue(acceptFriendResponse.err)) {
                showMessage(acceptFriendResponse.err);
            }
        }
    }

    @Override
    public void setUnFriendResponse(UnFriendResponse unFriendResponse, int size, String id) {
        if (unFriendResponse != null && unFriendResponse.status.equalsIgnoreCase("1")) {
            if (size == 1) {
                viewAllBottomDialog.cancel();
            }
            Intent intent = new Intent();
            intent.setAction(getString(R.string.app_name) + "user.refresh");
            context.sendBroadcast(intent);
            AppDatabase db = AppDatabase.getAppDatabase(context);
            ContactModel contactModel = db.userDao().getSearchContactWithId(id);
            if (contactModel != null) {
                contactModel.setStatus("add");
                db.userDao().updateUserStatus(contactModel);
            }
        } else {
            showMessage(unFriendResponse.err);
        }
    }

    private void shareLink(String link) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Play Quizzes, Watch Videos, Challenge Friends. Download OK Tested Now.\n" + link);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void showVideoEnableDisableDialog() {
        isVideoEnable = true;
        isAudioEnable = true;
        View view = getLayoutInflater().inflate(R.layout.video_enable_disable_layout, null);
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(view);
        dialog.show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }

        RelativeLayout micRL = dialog.findViewById(R.id.micRL);
        RelativeLayout cameraRL = dialog.findViewById(R.id.cameraRL);
        ImageView micIV = dialog.findViewById(R.id.micIV);
        ImageView cameraIV = dialog.findViewById(R.id.cameraIV);
        TextView nextTV = dialog.findViewById(R.id.nextTV);

        micRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (micRL.isSelected()) {
                    isAudioEnable = true;
                    micRL.setSelected(false);
                    micIV.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite));
                    micRL.setBackground(context.getResources().getDrawable(R.drawable.circle_gender_bg));
                } else {
                    isAudioEnable = false;
                    micRL.setSelected(true);
                    micIV.setColorFilter(ContextCompat.getColor(context, R.color.colorButtonDisable));
                    micRL.setBackground(context.getResources().getDrawable(R.drawable.button_disabled_bg));
                }
            }
        });

        cameraRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cameraRL.isSelected()) {
                    isVideoEnable = true;
                    cameraRL.setSelected(false);
                    cameraIV.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite));
                    cameraRL.setBackground(context.getResources().getDrawable(R.drawable.circle_gender_bg));
                } else {
                    isVideoEnable = false;
                    cameraRL.setSelected(true);
                    cameraIV.setColorFilter(ContextCompat.getColor(context, R.color.colorButtonDisable));
                    cameraRL.setBackground(context.getResources().getDrawable(R.drawable.button_disabled_bg));
                }
            }
        });

        nextTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                inviteFriendToRoom(articleId, friendId);
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
}