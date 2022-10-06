package com.oktested.pickQuiz.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.oktested.R;
import com.oktested.home.model.Questions;
import com.oktested.pickQuiz.model.SelectQuizResponse;
import com.oktested.pickQuiz.model.SuggestQuizResponse;
import com.oktested.pickQuiz.presenter.PickQuizDetailPresenter;
import com.oktested.quizDetail.model.QuizDetailResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.Helper;

import java.util.ArrayList;

public class PickQuizDetailFragment extends Fragment implements PickQuizDetailView, View.OnClickListener {

    private Context context;
    private ImageView quizCoverPic, crossIV;
    private TextView quizCategory, questionLength, timePerQuestion, questionPoint, quizTitle, addFriends;
    private LinearLayout parentRL;
    private PickQuizDetailPresenter pickQuizDetailPresenter;
    private BroadcastReceiver mReceiver;
    private SpinKitView spinKit;
    private static HandleQuizLoading handleQuizLoading;
    private ArrayList<Questions> questionsAL;
    private static boolean isAdmin;
    private static String article, channelId;

    public static PickQuizDetailFragment newInstance(HandleQuizLoading handleQuizLoadings, boolean admin, String articles, String channelName) {
        PickQuizDetailFragment fragment = new PickQuizDetailFragment();
        handleQuizLoading = handleQuizLoadings;
        isAdmin = admin;
        channelId = channelName;
        article = articles;
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void receiverRegister() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                parentRL.setVisibility(View.INVISIBLE);
                String articleId = intent.getStringExtra("articleId");
                article = articleId;
                callQuizDetail(articleId);
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "quiz.detail");
        context.registerReceiver(mReceiver, intentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_pick_quiz_detail, container, false);
        pickQuizDetailPresenter = new PickQuizDetailPresenter(context, this);

        inUi(root_view);
        receiverRegister();
        if (article != null) {
            callQuizDetail(article);
        }
        return root_view;
    }

    private void callQuizDetail(String articleId) {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            pickQuizDetailPresenter.callQuizDetailApi(articleId);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    @Override
    public void onDestroyView() {
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
        }
        pickQuizDetailPresenter.onDestroyedView();
        super.onDestroyView();
    }

    private void inUi(View root_view) {
        parentRL = (LinearLayout) root_view.findViewById(R.id.parentRL);
        quizCoverPic = (ImageView) root_view.findViewById(R.id.quizCoverPic);
        crossIV = (ImageView) root_view.findViewById(R.id.crossIV);
        quizTitle = (TextView) root_view.findViewById(R.id.quizTitle);
        questionLength = (TextView) root_view.findViewById(R.id.questionLength);
        timePerQuestion = (TextView) root_view.findViewById(R.id.timePerQuestion);
        questionPoint = (TextView) root_view.findViewById(R.id.questionPoint);
        addFriends = (TextView) root_view.findViewById(R.id.addFriends);
        TextView pickAnother = (TextView) root_view.findViewById(R.id.pickAnother);
        quizCategory = (TextView) root_view.findViewById(R.id.quizCategory);
        spinKit = (SpinKitView) root_view.findViewById(R.id.spinKit);
        if (article != null) {
            pickAnother.setVisibility(View.GONE);
        } else {
            pickAnother.setVisibility(View.VISIBLE);
        }
        pickAnother.setOnClickListener(this);
        addFriends.setOnClickListener(this);
        crossIV.setOnClickListener(this);

        if (isAdmin) {
            addFriends.setText("Select");
        } else {
            addFriends.setText("Suggest");
        }
    }

    @Override
    public void setQuizDetailResponse(QuizDetailResponse quizDetailResponse) {
        if (quizDetailResponse != null && quizDetailResponse.data != null) {
            parentRL.setVisibility(View.VISIBLE);
            crossIV.setVisibility(View.VISIBLE);
            if (Helper.isContainValue(quizDetailResponse.data.feature_img_1x1)) {
                Glide.with(getContext())
                        .load(quizDetailResponse.data.feature_img_1x1)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(quizCoverPic);
            } else {
                quizCoverPic.setImageDrawable(getContext().getResources().getDrawable(R.drawable.placeholder));
            }

            if (Helper.isContainValue(quizDetailResponse.data.cat_display.get(0).category_display)) {
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
    public void selectQuizResponse(SelectQuizResponse selectQuizResponse) {
        addFriends.setEnabled(true);
        if (selectQuizResponse != null && Helper.isContainValue(selectQuizResponse.status)) {
            if (selectQuizResponse.status.equalsIgnoreCase("1")) {
                if (Helper.isContainValue(selectQuizResponse.message)) {
                    showMessage(selectQuizResponse.message);
                }

                Intent intent = new Intent(AppConstants.QUIZ_SELECTED);
                intent.putExtra("quizId", article);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                if (getActivity() != null) {
                    getActivity().finish();
                }
            } else {
                if (Helper.isContainValue(selectQuizResponse.err)) {
                    showMessage(selectQuizResponse.err);
                }
            }
        }
    }

    @Override
    public void suggestQuizResponse(SuggestQuizResponse suggestQuizResponse) {
        addFriends.setEnabled(true);
        if (suggestQuizResponse != null && Helper.isContainValue(suggestQuizResponse.status)) {
            if (suggestQuizResponse.status.equalsIgnoreCase("1")) {
                if (Helper.isContainValue(suggestQuizResponse.message)) {
                    showMessage(suggestQuizResponse.message);
                }

                if (getActivity() != null) {
                    getActivity().finish();
                }
            } else {
                if (Helper.isContainValue(suggestQuizResponse.err)) {
                    showMessage(suggestQuizResponse.err);
                }
            }
        }
    }

    @Override
    public void showLoader() {
        spinKit.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        spinKit.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addFriends:
                if (addFriends.getText().toString().equalsIgnoreCase("Select")) {
                    callSelectQuiz();
                } else {
                    callSuggestQuiz();
                }
                break;

            case R.id.pickAnother:
                handleQuizLoading.loadingQuizFragment();
                break;

            case R.id.crossIV:
                if (getActivity() != null) {
                    getActivity().finish();
                }
                break;

            default:
                break;
        }
    }

    private void callSelectQuiz() {
        if (Helper.isNetworkAvailable(context)) {
            addFriends.setEnabled(false);
            pickQuizDetailPresenter.callSelectQuizApi(article, channelId);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void callSuggestQuiz() {
        if (Helper.isNetworkAvailable(context)) {
            addFriends.setEnabled(false);
            pickQuizDetailPresenter.callSuggestQuizApi(article, channelId);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }
}