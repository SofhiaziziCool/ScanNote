package com.example.scannote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class MainActivity extends AppCompatActivity implements NoteListAdapter.OnNoteListener {

    // Constants
    private static final String TAG = "MainActivity";
    public static String API_KEY = "5C95750ED6D42CCC6EFA0778ED082FD192341718BE02731F03CC5617D9D9A94F";
    public static final String SELECTED_NOTE = "SLNTE";

    // Vars
    private final ArrayList<Note> mNotes = new ArrayList<>();
    private MainActivityViewModel mMainActivityViewModel;
    private NoteListAdapter mNoteListAdapter;

    //UI Views
    private RecyclerView mRecyclerView;
    private FloatingActionButton mAddNoteBtn;


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



        mMainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        mRecyclerView = findViewById(R.id.note_list_rv);
        mAddNoteBtn = findViewById(R.id.add_note_fab);

        mAddNoteBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, NoteEditorActivity.class);
            startActivity(intent);
        });

        mMainActivityViewModel.getAllNotes().observe(this, this::updateNoteList);

        mNoteListAdapter = new NoteListAdapter(mNotes, this);
        GridLayoutManager lm = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.setAdapter(mNoteListAdapter);
    }

    private void updateNoteList(List<Note> notes) {
        mNotes.clear();
        mNotes.addAll(notes);
        mNoteListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNoteClick(int position) {
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra(SELECTED_NOTE, mNotes.get(position));
        startActivity(intent);
    }
}