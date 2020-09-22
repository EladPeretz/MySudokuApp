package com.eladper.sudoku;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;


public class BoardNoteRepository {

    private BoardNoteDao boardNoteDao;

    private LiveData<List<BoardNote>> easyBoardNotes;
    private LiveData<List<BoardNote>> advancedBoardNotes;
    private LiveData<List<BoardNote>> professionalBoardNotes;

    public BoardNoteRepository(Application application){
        BoardNoteDatabase boardNoteDatabase = BoardNoteDatabase.getInstance(application);
        boardNoteDao = boardNoteDatabase.noteDao();
        easyBoardNotes = boardNoteDao.getEasyBoardNotes();
        advancedBoardNotes = boardNoteDao.getAdvancedBoardNotes();
        professionalBoardNotes = boardNoteDao.getProfessionalBoardNotes();
    }

    public void insert(BoardNote boardNote){
        new InsertBoardNoteAsyncTask(boardNoteDao).execute(boardNote);
    }

    public void update(BoardNote boardNote){
        new UpdateBoardNoteAsyncTask(boardNoteDao).execute(boardNote);
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

    private static class InsertBoardNoteAsyncTask extends AsyncTask<BoardNote,Void,Void> {
        private BoardNoteDao boardNoteDao;

        private  InsertBoardNoteAsyncTask(BoardNoteDao boardNoteDao){
            this.boardNoteDao=boardNoteDao;
        }
        @Override
        protected Void doInBackground(BoardNote... boardNotes) {
            boardNoteDao.insert(boardNotes[0]);
            return null;
        }
    }

    private static class UpdateBoardNoteAsyncTask extends AsyncTask<BoardNote,Void,Void> {
        private BoardNoteDao boardNoteDao;

        private UpdateBoardNoteAsyncTask(BoardNoteDao boardNoteDao){
            this.boardNoteDao=boardNoteDao;
        }
        @Override
        protected Void doInBackground(BoardNote... boardNotes) {
            boardNoteDao.update(boardNotes[0]);
            return null;
        }
    }


}
