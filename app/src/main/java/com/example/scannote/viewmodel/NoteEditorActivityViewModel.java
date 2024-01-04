package com.example.scannote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.scannote.database.entity.Note;
import com.example.scannote.listeners.OnNoteCreatedListener;
import com.example.scannote.repository.NotesRepository;

public class NoteEditorActivityViewModel extends AndroidViewModel {

    NotesRepository notesRepository;

    public NoteEditorActivityViewModel(@NonNull Application application) {
        super(application);
        this.notesRepository = new NotesRepository(application.getApplicationContext());
    }

    public void createNewNote(Note note, OnNoteCreatedListener listener){
        notesRepository.insert(note, listener);
    }

    public void updateNote(Note note) {
        notesRepository.update(note);
    }
}
