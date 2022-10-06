package com.oktested.quizResult.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.oktested.R;
import com.oktested.quizResult.adapter.GroupQuizResultAdapter;
import com.oktested.quizResult.model.GroupQuizResultResponse;
import com.oktested.quizResult.presenter.GroupQuizResultPresenter;
import com.oktested.utils.Helper;

import java.util.ArrayList;

public class GroupQuizResultActivity extends AppCompatActivity implements GroupQuizResultView, View.OnClickListener {

    private Context context;
    private GroupQuizResultPresenter groupQuizResultPresenter;
    private SpinKitView spinKitView;
    private LinearLayout parentLL;
    private RelativeLayout oneRL, twoRL;
    private TextView winnerTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_quiz_result);

        inUi();
    }

    private void inUi() {
        context = this;
        groupQuizResultPresenter = new GroupQuizResultPresenter(context, this);

        String channelId = getIntent().getExtras().getString("channelId");
        String quizId = getIntent().getExtras().getString("quizId");

        spinKitView = findViewById(R.id.spinKit);
        parentLL = findViewById(R.id.parentLL);
        ImageView backIV = findViewById(R.id.backIV);
        ImageView shareIV = findViewById(R.id.shareIV);
        winnerTV = findViewById(R.id.winnerTV);
        oneRL = findViewById(R.id.oneRL);
        twoRL = findViewById(R.id.twoRL);

        backIV.setOnClickListener(this);
        shareIV.setOnClickListener(this);

        callGroupQuizResult(quizId, channelId);
    }

    private void callGroupQuizResult(String quizId, String channelId) {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            groupQuizResultPresenter.callGroupQuizResultApi(quizId, channelId);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
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
        groupQuizResultPresenter.onDestroyedView();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backIV:
                finish();
                break;

            case R.id.shareIV:
                break;

            default:
                break;
        }
    }

    @Override
    public void setGroupQuizResultResponse(GroupQuizResultResponse groupQuizResultResponse) {
        if (groupQuizResultResponse != null && Helper.isContainValue(groupQuizResultResponse.status)) {
            if (groupQuizResultResponse.status.equalsIgnoreCase("1")) {
                if (groupQuizResultResponse.data != null && groupQuizResultResponse.data.size() > 0) {
                    parentLL.setVisibility(View.VISIBLE);

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
                }
            } else {
                if (Helper.isContainValue(groupQuizResultResponse.err)) {
                    showMessage(groupQuizResultResponse.err);
                }
            }
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

        if (Helper.isContainValue(String.valueOf(dataAL.get(0).max_score)) && Helper.isContainValue(String.valueOf(dataAL.get(0).total_score))) {
            oneScoreTV.setText(dataAL.get(0).total_score + "/" + dataAL.get(0).max_score);
            if (dataAL.get(0).total_score == 0) {
                oneRatingBar.setVisibility(View.GONE);
            } else {
                oneRatingBar.setVisibility(View.VISIBLE);
                oneRatingBar.setNumStars(dataAL.get(0).total_score);
                oneRatingBar.setRating(dataAL.get(0).total_score);
            }
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

        if (Helper.isContainValue(String.valueOf(dataAL.get(1).max_score)) && Helper.isContainValue(String.valueOf(dataAL.get(1).total_score))) {
            twoScoreTV.setText(dataAL.get(1).total_score + "/" + dataAL.get(1).max_score);
            if (dataAL.get(1).total_score == 0) {
                twoRatingBar.setVisibility(View.GONE);
            } else {
                twoRatingBar.setVisibility(View.VISIBLE);
                twoRatingBar.setNumStars(dataAL.get(1).total_score);
                twoRatingBar.setRating(dataAL.get(1).total_score);
            }
        }
    }

    private void setAdapter(ArrayList<GroupQuizResultResponse.GroupQuizResultDataResponse> dataAL) {
        RecyclerView resultRV = findViewById(R.id.resultRV);
        resultRV.setNestedScrollingEnabled(false);
        resultRV.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        resultRV.setLayoutManager(mLayoutManager);
        resultRV.setItemAnimator(new DefaultItemAnimator());

        GroupQuizResultAdapter groupQuizResultAdapter = new GroupQuizResultAdapter(context, dataAL, 0);
        resultRV.setAdapter(groupQuizResultAdapter);
    }
}