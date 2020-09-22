package com.eladper.sudoku;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;






public class BoardNoteActivity extends AppCompatActivity {

    private BoardNoteViewModel boardNoteViewModel;
    private int difficulty;
    private RecyclerView recyclerView;
    List <BoardNote> boardNoteList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_notes);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        final BoardNoteAdapter boardNoteAdapter = new BoardNoteAdapter();
        recyclerView.setAdapter(boardNoteAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);


        difficulty = getIntent().getIntExtra("difficulty",-1);

        boardNoteViewModel = ViewModelProviders.of(this).get(BoardNoteViewModel.class);

        if(difficulty==0) {
            boardNoteViewModel.getEasyBoardNotes().observe(this, new Observer<List<BoardNote>>() {
                @Override
                public void onChanged(List<BoardNote> boardNotes) {
                    boardNoteList = boardNotes;
                    boardNoteAdapter.setNotes(boardNotes);
                }
            });
        }

        if(difficulty==1) {
            boardNoteViewModel.getAdvancedBoardNotes().observe(this, new Observer<List<BoardNote>>() {
                @Override
                public void onChanged(List<BoardNote> boardNotes) {
                    boardNoteList = boardNotes;
                    boardNoteAdapter.setNotes(boardNotes);
                }
            });
        }

        if(difficulty==2) {
            boardNoteViewModel.getProfessionalBoardNotes().observe(this, new Observer<List<BoardNote>>() {
                @Override
                public void onChanged(List<BoardNote> boardNotes) {
                    boardNoteList = boardNotes;
                    boardNoteAdapter.setNotes(boardNotes);
                }
            });
        }

        boardNoteAdapter.setOnClickListener(new BoardNoteAdapter.onItemClickListener() {
            @Override
            public void onItemClick(BoardNote boardNote) {
                if (!boardNote.getStatus().equals("Locked")) {
                    Intent intent = new Intent(BoardNoteActivity.this, SudokuGameActivity.class);
                    intent.putExtra("difficulty", difficulty);
                    intent.putExtra(BoardNoteAdapter.EXTRA_ID, boardNote.getId());
                    intent.putExtra(BoardNoteAdapter.EXTRA_BOARD_NUMBER, boardNote.getBoardNumber());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }
            }
        });
    }

}



