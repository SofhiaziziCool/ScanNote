package com.example.scannote.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.scannote.async.UpdateAsyncTask;
import com.example.scannote.database.AppDatabase;
import com.example.scannote.database.dao.NotesDao;
import com.example.scannote.database.entity.Note;
import com.example.scannote.listeners.OnNoteCreatedListener;

import java.util.List;

public class NotesRepository {
    AppDatabase appDatabase;

    public NotesRepository(Context context) {
        appDatabase = AppDatabase.getDatabase(context);
    }

    public NotesDao getNoteDao() {
        return appDatabase.notesDao();
    }

    public void insert(Note note, OnNoteCreatedListener listener) {
        new Thread(() -> {
            long a = getNoteDao().insert(note);
            int noteId = Integer.parseInt(String.valueOf(a));
            listener.onNoteCreated(noteId);
        }).start();
    }

    public void update(Note note) {
        Log.d("TAG", "update: " + note.getContents());
        new UpdateAsyncTask(getNoteDao()).execute(note);
    }

    public LiveData<List<Note>> getAllNotes() {
        return getNoteDao().getAllNotes();
    }

}
