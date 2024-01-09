package com.example.scannote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.scannote.database.entity.DBImage;
import com.example.scannote.database.entity.Note;
import com.example.scannote.listeners.OnNoteCreatedListener;
import com.example.scannote.repository.DBImageRepository;
import com.example.scannote.repository.NotesRepository;

import java.util.List;

public class NoteEditorActivityViewModel extends AndroidViewModel {

    NotesRepository notesRepository;
    DBImageRepository dbImageRepository;

    public NoteEditorActivityViewModel(@NonNull Application application) {
        super(application);
        this.notesRepository = new NotesRepository(application.getApplicationContext());
        this.dbImageRepository = new DBImageRepository(application.getApplicationContext());
    }

    public void createNewNote(Note note, OnNoteCreatedListener listener){
        notesRepository.insert(note, listener);
    }

    public void updateNote(Note note) {
        notesRepository.update(note);
    }

    public void deleteNote(Note note) {
        notesRepository.delete(note);
    }

    public void saveImageToLocalDb(DBImage dbImage) {
        dbImageRepository.insert(dbImage);
    }

    public void deleteImage(DBImage DBImage) {
        dbImageRepository.deleteImage(DBImage);
    }

    public LiveData<List<DBImage>> getAllImages(int noteId) {
       return dbImageRepository.getAllImagesByNoteId(noteId);
    }
}
