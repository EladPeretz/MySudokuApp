package com.eladper.sudoku;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;

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
        statusBarHide();
        setContentView(R.layout.activity_main);

        fbAnalytics = FirebaseAnalytics.getInstance(this);

        this.settingsButton = (Button)this.findViewById(R.id.SettingsButton);
        this.continueButton = (Button)this.findViewById(R.id.ContinueButton);
        this.exitButton = (Button)this.findViewById(R.id.ExitButton);
        this.newgameButoon = (Button)this.findViewById(R.id.NewGameButton);

        this.settingsButton.setOnClickListener((View.OnClickListener)this);
        this.continueButton.setOnClickListener(null);
        //Continue Button
        if(Utilities.readSerializable(MainActivity.this,"CurrentBoard.bin")!=null&&Utilities.readSerializable(MainActivity.this,"StartBoard.bin")!=null){
            this.continueButton.setOnClickListener((View.OnClickListener)this);
        }
        //
        this.exitButton.setOnClickListener((View.OnClickListener)this);
        this.newgameButoon.setOnClickListener((View.OnClickListener)this);


        //Ad loading.
        MobileAds.initialize(this, getResources().getString(R.string.ADMOB_APP));
        AdView adview=(AdView)findViewById(R.id.adView);
        AdRequest adRequest= new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adview.loadAd(adRequest);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.NewGameButton: {
           startActivity(new Intent(MainActivity.this,GameDifficultyActivity.class));
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

        statusBarHide();
    }
    // A function to hide the status bar.
    public void statusBarHide(){
        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else{
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);}
    }
}