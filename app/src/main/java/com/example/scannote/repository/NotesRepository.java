package com.example.scannote.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.scannote.database.AppDatabase;
import com.example.scannote.database.dao.NotesDao;
import com.example.scannote.database.entity.Note;

import java.util.List;

public class NotesRepository {
    private final NotesDao notesDao;
    public NotesRepository(Context context) {
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        notesDao = appDatabase.notesDao();
    }

    public void insert(Note note) {
        Log.d("TAG", "insert: " + note);
        new Thread(() -> notesDao.insert(note)).start();
    }

    public void update(Note note) {
        Log.d("TAG", "update: " + note.getTitle());
        new Thread(() -> notesDao.update(note)).start();
    }

    public LiveData<List<Note>> getAllNotes() {
        return notesDao.getAllNotes();
    }
}
