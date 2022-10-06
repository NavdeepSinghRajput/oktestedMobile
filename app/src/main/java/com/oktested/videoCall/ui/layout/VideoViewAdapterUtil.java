package com.oktested.videoCall.ui.layout;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.oktested.R;
import com.oktested.utils.AppConstants;
import com.oktested.utils.Helper;
import com.oktested.videoCall.model.ChannelDetailResponse;
import com.oktested.videoCall.model.MemberInfoKeyValueData;
import com.oktested.videoCall.model.QuestionWiseResultKeyValueData;
import com.oktested.videoCall.propeller.Constant;
import com.oktested.videoCall.propeller.UserStatusData;
import com.oktested.videoCall.propeller.VideoInfoData;
import com.oktested.videoCall.propeller.ui.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class VideoViewAdapterUtil {

    private static final boolean DEBUG = false;

    public static void composeDataItem1(final ArrayList<UserStatusData> users, HashMap<Integer, SurfaceView> uids, int localUid) {
        for (HashMap.Entry<Integer, SurfaceView> entry : uids.entrySet()) {
         /*   if (DEBUG) {
                log.debug("composeDataItem1 " + (entry.getKey() & 0xFFFFFFFFL) + " " + (localUid & 0xFFFFFFFFL) + " " + users.size() + " " + entry.getValue());
            }*/
            SurfaceView surfaceV = entry.getValue();
            surfaceV.setZOrderOnTop(false);
            surfaceV.setZOrderMediaOverlay(false);
            searchUidsAndAppend(users, entry, localUid, UserStatusData.DEFAULT_STATUS, UserStatusData.DEFAULT_VOLUME, null);
        }
        removeNotExisted(users, uids, localUid);
    }

    private static void removeNotExisted(ArrayList<UserStatusData> users, HashMap<Integer, SurfaceView> uids, int localUid) {
       /* if (DEBUG) {
            log.debug("removeNotExisted all " + uids + " " + users.size());
        }*/
        Iterator<UserStatusData> it = users.iterator();
        while (it.hasNext()) {
            UserStatusData user = it.next();
         /*   if (DEBUG) {
                log.debug("removeNotExisted " + user + " " + localUid);
            }*/
            if (uids.get(user.mUid) == null && user.mUid != localUid) {
                it.remove();
            }
        }
    }

    private static void searchUidsAndAppend(ArrayList<UserStatusData> users, HashMap.Entry<Integer, SurfaceView> entry,
                                            int localUid, Integer status, int volume, VideoInfoData i) {
        if (entry.getKey() == 0 || entry.getKey() == localUid) {
            boolean found = false;
            for (UserStatusData user : users) {
                if ((user.mUid == entry.getKey() && user.mUid == 0) || user.mUid == localUid) { // first time
                    user.mUid = localUid;
                    if (status != null) {
                        user.mStatus = status;
                    }
                    user.mVolume = volume;
                    user.setVideoInfo(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                users.add(0, new UserStatusData(localUid, entry.getValue(), status, volume, i));
            }
        } else {
            boolean found = false;
            for (UserStatusData user : users) {
                if (user.mUid == entry.getKey()) {
                    if (status != null) {
                        user.mStatus = status;
                    }
                    user.mVolume = volume;
                    user.setVideoInfo(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                users.add(new UserStatusData(entry.getKey(), entry.getValue(), status, volume, i));
            }
        }
    }

    public static void composeDataItem(final ArrayList<UserStatusData> users, HashMap<Integer, SurfaceView> uids,
                                       int localUid,
                                       HashMap<Integer, Integer> status,
                                       HashMap<Integer, Integer> volume,
                                       HashMap<Integer, VideoInfoData> video) {
        composeDataItem(users, uids, localUid, status, volume, video, 0);
    }

    public static void composeDataItem(final ArrayList<UserStatusData> users, HashMap<Integer, SurfaceView> uids,
                                       int localUid,
                                       HashMap<Integer, Integer> status,
                                       HashMap<Integer, Integer> volume,
                                       HashMap<Integer, VideoInfoData> video, int uidExcepted) {
        for (HashMap.Entry<Integer, SurfaceView> entry : uids.entrySet()) {
            int uid = entry.getKey();

            if (uid == uidExcepted && uidExcepted != 0) {
                continue;
            }

            boolean local = uid == 0 || uid == localUid;

            Integer s = null;
            if (status != null) {
                s = status.get(uid);
                if (local && s == null) { // check again
                    s = status.get(uid == 0 ? localUid : 0);
                }
            }
            Integer v = null;
            if (volume != null) {
                v = volume.get(uid);
                if (local && v == null) { // check again
                    v = volume.get(uid == 0 ? localUid : 0);
                }
            }
            if (v == null) {
                v = UserStatusData.DEFAULT_VOLUME;
            }
            VideoInfoData i;
            if (video != null) {
                i = video.get(uid);
                if (local && i == null) { // check again
                    i = video.get(uid == 0 ? localUid : 0);
                }
            } else {
                i = null;
            }
          /*  if (DEBUG) {
                log.debug("composeDataItem " + users + " " + entry + " " + (localUid & 0XFFFFFFFFL) + " " + s + " " + v + " " + i + " " + local + " " + (uid & 0XFFFFFFFFL) + " " + (uidExcepted & 0XFFFFFFFFL));
            }*/
            searchUidsAndAppend(users, entry, localUid, s, v, i);
        }

        removeNotExisted(users, uids, localUid);
    }

    public static void renderExtraData(Context context, UserStatusData user, VideoUserStatusHolder myHolder, boolean quizSelected, boolean allAnswerSubmit, boolean displayScore, HashMap<Integer, QuestionWiseResultKeyValueData> dataHashMap, ChannelDetailResponse.ChannelDetailResponseData detailResponseData, HashMap<Integer, MemberInfoKeyValueData> memberInfoKeyValueHM) {
        if (user.mStatus != null) {
//            if ((user.mStatus & UserStatusData.VIDEO_MUTED) != 0) {
            if (memberInfoKeyValueHM != null) {
                MemberInfoKeyValueData infoKeyValueData = memberInfoKeyValueHM.get(user.mUid);
                if (infoKeyValueData != null) {
                    myHolder.nameTV.setVisibility(View.VISIBLE);
                    myHolder.userIV.setVisibility(View.VISIBLE);

                    if (detailResponseData.user_channel_id == user.mUid) {
                        myHolder.nameTV.setText("You");
                    } else {
                        myHolder.nameTV.setText(infoKeyValueData.name);
                    }

                    if (Helper.isContainValue(infoKeyValueData.profile_pic)) {
                        if (quizSelected) {
                            Glide.with(context)
                                    .load(infoKeyValueData.profile_pic)
                                    .centerCrop()
                                    .placeholder(R.drawable.ic_vector_default_profile_square)
                                    .into(myHolder.userAfterIV);
                        } else {
                            Glide.with(context)
                                    .load(infoKeyValueData.profile_pic)
                                    .centerCrop()
                                    .placeholder(R.drawable.ic_vector_default_profile)
                                    .into(myHolder.userIV);
                        }
                    }

                    if (infoKeyValueData.is_camera_enable) {
                        myHolder.userLL.setVisibility(View.GONE);
                        myHolder.userAfterIV.setVisibility(View.GONE);
                        myHolder.userAfterRL.setVisibility(View.GONE);
                    } else {
                        if (quizSelected) {
                            myHolder.userLL.setVisibility(View.GONE);
                            myHolder.userAfterRL.setVisibility(View.VISIBLE);
                            myHolder.userAfterIV.setVisibility(View.VISIBLE);
                        } else {
                            myHolder.userAfterRL.setVisibility(View.GONE);
                            myHolder.userAfterIV.setVisibility(View.GONE);
                            myHolder.userLL.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    myHolder.userLL.setVisibility(View.VISIBLE);
                    myHolder.nameTV.setVisibility(View.GONE);
                    myHolder.userIV.setVisibility(View.GONE);
                }
            } else {
                myHolder.userLL.setVisibility(View.VISIBLE);
                myHolder.nameTV.setVisibility(View.GONE);
                myHolder.userIV.setVisibility(View.GONE);
            }

            if (quizSelected) {
                myHolder.videoCardView.setRadius((int) Helper.pxFromDp(context, 6));
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) myHolder.videoCardView.getLayoutParams();
                layoutParams.setMargins(0, 0, (int) Helper.pxFromDp(context, 10), (int) Helper.pxFromDp(context, 15));
                myHolder.videoCardView.requestLayout();
            }

            if (allAnswerSubmit) {
                if (displayScore) {
                    myHolder.answerRL.setVisibility(View.GONE);
                    myHolder.answerIV.setVisibility(View.GONE);
                    myHolder.scoreTV.setVisibility(View.VISIBLE);

                    QuestionWiseResultKeyValueData keyValueData = dataHashMap.get(user.mUid);
                    if (keyValueData != null) {
                       /* if (keyValueData.status.equalsIgnoreCase("not_playing")) {
                            myHolder.scoreTV.setText("0");
                        } else {*/
                        myHolder.scoreTV.setText(keyValueData.total_score + "");
                        //}
                    }
                } else {
                    myHolder.answerRL.setVisibility(View.VISIBLE);
                    myHolder.answerIV.setVisibility(View.VISIBLE);
                    myHolder.scoreTV.setVisibility(View.GONE);

                    QuestionWiseResultKeyValueData keyValueData = dataHashMap.get(user.mUid);
                    if (keyValueData != null) {
                        if (keyValueData.status.equalsIgnoreCase("not_playing")) {
                            myHolder.answerRL.setBackground(context.getResources().getDrawable(R.drawable.wrong_answer_border_bg));
                            myHolder.answerIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_wrong));
                        } else {
                            if (keyValueData.current_qus_status.equalsIgnoreCase("correct")) {
                                myHolder.answerRL.setBackground(context.getResources().getDrawable(R.drawable.correct_answer_border_bg));
                                myHolder.answerIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_correct));
                            } else {
                                myHolder.answerRL.setBackground(context.getResources().getDrawable(R.drawable.wrong_answer_border_bg));
                                myHolder.answerIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_wrong));
                            }
                        }
                    }
                }
            } else {
                myHolder.answerRL.setVisibility(View.GONE);
                myHolder.answerIV.setVisibility(View.GONE);
                myHolder.scoreTV.setVisibility(View.GONE);
            }

            if (detailResponseData != null) {
                if (!quizSelected) {
                    if (detailResponseData.is_channel_admin) {
                        if (detailResponseData.user_channel_id == user.mUid) {
                            myHolder.removeTV.setVisibility(View.GONE);
                        } else {
                            myHolder.removeTV.setVisibility(View.VISIBLE);
                        }
                    } else {
                        myHolder.removeTV.setVisibility(View.GONE);
                    }

                    if (detailResponseData.user_channel_id == user.mUid) {
                        myHolder.userNameTV.setText("You");
                    } else {
                        if (memberInfoKeyValueHM != null) {
                            MemberInfoKeyValueData infoKeyValueData = memberInfoKeyValueHM.get(user.mUid);
                            if (infoKeyValueData != null) {
                                myHolder.userNameTV.setText(infoKeyValueData.name);
                            }
                        }
                    }

                    if (detailResponseData.user_channel_id == user.mUid) {
                        if (detailResponseData.is_channel_admin) {
                            myHolder.readyRB.setVisibility(View.GONE);
                        } else {
                            myHolder.readyRB.setVisibility(View.VISIBLE);
                            if (detailResponseData.is_ready) {
                                myHolder.readyRB.setChecked(true);
                                myHolder.readyRB.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent)));
                            } else {
                                myHolder.readyRB.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorWhite)));
                                myHolder.readyRB.setChecked(false);
                            }
                        }
                    } else {
                        myHolder.readyRB.setVisibility(View.GONE);
                    }

                    if (memberInfoKeyValueHM != null) {
                        MemberInfoKeyValueData infoKeyValueData = memberInfoKeyValueHM.get(user.mUid);
                        if (infoKeyValueData != null) {
                            if (infoKeyValueData.is_ready) {
                                myHolder.readyTV.setTextColor(context.getResources().getColor(R.color.colorAccent));
                            } else {
                                myHolder.readyTV.setTextColor(context.getResources().getColor(R.color.unreadyColor));
                            }
                        }
                    }

                    if (detailResponseData.user_channel_id == user.mUid) {
                        if (memberInfoKeyValueHM != null) {
                            MemberInfoKeyValueData infoKeyValueData = memberInfoKeyValueHM.get(user.mUid);
                            if (infoKeyValueData != null) {
                                if (infoKeyValueData.is_channel_admin) {
                                    myHolder.readyTV.setText("HOST");
                                } else {
                                    myHolder.readyTV.setText("READY");
                                }
                            }
                        }
                    } else {
                        if (memberInfoKeyValueHM != null) {
                            MemberInfoKeyValueData infoKeyValueData = memberInfoKeyValueHM.get(user.mUid);
                            if (infoKeyValueData != null) {
                                if (infoKeyValueData.is_channel_admin) {
                                    myHolder.readyTV.setText("HOST");
                                    myHolder.readyTV.setTextColor(context.getResources().getColor(R.color.colorHeader));
                                } else {
                                    if (infoKeyValueData.is_ready) {
                                        myHolder.readyTV.setText("READY");
                                        myHolder.readyTV.setTextColor(context.getResources().getColor(R.color.colorAccent));
                                    } else {
                                        myHolder.readyTV.setText("NOT READY");
                                        myHolder.readyTV.setTextColor(context.getResources().getColor(R.color.unreadyColor));
                                    }
                                }
                            }
                        }
                    }

                    myHolder.removeTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (memberInfoKeyValueHM != null) {
                                MemberInfoKeyValueData infoKeyValueData = memberInfoKeyValueHM.get(user.mUid);
                                if (infoKeyValueData != null) {
                                    Intent intent = new Intent(AppConstants.REMOVE_PARTICIPANT);
                                    intent.putExtra("participantName", infoKeyValueData.name);
                                    intent.putExtra("participantId", infoKeyValueData.user_id);
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                }
                            }
                        }
                    });

                    //  if (!detailResponseData.is_channel_admin) {
                    myHolder.readyRB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (detailResponseData.is_ready) {
                                Intent intent = new Intent(AppConstants.READY_UNREADY);
                                intent.putExtra("action", "not_ready");
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            } else {
                                Intent intent = new Intent(AppConstants.READY_UNREADY);
                                intent.putExtra("action", "ready");
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            }
                        }
                    });
                    // }
                } else {
                    myHolder.removeTV.setVisibility(View.GONE);
                    myHolder.readyLL.setVisibility(View.GONE);
                    myHolder.userNameTV.setVisibility(View.GONE);
                }
            }

        /*    if ((user.mStatus & UserStatusData.AUDIO_MUTED) != 0) {
                myHolder.mIndicator.setImageResource(R.drawable.icon_muted);
                myHolder.mIndicator.setVisibility(View.VISIBLE);
                myHolder.mIndicator.setTag(System.currentTimeMillis());
                return;
            } else {
                myHolder.mIndicator.setTag(null);
                myHolder.mIndicator.setVisibility(View.INVISIBLE);
            }*/
        }

      /*  Object tag = myHolder.mIndicator.getTag();
        if (tag != null && System.currentTimeMillis() - (Long) tag < 1500) { // workaround for audio volume comes just later than mute
            return;
        }

        int volume = user.mVolume;

        if (volume > 0) {
            myHolder.mIndicator.setImageResource(R.drawable.icon_speaker);
            myHolder.mIndicator.setVisibility(View.VISIBLE);
        } else {
            myHolder.mIndicator.setVisibility(View.INVISIBLE);
        }*/

        if (Constant.SHOW_VIDEO_INFO && user.getVideoInfoData() != null) {
            VideoInfoData videoInfo = user.getVideoInfoData();
            myHolder.mMetaData.setText(ViewUtil.composeVideoInfoString(context, videoInfo));
            myHolder.mVideoInfo.setVisibility(View.VISIBLE);
        } else {
            myHolder.mVideoInfo.setVisibility(View.GONE);
        }
    }

    public static void stripView(SurfaceView view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((FrameLayout) parent).removeView(view);
        }
    }
}