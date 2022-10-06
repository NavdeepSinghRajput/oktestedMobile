package com.oktested.playSolo.adapter;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oktested.R;
import com.oktested.home.model.QuestionOption;
import com.oktested.utils.Helper;

import java.util.ArrayList;

public class QuizOptionsAdapter extends RecyclerView.Adapter<QuizOptionsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<QuestionOption> choicesAL;
    private boolean optionNotSelected = true, isPlaySolo;
    private int correctAns, selectedAns;
    private Quiz quiz;

    public QuizOptionsAdapter(Context context, ArrayList<QuestionOption> choicesAL, int correctAns, boolean isPlaySolo, Quiz quiz) {
        this.context = context;
        this.choicesAL = choicesAL;
        this.correctAns = correctAns;
        this.isPlaySolo = isPlaySolo;
        this.quiz = quiz;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_option_item, parent, false);
        return new QuizOptionsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (Helper.isContainValue(choicesAL.get(position).value)) {
            holder.optionTV.setText(Html.fromHtml(choicesAL.get(position).value));
        }

        if (!optionNotSelected) {
            if (correctAns == selectedAns) {
                if (correctAns == position) {
                    holder.optionRL.setBackground(context.getResources().getDrawable(R.drawable.quiz_correct_answer_bg));
                    holder.optionTV.setTextColor(context.getResources().getColor(R.color.blackOverlayColor));
                    holder.answerIV.setVisibility(View.VISIBLE);
                    holder.answerIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_quiz_right));

                    ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                            holder.optionRL,
                            PropertyValuesHolder.ofFloat("scaleX", 1.4f),
                            PropertyValuesHolder.ofFloat("scaleY", 1.4f));
                    scaleDown.setDuration(100);
                    scaleDown.setRepeatCount(1);
                    scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
                    scaleDown.start();
                } else {
                    holder.optionRL.setBackground(context.getResources().getDrawable(R.drawable.quiz_unselect_option_bg));
                    holder.optionTV.setTextColor(context.getResources().getColor(R.color.colorLightGreen));
                    holder.answerIV.setVisibility(View.GONE);
                }
            } else {
                if (position == correctAns) {
                    holder.optionRL.setBackground(context.getResources().getDrawable(R.drawable.quiz_correct_answer_bg));
                    holder.optionTV.setTextColor(context.getResources().getColor(R.color.blackOverlayColor));
                    holder.answerIV.setVisibility(View.VISIBLE);
                    holder.answerIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_quiz_right));

                    ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                            holder.optionRL,
                            PropertyValuesHolder.ofFloat("scaleX", 1.4f),
                            PropertyValuesHolder.ofFloat("scaleY", 1.4f));
                    scaleDown.setDuration(100);
                    scaleDown.setRepeatCount(1);
                    scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
                    scaleDown.start();
                } else if (position == selectedAns) {
                    holder.optionRL.setBackground(context.getResources().getDrawable(R.drawable.quiz_wrong_answer_bg));
                    holder.optionTV.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    holder.answerIV.setVisibility(View.VISIBLE);
                    holder.answerIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_quiz_wrong));

                    ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                            holder.optionRL,
                            PropertyValuesHolder.ofFloat("scaleX", 1.4f),
                            PropertyValuesHolder.ofFloat("scaleY", 1.4f));
                    scaleDown.setDuration(100);
                    scaleDown.setRepeatCount(1);
                    scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
                    scaleDown.start();
                } else {
                    holder.optionRL.setBackground(context.getResources().getDrawable(R.drawable.quiz_unselect_option_bg));
                    holder.optionTV.setTextColor(context.getResources().getColor(R.color.colorLightGreen));
                    holder.answerIV.setVisibility(View.GONE);
                }
            }
        }

        holder.optionRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (optionNotSelected) {
                    ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                            holder.optionRL,
                            PropertyValuesHolder.ofFloat("scaleX", 1.4f),
                            PropertyValuesHolder.ofFloat("scaleY", 1.4f));
                    scaleDown.setDuration(100);
                    scaleDown.setRepeatCount(1);
                    scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
                    scaleDown.start();

                    optionNotSelected = false;
                    holder.optionRL.setBackground(context.getResources().getDrawable(R.drawable.button_bg));
                    holder.optionTV.setTextColor(context.getResources().getColor(R.color.blackOverlayColor));

                    selectedAns = position;
                    if (selectedAns == correctAns) {
                        quiz.submitQuiz(true, "correct", choicesAL.get(position).id, true);
                    } else {
                        quiz.submitQuiz(false, "wrong", choicesAL.get(position).id, true);
                    }

                    if (isPlaySolo) {
                        new Handler().postDelayed(() -> {
                            notifyDataSetChanged();
                        }, 2000);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return choicesAL.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView optionTV;
        public RelativeLayout optionRL;
        public ImageView answerIV;

        ViewHolder(View itemView) {
            super(itemView);
            optionTV = itemView.findViewById(R.id.optionTV);
            optionRL = itemView.findViewById(R.id.optionRL);
            answerIV = itemView.findViewById(R.id.answerIV);
        }
    }

    public void timerComplete(boolean optionNotSelected, int selectedAns) {
        if (isPlaySolo) {
            if (this.optionNotSelected) {
                this.optionNotSelected = optionNotSelected;
                this.selectedAns = selectedAns;
                if (selectedAns == correctAns) {
                    quiz.submitQuiz(true, "timeout", "", false);
                } else {
                    quiz.submitQuiz(false, "timeout", "", false);
                }
                notifyDataSetChanged();
            }
        } else {
            if (this.optionNotSelected) {
                this.optionNotSelected = optionNotSelected;
                this.selectedAns = selectedAns;
                if (selectedAns == correctAns) {
                    quiz.submitQuiz(true, "timeout", "", false);
                } else {
                    quiz.submitQuiz(false, "timeout", "", false);
                }
            }
        }
    }

    public interface Quiz {
        void submitQuiz(boolean isCorrect, String answer, String choiceId, boolean optionSelect);
    }

    public void notifyAdapter() {
        notifyDataSetChanged();
    }
}