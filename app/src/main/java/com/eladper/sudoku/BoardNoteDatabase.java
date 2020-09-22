package com.eladper.sudoku;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;


@Database(entities = BoardNote.class, version = 1)
public abstract class BoardNoteDatabase extends RoomDatabase {

    private static BoardNoteDatabase instance;

    public abstract BoardNoteDao noteDao();

    public static synchronized BoardNoteDatabase getInstance(Context context){
        if(instance==null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    BoardNoteDatabase.class, "board_note_database")
                    .addCallback(roomCallback)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;

    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private BoardNoteDao noteDao;

        private PopulateDbAsyncTask(BoardNoteDatabase db) {
            noteDao = db.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i=1;i<31;i++) {
                if (i < 16)
                    noteDao.insert(new BoardNote("easy", "Not completed", i));
                else
                    noteDao.insert(new BoardNote("easy", "Locked", i));
            }
            for (int i=1;i<31;i++) {
                if (i < 16)
                    noteDao.insert(new BoardNote("advanced", "Not completed", i));
                else
                    noteDao.insert(new BoardNote("advanced", "Locked", i));
            }
            for (int i=1;i<31;i++) {
                if (i < 16)
                    noteDao.insert(new BoardNote("professional", "Not completed", i));
                else
                    noteDao.insert(new BoardNote("professional", "Locked", i));
            }
            return null;
        }
    }
}
