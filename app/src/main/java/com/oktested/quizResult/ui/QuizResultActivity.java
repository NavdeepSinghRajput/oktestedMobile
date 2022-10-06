package com.oktested.quizResult.ui;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ybq.android.spinkit.SpinKitView;
import com.oktested.R;
import com.oktested.home.adapter.CategoryQuizDetailAdapter;
import com.oktested.quizResult.model.QuizResultDataResponse;
import com.oktested.quizResult.model.QuizResultResponse;
import com.oktested.quizResult.presenter.QuizResultPresenter;
import com.oktested.utils.AppConstants;
import com.oktested.utils.DataHolder;
import com.oktested.utils.Helper;
import com.oktested.videoCall.model.ConstantApp;
import com.oktested.videoCall.model.RecommendedQuizResponse;
import com.warkiz.widget.IndicatorSeekBar;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuizResultActivity extends AppCompatActivity implements QuizResultView {

    private Context context;
    private TextView quizTitleTV, nameTV, scoreTV, accuracyTV, timeTV, messageTV;
    private CircleImageView userProfilePic;
    private RatingBar ratingBar;
    private LinearLayout parentLL, inflatedLL;
    private RelativeLayout moreQuizRL;
    private SpinKitView spinKitView;
    private QuizResultPresenter quizResultPresenter;
    private String quizId;
    private QuizResultDataResponse quizResultDataResponse;

    private CircleImageView badgeImage;
    private TextView currentLevel, upcomingLevel, quizPlayedCount;
    private IndicatorSeekBar seek_bar_indicators;
    private int maxScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);
        context = this;

        quizId = getIntent().getExtras().getString("quizId");
        maxScore = getIntent().getExtras().getInt("maxScore");
        inUi();

        callSoloQuizResult();
    }

    private void inUi() {
        quizResultPresenter = new QuizResultPresenter(context, this);
        quizTitleTV = findViewById(R.id.quizTitleTV);
        nameTV = findViewById(R.id.nameTV);
        messageTV = findViewById(R.id.messageTV);
        scoreTV = findViewById(R.id.scoreTV);
        accuracyTV = findViewById(R.id.accuracyTV);
        timeTV = findViewById(R.id.timeTV);
        userProfilePic = findViewById(R.id.userProfilePic);
        ratingBar = findViewById(R.id.ratingBar);
        parentLL = findViewById(R.id.parentLL);
        spinKitView = findViewById(R.id.spinKit);
        moreQuizRL = findViewById(R.id.moreQuizRL);
        inflatedLL = findViewById(R.id.inflatedLL);

        badgeImage = findViewById(R.id.badgeImage);
        currentLevel = findViewById(R.id.currentLevel);
        upcomingLevel = findViewById(R.id.upcomingLevel);
        quizPlayedCount = findViewById(R.id.quizPlayedCount);
        seek_bar_indicators = findViewById(R.id.seek_bar_indicators);
        CircleImageView friendPic = findViewById(R.id.friendPic);

        TextView shareResultTV = findViewById(R.id.shareResultTV);
        shareResultTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission()) {
                    new ShareSoloQuizResult(context, inflatedLL, quizResultDataResponse, quizId, maxScore);
                }
            }
        });

        if (Helper.isContainValue(DataHolder.getInstance().getUserDataresponse.picture)) {
            Glide.with(context)
                    .load(DataHolder.getInstance().getUserDataresponse.picture)
                    .centerCrop()
                    .placeholder(R.drawable.ic_vector_default_profile)
                    .into(friendPic);
        } else if (Helper.isContainValue(DataHolder.getInstance().getUserDataresponse.avatar)) {
            Glide.with(context)
                    .load(DataHolder.getInstance().getUserDataresponse.avatar)
                    .centerCrop()
                    .placeholder(R.drawable.ic_vector_default_profile)
                    .into(friendPic);
        } else {
            friendPic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_default_profile));
        }
    }

    private void callSoloQuizResult() {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            quizResultPresenter.callQuizResultApi(quizId);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    @Override
    public void setQuizResultResponse(QuizResultResponse quizResultResponse) {
        if (quizResultResponse != null && Helper.isContainValue(quizResultResponse.status)) {
            if (quizResultResponse.status.equalsIgnoreCase("1")) {
                if (quizResultResponse.data != null) {
                    quizResultDataResponse = quizResultResponse.data;
                    parentLL.setVisibility(View.VISIBLE);

                    if (Helper.isContainValue(quizResultResponse.data.user_pic)) {
                        Glide.with(context)
                                .load(quizResultResponse.data.user_pic)
                                .centerCrop()
                                .placeholder(R.drawable.ic_vector_default_profile)
                                .into(userProfilePic);
                    }

                    if (Helper.isContainValue(quizResultResponse.data.user_name)) {
                        nameTV.setText(quizResultResponse.data.user_name);
                    }

                    if (Helper.isContainValue(quizResultResponse.data.message)) {
                        messageTV.setText(quizResultResponse.data.message);
                    }

                    if (Helper.isContainValue(quizResultResponse.data.quiz_title)) {
                        quizTitleTV.setText(quizResultResponse.data.quiz_title);
                    }

                    if (Helper.isContainValue(String.valueOf(quizResultResponse.data.total_score))) {
                        scoreTV.setText(quizResultResponse.data.total_score + "/" + maxScore);
                        ratingBar.setNumStars(maxScore);
                        ratingBar.setMax(maxScore);
                        ratingBar.setRating(quizResultResponse.data.total_score);
                    }

                    if (Helper.isContainValue(quizResultResponse.data.avg_time)) {
                        timeTV.setText(quizResultResponse.data.avg_time + "sec/que");
                    }

                    if (Helper.isContainValue(String.valueOf(quizResultResponse.data.avg_accuracy))) {
                        accuracyTV.setText(quizResultResponse.data.avg_accuracy + "%");
                    }

                    if (quizResultResponse.data.level_count != -1) {
                        currentLevel.setText("Lv. " + quizResultResponse.data.level_count);
                        upcomingLevel.setText("Lv. " + (quizResultResponse.data.level_count + 1));
                    }
                    if (Helper.isContainValue(quizResultResponse.data.level_url)) {
                        Glide.with(context)
                                .load(quizResultResponse.data.level_url)
                                .centerCrop()
                                .placeholder(R.drawable.placeholder)
                                .into(badgeImage);
                    } else {
                        badgeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder));
                    }

                    seek_bar_indicators.setMax(quizResultResponse.data.level_max_count);
                    seek_bar_indicators.setMin(quizResultResponse.data.level_min_count);
                    seek_bar_indicators.setProgress(quizResultResponse.data.level_current_count);
                    quizPlayedCount.setText(quizResultResponse.data.level_current_count + "/" + quizResultResponse.data.level_max_count);

                    YoYo.with(Techniques.BounceIn).duration(800).repeat(0).playOn(parentLL);
                    //setAnimations(quizResultResponse.data.level_current_count);
                    if (Helper.isNetworkAvailable(context)) {
                        quizResultPresenter.callRecommendedQuizApi(quizId, "0");
                    }
                }
            } else {
                if (Helper.isContainValue(quizResultResponse.err)) {
                    showMessage(quizResultResponse.err);
                }
            }
        }
    }

    @Override
    public void setRecommendedQuizResponse(RecommendedQuizResponse recommendedQuizResponse) {
        if (recommendedQuizResponse != null && recommendedQuizResponse.data != null && recommendedQuizResponse.data.size() > 0) {
            moreQuizRL.setVisibility(View.VISIBLE);

            RecyclerView moreQuizRV = findViewById(R.id.moreQuizRV);
            moreQuizRV.setHasFixedSize(true);
            moreQuizRV.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            moreQuizRV.setItemAnimator(new DefaultItemAnimator());
            CategoryQuizDetailAdapter categoryQuizDetailAdapter = new CategoryQuizDetailAdapter(context, recommendedQuizResponse.data, "More like this", recommendedQuizResponse.next_offset, AppConstants.SECTION_QUIZ_CATEGORY_DETAILS_LAYOUT);
            moreQuizRV.setAdapter(categoryQuizDetailAdapter);
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
        quizResultPresenter.onDestroyedView();
        super.onDestroy();
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
                new ShareSoloQuizResult(context, inflatedLL, quizResultDataResponse, quizId, maxScore);
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

    private void setAnimations(int level_current_count) {
        YoYo.with(Techniques.BounceIn).duration(600).repeat(0).playOn(parentLL);

        new Handler().postDelayed(() -> {
            ValueAnimator anim = ValueAnimator.ofInt(0, level_current_count);
            anim.setDuration(100);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animProgress = (Integer) animation.getAnimatedValue();
                    seek_bar_indicators.setProgress(animProgress);
                }
            });
            anim.start();
        }, 600);
    }
}