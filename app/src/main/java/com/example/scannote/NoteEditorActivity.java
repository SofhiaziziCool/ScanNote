package com.example.scannote;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.scannote.database.entity.Note;
import com.example.scannote.util.DateUtility;
import com.example.scannote.viewmodel.NoteEditorActivityViewModel;

import java.util.Date;

public class NoteEditorActivity extends AppCompatActivity implements TextWatcher {

    // Constants
    public final static String NEW_NOTE_TITLE = "New Note";
    private final static long DELAY_MILLIS = 2000;

    //Vars
    private NoteEditorActivityViewModel noteEditorActivityViewModel;
    private boolean mIsNewNote;
    private Note mInitialNote;
    private Note mFinalNote;
    private final Handler handler = new Handler();
    private final Runnable saveNoteRunnable = this::saveNoteChanges;


    //UI Views
    EditText mNoteTitleTv, mNoteContentTv;
    Button saveBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        noteEditorActivityViewModel = new ViewModelProvider(this).get(NoteEditorActivityViewModel.class);

        mNoteTitleTv = findViewById(R.id.note_title_tv);
        mNoteContentTv = findViewById(R.id.note_content_tv);
        saveBtn = findViewById(R.id.save_btn);


        if (checkForIntent()) {
            // Go to edit note
            setInitialNoteProperties();
        } else {
            // Go to new note
            setNewNoteProperties();
        }

        mNoteTitleTv.addTextChangedListener(this);
        mNoteContentTv.addTextChangedListener(this);

        saveBtn.setOnClickListener(view -> {
            setEditedNoteProperties();
            saveNoteChanges();
        });

    }

    private boolean checkForIntent() {
        if (getIntent().hasExtra(MainActivity.SELECTED_NOTE)) {
            mInitialNote = getIntent().getParcelableExtra(MainActivity.SELECTED_NOTE);
            mFinalNote = getIntent().getParcelableExtra(MainActivity.SELECTED_NOTE);
            mIsNewNote = false;
            return true;
        }
        mIsNewNote = true;
        return false;
    }

    private void saveNoteChanges() {
        setEditedNoteTimeStamp();
        if (mIsNewNote) {
            Toast.makeText(this, "Creating note... " + mFinalNote.getId(), Toast.LENGTH_SHORT).show();
            noteEditorActivityViewModel.createNewNote(mFinalNote, noteId -> {
                mFinalNote.setId(noteId);
                mIsNewNote = false;
            });
        } else {
            Toast.makeText(this, "Updating note...", Toast.LENGTH_SHORT).show();
            String timeStamp = new Date().toString();
            mFinalNote.setTimeStamp(timeStamp);
            noteEditorActivityViewModel.updateNote(mFinalNote);
        }
    }

    private void setNewNoteProperties() {
        mNoteTitleTv.setText(this.getString(R.string.new_note));
        mInitialNote = new Note();
        mFinalNote = new Note();
        mInitialNote.setTitle(NEW_NOTE_TITLE);
        mFinalNote.setTitle(NEW_NOTE_TITLE);
    }

    private void setInitialNoteProperties() {
        mNoteTitleTv.setText(mInitialNote.getTitle());
        mNoteContentTv.setText(mInitialNote.getContents());
    }

    private void setEditedNoteProperties() {
        mFinalNote.setTitle(mNoteTitleTv.getText().toString());
        mFinalNote.setContents(mNoteContentTv.getText().toString());
    }

    private void setEditedNoteTimeStamp() {
        mFinalNote.setTimeStamp(DateUtility.getCurrentTimeStamp());
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        setEditedNoteProperties();
        handler.removeCallbacks(saveNoteRunnable);
        handler.postDelayed(saveNoteRunnable, DELAY_MILLIS);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
