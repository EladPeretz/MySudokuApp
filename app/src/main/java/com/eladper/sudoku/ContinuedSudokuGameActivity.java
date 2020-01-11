package com.eladper.sudoku;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ContinuedSudokuGameActivity extends AppCompatActivity implements CellGroupFragment.OnFragmentInteractionListener, View.OnClickListener {
    private final String TAG = "SudokuGameActivity";
    private TextView clickedCell;
    private int clickedGroup;
    private int clickedCellId;
    private Board startBoard;
    private Board currentBoard;
    private Button button1,button2,button3,button4,button5,button6,button7,button8,button9;
    private ImageButton imageButtonEye,imageButtonDelete,buttonCheckBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusBarHide();
        setContentView(R.layout.activity_sudoku_game);
        int difficulty = getIntent().getIntExtra("difficulty", 0);
        ArrayList<Board> boards = readGameBoards(difficulty);

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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!preferences.getBoolean("Mark As Unsure",false))
            imageButtonEye.setVisibility(View.INVISIBLE);
        else
            imageButtonEye.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        currentBoard=Utilities.readSerializable(ContinuedSudokuGameActivity.this,"CurrentBoard.bin");
        startBoard=Utilities.readSerializable(ContinuedSudokuGameActivity.this,"StartBoard.bin");
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

                if (currentValue != 0) {
                    tempCellGroupFragment.setValue(groupPosition, currentValue);
                }

                if (currentBoard.getGameCells()[i][j] != startBoard.getGameCells()[i][j])
                    tempCellGroupFragment.setValueOfSaved(groupPosition, currentValue);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sudoku_game_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:
                AlertDialog alertDialog = new AlertDialog.Builder(ContinuedSudokuGameActivity.this).create();
                alertDialog.setTitle("Save Game");
                alertDialog.setMessage("Do you want to save the game");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Utilities.saveSerializable(ContinuedSudokuGameActivity.this, currentBoard, "CurrentBoard.bin");
                        Utilities.saveSerializable(ContinuedSudokuGameActivity.this, startBoard, "StartBoard.bin");
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
        thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",false),preferences.getBoolean("Horizontal Guidelines",false),preferences.getBoolean("Block",false),preferences.getBoolean("Identical Number",false));
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

        statusBarHide();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("SavedBoard", currentBoard);
        outState.putSerializable("StartBoard", startBoard);
        super.onSaveInstanceState(outState);

    }

    // A function to hide the status bar.
    public void statusBarHide() {
        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

    }

    @Override
    public void onClick(View v) {
        int row = ((clickedGroup - 1) / 3) * 3 + (clickedCellId / 3);
        int column = ((clickedGroup - 1) % 3) * 3 + ((clickedCellId) % 3);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int[] cellGroupFragments=new int[]{R.id.cellGroupFragment, R.id.cellGroupFragment2, R.id.cellGroupFragment3,R.id.cellGroupFragment4,
                R.id.cellGroupFragment5, R.id.cellGroupFragment6, R.id.cellGroupFragment7, R.id.cellGroupFragment8, R.id.cellGroupFragment9};
        CellGroupFragment thisCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[clickedGroup - 1]);

        switch (v.getId()) {
            case R.id.btnNumpad1:
                clickedCell.setText("1");
                currentBoard.setValue(row, column, 1);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",false),preferences.getBoolean("Horizontal Guidelines",false),preferences.getBoolean("Block",false),preferences.getBoolean("Identical Number",false));
                if (currentBoard.isBoardFull())
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                break;
            case R.id.btnNumpad2:
                clickedCell.setText("2");
                currentBoard.setValue(row, column, 2);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",false),preferences.getBoolean("Horizontal Guidelines",false),preferences.getBoolean("Block",false),preferences.getBoolean("Identical Number",false));
                if (currentBoard.isBoardFull())
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                break;
            case R.id.btnNumpad3:
                clickedCell.setText("3");
                currentBoard.setValue(row, column, 3);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",false),preferences.getBoolean("Horizontal Guidelines",false),preferences.getBoolean("Block",false),preferences.getBoolean("Identical Number",false));
                if (currentBoard.isBoardFull())
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                break;
            case R.id.btnNumpad4:
                clickedCell.setText("4");
                currentBoard.setValue(row, column, 4);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",false),preferences.getBoolean("Horizontal Guidelines",false),preferences.getBoolean("Block",false),preferences.getBoolean("Identical Number",false));
                if (currentBoard.isBoardFull())
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                break;
            case R.id.btnNumpad5:
                clickedCell.setText("5");
                currentBoard.setValue(row, column, 5);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",false),preferences.getBoolean("Horizontal Guidelines",false),preferences.getBoolean("Block",false),preferences.getBoolean("Identical Number",false));
                if (currentBoard.isBoardFull())
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                break;
            case R.id.btnNumpad6:
                clickedCell.setText("6");
                currentBoard.setValue(row, column, 6);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",false),preferences.getBoolean("Horizontal Guidelines",false),preferences.getBoolean("Block",false),preferences.getBoolean("Identical Number",false));
                if (currentBoard.isBoardFull())
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                break;
            case R.id.btnNumpad7:
                clickedCell.setText("7");
                currentBoard.setValue(row, column, 7);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",false),preferences.getBoolean("Horizontal Guidelines",false),preferences.getBoolean("Block",false),preferences.getBoolean("Identical Number",false));
                if (currentBoard.isBoardFull())
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                break;
            case R.id.btnNumpad8:
                clickedCell.setText("8");
                currentBoard.setValue(row, column, 8);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",false),preferences.getBoolean("Horizontal Guidelines",false),preferences.getBoolean("Block",false),preferences.getBoolean("Identical Number",false));
                if (currentBoard.isBoardFull())
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                break;
            case R.id.btnNumpad9:
                clickedCell.setText("9");
                currentBoard.setValue(row, column, 9);
                thisCellGroupFragment.showGuidelines(clickedGroup,clickedCellId,preferences.getBoolean("Vertical Guidelines",false),preferences.getBoolean("Horizontal Guidelines",false),preferences.getBoolean("Block",false),preferences.getBoolean("Identical Number",false));
                if (currentBoard.isBoardFull())
                    buttonCheckBoard.setVisibility(View.VISIBLE);
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
                currentBoard.isBoardCorrect();
                if (checkAllGroups() && currentBoard.isBoardCorrect()) {
                    Toast.makeText(this, getString(R.string.board_correct), Toast.LENGTH_SHORT).show();
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
        buttonCheckBoard.setOnClickListener(this);
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

}