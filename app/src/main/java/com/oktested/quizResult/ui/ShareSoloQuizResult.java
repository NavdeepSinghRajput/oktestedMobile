package com.oktested.quizResult.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oktested.R;
import com.oktested.quizResult.model.QuizResultDataResponse;
import com.oktested.utils.Helper;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShareSoloQuizResult {

    private Activity context;
    private LinearLayout inflatedLL;
    private String quizId;
    private int maxScore;
    private QuizResultDataResponse quizResultDataResponse;

    public ShareSoloQuizResult(Context context, LinearLayout inflatedLL, QuizResultDataResponse quizResultDataResponse, String quizId, int maxScore) {
        this.context = (Activity) context;
        this.inflatedLL = inflatedLL;
        this.quizResultDataResponse = quizResultDataResponse;
        this.quizId = quizId;
        this.maxScore = maxScore;
        shareUsingImage();
    }

    private void shareUsingImage() {
        inflatedLL.setVisibility(View.VISIBLE);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = mInflater.inflate(R.layout.solo_result_share, null, true);
        inflatedLL.addView(view);
        inflatedLL.setVisibility(View.INVISIBLE);

        LinearLayout parentLL = view.findViewById(R.id.parentLL);
        ImageView quizIV = view.findViewById(R.id.quizIV);
        TextView titleTV = view.findViewById(R.id.titleTV);
        TextView tvSalgon = view.findViewById(R.id.tvSalgon);
        TextView topScoreTV = view.findViewById(R.id.topScoreTV);
        CircleImageView userIV = view.findViewById(R.id.userIV);
        TextView userNameTV = view.findViewById(R.id.userNameTV);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        TextView userScoreTV = view.findViewById(R.id.userScoreTV);
        TextView averageAccurayTV = view.findViewById(R.id.averageAccurayTV);
        TextView averageTimeTv = view.findViewById(R.id.averageTimeTv);

        if (Helper.isContainValue(quizResultDataResponse.user_name)) {
            userNameTV.setText(quizResultDataResponse.user_name);
        }

        if (Helper.isContainValue(quizResultDataResponse.quiz_title)) {
            titleTV.setText(quizResultDataResponse.quiz_title);
        }
        if (Helper.isContainValue(quizResultDataResponse.message)) {
            tvSalgon.setText(quizResultDataResponse.message);
        }
        if (Helper.isContainValue(String.valueOf(quizResultDataResponse.avg_accuracy))) {
            averageAccurayTV.setText(quizResultDataResponse.avg_accuracy + "%");
        }
        if (Helper.isContainValue(String.valueOf(quizResultDataResponse.avg_time))) {
            averageTimeTv.setText(quizResultDataResponse.avg_time + "sec/ques");
        }

        if (Helper.isContainValue(String.valueOf(quizResultDataResponse.total_score))) {
            userScoreTV.setText(quizResultDataResponse.total_score + "/" + maxScore);
            topScoreTV.setText("I scored " + quizResultDataResponse.total_score + "/" + maxScore + " on this quiz.");

            ratingBar.setNumStars(maxScore);
            ratingBar.setMax(maxScore);
            ratingBar.setRating(quizResultDataResponse.total_score);
        }

        if (Helper.isContainValue(quizResultDataResponse.user_pic)) {
            Glide.with(context)
                    .load(quizResultDataResponse.user_pic)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(userIV);
        }

        if (Helper.isContainValue(quizResultDataResponse.quiz_feature_img)) {
            Glide.with(context)
                    .load(quizResultDataResponse.quiz_feature_img)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(quizIV);
        }

        setHandler(parentLL);
    }

    private void setHandler(final LinearLayout parent) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = getScreenShot(parent);
                shareIntentWork(bitmap);
            }
        }, 1000);
    }

    public Bitmap getScreenShot(View screenView) {
        try {
            screenView.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
            screenView.setDrawingCacheEnabled(false);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void shareIntentWork(Bitmap bitmap) {
        String quizUrl = "I challenge you to beat me at this Quiz\n\n" + context.getString(R.string.quiz_share_url) + quizId;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, quizUrl);
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        if (bitmap != null) {
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "", null);
            Uri screenshotUri = Uri.parse(path);
            intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            intent.setType("image/*");
        } else {
            intent.setType("text/plain");
        }
        context.startActivity(Intent.createChooser(intent, "Share Result"));
        inflatedLL.removeAllViews();
        inflatedLL.setVisibility(View.GONE);
    }
}