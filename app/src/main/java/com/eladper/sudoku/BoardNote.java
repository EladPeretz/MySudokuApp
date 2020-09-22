package com.eladper.sudoku;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "board_note_table")
public class BoardNote {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String difficulty;

    private String status;

    private int boardNumber;


    public BoardNote(String difficulty, String status, int boardNumber) {
        this.difficulty = difficulty;
        this.status = status;
        this.boardNumber = boardNumber;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getStatus() {
        return status;
    }

    public int getBoardNumber() {
        return boardNumber;
    }

}
