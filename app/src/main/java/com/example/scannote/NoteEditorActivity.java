package com.example.scannote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.scannote.database.AppDatabase;
import com.example.scannote.database.dao.NotesDao;
import com.example.scannote.database.dao.UsersDao;
import com.example.scannote.database.entity.Note;
import com.example.scannote.viewmodel.NoteEditorActivityViewModel;

import java.util.Date;
import java.util.HashSet;

public class NoteEditorActivity extends AppCompatActivity {

    public static String NEW_NOTE_TITLE = "New Note";
    EditText mNoteTitleTv, mNoteContentTv;
    boolean mIsNewNote;
    Note mInitialNote;
    Note mFinalNote;
    NoteEditorActivityViewModel noteEditorActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        noteEditorActivityViewModel = new ViewModelProvider(this).get(NoteEditorActivityViewModel.class);

        mNoteTitleTv = findViewById(R.id.note_title_tv);
        mNoteContentTv = findViewById(R.id.note_content_tv);


        if (checkForIntent()) {
            // Go to edit note
        } else {
            // Go to new note
            setNewNoteProperties();
        }

        mNoteTitleTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mFinalNote.setTitle(String.valueOf(charSequence));
                mFinalNote.setTimeStamp(new Date().toString());
                noteEditorActivityViewModel.updateNote(mFinalNote);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mNoteContentTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mFinalNote.setContents(String.valueOf(charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                    mFinalNote.setTimeStamp(new Date().toString());
                    noteEditorActivityViewModel.updateNote(mFinalNote);
            }
        });


    }

    private boolean checkForIntent() {
        if (getIntent().hasExtra(MainActivity.SELECTED_NOTE)) {
            mIsNewNote = false;
            return true;
        }
        mIsNewNote = true;
        return false;
    }

    private void setNewNoteProperties() {
        mNoteTitleTv.setText(this.getString(R.string.new_note));

        mInitialNote = new Note();
        mInitialNote.setTitle(NEW_NOTE_TITLE);
        mInitialNote.setTimeStamp(new Date().toString());
        mFinalNote = mInitialNote;

        noteEditorActivityViewModel.createNewNote(mInitialNote);

    }

//    @Override
//    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//    }
//
//    @Override
//    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//    }
//
//    @Override
//    public void afterTextChanged(Editable editable) {
//
//    }
}
