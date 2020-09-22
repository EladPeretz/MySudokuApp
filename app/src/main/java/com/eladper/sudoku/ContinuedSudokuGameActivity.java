package com.eladper.sudoku;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ContinuedSudokuGameActivity extends AppCompatActivity implements CellGroupFragment.OnFragmentInteractionListener, View.OnClickListener, RewardedVideoAdListener {
    private final String TAG = "SudokuGameActivity";
    private TextView hintCounterText;
    private int hintCounterNuber;
    private TextView clickedCell;
    private int clickedGroup;
    private int clickedCellId;
    private Board startBoard, solvedBoard;
    private Board currentBoard;
    private Button button1,button2,button3,button4,button5,button6,button7,button8,button9;
    private ImageButton imageButtonEye,imageButtonDelete,buttonCheckBoard;
    private ProgressBar progressBar;
    private Runnable mHandlerTask;
    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd rewardedVideoAd;
    private int boardId, boardNumber, difficulty;
    private BoardNoteViewModel boardNoteViewModel;
    private int timesCompleted = 0;
    private List<BoardNote> boardNoteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku_game);



        ArrayList<Board> boards = readGameBoards(difficulty);
        solvedBoard = new Board();
        progressBar = findViewById(R.id.progressBar);

        button1 = (Button) findViewById(R.id.btnNumpad1);
        button2 = (Button) findViewById(R.id.btnNumpad2);
        button3 = (Button) findViewById(R.id.btnNumpad3);
        button4 = (Button) findViewById(R.id.btnNumpad4);
        button5 = (Button) findViewById(R.id.btnNumpad5);
        button6 = (Button) findViewById(R.id.btnNumpad6);
        button7 = (Button) findViewById(R.id.btnNumpad7);
        button8 = (Button) findViewById(R.id.btnNumpad8);
        button9 = (Button) findViewById(R.id.btnNumpad9);
        imageButtonEye=(ImageButton)findViewById(R.id.btnNumpadEye);
        imageButtonDelete=(ImageButton)findViewById(R.id.btnNumpadBack);
        buttonCheckBoard=(ImageButton)findViewById(R.id.buttonCheckBoard);
        buttonCheckBoard.setVisibility(View.INVISIBLE);
        buttonCheckBoard.setOnClickListener(null);


        boardNoteViewModel = ViewModelProviders.of(this).get(BoardNoteViewModel.class);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!preferences.getBoolean("Mark As Unsure",true)) {
            imageButtonEye.setVisibility(View.INVISIBLE);
        }
        else
            imageButtonEye.setVisibility(View.VISIBLE);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        if (savedInstanceState==null) {
            Log.e(TAG,"savedInstanceState is null");
            currentBoard = Utilities.readSerializable(ContinuedSudokuGameActivity.this, "CurrentBoard.bin");
            startBoard = Utilities.readSerializable(ContinuedSudokuGameActivity.this, "StartBoard.bin");
            hintCounterNuber = Utilities.readSerializable(ContinuedSudokuGameActivity.this, "hintCounterNumber.bin");
            timesCompleted = Utilities.readSerializable(ContinuedSudokuGameActivity.this,"timesCompleted.bin");
            difficulty = Utilities.readSerializable(ContinuedSudokuGameActivity.this,"difficulty.bin");
            boardId = Utilities.readSerializable(ContinuedSudokuGameActivity.this,"boardId.bin");
            boardNumber = Utilities.readSerializable(ContinuedSudokuGameActivity.this, "boardNumber.bin");
        }
        if(savedInstanceState!=null){
            Log.e(TAG,"savedInstanceState is not null");
            currentBoard=(Board) savedInstanceState.getSerializable("SavedBoard");
            startBoard=(Board) savedInstanceState.getSerializable("StartBoard");
            hintCounterNuber=savedInstanceState.getInt("hintCounterNumber");
            timesCompleted = savedInstanceState.getInt("timesCompleted");
            boardId=savedInstanceState.getInt("boardId");;
            boardNumber=savedInstanceState.getInt("boardNumber");
        }

        if(currentBoard.isBoardFull()){
            buttonCheckBoard.setOnClickListener(this);
            buttonCheckBoard.setVisibility(View.VISIBLE);
        }

        solvedBoard.copyValues(startBoard.getGameCells());



        if(difficulty==0)
            boardNoteViewModel.getEasyBoardNotes().observe(this, new Observer<List<BoardNote>>() {
                @Override
                public void onChanged(@Nullable List<BoardNote> boardNotes) {
                    boardNoteList = boardNotes;
                }
            });
        if(difficulty==1)
            boardNoteViewModel.getAdvancedBoardNotes().observe(this, new Observer<List<BoardNote>>() {
                @Override
                public void onChanged(@Nullable List<BoardNote> boardNotes) {
                    boardNoteList = boardNotes;
                }
            });
        if(difficulty==2)
            boardNoteViewModel.getProfessionalBoardNotes().observe(this, new Observer<List<BoardNote>>() {
                @Override
                public void onChanged(@Nullable List<BoardNote> boardNotes) {
                    boardNoteList = boardNotes;
                }
            });


        int cellGroupFragments[] = new int[]{R.id.cellGroupFragment, R.id.cellGroupFragment2, R.id.cellGroupFragment3, R.id.cellGroupFragment4,
                R.id.cellGroupFragment5, R.id.cellGroupFragment6, R.id.cellGroupFragment7, R.id.cellGroupFragment8, R.id.cellGroupFragment9};
        for (int i = 1; i < 10; i++) {
            CellGroupFragment thisCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[i - 1]);
            thisCellGroupFragment.setGroupId(i);
        }

        //Appear all values from the current board
        CellGroupFragment tempCellGroupFragment;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int column = j / 3;
                int row = i / 3;

                int fragmentNumber = (row * 3) + column;
                tempCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentNumber]);
                int groupColumn = j % 3;
                int groupRow = i % 3;

                int groupPosition = (groupRow * 3) + groupColumn;
                int currentValue = currentBoard.getValue(i, j);

                if (currentValue != 0 && currentValue==startBoard.getValue(i,j)) {
                    tempCellGroupFragment.setValue(groupPosition, currentValue);
                }

                if (currentBoard.getGameCells()[i][j] != startBoard.getGameCells()[i][j])
                    tempCellGroupFragment.setValueOfSaved(groupPosition, currentValue);
            }
        }

        //// ADS - START

        final Handler mHandler = new Handler();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6074185114340467/4670877878");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
        mHandlerTask = new Runnable() {
            @Override
            public void run() {

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                mHandler.postDelayed(mHandlerTask, 1000 * 60 * 2);
            }
        };

        mHandlerTask.run();

        //

        MobileAds.initialize(this, "ca-app-pub-6074185114340467~4331487592");
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);
        loadAds();
    }

    private void loadAds() {
        rewardedVideoAd.loadAd("ca-app-pub-6074185114340467/7116222204",
                new AdRequest.Builder().build());
    }

    private void showDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.reward_alert_dialog,null);
        Button claimButton = view.findViewById(R.id.claimRewardButton);

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        alertDialog.show();

        claimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintCounterNuber = 1;
                hintCounterText.setText(String.valueOf(hintCounterNuber));
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadAds();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        showDialog();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        if(hintCounterNuber<0)
            Toast.makeText(this,("Oops no videos were found, try again."), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted() {

    }


    //// ADS - END

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sudoku_game_menu,menu);

        final MenuItem menuItem = menu.findItem(R.id.hint);
        View actionView = MenuItemCompat.getActionView(menuItem);
        hintCounterText = (TextView) actionView.findViewById(R.id.hint_badge);
        hintCounterText.setVisibility(View.VISIBLE);
        hintCounterText.setText(String.valueOf(hintCounterNuber));
        if(hintCounterNuber<=0)
            hintCounterText.setText("+");
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.hint:
                if (clickedCell == null || isStartPiece(clickedGroup, clickedCellId))
                    Toast.makeText(this, R.string.TileToRevealItsNumber, Toast.LENGTH_SHORT).show();
                else {
                    setupBadge();
                }
                break;
            case R.id.save:
                AlertDialog alertDialog = new AlertDialog.Builder(ContinuedSudokuGameActivity.this).create();
                alertDialog.setTitle("Save Game");
                alertDialog.setMessage("Do you want to save the game");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Utilities.saveSerializable(ContinuedSudokuGameActivity.this, currentBoard, "CurrentBoard.bin");
                        Utilities.saveSerializable(ContinuedSudokuGameActivity.this, startBoard, "StartBoard.bin");
                        Utilities.saveSerializable(ContinuedSudokuGameActivity.this, hintCounterNuber, "hintCounterNumber.bin");
                        Utilities.saveSerializable(ContinuedSudokuGameActivity.this, difficulty,"difficulty.bin");
                        Utilities.saveSerializable(ContinuedSudokuGameActivity.this,boardId,"boardId.bin");
                        Utilities.saveSerializable(ContinuedSudokuGameActivity.this,boardNumber,"boardNumber.bin");
                        Utilities.saveSerializable(ContinuedSudokuGameActivity.this,timesCompleted,"timesCompleted.bin");
                        SudokuGameActivity.isSaved=1;
                        dialog.dismiss();
                        Log.w(TAG, "Boards Saved");
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
                break;
            case R.id.share:
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=com.eladper.sudoku");
                startActivity(Intent.createChooser(intent,"Share Using"));
                break;
            case R.id.settings:
                startActivity(new Intent(ContinuedSudokuGameActivity.this, SettingsActivity.class));
                break;
            case R.id.exit:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Board> readGameBoards(int difficulty) {
        ArrayList<Board> boards = new ArrayList<>();
        int fileId;
        if (difficulty == 1) {
            fileId = R.raw.normal;
        } else if (difficulty == 0) {
            fileId = R.raw.easy;
        } else {
            fileId = R.raw.hard;
        }

        InputStream inputStream = getResources().openRawResource(fileId);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                Board board = new Board();
                // read all lines in the board
                for (int i = 0; i < 9; i++) {
                    String rowCells[] = line.split(" ");
                    for (int j = 0; j < 9; j++) {
                        if (rowCells[j].equals("-")) {
                            board.setValue(i, j, 0);
                        } else {
                            board.setValue(i, j, Integer.parseInt(rowCells[j]));
                        }
                    }
                    line = bufferedReader.readLine();
                }
                boards.add(board);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        //reading from internal storage (/data/data/<package-name>/files)
        String fileName = "boards-";
        if (difficulty == 0) {
            fileName += "easy";
        } else if (difficulty == 1) {
            fileName += "normal";
        } else {
            fileName += "hard";
        }

        FileInputStream fileInputStream;
        try {
            fileInputStream = this.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader internalBufferedReader = new BufferedReader(inputStreamReader);
            String line = internalBufferedReader.readLine();
            line = internalBufferedReader.readLine();
            while (line != null) {
                Board board = new Board();
                // read all lines in the board
                for (int i = 0; i < 9; i++) {
                    String rowCells[] = line.split(" ");
                    for (int j = 0; j < 9; j++) {
                        if (rowCells[j].equals("-")) {
                            board.setValue(i, j, 0);
                        } else {
                            board.setValue(i, j, Integer.parseInt(rowCells[j]));
                        }
                    }
                    line = internalBufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                }
                boards.add(board);
                line = internalBufferedReader.readLine();
            }
            internalBufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return boards;
    }

    private Board chooseRandomBoard(ArrayList<Board> boards) {
        int randomNumber = (int) (Math.random() * boards.size());
        return boards.get(randomNumber);
    }

    private boolean isStartPiece(int group, int cell) {
        int row = ((group - 1) / 3) * 3 + (cell / 3);
        int column = ((group - 1) % 3) * 3 + ((cell) % 3);
        return startBoard.getValue(row, column) != 0;
    }

    private boolean checkAllGroups() {
        int cellGroupFragments[] = new int[]{R.id.cellGroupFragment, R.id.cellGroupFragment2, R.id.cellGroupFragment3, R.id.cellGroupFragment4,
                R.id.cellGroupFragment5, R.id.cellGroupFragment6, R.id.cellGroupFragment7, R.id.cellGroupFragment8, R.id.cellGroupFragment9};
        for (int i = 0; i < 9; i++) {
            CellGroupFragment thisCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[i]);
            if (!thisCellGroupFragment.checkGroupCorrect()) {
                return false;
            }
        }
        return true;
    }

    public void onCheckBoardButtonClicked(View view) {
        currentBoard.isBoardCorrect();
        if (checkAllGroups() && currentBoard.isBoardCorrect()) {
            Toast.makeText(this, getString(R.string.board_correct), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.board_incorrect), Toast.LENGTH_SHORT).show();
        }
    }

    public void onGoBackButtonClicked(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void onShowInstructionsButtonClicked(View view) {
        Intent intent = new Intent("me.kirkhorn.knut.InstructionsActivity");
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            int row = ((clickedGroup - 1) / 3) * 3 + (clickedCellId / 3);
            int column = ((clickedGroup - 1) % 3) * 3 + ((clickedCellId) % 3);

            if (data.getBooleanExtra("removePiece", false)) {
                clickedCell.setText("");
                clickedCell.setBackground(getResources().getDrawable(R.drawable.table_border_cell));
                currentBoard.setValue(row, column, 0);
                buttonCheckBoard.setVisibility(View.INVISIBLE);
            } else {
                int number = data.getIntExtra("chosenNumber", 1);
                clickedCell.setText(String.valueOf(number));
                currentBoard.setValue(row, column, number);

                boolean isUnsure = data.getBooleanExtra("isUnsure", false);
                if (isUnsure) {
                    clickedCell.setBackground(getResources().getDrawable(R.drawable.table_border_cell_unsure));
                } else {
                    clickedCell.setBackground(getResources().getDrawable(R.drawable.table_border_cell));
                }

                if (currentBoard.isBoardFull()) {
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                } else {
                    buttonCheckBoard.setVisibility(View.INVISIBLE);
                }
            }
        }

    }

    @Override
    public void onFragmentInteraction(int groupId, int cellId, View view) {
        clickedCell = (TextView) view;
        clickedGroup = groupId;
        clickedCellId = cellId;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.i(TAG, "Clicked group " + groupId + ", cell " + cellId);
        if (!isStartPiece(groupId, cellId)) {
            setKeyBoardOn();
        } else {
            setKeyBoardOff();
            Toast.makeText(this, getString(R.string.start_piece_error), Toast.LENGTH_SHORT).show();
        }
        int[] cellGroupFragments=new int[]{R.id.cellGroupFragment, R.id.cellGroupFragment2, R.id.cellGroupFragment3,R.id.cellGroupFragment4,
                R.id.cellGroupFragment5, R.id.cellGroupFragment6, R.id.cellGroupFragment7, R.id.cellGroupFragment8, R.id.cellGroupFragment9};
        CellGroupFragment thisCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[groupId - 1]);
        thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",true),preferences.getBoolean("Horizontal Guidelines",true),preferences.getBoolean("Block",true),preferences.getBoolean("Identical Number",true));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!preferences.getBoolean("Mark As Unsure",false)) {
            imageButtonEye.setVisibility(View.INVISIBLE);
            imageButtonEye.setOnClickListener(null);
        }
        else {
            imageButtonEye.setVisibility(View.VISIBLE);
            imageButtonEye.setOnClickListener(this);
        }
        rewardedVideoAd.resume(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    protected void onPause() {
        rewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        rewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putSerializable("SavedBoard", currentBoard);
        outState.putSerializable("StartBoard", startBoard);
        outState.putInt("hintCounterNumber",hintCounterNuber);
        outState.putInt("timesCompleted", timesCompleted);
        outState.putInt("boardId", boardId);
        outState.putInt("boardNumber", boardNumber);
    }





    @Override
    public void onClick(View v) {
        int row=0,column=0;
        int [] cellGroupFragments=new int[1];
        CellGroupFragment thisCellGroupFragment = new CellGroupFragment();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ;
        if(v.getId()!=buttonCheckBoard.getId()) {
            row = ((clickedGroup - 1) / 3) * 3 + (clickedCellId / 3);
            column = ((clickedGroup - 1) % 3) * 3 + ((clickedCellId) % 3);
            cellGroupFragments = new int[]{R.id.cellGroupFragment, R.id.cellGroupFragment2, R.id.cellGroupFragment3, R.id.cellGroupFragment4,
                    R.id.cellGroupFragment5, R.id.cellGroupFragment6, R.id.cellGroupFragment7, R.id.cellGroupFragment8, R.id.cellGroupFragment9};
            thisCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[clickedGroup - 1]);
        }

        switch (v.getId()) {
            case R.id.btnNumpad1:
                clickedCell.setText("1");
                currentBoard.setValue(row, column, 1);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",true),preferences.getBoolean("Horizontal Guidelines",true),preferences.getBoolean("Block",true),preferences.getBoolean("Identical Number",true));
                if (currentBoard.isBoardFull()) {
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                    buttonCheckBoard.setOnClickListener(this);
                }
                break;
            case R.id.btnNumpad2:
                clickedCell.setText("2");
                currentBoard.setValue(row, column, 2);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",true),preferences.getBoolean("Horizontal Guidelines",true),preferences.getBoolean("Block",true),preferences.getBoolean("Identical Number",true));
                if (currentBoard.isBoardFull()) {
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                    buttonCheckBoard.setOnClickListener(this);
                }
                break;
            case R.id.btnNumpad3:
                clickedCell.setText("3");
                currentBoard.setValue(row, column, 3);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",true),preferences.getBoolean("Horizontal Guidelines",true),preferences.getBoolean("Block",true),preferences.getBoolean("Identical Number",true));
                if (currentBoard.isBoardFull()) {
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                    buttonCheckBoard.setOnClickListener(this);
                }
                break;
            case R.id.btnNumpad4:
                clickedCell.setText("4");
                currentBoard.setValue(row, column, 4);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",true),preferences.getBoolean("Horizontal Guidelines",true),preferences.getBoolean("Block",true),preferences.getBoolean("Identical Number",true));
                if (currentBoard.isBoardFull()) {
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                    buttonCheckBoard.setOnClickListener(this);
                }
                break;
            case R.id.btnNumpad5:
                clickedCell.setText("5");
                currentBoard.setValue(row, column, 5);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",true),preferences.getBoolean("Horizontal Guidelines",true),preferences.getBoolean("Block",true),preferences.getBoolean("Identical Number",true));
                if (currentBoard.isBoardFull()) {
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                    buttonCheckBoard.setOnClickListener(this);
                }
                break;
            case R.id.btnNumpad6:
                clickedCell.setText("6");
                currentBoard.setValue(row, column, 6);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",true),preferences.getBoolean("Horizontal Guidelines",true),preferences.getBoolean("Block",true),preferences.getBoolean("Identical Number",true));
                if (currentBoard.isBoardFull()) {
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                    buttonCheckBoard.setOnClickListener(this);
                }
                break;
            case R.id.btnNumpad7:
                clickedCell.setText("7");
                currentBoard.setValue(row, column, 7);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",true),preferences.getBoolean("Horizontal Guidelines",true),preferences.getBoolean("Block",true),preferences.getBoolean("Identical Number",true));
                if (currentBoard.isBoardFull()) {
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                    buttonCheckBoard.setOnClickListener(this);
                }
                break;
            case R.id.btnNumpad8:
                clickedCell.setText("8");
                currentBoard.setValue(row, column, 8);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",true),preferences.getBoolean("Horizontal Guidelines",true),preferences.getBoolean("Block",true),preferences.getBoolean("Identical Number",true));
                if (currentBoard.isBoardFull()) {
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                    buttonCheckBoard.setOnClickListener(this);
                }
                break;
            case R.id.btnNumpad9:
                clickedCell.setText("9");
                currentBoard.setValue(row, column, 9);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",true),preferences.getBoolean("Horizontal Guidelines",true),preferences.getBoolean("Block",true),preferences.getBoolean("Identical Number",true));
                if (currentBoard.isBoardFull()) {
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                    buttonCheckBoard.setOnClickListener(this);
                }
                break;
            case R.id.btnNumpadEye:
                if (clickedCell.getBackground().getConstantState()!=getResources().getDrawable(R.drawable.table_border_cell_unsure).getConstantState())
                    clickedCell.setBackground(getResources().getDrawable(R.drawable.table_border_cell_unsure));
                else
                    clickedCell.setBackground(getResources().getDrawable(R.drawable.table_border_cell_selected));
                break;

            case R.id.btnNumpadBack:
                clickedCell.setText("");
                clickedCell.setBackground(getResources().getDrawable(R.drawable.table_border_cell));
                currentBoard.setValue(row, column, 0);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",false),preferences.getBoolean("Horizontal Guidelines",false),preferences.getBoolean("Block",false),preferences.getBoolean("Identical Number",false));
                buttonCheckBoard.setVisibility(View.INVISIBLE);
                break;
            case R.id.buttonCheckBoard:
                if (checkAllGroups() && currentBoard.isBoardCorrect()) {
                    Toast.makeText(this, getString(R.string.board_correct), Toast.LENGTH_SHORT).show();
                    timesCompleted++;
                    Utilities.saveSerializable(ContinuedSudokuGameActivity.this,timesCompleted,"timesCompleted.bin");
                    if(difficulty == 0 && !boardNoteList.get(boardNumber - 1).getStatus().equals("Completed") && timesCompleted==1) {
                        BoardNote easyBoardNote = new BoardNote("easy","Completed",boardNumber);
                        easyBoardNote.setId(boardId);
                        boardNoteViewModel.update(easyBoardNote);
                        for(int i=15;i<30;i++){
                            BoardNote tempBoardNote = boardNoteList.get(i);
                            if(tempBoardNote.getStatus().equals("Locked")){
                                BoardNote newBoardNote = new BoardNote("easy","Not completed",tempBoardNote.getBoardNumber());
                                newBoardNote.setId(tempBoardNote.getId());
                                boardNoteViewModel.update(newBoardNote);
                                showUnlockedDialog(i+1,"easy");
                                break;
                            }

                        }
                    }

                    if(difficulty == 1 && !boardNoteList.get(boardNumber - 1).getStatus().equals("Completed") && timesCompleted==1) {
                        BoardNote advancedBoardNote = new BoardNote("advanced","Completed",boardNumber);
                        advancedBoardNote.setId(boardId);
                        boardNoteViewModel.update(advancedBoardNote);
                        for(int i=15;i<30;i++){
                            BoardNote tempBoardNote = boardNoteList.get(i);
                            if(tempBoardNote.getStatus().equals("Locked")){
                                BoardNote newBoardNote = new BoardNote("advanced","Not completed",tempBoardNote.getBoardNumber());
                                newBoardNote.setId(tempBoardNote.getId());
                                boardNoteViewModel.update(newBoardNote);
                                showUnlockedDialog(i+1,"advanced");
                                break;
                            }

                        }
                    }

                    if(difficulty == 2 && !boardNoteList.get(boardNumber - 1).getStatus().equals("Completed") && timesCompleted==1) {
                        BoardNote professionalBoardNote = new BoardNote("professional","Completed",boardNumber);
                        professionalBoardNote.setId(boardId);
                        boardNoteViewModel.update(professionalBoardNote);
                        for(int i=15;i<30;i++){
                            BoardNote tempBoardNote = boardNoteList.get(i);
                            if(tempBoardNote.getStatus().equals("Locked")){
                                BoardNote newBoardNote = new BoardNote("professional","Not completed",tempBoardNote.getBoardNumber());
                                newBoardNote.setId(tempBoardNote.getId());
                                boardNoteViewModel.update(newBoardNote);
                                showUnlockedDialog(i+1,"professional");
                                break;
                            }

                        }
                    }

                } else {
                    Toast.makeText(this, getString(R.string.board_incorrect), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void setKeyBoardOn() {
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        imageButtonDelete.setOnClickListener(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(preferences.getBoolean("Mark As Unsure",true))
            imageButtonEye.setOnClickListener(this);
        else
            imageButtonEye.setOnClickListener(null);
    }
    private void setKeyBoardOff() {
        button1.setOnClickListener(null);
        button2.setOnClickListener(null);
        button3.setOnClickListener(null);
        button4.setOnClickListener(null);
        button5.setOnClickListener(null);
        button6.setOnClickListener(null);
        button7.setOnClickListener(null);
        button8.setOnClickListener(null);
        button9.setOnClickListener(null);
        imageButtonEye.setOnClickListener(null);
        imageButtonDelete.setOnClickListener(null);
        buttonCheckBoard.setOnClickListener(null);
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(ContinuedSudokuGameActivity.this).create();
        alertDialog.setTitle("Leave Game");
        alertDialog.setMessage("Any unsaved progress will be lost");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }




    //Solver Async Task

    public void startAsyncTask(Board board) {
        ExampleAsyncTask task = new ExampleAsyncTask(this);
        task.execute(board);
    }

    private static class ExampleAsyncTask extends AsyncTask<Board, Void, Void> {
        private WeakReference<ContinuedSudokuGameActivity> activityWeakReference;

        ExampleAsyncTask(ContinuedSudokuGameActivity activity) {
            activityWeakReference = new WeakReference<ContinuedSudokuGameActivity>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ContinuedSudokuGameActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            if (activity.progressBar != null)
                activity.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Board... boards) {
            ContinuedSudokuGameActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return null;
            }
            boards[0].solveSudoku(activity.solvedBoard.getGameCells(), activity.solvedBoard.getGameCells().length);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ContinuedSudokuGameActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            int row = ((activity.clickedGroup - 1) / 3) * 3 + (activity.clickedCellId / 3);
            int column = ((activity.clickedGroup - 1) % 3) * 3 + ((activity.clickedCellId) % 3);
            activity.clickedCell.setText("" + activity.solvedBoard.getGameCells()[row][column]);
            activity.currentBoard.setValue(row, column, activity.solvedBoard.getGameCells()[row][column]);
            if (activity.currentBoard.isBoardFull()) {
                activity.buttonCheckBoard.setVisibility(View.VISIBLE);
                activity.buttonCheckBoard.setOnClickListener(activity);
            }
            activity.progressBar.setVisibility(View.INVISIBLE);


        }
    }
    //

    // Hint Badge Method

    private void setupBadge() {

        if (hintCounterText != null) {
            this.hintCounterNuber--;
            if (hintCounterText.getVisibility() != View.VISIBLE)
                hintCounterText.setVisibility(View.VISIBLE);
            if (this.hintCounterNuber > 0) {
                hintCounterText.setText(String.valueOf(hintCounterNuber));
                //solves the board and writes the number down.
                startAsyncTask(this.startBoard);
            }
            if (this.hintCounterNuber == 0) {
                startAsyncTask(this.startBoard);
                hintCounterText.setText("+");
            }
            if (this.hintCounterNuber < 0) {
                AlertDialog alertDialog = new AlertDialog.Builder(ContinuedSudokuGameActivity.this).create();
                alertDialog.setTitle("Free Hint");
                alertDialog.setMessage("Watch a short video and get a free hint");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(rewardedVideoAd.isLoaded())
                            rewardedVideoAd.show();
                        dialog.dismiss();
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        }
    }

    private void showUnlockedDialog(int boardNumber, String difficulty){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.board_unlocked_alert_dialog,null);

        TextView boardUnlocked = view.findViewById(R.id.board_unlocked_text);
        boardUnlocked.setText("board "+boardNumber+" unlocked");
        ImageView board = view.findViewById(R.id.image_view_board);
        String drawableRes = difficulty+"board"+boardNumber;
        board.setImageResource(getResources().getIdentifier("com.eladper.sudoku:drawable/" +drawableRes, null, null));
        Button continueButton = view.findViewById(R.id.continue_button);

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        alertDialog.show();

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });
    }


}
