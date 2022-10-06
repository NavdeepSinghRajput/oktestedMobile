package com.oktested.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.oktested.R;
import com.oktested.activities.SplashActivity;
import com.oktested.community.ui.PostDetailActivity;
import com.oktested.dashboard.ui.DashboardActivity;
import com.oktested.joinRoom.ui.JoinRoomActivity;
import com.oktested.quizEnable.QuizEnableActivity;
import com.oktested.roomDatabase.AppDatabase;
import com.oktested.roomDatabase.ContactService;
import com.oktested.roomDatabase.model.ContactModel;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.DataHolder;
import com.oktested.utils.Helper;
import com.oktested.videoPlayer.ui.VideoPlayerActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("ALL")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private String channelId = "OkTested";
    private String title, message, videoId, image, commentId, type, postId, notifyType;
    private String roomId, messageBody, messageTitle, quizImage, quizId, quizFeatureImage, quizTitle, participantName, quizCategory, participantId, adminName, contact_status, contact_filename, userName;
    private String contact_number, contact_user_id, contact_request_send_by;
    private Bitmap contactPic;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage != null && remoteMessage.getData().size() > 0) {
            Map<String, String> params = remoteMessage.getData();
            if (params != null && params.size() > 0) {
                JSONObject jsonObject = new JSONObject(params);
                Log.d(TAG, jsonObject.toString());

                videoId = jsonObject.optString("videoId");
                if (!Helper.isContainValue(videoId)) {
                    videoId = jsonObject.optString("videoid");
                }

                postId = jsonObject.optString("post_id");
                commentId = jsonObject.optString("parent_id");
                image = jsonObject.optString("image");
                title = jsonObject.optString("title");
                message = jsonObject.optString("message");
                notifyType = jsonObject.optString("notify_type");
                type = jsonObject.optString("post_type");

                messageBody = jsonObject.optString("message_body");
                messageTitle = jsonObject.optString("message_title");
                roomId = jsonObject.optString("channel_id");
                quizImage = jsonObject.optString("image");
                quizId = jsonObject.optString("quiz_id");

                userName = jsonObject.optString("user_name");
                adminName = jsonObject.optString("admin_name");
                quizFeatureImage = jsonObject.optString("quiz_feature_img");
                quizTitle = jsonObject.optString("quiz_title");
                participantName = jsonObject.optString("participant_name");
                participantId = jsonObject.optString("participant_id");
                quizCategory = jsonObject.optString("quiz_category");
                contact_status = jsonObject.optString("status");
                contact_filename = jsonObject.optString("filename");
                contact_number = jsonObject.optString("contact");
                contact_user_id = jsonObject.optString("user_id");
                contact_request_send_by = jsonObject.optString("request_send_by");
            }
        }

        if (Helper.isContainValue(videoId) || Helper.isContainValue(type)) {
            sendNotification(getApplicationContext(), message);
        }

        if (Helper.isContainValue(notifyType)) {
            if (notifyType.equalsIgnoreCase("friend_request")) {
                message = messageBody;
                title = messageTitle;
                image = "";
                sendNotification(getApplicationContext(), message);
                if (!DataHolder.getInstance().getUserDataresponse.is_quiz_enable) {
                    Intent intent = new Intent(getApplicationContext(), QuizEnableActivity.class);
                    intent.putExtra("userName", userName);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                AppDatabase db = AppDatabase.getAppDatabase(getBaseContext());
                ContactModel contactModel = db.userDao().getSearchContactWithId(contact_request_send_by);
                if (contactModel != null) {
                    contactModel.setStatus("pendingfriendRequest");
                    db.userDao().updateUserStatus(contactModel);
                }
            } else if (notifyType.equalsIgnoreCase("accept_friend_request")) {
                Intent intent = new Intent();
                intent.setAction(getString(R.string.app_name) + "user.refresh");
                sendBroadcast(intent);
                message = messageBody;
                title = messageTitle;
                image = "";
                sendNotification(getApplicationContext(), message);
                AppDatabase db = AppDatabase.getAppDatabase(getBaseContext());
                ContactModel contactModel = db.userDao().getSearchContactWithNumber(contact_number);
                if (contactModel != null) {
                    contactModel.setStatus("friend");
                    db.userDao().updateUserStatus(contactModel);
                }
            } else if (notifyType.equalsIgnoreCase("reject_friend_request")) {
                Intent intent = new Intent();
                intent.setAction(getString(R.string.app_name) + "user.refresh");
                sendBroadcast(intent);
                AppDatabase db = AppDatabase.getAppDatabase(getBaseContext());
                ContactModel contactModel = db.userDao().getSearchContactWithNumber(contact_number);
                if (contactModel != null) {
                    contactModel.setStatus("add");
                    db.userDao().updateUserStatus(contactModel);
                }
            } else if (notifyType.equalsIgnoreCase("user_active") || notifyType.equalsIgnoreCase("user_inactive") || notifyType.equalsIgnoreCase("manage_user_busy_status")) {
                Intent intent = new Intent();
                intent.setAction(getString(R.string.app_name) + "user.refresh");
                sendBroadcast(intent);
            } else if (notifyType.equalsIgnoreCase("start_play_quiz")) {
                Intent intent = new Intent();
                intent.setAction(getString(R.string.app_name) + "start.quiz");
                sendBroadcast(intent);
            } else if (notifyType.equalsIgnoreCase("invite_join_room")) {
                Intent intent = new Intent(getApplicationContext(), JoinRoomActivity.class);
                intent.putExtra("channelId", roomId);
                intent.putExtra("message", messageBody);
                intent.putExtra("image", quizImage);
                intent.putExtra("quizId", quizId);
                intent.putExtra("quizFeatureImage", quizFeatureImage);
                intent.putExtra("quizTitle", quizTitle);
                intent.putExtra("quizCategory", quizCategory);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (notifyType.equalsIgnoreCase("select_quiz_by_admin") || notifyType.equalsIgnoreCase("accept_suggested_quiz")) {
                Intent intent = new Intent();
                intent.setAction(getString(R.string.app_name) + "admin.select");
                intent.putExtra("quizId", quizId);
                intent.putExtra("adminName", adminName);
                sendBroadcast(intent);
            } else if (notifyType.equalsIgnoreCase("suggestion_by_participant")) {
                Intent intent = new Intent();
                intent.setAction(getString(R.string.app_name) + "participant.suggestion");
                intent.putExtra("quizFeatureImage", quizFeatureImage);
                intent.putExtra("quizTitle", quizTitle);
                intent.putExtra("participantName", participantName);
                intent.putExtra("participantId", participantId);
                intent.putExtra("quizId", quizId);
                intent.putExtra("quizCategory", quizCategory);
                sendBroadcast(intent);
            } else if (notifyType.equalsIgnoreCase("change_ready_status")) {
                Intent intent = new Intent();
                intent.setAction(getString(R.string.app_name) + "ready.status");
                sendBroadcast(intent);
            } else if (notifyType.equalsIgnoreCase("enable_room_camera")) {
                Intent intent = new Intent();
                intent.setAction(getString(R.string.app_name) + "enable.camera");
                sendBroadcast(intent);
            } else if (notifyType.equalsIgnoreCase("remove_participant_by_admin")) {
                Intent intent = new Intent();
                intent.setAction(getString(R.string.app_name) + "remove.participant");
                sendBroadcast(intent);
            } else if (notifyType.equalsIgnoreCase("shifted_admin_control")) {
                Intent intent = new Intent();
                intent.setAction(getString(R.string.app_name) + "shift.admin");
                intent.putExtra("adminName", adminName);
                sendBroadcast(intent);
            } else if (notifyType.equalsIgnoreCase("reject_invitation_to_join_channel")) {
                Intent intent = new Intent();
                intent.setAction(getString(R.string.app_name) + "reject.invitation");
                intent.putExtra("participantName", participantName);
                sendBroadcast(intent);
            } else if (notifyType.equalsIgnoreCase("process_user_contacts")) {
                if (contact_status.equalsIgnoreCase("1")) {
                    startService(new Intent(getApplicationContext(), ContactService.class).putExtra("filename", contact_filename));

                /*    Intent intent = new Intent();
                    intent.setAction(getString(R.string.app_name) + "contact.process");
                    sendBroadcast(intent);*/
                }
            } else if (notifyType.equalsIgnoreCase("user_installed_app")) {
                AppDatabase db = AppDatabase.getAppDatabase(getBaseContext());
                ContactModel contactModel = db.userDao().getSearchContactWithNumber(contact_number);
                if (contactModel != null) {
                    contactModel.setStatus("pending");
                    contactModel.setId(contact_user_id);
                    db.userDao().updateUserStatus(contactModel);
                }
            } else if (notifyType.equalsIgnoreCase("unfriend")) {
                if (Helper.isContainValue(contact_number)) {
                    AppDatabase db = AppDatabase.getAppDatabase(getBaseContext());
                    ContactModel contactModel = db.userDao().getSearchContactWithNumber(contact_number);
                    if (contactModel != null) {
                        contactModel.setStatus("add");
                        db.userDao().updateUserStatus(contactModel);
                    }
                }
                Intent intent = new Intent();
                intent.setAction(getString(R.string.app_name) + "user.refresh");
                sendBroadcast(intent);
            } else if (notifyType.equalsIgnoreCase("join_channel")) {

            } else {
                sendNotification(getApplicationContext(), message);
            }
        }
    }

    private void sendNotification(final Context applicationContext, String message) {
        Intent intent = null;
        int notificationId = new Random().nextInt(60000);
        if (!TextUtils.isEmpty(image)) {
            try {
                contactPic = new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        try {
                            return Picasso.with(applicationContext).load(image).get();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if ((Helper.isContainValue(videoId) || Helper.isContainValue(type) || Helper.isContainValue(notifyType)) && Helper.isContainValue(AppPreferences.getInstance(applicationContext).getPreferencesString(AppConstants.ACCESS_TOKEN))) {
            if (Helper.isContainValue(videoId)) {
                intent = new Intent(getApplicationContext(), VideoPlayerActivity.class);
                intent.putExtra("videoId", videoId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else if (Helper.isContainValue(type)) {
                if (type.equalsIgnoreCase("video")) {
                    intent = new Intent(getApplicationContext(), VideoPlayerActivity.class);
                    intent.putExtra("videoId", postId);
                    intent.putExtra("commentId", commentId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                } else if (type.equalsIgnoreCase("community_post")) {
                    intent = new Intent(getApplicationContext(), PostDetailActivity.class);
                    intent.putExtra("postId", postId);
                    intent.putExtra("commentId", commentId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
            } else if (Helper.isContainValue(notifyType)) {
                intent = new Intent(getApplicationContext(), DashboardActivity.class);
                intent.putExtra("loadNotificationTab", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        } else {
            intent = new Intent(getApplicationContext(), SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = "OkTested";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.icon_silhouette)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationBuilder.setColor(applicationContext.getColor(R.color.colorPrimary));
        }

        if (null != contactPic) {
            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(contactPic));
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}