package com.eladper.sudoku;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;


import java.util.List;




public class BoardNoteViewModel extends AndroidViewModel {

    private BoardNoteRepository boardNoteRepository;
    private LiveData<List<BoardNote>> easyBoardNotes;
    private LiveData<List<BoardNote>> advancedBoardNotes;
    private LiveData<List<BoardNote>> professionalBoardNotes;

    public BoardNoteViewModel(@NonNull Application application) {
        super(application);
        boardNoteRepository = new BoardNoteRepository(application);
        easyBoardNotes = boardNoteRepository.getEasyBoardNotes();
        advancedBoardNotes = boardNoteRepository.getAdvancedBoardNotes();
        professionalBoardNotes = boardNoteRepository.getProfessionalBoardNotes();

    }

    public void insert (BoardNote boardNote){
        boardNoteRepository.insert(boardNote);
    }

    public void update (BoardNote boardNote){
        boardNoteRepository.update(boardNote);
    }

    public LiveData<List<BoardNote>> getEasyBoardNotes() {
        return easyBoardNotes;
    }

    public LiveData<List<BoardNote>> getAdvancedBoardNotes() {
        return advancedBoardNotes;
    }

    public LiveData<List<BoardNote>> getProfessionalBoardNotes() {
        return professionalBoardNotes;
    }
}
