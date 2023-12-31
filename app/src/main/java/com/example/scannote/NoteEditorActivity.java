package com.example.scannote;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.scannote.database.entity.Note;
import com.example.scannote.viewmodel.NoteEditorActivityViewModel;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;

import java.util.Date;

public class NoteEditorActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int resultCode = -1;
    private Uri mImageUri;
    private Bitmap bitmap;
    Button setImage, analyseImage;
    ImageView imageView;
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

        setImage = findViewById(R.id.take_pic);
        imageView = (ImageView) findViewById(R.id.set_img);
        analyseImage = findViewById(R.id.analyse_pic);


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

    public void TestAnalyze() {
        MLLocalTextSetting setting = new MLLocalTextSetting.Factory()
                .setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
                // Specify languages that can be recognized.
                .setLanguage("en")
                .create();
        MLTextAnalyzer analyzer1 = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(setting);
        // Create an MLFrame object using the bitmap, which is the image data in bitmap format.
        MLFrame frame = MLFrame.fromBitmap(bitmap);
        Task<MLText> task = analyzer1.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(text -> Toast.makeText(NoteEditorActivity.this, text.getStringValue(), Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> {
            // Processing logic for recognition failure.
        });

    }

}
