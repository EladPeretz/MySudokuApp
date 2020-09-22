package com.eladper.sudoku;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class GameDifficultyActivity extends AppCompatActivity implements View.OnClickListener {
    private int selectedDifficulty = -1, progressCounter=0;
    private Button startGame,startGame1,startGame2;
    private ImageButton btnEasy,btnAdvanced,btnProfessional;
    private TextView easy,advanced,professional;
    private ProgressBar progressBar1,progressBar2,progressBar3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_difficulty);


        startGame=findViewById(R.id.buttonContinue);
        startGame1=findViewById(R.id.buttonContinue1);
        startGame2=findViewById(R.id.buttonContinue2);
        btnEasy=findViewById(R.id.Button_view_easy);
        btnAdvanced=findViewById(R.id.Button_view_advanced);
        btnProfessional=findViewById(R.id.Button_view_professional);
        progressBar1=findViewById(R.id.progressBar1);
        progressBar2=findViewById(R.id.progressBar2);
        progressBar3=findViewById(R.id.progressBar3);
        easy=findViewById(R.id.easy_text);
        advanced=findViewById(R.id.advanced_text);
        professional=findViewById(R.id.professional_text);
        easy.bringToFront();
        advanced.bringToFront();
        professional.bringToFront();

        btnProfessional.setBackgroundResource(R.drawable.professional);
        btnEasy.setBackgroundResource(R.drawable.easy_default);
        btnAdvanced.setBackgroundResource(R.drawable.advanced);

        startGame.setOnClickListener((View.OnClickListener)this);
        startGame1.setOnClickListener((View.OnClickListener)this);
        startGame2.setOnClickListener((View.OnClickListener)this);
        btnEasy.setOnClickListener((View.OnClickListener)this);
        btnAdvanced.setOnClickListener((View.OnClickListener)this);
        btnProfessional.setOnClickListener((View.OnClickListener)this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonContinue: {
                startGame.setOnClickListener(null);
                startGame.setText(R.string.Loading);
                btnEasy.setOnClickListener(null);
                btnAdvanced.setOnClickListener(null);
                btnProfessional.setOnClickListener(null);
                progressBar1.setVisibility(View.VISIBLE);
                progAnimation(progressBar1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent mySuperIntent = new Intent(GameDifficultyActivity.this, BoardNoteActivity.class);
                        mySuperIntent.putExtra("difficulty", selectedDifficulty);
                        startActivity(mySuperIntent);
                        finish();
                    }
                }, 5000);
                return;
            }
            case R.id.buttonContinue1: {
                startGame1.setOnClickListener(null);
                startGame1.setText(R.string.Loading);
                btnEasy.setOnClickListener(null);
                btnAdvanced.setOnClickListener(null);
                btnProfessional.setOnClickListener(null);
                progressBar2.setVisibility(View.VISIBLE);
                progAnimation(progressBar2);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent mySuperIntent = new Intent(GameDifficultyActivity.this, BoardNoteActivity.class);
                        mySuperIntent.putExtra("difficulty", selectedDifficulty);
                        startActivity(mySuperIntent);
                        finish();
                    }
                }, 5000);
                return;
            }
            case R.id.buttonContinue2: {
                startGame2.setOnClickListener(null);
                startGame2.setText(R.string.Loading);
                btnEasy.setOnClickListener(null);
                btnAdvanced.setOnClickListener(null);
                btnProfessional.setOnClickListener(null);
                progressBar3.setVisibility(View.VISIBLE);
                progAnimation(progressBar3);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent mySuperIntent = new Intent(GameDifficultyActivity.this, BoardNoteActivity.class);
                        mySuperIntent.putExtra("difficulty", selectedDifficulty);
                        startActivity(mySuperIntent);
                        finish();
                    }
                }, 5000);
                return;
            }
            case R.id.Button_view_easy: {
                btnProfessional.setBackgroundResource(R.drawable.professional);
                btnEasy.setBackgroundResource(R.drawable.easybright);
                btnAdvanced.setBackgroundResource(R.drawable.advanced);
                startGame.setVisibility(View.VISIBLE);
                startGame1.setVisibility(View.INVISIBLE);
                startGame2.setVisibility(View.INVISIBLE);
                selectedDifficulty=0;
                return;
            }
            case R.id.Button_view_advanced: {
                btnProfessional.setBackgroundResource(R.drawable.professional);
                btnEasy.setBackgroundResource(R.drawable.easy_default);
                btnAdvanced.setBackgroundResource(R.drawable.advancedbright);
                startGame.setVisibility(View.INVISIBLE);
                startGame1.setVisibility(View.VISIBLE);
                startGame2.setVisibility(View.INVISIBLE);
                selectedDifficulty=1;
                return;
            }
            case R.id.Button_view_professional: {
                btnProfessional.setBackgroundResource(R.drawable.professionalbright);
                btnEasy.setBackgroundResource(R.drawable.easy_default);
                btnAdvanced.setBackgroundResource(R.drawable.advanced);
                startGame.setVisibility(View.INVISIBLE);
                startGame1.setVisibility(View.INVISIBLE);
                startGame2.setVisibility(View.VISIBLE);
                selectedDifficulty=2;
            }
        }

    }

    public void progAnimation(final ProgressBar progressBar){
        progressCounter=0;
        final Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run()
            {
                progressCounter++;
                progressBar.setProgress(progressCounter);

                if(progressCounter == 100)
                    t.cancel();
            }
        };

        t.schedule(tt,0,33);

    }



}