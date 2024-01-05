package com.example.scannote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import com.example.scannote.adapter.NoteListAdapter;
import com.example.scannote.database.entity.Note;
import com.example.scannote.viewmodel.MainActivityViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.huawei.hms.mlsdk.common.MLApplication;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteListAdapter.OnNoteListener {

    // Constants
    private static final String TAG = "MainActivity";
    public static String API_KEY = "DAEDAMI7lg9IN8WopLGAJB57mN5jCvRf9tiNopPxcDqvbYO+r7sJSIL66ejUmbCwHrURV/++7Nyumt+GUDJwrMuUOjMn6z8+eRe2MQ==";
    public static final String SELECTED_NOTE = "SLNTE";

    // Vars
    private final ArrayList<Note> mNotes = new ArrayList<>();
    private NoteListAdapter mNoteListAdapter;
    private Activity mActivity;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        needsAuth();

        mActivity = this;

        try {
            MLApplication.initialize(getApplicationContext());// Called if your app runs multiple processes.
            MLApplication.getInstance().setApiKey(API_KEY);
        } catch (Exception exception){
            Log.d("ML Initialization", "onCreate:" + exception.getMessage());
        }


        MainActivityViewModel mMainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        //UI Views
        RecyclerView mRecyclerView = findViewById(R.id.note_list_rv);
        FloatingActionButton mAddNoteBtn = findViewById(R.id.add_note_fab);
        Button mSignOutBtn = findViewById(R.id.sign_out_btn);

        mSignOutBtn.setOnClickListener(view -> AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> mActivity.finish()));

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

    @Override
    protected void onResume() {
        super.onResume();
        needsAuth();
    }

    private void needsAuth() {
        if(currentUser == null){
            startActivity(new Intent(this, AuthActivity.class));
            this.finish();
        }
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