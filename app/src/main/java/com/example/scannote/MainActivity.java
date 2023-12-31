package com.example.scannote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.scannote.adapter.NoteListAdapter;
import com.example.scannote.database.entity.Note;
import com.example.scannote.viewmodel.MainActivityViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String SELECTED_NOTE = "selected_note";
   private RecyclerView mNoteListRv;
   private FloatingActionButton mAddNoteFloatingBtn;
   NoteListAdapter noteListAdapter;
   MainActivityViewModel mainActivityViewModel;
   ArrayList<Note> noteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        mNoteListRv = findViewById(R.id.note_list_rv);
        mAddNoteFloatingBtn = findViewById(R.id.add_note_fab);

        mAddNoteFloatingBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, NoteEditorActivity.class);
            startActivity(intent);
        });

        mainActivityViewModel.getAllNotes().observe(this, this::updateNoteList);

        noteListAdapter = new NoteListAdapter(noteList);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        mNoteListRv.setLayoutManager(lm);
        mNoteListRv.setAdapter(noteListAdapter);

    }

    private void updateNoteList(List<Note> notes) {
        noteList.clear();
        noteList.addAll(notes);
        noteListAdapter.notifyDataSetChanged();
    }
}