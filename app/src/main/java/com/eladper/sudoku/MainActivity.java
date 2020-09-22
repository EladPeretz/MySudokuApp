package com.eladper.sudoku;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "sudoku";
    private Button settingsButton;
    private Button continueButton;
    private Button exitButton;
    private Button newgameButoon;
    private FirebaseAnalytics fbAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fbAnalytics = FirebaseAnalytics.getInstance(this);

        this.settingsButton = (Button)this.findViewById(R.id.SettingsButton);
        this.continueButton = (Button)this.findViewById(R.id.ContinueButton);
        this.exitButton = (Button)this.findViewById(R.id.ExitButton);
        this.newgameButoon = (Button)this.findViewById(R.id.NewGameButton);

        this.settingsButton.setOnClickListener(this);
        this.continueButton.setOnClickListener(null);
        //Continue Button
        if(Utilities.readSerializable(MainActivity.this,"CurrentBoard.bin")!=null&&Utilities.readSerializable(MainActivity.this,"StartBoard.bin")!=null){
            this.continueButton.setOnClickListener(this);
        }
        //
        this.exitButton.setOnClickListener(this);
        this.newgameButoon.setOnClickListener(this);


        //Ad loading.
        MobileAds.initialize(this, getResources().getString(R.string.ADMOB_APP));
        AdView adview=(AdView)findViewById(R.id.adView);
        AdRequest adRequest= new AdRequest.Builder()
                .build();
        adview.loadAd(adRequest);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.NewGameButton: {
           startActivity(new Intent(MainActivity.this, GameDifficultyActivity.class));
                return;
            }

            case R.id.SettingsButton: {
                this.startActivity(new Intent((Context)this, SettingsActivity.class));
                return;
            }
            case R.id.ContinueButton: {
                this.startActivity(new Intent(this,ContinuedSudokuGameActivity.class));
                return;
            }
            case R.id.ExitButton:
        }
            finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SudokuGameActivity.isSaved==1)
            this.continueButton.setOnClickListener(this);
        if(SudokuGameActivity.isSaved==-1)
            this.continueButton.setOnClickListener(null);
    }

}