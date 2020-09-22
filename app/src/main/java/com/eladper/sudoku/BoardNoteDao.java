package com.eladper.sudoku;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;



@Dao
public interface BoardNoteDao {

    @Insert
    void  insert(BoardNote boardNote);

    @Update
    void update(BoardNote boardNote);

    @Query("SELECT * FROM board_note_table WHERE difficulty LIKE '%easy%' ORDER by boardNumber ASC")
    LiveData<List<BoardNote>> getEasyBoardNotes();

    @Query("SELECT * FROM board_note_table WHERE difficulty LIKE '%advanced%' ORDER by boardNumber ASC")
    LiveData<List<BoardNote>> getAdvancedBoardNotes();

    @Query("SELECT * FROM board_note_table WHERE difficulty LIKE '%professional%' ORDER by boardNumber ASC")
    LiveData<List<BoardNote>> getProfessionalBoardNotes();
}
