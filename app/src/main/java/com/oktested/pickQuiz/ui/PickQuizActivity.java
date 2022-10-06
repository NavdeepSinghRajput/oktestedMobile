package com.oktested.pickQuiz.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.oktested.R;

public class PickQuizActivity extends AppCompatActivity implements HandleQuizLoading {

    private FragmentManager fragmentManager;
    private Fragment pickQuizFragment, pickQuizDetailFragment, activeFragment;
    private boolean isAdmin;
    private String articleIds, channelName;
    private BroadcastReceiver mReceiver, quizSuggestionReceiver, quizSelectReceiver, shiftAdminReceiver;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_pick_quiz);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        context = this;
        startQuizReceiverRegister();
        quizSuggestionReceiverRegister();
        quizSelectReceiverRegister();
        shiftAdminReceiver();

        isAdmin = getIntent().getExtras().getBoolean("isAdmin");
        articleIds = getIntent().getExtras().getString("articleId");
        channelName = getIntent().getExtras().getString("channelName");
        addFragments(articleIds);
    }

    private void startQuizReceiverRegister() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "start.quiz");
        context.registerReceiver(mReceiver, intentFilter);
    }

    private void quizSuggestionReceiverRegister() {
        quizSuggestionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() != null) {
                    finish();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "participant.suggestion");
        context.registerReceiver(quizSuggestionReceiver, intentFilter);
    }

    private void quizSelectReceiverRegister() {
        quizSelectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() != null) {
                    finish();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "admin.select");
        context.registerReceiver(quizSelectReceiver, intentFilter);
    }

    private void shiftAdminReceiver() {
        shiftAdminReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() != null) {
                    finish();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "shift.admin");
        context.registerReceiver(shiftAdminReceiver, intentFilter);
    }

    private void addFragments(String articleIds) {
        fragmentManager = getSupportFragmentManager();
        pickQuizFragment = PickQuizFragment.newInstance(PickQuizActivity.this);
        pickQuizDetailFragment = PickQuizDetailFragment.newInstance(PickQuizActivity.this, isAdmin, articleIds, channelName);
        try {
            if (articleIds != null) {
                fragmentManager.beginTransaction().add(R.id.frame_container, pickQuizFragment, "pickQuiz").hide(pickQuizFragment).commitAllowingStateLoss();
                fragmentManager.beginTransaction().add(R.id.frame_container, pickQuizDetailFragment, "pickQuizDetails").show(pickQuizDetailFragment).commitAllowingStateLoss();
                activeFragment = pickQuizDetailFragment;
            } else {
                fragmentManager.beginTransaction().add(R.id.frame_container, pickQuizFragment, "pickQuiz").commitAllowingStateLoss();
                fragmentManager.beginTransaction().add(R.id.frame_container, pickQuizDetailFragment, "pickQuizDetails").hide(pickQuizDetailFragment).commitAllowingStateLoss();
                activeFragment = pickQuizFragment;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadingQuizDetailFragment(String articleId) {
        Intent intent = new Intent();
        intent.putExtra("articleId", articleId);
        intent.setAction(getString(R.string.app_name) + "quiz.detail");
        sendBroadcast(intent);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(activeFragment).show(pickQuizDetailFragment).commit();
        activeFragment = pickQuizDetailFragment;
    }

    @Override
    public void loadingQuizFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(activeFragment).show(pickQuizFragment).commit();
        activeFragment = pickQuizFragment;
    }

    @Override
    public void onBackPressed() {
        if (articleIds == null) {
            if (activeFragment instanceof PickQuizDetailFragment) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.hide(activeFragment).show(pickQuizFragment).commit();
                activeFragment = pickQuizFragment;
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
        }
        if (quizSuggestionReceiver != null) {
            context.unregisterReceiver(quizSuggestionReceiver);
        }
        if (quizSelectReceiver != null) {
            context.unregisterReceiver(quizSelectReceiver);
        }
        if (shiftAdminReceiver != null) {
            context.unregisterReceiver(shiftAdminReceiver);
        }
        super.onDestroy();
    }
}