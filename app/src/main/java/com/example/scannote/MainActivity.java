package com.example.scannote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.scannote.adapter.NoteListAdapter;
import com.example.scannote.database.entity.Note;
import com.example.scannote.viewmodel.MainActivityViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.huawei.hms.mlsdk.common.MLApplication;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static String API_KEY = "5C95750ED6D42CCC6EFA0778ED082FD192341718BE02731F03CC5617D9D9A94F";
    public static String SELECTED_NOTE = "selected_note";
    NoteListAdapter noteListAdapter;
   MainActivityViewModel mainActivityViewModel;
   ArrayList<Note> noteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            MLApplication.initialize(getApplicationContext());// Called if your app runs multiple processes.
            MLApplication.getInstance().setApiKey(API_KEY);
        } catch (Exception exception){
            Log.d("ML Initialization", "onCreate:" + exception.getMessage());
        }

        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        RecyclerView mNoteListRv = findViewById(R.id.note_list_rv);
        FloatingActionButton mAddNoteFloatingBtn = findViewById(R.id.add_note_fab);

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