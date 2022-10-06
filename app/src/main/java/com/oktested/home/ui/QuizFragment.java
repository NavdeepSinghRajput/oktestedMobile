package com.oktested.home.ui;

import android.Manifest;
import android.app.ActivityManager;
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
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.oktested.R;
import com.oktested.dashboard.OfflineService;
import com.oktested.home.adapter.AddNewFriendAdapter;
import com.oktested.home.adapter.QuizAdapter;
import com.oktested.home.adapter.QuizFriendAdapter;
import com.oktested.home.adapter.ViewAllFriendAdapter;
import com.oktested.home.async.ContactFetching;
import com.oktested.home.async.ContactFileInterface;
import com.oktested.home.model.AcceptFriendResponse;
import com.oktested.home.model.CategoryDetailQuizResponse;
import com.oktested.home.model.CategoryQuizResponse;
import com.oktested.home.model.ContactFileLinkResponse;
import com.oktested.home.model.ContactFileUploadResponse;
import com.oktested.home.model.FriendListResponse;
import com.oktested.home.model.GenerateInviteLinkResponse;
import com.oktested.home.model.LatestQuizResponse;
import com.oktested.home.model.QuizStructureResponse;
import com.oktested.home.model.SearchQuizResponse;
import com.oktested.home.model.SendFriendRequestResponse;
import com.oktested.home.model.TrendingQuizResponse;
import com.oktested.home.model.UnFriendResponse;
import com.oktested.home.model.UserContactListResponse;
import com.oktested.home.presenter.QuizPresenter;
import com.oktested.quizDetail.model.InviteFriendToRoomResponse;
import com.oktested.quizProfile.ui.QuizProfileActivity;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuizFragment extends Fragment implements QuizView, FriendsActionInterface {

    private RecyclerView friendListRecyclerView, quizSectionRV;
    private BottomSheetBehavior bottomSheetBehavior, contactBottomSheetBehavior, friendBottomSheetBehavior;
    private QuizPresenter quizPresenter;
    private Map<Integer, Object> sectionMap = new HashMap<>();
    private int totalCategoryDetailCount = AppConstants.SECTION_QUIZ_CATEGORY_DETAILS;
    private int categoryDetailCount = 0;
    private QuizAdapter quizAdapter;
    private QuizStructureResponse quizStructureResponse;
    private ArrayList<Integer> moreDetailsCountAL = new ArrayList<>();
    private Context context;
    // private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private JSONArray jsonArray = new JSONArray();
    private String offset = "0", friendsType = "online", friendId;
    private int friendsPage = 1;
    private boolean dataLoaded = true, friendDataLoaded = true, isVideoEnable = true, isAudioEnable = true;
    private boolean userClickedContact = false;
    //BottomSheets
    private RecyclerView addNewFriendList;
    private AddNewFriendAdapter addNewFriendAdapter;
    //    private ProgressDialog progressDialog;
    private AlertDialog contactSyncAlert;
    private SpinKitView spinKitView;
    private QuizFriendAdapter quizFriendAdapter;
    private TextView userName;
    private CircleImageView userProfilePic;
    private RelativeLayout addFriendLayout;
    private LinearLayout friendList;
    private RecyclerView viewFriendRoomList;
    private ViewAllFriendAdapter viewAllFriendAdapter;
    private BroadcastReceiver mReceiver;
    private boolean refresingData = true, hitApiAgain = false;
    private BottomSheetDialog contactBottomDialog, viewAllBottomDialog;
    //    private RecyclerView searchFriendList;
    private Boolean refreshcontactData = false;
    //    private SeacrhAddNewFriendAdapter seacrhAddNewFriendAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    //    private List<ContactModel> allContactList = new ArrayList<>();
    private AppDatabase database;
    private String agoraAccessToken, channelId;
    private int userChannelId;
    private boolean isChannelAdmin;

    @Override
    public void onDestroyView() {
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
        }
        super.onDestroyView();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.d("isMyServiceRunning?", false + "");
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_quiz, container, false);

        quizPresenter = new QuizPresenter(getContext(), this);
        database = AppDatabase.getAppDatabase(context);
        if (!isMyServiceRunning(OfflineService.class)) {
            context.startService(new Intent(context, OfflineService.class));
        }

        receiverRegister();
        inUi(root_view);
        setUserNameImage();
        RelativeLayout userDetail = root_view.findViewById(R.id.userDetail);
        userDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   startActivity(new Intent(getContext(), PickQuizActivity.class));
            }
        });

        settingContactBottomSheet();
        settingViewAllFriendBottomSheet();

        LocalBroadcastManager.getInstance(context).registerReceiver(updateUserDataReceiver, new IntentFilter(AppConstants.USER_DATA_UPDATE));

        return root_view;
    }

    private void receiverRegister() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                friendDataLoaded = true;
                friendsType = "online";
                friendsPage = 1;
           /*     if (refresingData) {
                    refresingData = false;*/
                quizPresenter.callFriendListApi(friendsType, friendsPage);
            /*    } else {
                    hitApiAgain = true;
                }*/
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "user.refresh");
        context.registerReceiver(mReceiver, intentFilter);
    }

/*    private void contactProcessCompleteReceiver() {
        contactProcessReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new Handler().postDelayed(() -> {
                    if (AppPreferences.getInstance(context).getPreferencesString(AppConstants.CONTACT_UPDATE).equalsIgnoreCase("true")) {
                        gettingListContactDb();
                        contactFriendPopup();
                    }
                }, 2000);
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "contact.process");
        context.registerReceiver(contactProcessReceiver, intentFilter);
    }*/

    private void inUi(View root_view) {
        spinKitView = root_view.findViewById(R.id.spinKit);
        userName = (TextView) root_view.findViewById(R.id.userName);
        userProfilePic = (CircleImageView) root_view.findViewById(R.id.userProfilePic);
        CircleImageView inviteFriends = (CircleImageView) root_view.findViewById(R.id.inviteFriends);
        TextView addFriends = (TextView) root_view.findViewById(R.id.addFriends);
        friendListRecyclerView = (RecyclerView) root_view.findViewById(R.id.friendListRecylerView);
        quizSectionRV = (RecyclerView) root_view.findViewById(R.id.quizesSectionRV);
        addFriendLayout = (RelativeLayout) root_view.findViewById(R.id.addFriendLayout);
        friendList = (LinearLayout) root_view.findViewById(R.id.friendList);
        friendList = (LinearLayout) root_view.findViewById(R.id.friendList);

        swipeRefreshLayout = root_view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Helper.isNetworkAvailable(context)) {
                    sectionMap.clear();
                    moreDetailsCountAL.clear();
                    categoryDetailCount = 0;
                    totalCategoryDetailCount = AppConstants.SECTION_QUIZ_CATEGORY_DETAILS;
                    swipeRefreshLayout.setRefreshing(false);
                    callQuizStructureApi();
//                    getQuizStructureFromFile();

                    friendsPage = 1;
                    friendsType = "online";
                    quizPresenter.callFriendListApi(friendsType, friendsPage);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    showMessage(getString(R.string.please_check_internet_connection));
                }
            }
        });

        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), QuizProfileActivity.class));
            }
        });

        addFriends.setOnClickListener(new View.OnClickListener() {
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

        root_view.findViewById(R.id.viewAllFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAllFriendBottomPopup();
            }
        });

        quizPresenter.callFriendListApi(friendsType, friendsPage);

        friendListRecyclerView.setHasFixedSize(true);
        friendListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        friendListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        quizFriendAdapter = new QuizFriendAdapter(getContext(), new ArrayList<>(), new QuizFriendAdapter.Room() {
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

        quizSectionRV.setNestedScrollingEnabled(false);
        quizSectionRV.setHasFixedSize(true);
        quizSectionRV.setLayoutManager(new LinearLayoutManager(getContext()));
        quizSectionRV.setItemAnimator(new DefaultItemAnimator());

        callQuizStructureApi();
//        getQuizStructureFromFile();
//        context.startService(new Intent(context, ContactService.class).putExtra("downloadUrl","https://okt-storage.scoopwhoop.com/oktested/static/contact/mobile/5dd5830f9e68476204414a7c.json"));

        if (!AppPreferences.getInstance(context).getPreferencesString(AppConstants.QUIZ_FIRST_TIME).equalsIgnoreCase("true")) {
            inviteFirstTimePopup();
        }
    }

    private void setUserNameImage() {
        if (Helper.isContainValue(DataHolder.getInstance().getUserDataresponse.display_name)) {
            userName.setText("Welcome, " + DataHolder.getInstance().getUserDataresponse.display_name);
        } else {
            if (Helper.isContainValue(DataHolder.getInstance().getUserDataresponse.name)) {
                userName.setText("Welcome, " + DataHolder.getInstance().getUserDataresponse.name);
            } else {
                userName.setText("Welcome");
            }
        }

        if (Helper.isContainValue(DataHolder.getInstance().getUserDataresponse.picture)) {
            Glide.with(context)
                    .load(DataHolder.getInstance().getUserDataresponse.picture)
                    .centerCrop()
                    .placeholder(R.drawable.ic_vector_default_profile)
                    .into(userProfilePic);
        } else if (Helper.isContainValue(DataHolder.getInstance().getUserDataresponse.avatar)) {
            Glide.with(context)
                    .load(DataHolder.getInstance().getUserDataresponse.avatar)
                    .centerCrop()
                    .placeholder(R.drawable.ic_vector_default_profile)
                    .into(userProfilePic);
        } else {
            userProfilePic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_default_profile));
        }
    }

    private BroadcastReceiver updateUserDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setUserNameImage();
        }
    };

    private void inviteFriendToRoom(String friendId) {
        if (Helper.isNetworkAvailable(context)) {
            quizPresenter.inviteFriendToRoomApi(friendId);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
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
                    userClickedContact = true;
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        addNewFriendList.setLayoutManager(linearLayoutManager);
        addNewFriendList.setItemAnimator(new DefaultItemAnimator());
        addNewFriendAdapter = new AddNewFriendAdapter(getContext(), this, new ArrayList<>());
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
                    String time = AppPreferences.getInstance(getContext()).getPreferencesString(AppConstants.CONTACT_UPDATE_TIME);
                    if (Calendar.getInstance().getTimeInMillis() > Long.parseLong(time)) {
                        quizPresenter.callContactFileLink();
                    } else {
                        showMessage("Fetching Contacts, Please Wait...");
                    }
                } else {
                    if (checkSelfPermission(Manifest.permission.READ_CONTACTS, ConstantApp.PERMISSION_REQ_ID_CONTACTS)) {
                        userClickedContact = true;
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        viewFriendRoomList.setLayoutManager(linearLayoutManager);
        viewFriendRoomList.setItemAnimator(new DefaultItemAnimator());

        viewAllFriendAdapter = new ViewAllFriendAdapter(getContext(), new ArrayList<>(), new ViewAllFriendAdapter.Room() {
            @Override
            public void createRoom(String id) {
                friendId = id;
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO) &&
                        checkSelfPermission(Manifest.permission.CAMERA, ConstantApp.PERMISSION_REQ_ID_CAMERA)) {
                    showVideoEnableDisableDialog();
                }
            }

            @Override
            public void removeFriend(String friendId, int size) {
                quizPresenter.callUnfriendApi(friendId, size);
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
                            quizPresenter.callFriendListApi(friendsType, friendsPage);
                        }
                    }
                }
            }
        });
    }

    private void viewAllFriendBottomPopup() {
        viewAllBottomDialog.show();
    }

    private void shareLink(String link) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Play Quizzes, Watch Videos, Challenge Friends. Download OK Tested Now.\n" + link);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void sendLinkViaSma(String link, String mobileNumber) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("address", mobileNumber);
        sendIntent.putExtra("sms_body", "Play Quizzes, Watch Videos, Challenge Friends. Download OK Tested Now.\n" + link);
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivity(sendIntent);
    }

    private void inviteFirstTimePopup() {
        AppPreferences.getInstance(getContext()).savePreferencesString(AppConstants.QUIZ_FIRST_TIME, "true");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        View customLayout = getLayoutInflater().inflate(R.layout.invite_friend_dialog, null);
        alertDialog.setView(customLayout);

        TextView inviteFriendsTV = customLayout.findViewById(R.id.inviteFriendsTV);
        ImageView crossIV = customLayout.findViewById(R.id.crossIV);
        ImageView firstTymIV = customLayout.findViewById(R.id.firstTymIV);

        AlertDialog alert = alertDialog.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.setCanceledOnTouchOutside(false);
        alert.show();
        inviteFriendsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendWaysBottomPopup();
                alert.dismiss();
            }
        });

        Glide.with(context)
                .load(R.raw.first_tym)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(firstTymIV);


        crossIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }

    private void callQuizStructureApi() {
        if (Helper.isNetworkAvailable(getContext())) {
            showLoader();
            quizPresenter.callQuizStructure();
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

 /*   @Override
    public void setSeacrhContactListResponse(SearchUserContactListResponse searchUserContactListResponse) {
        if (searchUserContactListResponse != null && searchUserContactListResponse.data != null && searchUserContactListResponse.data.size() > 0) {
            addNewFriendList.setVisibility(View.GONE);
            searchFriendList.setVisibility(View.VISIBLE);
        } else {
            addNewFriendList.setVisibility(View.VISIBLE);
            searchFriendList.setVisibility(View.GONE);
            showMessage(searchUserContactListResponse.message);
        }
    }*/

    @Override
    public void setContactLinkResponse(ContactFileLinkResponse contactFileLinkResponse) {
        if (contactFileLinkResponse != null && contactFileLinkResponse.status.equalsIgnoreCase("1")) {
            context.startService(new Intent(context, ContactService.class).putExtra("filename", contactFileLinkResponse.filename));
        } else {
            showMessage(contactFileLinkResponse.err);
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

    @Override
    public void setQuizStructureResponse(QuizStructureResponse quizStructureResponse) {
        if (quizStructureResponse != null && quizStructureResponse.data != null && quizStructureResponse.data.size() > 0) {
            this.quizStructureResponse = quizStructureResponse;
            for (int i = 0; i < quizStructureResponse.data.size(); i++) {
                if (quizStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_QUIZ_TRENDING_LAYOUT)) {
                    showLoader();
                    quizPresenter.callTrendingQuizApi(quizStructureResponse.data.get(i).section_type, "0");
                } else if (quizStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_QUIZ_LATEST_LAYOUT)) {
                    showLoader();
                    quizPresenter.callLatestQuizApi("0");
                } else if (quizStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_QUIZ_CATEGORY_LAYOUT)) {
                    showLoader();
                    quizPresenter.callCategoryQuizApi("0");
                } else if (quizStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_QUIZ_CATEGORY_DETAILS_LAYOUT)) {
                    showLoader();
                    categoryDetailCount++;
                    quizPresenter.callCategoryDetailQuizApi(quizStructureResponse.data.get(i).value.slug, "0");
                }
            }
        }
    }

    private void friendBottomSheetBehaviour() {
        if (friendBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            friendBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            friendBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    private void getQuizStructureFromFile() {
        String myJson = inputStreamToString(getContext().getResources().openRawResource(R.raw.dynamicquizhome));
        quizStructureResponse = new Gson().fromJson(myJson, QuizStructureResponse.class);
        if (quizStructureResponse != null && quizStructureResponse.data != null && quizStructureResponse.data.size() > 0) {
            for (int i = 0; i < quizStructureResponse.data.size(); i++) {
                if (quizStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_QUIZ_TRENDING_LAYOUT)) {
                    showLoader();
                    quizPresenter.callTrendingQuizApi(quizStructureResponse.data.get(i).section_type, "0");
                } else if (quizStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_QUIZ_LATEST_LAYOUT)) {
                    showLoader();
                    quizPresenter.callLatestQuizApi("0");
                } else if (quizStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_QUIZ_CATEGORY_LAYOUT)) {
                    showLoader();
                    quizPresenter.callCategoryQuizApi("0");
                } else if (quizStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_QUIZ_CATEGORY_DETAILS_LAYOUT)) {
                    showLoader();
                    categoryDetailCount++;
                    quizPresenter.callCategoryDetailQuizApi(quizStructureResponse.data.get(i).value.slug, "0");
                }
            }
        }
    }

    private String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            return new String(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void showLoader() {
        // spinKitView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        //spinKitView.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        if (quizPresenter != null) {
            quizPresenter.onDestroyedView();
            LocalBroadcastManager.getInstance(context).unregisterReceiver(updateUserDataReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void setTrendingQuizResponse(TrendingQuizResponse trendingQuizResponse) {
        if (trendingQuizResponse != null && trendingQuizResponse.data != null && trendingQuizResponse.data.size() > 0) {
            sectionMap.put(AppConstants.SECTION_QUIZ_TRENDING, trendingQuizResponse);
        }
    }

    @Override
    public void setLatestQuizResponse(LatestQuizResponse latestQuizResponse) {
        if (latestQuizResponse != null && latestQuizResponse.data != null && latestQuizResponse.data.size() > 0) {
            sectionMap.put(AppConstants.SECTION_QUIZ_LATEST, latestQuizResponse);
        }
    }

    @Override
    public void setCategoryQuizResponse(CategoryQuizResponse categoryQuizResponse) {
        if (categoryQuizResponse != null && categoryQuizResponse.data != null && categoryQuizResponse.data.size() > 0) {
            sectionMap.put(AppConstants.SECTION_QUIZ_CATEGORY, categoryQuizResponse);
        }
    }

    @Override
    public void setCategoryDetailQuizResponse(CategoryDetailQuizResponse categoryDetailQuizResponse) {
        if (categoryDetailQuizResponse != null && categoryDetailQuizResponse.data != null && categoryDetailQuizResponse.data.size() > 0) {
            int count = totalCategoryDetailCount++;
            sectionMap.put(count, categoryDetailQuizResponse);
            moreDetailsCountAL.add(count);
            if (moreDetailsCountAL.size() == categoryDetailCount) {
                quizAdapter = new QuizAdapter(getContext(), quizStructureResponse.data, sectionMap, moreDetailsCountAL);
                quizSectionRV.setAdapter(quizAdapter);
            }
        }
    }

    @Override
    public void setSearchQuizResponse(SearchQuizResponse searchQuizResponse) {

    }

    @Override
    public void setContactListResponse(UserContactListResponse userContactListResponse) {
        if (userContactListResponse != null && userContactListResponse.data != null && userContactListResponse.data.size() > 0) {
            dataLoaded = true;
//            addNewFriendAdapter.notifyItem(userContactListResponse.data);
            if (offset.equalsIgnoreCase("0")) {
                contactFriendPopup();
            }
            offset = userContactListResponse.next_offset;
        }
    }

    @Override
    public void setUploadContactResponse(ContactFileUploadResponse contactFileUploadResponse) {
        if (contactFileUploadResponse != null && Helper.isContainValue(contactFileUploadResponse.status)) {
            if (contactFileUploadResponse.status.equalsIgnoreCase("1")) {
                AppPreferences.getInstance(getContext()).savePreferencesString(AppConstants.CONTACT_UPDATE, "syncing");
                Calendar calendar = Calendar.getInstance();
                long time = calendar.getTimeInMillis() + 600000;
                AppPreferences.getInstance(getContext()).savePreferencesString(AppConstants.CONTACT_UPDATE_TIME, String.valueOf(time));
                //     if (userClickedContact) {
                if (Helper.isContainValue(contactFileUploadResponse.message)) {
                    showMessage(contactFileUploadResponse.message);
                }
                //   }
            } else {
                if (Helper.isContainValue(contactFileUploadResponse.err)) {
                    showMessage(contactFileUploadResponse.err);
                }
            }
        }
        contactSyncAlert.dismiss();
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
    public void callFriendAcceptApi(ContactModel contactModel, int postion) {
        quizPresenter.callAcceptFriendRequestApi(contactModel, "mobile_contact", postion);

    }

    @Override
    public void setAcceptFriendResponse(AcceptFriendResponse acceptFriendResponse, int position, ContactModel contactModel) {
        if (acceptFriendResponse != null && acceptFriendResponse.status.equalsIgnoreCase("1")) {
            contactModel.setStatus("friend");
            addNewFriendAdapter.notifyRemoved(position);
//            addNewFriendAdapter.notifyItemPostion(position, contactModel);
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
    public void setFriendListResponse(FriendListResponse friendListResponse) {
        if (friendListResponse != null && friendListResponse.data != null && friendListResponse.data.size() > 0) {
            quizFriendAdapter.clearAll();
            viewAllFriendAdapter.clearAll();
            quizFriendAdapter.notifyItem(friendListResponse.data);
            viewAllFriendAdapter.notifyItem(friendListResponse.data);
            addFriendLayout.setVisibility(View.GONE);
            friendList.setVisibility(View.VISIBLE);
            friendDataLoaded = true;
            friendsPage = friendListResponse.next_page;
            friendsType = friendListResponse.type;

            refresingData = true;
            if (hitApiAgain) {
                hitApiAgain = false;
                friendDataLoaded = true;
                friendsType = "online";
                friendsPage = 1;
                quizPresenter.callFriendListApi(friendsType, friendsPage);
            } else {
                hitApiAgain = false;
            }
        } else {
            addFriendLayout.setVisibility(View.VISIBLE);
            friendList.setVisibility(View.GONE);
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
                        i.putExtra("quizId", "");
                        i.putExtra("isAudioEnable", isAudioEnable);
                        i.putExtra("isVideoEnable", isVideoEnable);
                        startActivity(i);
                    } else {
                        if (Helper.isNetworkAvailable(context)) {
                            channelId = inviteFriendToRoomResponse.channel_data._id;
                            agoraAccessToken = inviteFriendToRoomResponse.channel_data.agora_access_token;
                            isChannelAdmin = inviteFriendToRoomResponse.channel_data.is_channel_admin;
                            userChannelId = inviteFriendToRoomResponse.channel_data.user_channel_id;

                            quizPresenter.changeCameraStatusApi(inviteFriendToRoomResponse.channel_data._id, "enable");
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
                i.putExtra("quizId", "");
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

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
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
                    userClickedContact = false;
                    showAlertDialogForDenyPermission("Without this permission the app is unable to invite friends, so please allow the permission");
                }
                break;
            }
        }
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
                inviteFriendToRoom(friendId);
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

    private void getAllContacts() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
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

        new ContactFetching(getContext(), new ContactFileInterface() {
            @Override
            public void uploadContacts(File file) {
                if (file != null) {
                    quizPresenter.callContactUploadApi(file);
                } else {
                    showMessage("Error occurred while fetching contact");
                }
            }
        }).execute();
    }

    @Override
    public void callAddRequestApi(int position, ContactModel contactModel) {
        quizPresenter.callSendFriendApi(contactModel.id, contactModel.contact_no, "mobile_contact", position, contactModel);
    }

    @Override
    public void callInviteApi(String contactNumber) {
        if (Helper.isContainValue(DataHolder.getInstance().getUserDataresponse.invite_link)) {
            sendLinkViaSma(DataHolder.getInstance().getUserDataresponse.invite_link, contactNumber);
        } else {
            showMessage("No link found");
        }
        //  quizPresenter.callGenerateInviteLinkApi(contactNumber, "mobile_contact");
    }
}