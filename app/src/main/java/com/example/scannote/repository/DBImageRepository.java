package com.example.scannote.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.scannote.async.UpdateAsyncTask;
import com.example.scannote.database.AppDatabase;
import com.example.scannote.database.dao.DBImageDao;
import com.example.scannote.database.dao.NotesDao;
import com.example.scannote.database.entity.DBImage;
import com.example.scannote.database.entity.Note;
import com.example.scannote.listeners.OnNoteCreatedListener;

import java.util.List;

public class DBImageRepository {
    AppDatabase appDatabase;

    public DBImageRepository(Context context) {
        appDatabase = AppDatabase.getDatabase(context);
    }

    public DBImageDao getDBImageDao() {
        return appDatabase.dbImageDao();
    }

    public void insert(DBImage image) {
        new Thread(() -> {
             getDBImageDao().insert(image);
        }).start();
    }

    public void deleteImage(DBImage image) {
        new Thread(() -> {
            getDBImageDao().delete(image);
        }).start();
    }

    public LiveData<List<DBImage>> getAllImagesByNoteId(int noteId) {
        return getDBImageDao().getAllImagesByNoteId(noteId);
    }

}
