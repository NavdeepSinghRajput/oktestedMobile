package com.oktested.playSolo.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import com.oktested.R;
import com.oktested.home.model.Questions;
import com.oktested.playSolo.adapter.QuizOptionsAdapter;
import com.oktested.playSolo.model.InsertQuizQuestionResponse;
import com.oktested.playSolo.model.QuizStartResponse;
import com.oktested.playSolo.presenter.PlaySoloPresenter;
import com.oktested.quizResult.ui.QuizResultActivity;
import com.oktested.utils.Helper;

import java.util.ArrayList;
import java.util.Random;

public class PlaySoloActivity extends AppCompatActivity implements View.OnClickListener, PlaySoloView {

    private Context context;
    private PlaySoloPresenter playSoloPresenter;
    private CardView questionCV, questionImageCV;
    private ImageView questionIV, loadingIV;
    private SpinKitView spinKitView;
    private TextView questionTV, skipTV, scoreTV, timerTV;
    private RelativeLayout parentRL, timerRL;
    private ProgressBar timerProgressBar;
    private RecyclerView optionsRV;
    private LinearLayout indicatorLL;
    private ArrayList<Questions> questionsAL = new ArrayList<>();
    private ArrayList<Questions> loadingQuestionsAL = new ArrayList<>();
    private int position = 0, totalScore = 0, pos = 0, maxScore = 0;
    private ObjectAnimator objectAnimator;
    private ValueAnimator valueAnimator;
    private String quizId, attemptedTime;
    private boolean dataInserted = false, isAnswerCorrect, videoFinish = false;
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
        setContentView(R.layout.activity_play_solo);
        context = this;
        questionsAL = getIntent().getParcelableArrayListExtra("questionsAL");
        quizId = getIntent().getExtras().getString("quizId");
        inUi();

        callPlayQuiz();
    }

    private void callPlayQuiz() {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            playSoloPresenter.callPlayQuizApi(quizId);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void callInsertQuizDataApi(String answer, String choiceId) {
        if (Helper.isNetworkAvailable(context)) {
            playSoloPresenter.insertQuizDataApi(quizId, questionsAL.get(position).question.id, choiceId, answer, String.valueOf(questionsAL.get(position).score), attemptedTime);
        }
    }

    private void inUi() {
        playSoloPresenter = new PlaySoloPresenter(context, this);
        spinKitView = findViewById(R.id.spinKit);
        questionCV = findViewById(R.id.questionCV);
        questionImageCV = findViewById(R.id.questionImageCV);
        questionIV = findViewById(R.id.questionIV);
        loadingIV = findViewById(R.id.loadingIV);
        questionTV = findViewById(R.id.questionTV);
        skipTV = findViewById(R.id.skipTV);
        parentRL = findViewById(R.id.parentRL);
        timerRL = findViewById(R.id.timerRL);
        indicatorLL = findViewById(R.id.indicatorLL);
        scoreTV = findViewById(R.id.scoreTV);
        optionsRV = findViewById(R.id.optionsRV);
        timerTV = findViewById(R.id.timerTV);
        timerProgressBar = findViewById(R.id.timerProgressBar);

        buildQuizLL = findViewById(R.id.buildQuizLL);
        buildQuizIV = findViewById(R.id.buildQuizIV);
        buildTitleTV = findViewById(R.id.buildTitleTV);
        buildMessageTV = findViewById(R.id.buildMessageTV);

        playerView = findViewById(R.id.playerView);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        player = new SimpleExoPlayer.Builder(context).build();
        playerView.setPlayer(player);

        skipTV.setOnClickListener(this);

        for (int i = 0; i < questionsAL.size(); i++) {
            TextView textView = new TextView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) Helper.pxFromDp(context, 15), (int) Helper.pxFromDp(context, 6));
            layoutParams.setMargins(0, 0, (int) Helper.pxFromDp(context, 3), 0);
            textView.setLayoutParams(layoutParams);
            textView.setBackground(context.getResources().getDrawable(R.drawable.unselect_indicator));
            indicatorLL.addView(textView);
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

                skipTV.setVisibility(View.INVISIBLE);
                parentRL.setVisibility(View.VISIBLE);
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

                skipTV.setVisibility(View.INVISIBLE);
                parentRL.setVisibility(View.VISIBLE);
                if (Helper.isContainValue(questionsAL.get(position).question.text)) {
                    questionTV.setText(Html.fromHtml(questionsAL.get(position).question.text));
                }

                if (Helper.isContainValue(questionsAL.get(position).question.image)) {
                    Glide.with(context)
                            .load(questionsAL.get(position).question.image)
                            .placeholder(R.drawable.quiz_placeholder)
                            .into(questionIV);
                }
                maxScore = maxScore + questionsAL.get(position).score;

                setQuizDisplayAnimation();
                setOptionsAdapter();
            } else if (questionsAL.get(position).question.type.equalsIgnoreCase("image")) {
                questionImageCV.setVisibility(View.VISIBLE);
                questionCV.setVisibility(View.GONE);

                skipTV.setVisibility(View.INVISIBLE);
                parentRL.setVisibility(View.VISIBLE);
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
        optionsRV.setHasFixedSize(true);
        optionsRV.setLayoutManager(new LinearLayoutManager(context));
        optionsRV.setItemAnimator(new DefaultItemAnimator());
        QuizOptionsAdapter quizOptionsAdapter = new QuizOptionsAdapter(context, questionsAL.get(position).choices, questionsAL.get(position).correct_ans, true, new QuizOptionsAdapter.Quiz() {
            @Override
            public void submitQuiz(boolean isCorrect, String answer, String choiceId, boolean optionSelect) {
                isAnswerCorrect = isCorrect;
                if (optionSelect) {
                    new Handler().postDelayed(() -> {
                        if (isCorrect) {
                            totalScore = totalScore + questionsAL.get(position).score;
                            scoreTV.setText(totalScore + "");

                            playCorrectAnswerSound();
                        } else {
                            playWrongAnswerSound();
                        }

                        skipTV.setBackground(context.getResources().getDrawable(R.drawable.next_bg));
                        if (position == questionsAL.size() - 1) {
                            skipTV.setText("Finish");
                        } else {
                            skipTV.setText("Next");
                        }
                        setPulseAnimation();
                    }, 2000);

                    objectAnimator.pause();
                    valueAnimator.pause();
                    playOptionSelectSound();
                } else {
                    if (isCorrect) {
                        totalScore = totalScore + questionsAL.get(position).score;
                        scoreTV.setText(totalScore + "");
                    }

                    skipTV.setBackground(context.getResources().getDrawable(R.drawable.next_bg));
                    if (position == questionsAL.size() - 1) {
                        skipTV.setText("Finish");
                    } else {
                        skipTV.setText("Next");
                    }
                    setPulseAnimation();
                    playWrongAnswerSound();
                }

                dataInserted = true;
                callInsertQuizDataApi(answer, choiceId);
            }
        });
        optionsRV.setAdapter(quizOptionsAdapter);

        // When we reach at last question
        if (position == questionsAL.size() - 1) {
            skipTV.setBackground(context.getResources().getDrawable(R.drawable.next_bg));
            skipTV.setText("Finish");
        }

        // Move the indicator position one by one
        indicatorLL.getChildAt(position).setBackground(context.getResources().getDrawable(R.drawable.select_indicator));

        if (Helper.isContainValue(String.valueOf(questionsAL.get(position).timer))) {
            timerTV.setText(questionsAL.get(position).timer + "");

            objectAnimator = ObjectAnimator.ofInt(timerProgressBar, "progress", 0, 100);
            objectAnimator.setDuration(questionsAL.get(position).timer * 1000);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    quizOptionsAdapter.timerComplete(false, -1);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });

            valueAnimator = ValueAnimator.ofInt(questionsAL.get(position).timer, 0);
            valueAnimator.setDuration(questionsAL.get(position).timer * 1000);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    timerTV.setText(animation.getAnimatedValue().toString());
                    if (questionsAL.get(position).timer == (int) animation.getAnimatedValue()) {
                        attemptedTime = String.valueOf(questionsAL.get(position).timer - ((int) animation.getAnimatedValue() - 1));
                    } else {
                        attemptedTime = String.valueOf(questionsAL.get(position).timer - (int) animation.getAnimatedValue());
                    }
                }
            });

            valueAnimator.start();
            objectAnimator.start();
        }
    }

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

        messageTV.setText("Are you sure you want to leave this quiz");

        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        yesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (objectAnimator != null) {
                    objectAnimator.removeAllListeners();
                }
                alertDialog.dismiss();
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.skipTV:
                if (!dataInserted) {
                    isAnswerCorrect = false;
                    callInsertQuizDataApi("skipped", "");
                }
                dataInserted = false;

                if (!skipTV.getText().toString().trim().equalsIgnoreCase("Finish")) {
                    attemptedTime = "";
                    skipTV.setText("Skip");
                    skipTV.setBackground(context.getResources().getDrawable(R.drawable.skip_bg));

                    // Set the previous indicator value based on correct or wrong answer
                    if (isAnswerCorrect) {
                        indicatorLL.getChildAt(position).setBackground(context.getResources().getDrawable(R.drawable.correct_indicator));
                    } else {
                        indicatorLL.getChildAt(position).setBackground(context.getResources().getDrawable(R.drawable.wrong_indicator));
                    }

                    objectAnimator.removeAllListeners();
                    position++;
                    setQuizQuestionUi();
                } else {
                    skipTV.setEnabled(false);
                    new Handler().postDelayed(() -> {
                        objectAnimator.removeAllListeners();
                        Intent intent = new Intent(context, QuizResultActivity.class);
                        intent.putExtra("quizId", quizId);
                        intent.putExtra("maxScore", maxScore);
                        startActivity(intent);
                        finish();
                    }, 1000);
                }
                break;

            default:
                break;
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
        playSoloPresenter.onDestroyedView();
        if (playerView != null && player != null) {
            player.release();
            player = null;
        }
        super.onDestroy();
    }

    @Override
    public void setPlaySoloResponse(QuizStartResponse quizStartResponse) {
        if (quizStartResponse != null && Helper.isContainValue(quizStartResponse.status)) {
            if (quizStartResponse.status.equalsIgnoreCase("1")) {
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
                if (playbackState == Player.STATE_ENDED && !videoFinish) {
                    videoFinish = true;
                    playerView.setVisibility(View.GONE);
                    setQuizQuestionUi();
                }
            }
        });
    }

    @Override
    public void insertQuizDataResponse(InsertQuizQuestionResponse insertQuizQuestionResponse) {

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

    private void setPulseAnimation() {
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                skipTV,
                PropertyValuesHolder.ofFloat("scaleX", 1.4f),
                PropertyValuesHolder.ofFloat("scaleY", 1.4f));
        scaleDown.setDuration(100);
        scaleDown.setRepeatCount(1);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();
    }

    private void setQuizDisplayAnimation() {
        YoYo.with(Techniques.ZoomInDown).duration(600).repeat(0).playOn(indicatorLL);
        YoYo.with(Techniques.ZoomInDown).duration(600).repeat(0).playOn(questionCV);
        YoYo.with(Techniques.ZoomInDown).duration(600).repeat(0).playOn(questionImageCV);

        YoYo.with(Techniques.ZoomInUp).duration(600).repeat(0).playOn(optionsRV);
        YoYo.with(Techniques.ZoomInUp).duration(600).repeat(0).playOn(timerRL);
        YoYo.with(Techniques.ZoomInUp).duration(600).repeat(0).playOn(scoreTV);

        new Handler().postDelayed(() -> {
            skipTV.setVisibility(View.VISIBLE);
            setPulseAnimation();
        }, 1000);

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
}