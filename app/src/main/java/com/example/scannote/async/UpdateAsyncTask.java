package com.example.scannote.async;

import android.os.AsyncTask;
import android.util.Log;

import com.example.scannote.database.dao.NotesDao;
import com.example.scannote.database.entity.Note;

public class UpdateAsyncTask extends AsyncTask<Note, Void, Void> {
    private static final String TAG = "UpdateAsyncTask";
    private final NotesDao mNotesDao;

    public UpdateAsyncTask(NotesDao mNotesDao) {
        this.mNotesDao = mNotesDao;
    }

    @Override
    protected Void doInBackground(Note... notes) {
        Log.d(TAG, "doInBackground: Thread " + Thread.currentThread().getName());
        mNotesDao.update(notes);
        return null;
    }
}
