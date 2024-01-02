package com.example.scannote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.scannote.database.entity.Note;
import com.example.scannote.database.entity.User;
import com.example.scannote.repository.NotesRepository;
import com.example.scannote.repository.UsersRepository;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {
    NotesRepository notesRepository;
    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        notesRepository = new NotesRepository(application.getApplicationContext());
    }

   public LiveData<List<Note>> getAllNotes() {
        return notesRepository.getAllNotes();
   }

}
