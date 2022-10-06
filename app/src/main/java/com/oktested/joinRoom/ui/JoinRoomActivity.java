package com.oktested.joinRoom.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.oktested.R;
import com.oktested.home.model.Questions;
import com.oktested.joinRoom.model.JoinRoomResponse;
import com.oktested.joinRoom.model.RejectInvitationResponse;
import com.oktested.joinRoom.presenter.JoinRoomPresenter;
import com.oktested.quizDetail.model.QuizDetailResponse;
import com.oktested.utils.Helper;
import com.oktested.videoCall.model.CameraStatusResponse;
import com.oktested.videoCall.model.ConstantApp;
import com.oktested.videoCall.ui.CallActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class JoinRoomActivity extends AppCompatActivity implements View.OnClickListener, JoinRoomView {

    private Context context;
    private TextView invitationTV, invitationTV2, declineTV, joinTV, quizCategoryTV, quizTitleTV;
    private CircleImageView adminIV, adminIV2;
    private RelativeLayout micRL, cameraRL;
    private LinearLayout withQuizLL;
    private ImageView micIV, cameraIV, quizIV;
    private CardView withOutQuizCV;
    private String channelId, image, quizId, message;
    private boolean isVideoEnable = true, isAudioEnable = true;
    private JoinRoomPresenter joinRoomPresenter;
    private String agoraAccessToken;
    private int userChannelId;
    private boolean isChannelAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_join_room);
        context = this;

        inUi();

        new Handler().postDelayed(this::finish, 15000);
    }

    private void inUi() {
        joinRoomPresenter = new JoinRoomPresenter(context, this);
        channelId = getIntent().getExtras().getString("channelId");
        image = getIntent().getExtras().getString("image");
        quizId = getIntent().getExtras().getString("quizId");
        message = getIntent().getExtras().getString("message");
        String quizFeatureImage = getIntent().getExtras().getString("quizFeatureImage");
        String quizTitle = getIntent().getExtras().getString("quizTitle");
        String quizCategory = getIntent().getExtras().getString("quizCategory");

        withQuizLL = findViewById(R.id.withQuizLL);
        withOutQuizCV = findViewById(R.id.withOutQuizCV);
        invitationTV = findViewById(R.id.invitationTV);
        invitationTV2 = findViewById(R.id.invitationTV2);
        quizCategoryTV = findViewById(R.id.quizCategoryTV);
        quizTitleTV = findViewById(R.id.quizTitleTV);
        declineTV = findViewById(R.id.declineTV);
        joinTV = findViewById(R.id.joinTV);
        adminIV = findViewById(R.id.adminIV);
        adminIV2 = findViewById(R.id.adminIV2);
        micIV = findViewById(R.id.micIV);
        quizIV = findViewById(R.id.quizIV);
        cameraIV = findViewById(R.id.cameraIV);
        micRL = findViewById(R.id.micRL);
        cameraRL = findViewById(R.id.cameraRL);

        declineTV.setOnClickListener(this);
        joinTV.setOnClickListener(this);
        micRL.setOnClickListener(this);
        cameraRL.setOnClickListener(this);

        if (Helper.isContainValue(quizId)) {
            withQuizLL.setVisibility(View.VISIBLE);
            withOutQuizCV.setVisibility(View.GONE);

            if (Helper.isContainValue(message)) {
                invitationTV2.setText(Html.fromHtml(message));
            }

            if (Helper.isContainValue(image)) {
                Glide.with(context)
                        .load(image)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(adminIV2);
            }

            if (Helper.isContainValue(quizTitle)) {
                quizTitleTV.setText(quizTitle);
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
        } else {
            withQuizLL.setVisibility(View.GONE);
            withOutQuizCV.setVisibility(View.VISIBLE);

            if (Helper.isContainValue(message)) {
                invitationTV.setText(Html.fromHtml(message));
            }

            if (Helper.isContainValue(image)) {
                Glide.with(context)
                        .load(image)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(adminIV);
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        joinRoomPresenter.onDestroyedView();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.declineTV:
                rejectInvitation();
                break;

            case R.id.joinTV:
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO) &&
                        checkSelfPermission(Manifest.permission.CAMERA, ConstantApp.PERMISSION_REQ_ID_CAMERA)) {
                    joinTV.setEnabled(false);
                    joinRoom();
                }
                break;

            case R.id.cameraRL:
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
                break;

            case R.id.micRL:
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
                break;

            default:
                break;
        }
    }

    private void joinRoom() {
        if (Helper.isNetworkAvailable(context)) {
            joinRoomPresenter.joinRoomApi(channelId);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void rejectInvitation() {
        if (Helper.isNetworkAvailable(context)) {
            joinRoomPresenter.rejectInvitationApi(channelId);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    @Override
    public void showLoader() {

    }

    @Override
    public void hideLoader() {

    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setJoinRoomResponse(JoinRoomResponse joinRoomResponse) {
        if (joinRoomResponse != null && Helper.isContainValue(joinRoomResponse.status)) {
            joinTV.setEnabled(true);
            if (joinRoomResponse.status.equalsIgnoreCase("1")) {
                if (joinRoomResponse.channel_data != null) {
                    if (!isVideoEnable) {
                        Intent i = new Intent(context, CallActivity.class);
                        i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, "");
                        i.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, joinRoomResponse.channel_data._id);
                        i.putExtra(ConstantApp.ACTION_KEY_ACCESS_TOKEN, joinRoomResponse.channel_data.agora_access_token);
                        i.putExtra("isAdmin", joinRoomResponse.channel_data.is_channel_admin);
                        i.putExtra("userChannelId", joinRoomResponse.channel_data.user_channel_id);
                        i.putExtra("quizId", quizId);
                        i.putExtra("isAudioEnable", isAudioEnable);
                        i.putExtra("isVideoEnable", isVideoEnable);
                        startActivity(i);
                        finish();
                    } else {
                        if (Helper.isNetworkAvailable(context)) {
                            channelId = joinRoomResponse.channel_data._id;
                            agoraAccessToken = joinRoomResponse.channel_data.agora_access_token;
                            isChannelAdmin = joinRoomResponse.channel_data.is_channel_admin;
                            userChannelId = joinRoomResponse.channel_data.user_channel_id;

                            joinRoomPresenter.changeCameraStatusApi(joinRoomResponse.channel_data._id, "enable");
                        }
                    }
                }
            } else {
                if (Helper.isContainValue(joinRoomResponse.err)) {
                    showMessage(joinRoomResponse.err);
                    finish();
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
                i.putExtra("quizId", quizId);
                i.putExtra("isAudioEnable", isAudioEnable);
                i.putExtra("isVideoEnable", isVideoEnable);
                startActivity(i);
                finish();
            } else {
                if (Helper.isContainValue(cameraStatusResponse.err)) {
                    showMessage(cameraStatusResponse.err);
                    finish();
                }
            }
        }
    }

    @Override
    public void setRejectInvitationResponse(RejectInvitationResponse rejectInvitationResponse) {
        finish();
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
                    joinTV.setEnabled(false);
                    joinRoom();
                } else {
                    showAlertDialogForDenyPermission("Without this permission the app is unable to make video call to your friends, so please allow the permission");
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
}