package com.example.scannote;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.scannote.database.entity.Note;
import com.example.scannote.util.DateUtility;
import com.example.scannote.viewmodel.NoteEditorActivityViewModel;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.LensEngine;
import com.huawei.hms.mlsdk.common.MLAnalyzer;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;

import java.io.IOException;
import java.util.Date;

public class NoteEditorActivity extends AppCompatActivity implements TextWatcher {

    //Huawei
    private static final int CAMERA_PERMISSION_CODE = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int resultCode = -1;
    private Uri mImageUri;
    private Bitmap bitmap;
    Button setImage, analyseImage;
    ImageView imageView;

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

        setImage = findViewById(R.id.take_pic);
        imageView = (ImageView) findViewById(R.id.set_img);
        analyseImage = findViewById(R.id.analyse_pic);

        setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestBitmap();
            }
        });

        analyseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testAnalyze();
            }
        });

        // Check whether the app has the camera permission.
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            requestCameraPermission();
        }
        {

        }

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

    //Starts Huawei integration
    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) &&
                !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, CAMERA_PERMISSION_CODE);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != CAMERA_PERMISSION_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
    }

    public void requestBitmap(){
        Intent intent;
        if (Build.VERSION.SDK_INT < 20){
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }else{
            intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        intent.setType("image/*");
        //startActivityForResult actually deprecated
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), PICK_IMAGE_REQUEST);
    }

    public void testAnalyze() {

        Context context = getApplicationContext();
        MLTextAnalyzer analyzer = new MLTextAnalyzer.Factory(context).setLocalOCRMode(MLLocalTextSetting.OCR_DETECT_MODE).setLanguage("zh").create();
        //SparseArray<MLText.Block> blocks = analyzer.analyseFrame(frame);

        //text recognition from camera on device
        // Method 2: Use the custom parameter MLTextAnalyzer.Factory to configure the text analyzer. Other supported languages can be recognized.

        MLTextAnalyzer.Factory factory = new MLTextAnalyzer.Factory(context);
// Specify languages that can be recognized.
        factory.setLanguage("en");
        //MLTextAnalyzer analyzer = factory.create();

        analyzer.setTransactor(new OcrDetectorProcessor());

        LensEngine lensEngine = new LensEngine.Creator(getApplicationContext(),analyzer)
                .setLensType(LensEngine.BACK_LENS)
                .applyDisplayDimension(1440, 1080)
                .applyFps(30.0f)
                .enableAutomaticFocus(true)
                .create();

        /*SurfaceView mSurfaceView = findViewById(R.id.surface_view);
        try {
            lensEngine.run(mSurfaceView.getHolder());
        } catch (IOException e) {
            // Exception handling logic.
        }*/

        if (analyzer != null) {
            try {
                analyzer.stop();
            } catch (IOException e) {
                // Exception handling.
            }
        }
        if (lensEngine != null) {
            lensEngine.release();
        }

        //text recognition from images on device
        MLLocalTextSetting setting = new MLLocalTextSetting.Factory()
                .setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
                // Specify languages that can be recognized.
                .setLanguage("en")
                .create();
        MLTextAnalyzer analyzer1 = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(setting);

        // Create an MLFrame object using the bitmap, which is the image data in bitmap format.
        MLFrame frame = MLFrame.fromBitmap(bitmap);
        Task<MLText> task = analyzer1.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(new OnSuccessListener<MLText>() {
            @Override
            public void onSuccess(MLText text) {
                MLText tt = text;
                Toast.makeText(NoteEditorActivity.this,tt.getStringValue(),Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // Processing logic for recognition failure.
            }



        });



    }
    public class OcrDetectorProcessor implements MLAnalyzer.MLTransactor<MLText.Block> {

        @Override
        public void transactResult(MLAnalyzer.Result<MLText.Block> results) {
            SparseArray<MLText.Block> items = results.getAnalyseList();
            // Determine detection result processing as required. Note that only the detection results are processed.
            // Other detection-related APIs provided by ML Kit cannot be called.
        }
        @Override
        public void destroy() {
            // Callback method used to release resources when the detection ends.
        }


    }
}
