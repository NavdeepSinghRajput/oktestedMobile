package com.oktested.quizProfile.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.ybq.android.spinkit.SpinKitView;
import com.oktested.R;
import com.oktested.quizProfile.adapter.RecentPlayedQuizAdapter;
import com.oktested.quizProfile.model.RecentPlayedQuizResponse;
import com.oktested.quizProfile.model.UserStatsResponse;
import com.oktested.quizProfile.presenter.QuizProfilePresenter;
import com.oktested.utils.DataHolder;
import com.oktested.utils.Helper;
import com.warkiz.widget.IndicatorSeekBar;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuizProfileActivity extends AppCompatActivity implements QuizProfileView {

    private Context context;
    private RecyclerView quizPlayedRV;
    private QuizProfilePresenter quizProfilePresenter;
    private TextView recentQuiz;
    private SpinKitView spinKitView;
    private LinearLayout parentLL;
    private PieChart chart;
    private TextView questionPoint, timePerQuestion, noChartText, currentLevel, upcommingLevel;
    private CircleImageView badgeImage;
    private IndicatorSeekBar seekbarIndicator;
    private TextView quizPlayedCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_profile);
        context = this;

        inUi();
        callRecentQuiz();
        settingPieChart();
    }

    private void settingPieChart() {
        chart = findViewById(R.id.chart);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setExtraOffsets(55.f, 0.f, 55.f, 0.f);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(getResources().getColor(R.color.colorBackground));
        chart.setTransparentCircleColor(getResources().getColor(R.color.colorBackground));
        chart.setTransparentCircleAlpha(80);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
    }

    private void setData(List<UserStatsResponse.UserStatsData.CategoryQuizPlayed> count) {
        try {
            ArrayList<PieEntry> entries = new ArrayList<>();
            for (int i = 0; i < count.size(); i++) {
                if (count.get(i).category_name.length() < 18) {
                    entries.add(new PieEntry(count.get(i).percentage, count.get(i).category_name));
                } else {
                    String val = count.get(i).category_name.substring(0, 15) + "..";
                    entries.add(new PieEntry(count.get(i).percentage, val));

                }
            }

            PieDataSet dataSet = new PieDataSet(entries, "Results");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            // add a lot of colors

            ArrayList<Integer> colors = new ArrayList<>();
            colors.add(context.getResources().getColor(R.color.graph1));
            colors.add(context.getResources().getColor(R.color.graph2));
            colors.add(context.getResources().getColor(R.color.graph3));
            colors.add(context.getResources().getColor(R.color.graph4));
            colors.add(context.getResources().getColor(R.color.graph5));
            colors.add(context.getResources().getColor(R.color.graph6));
            colors.add(context.getResources().getColor(R.color.graph7));
            colors.add(context.getResources().getColor(R.color.graph8));
            colors.add(context.getResources().getColor(R.color.graph9));

            dataSet.setColors(colors);
            //dataSet.setSelectionShift(0f);

            dataSet.setValueLinePart1OffsetPercentage(80.f);
            dataSet.setValueLinePart1Length(0.3f);
            dataSet.setValueLinePart2Length(0.4f);
            dataSet.setUsingSliceColorAsValueLineColor(true);
            dataSet.setDrawValues(false);

            dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(6f);
            data.setValueTextColor(Color.WHITE);
            //  data.setValueTypeface(tf);
            chart.setData(data);

            // undo all highlights
            chart.highlightValues(null);
            chart.animateXY(1400, 1400);

            chart.invalidate();
        } catch (Exception e) {

        }
    }

    private void inUi() {
        quizProfilePresenter = new QuizProfilePresenter(getBaseContext(), this);
        quizProfilePresenter.callUserStatsApi();

        timePerQuestion = findViewById(R.id.timePerQuestion);
        questionPoint = findViewById(R.id.questionPoint);
        spinKitView = findViewById(R.id.spinKit);
        parentLL = findViewById(R.id.parentLL);
        recentQuiz = findViewById(R.id.recentQuiz);
        quizPlayedRV = findViewById(R.id.quizPlayedRV);
        noChartText = findViewById(R.id.noChartText);
        upcommingLevel = findViewById(R.id.upcommingLevel);
        currentLevel = findViewById(R.id.currentLevel);
        badgeImage = findViewById(R.id.badgeImage);
        quizPlayedCount = findViewById(R.id.quizPlayedCount);
        quizPlayedRV.setHasFixedSize(true);
        quizPlayedRV.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.HORIZONTAL, false));
        quizPlayedRV.setItemAnimator(new DefaultItemAnimator());
        seekbarIndicator = findViewById(R.id.seek_bar_indicators);
        CircleImageView friendPic = findViewById(R.id.friendPic);

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

    private void callRecentQuiz() {
        if (Helper.isNetworkAvailable(getBaseContext())) {
            showLoader();
            quizProfilePresenter.callRecentPlayedQuizApi("0");
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    @Override
    public void setRecentPlayedQuizResponse(RecentPlayedQuizResponse recentPlayedQuizResponse) {
        if (recentPlayedQuizResponse != null && recentPlayedQuizResponse.data != null && recentPlayedQuizResponse.data.size() > 0) {
            RecentPlayedQuizAdapter latestQuizAdapter = new RecentPlayedQuizAdapter(getBaseContext(), recentPlayedQuizResponse.data, recentPlayedQuizResponse.next_offset);
            quizPlayedRV.setAdapter(latestQuizAdapter);
            quizPlayedRV.setVisibility(View.VISIBLE);
            recentQuiz.setVisibility(View.GONE);
        } else {
            quizPlayedRV.setVisibility(View.GONE);
            recentQuiz.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setUserStatsResponse(UserStatsResponse userStatsResponse) {
        parentLL.setVisibility(View.VISIBLE);
        if (userStatsResponse != null && Helper.isContainValue(userStatsResponse.status)) {
            if (userStatsResponse.status.equalsIgnoreCase("1")) {
                if (userStatsResponse.data.average_accuracy != 0) {
                    questionPoint.setText(userStatsResponse.data.average_accuracy + " %");
                }
                if (userStatsResponse.data.average_time != 0) {
                    timePerQuestion.setText(userStatsResponse.data.average_time + " sec/ques");
                }
                if (userStatsResponse.data.category_data.size() > 0) {

                    setData(userStatsResponse.data.category_data);
                    chart.setVisibility(View.VISIBLE);
                    noChartText.setVisibility(View.GONE);
                } else {
                    chart.setVisibility(View.GONE);
                    noChartText.setVisibility(View.VISIBLE);
                }
                if (userStatsResponse.data.level_count != -1) {
                    currentLevel.setText("Lv. " + userStatsResponse.data.level_count);
                    upcommingLevel.setText("Lv. " + (userStatsResponse.data.level_count + 1));
                }
                if (Helper.isContainValue(userStatsResponse.data.level_url)) {
                    Glide.with(context)
                            .load(userStatsResponse.data.level_url)
                            .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .into(badgeImage);
                } else {
                    badgeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder));
                }
                seekbarIndicator.setMax(userStatsResponse.data.level_max_count);
                seekbarIndicator.setMin(userStatsResponse.data.level_min_count);
                ValueAnimator anim = ValueAnimator.ofInt(0, userStatsResponse.data.level_current_count);
                anim.setDuration(100);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int animProgress = (Integer) animation.getAnimatedValue();
                        seekbarIndicator.setProgress(animProgress);
                    }
                });
                anim.start();
//                seekbarIndicator.setProgress(userStatsResponse.data.level_current_count);
                quizPlayedCount.setText(userStatsResponse.data.level_current_count + "/" + userStatsResponse.data.level_max_count);
            } else {
                showMessage(userStatsResponse.err);
            }
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
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        quizProfilePresenter.onDestroyedView();
        super.onDestroy();
    }
}