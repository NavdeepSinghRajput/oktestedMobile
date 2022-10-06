package com.oktested.quizEnable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.oktested.R;
import com.oktested.dashboard.ui.DashboardActivity;

public class QuizEnableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_enable);
        inUi();
    }

    private void inUi() {
        TextView yesTV = findViewById(R.id.yesTV);
        TextView cancelTV = findViewById(R.id.cancelTV);
        TextView messageTV = findViewById(R.id.messageTV);

        messageTV.setText(getIntent().getExtras().getString("userName") + " has a surprise for you. Update now to see it.");

        yesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizEnableActivity.this, DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}