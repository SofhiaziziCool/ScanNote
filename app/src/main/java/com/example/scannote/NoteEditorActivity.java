package com.example.scannote;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.scannote.database.entity.DBImage;
import com.example.scannote.database.entity.Note;
import com.example.scannote.util.DateUtility;
import com.example.scannote.viewmodel.NoteEditorActivityViewModel;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.LensEngine;
import com.huawei.hms.mlsdk.common.MLAnalyzer;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoteEditorActivity extends AppCompatActivity implements TextWatcher {

    // Constants
    public final static String NEW_NOTE_TITLE = "New Note";
    private static final String TAG = "NoteEditorActivity";
    private static final int CAMERA_PERMISSION_CODE = 1001;
    private static final int PICK_IMAGE_REQUEST = 1002;
    private static final int resultCode = -1;
    private final static long DELAY_MILLIS = 2000;

    //UI Views
    Button analyseImage;
    ImageView imageView, setImage, deleteNoteBtn, exportNoteBtn;
    EditText mNoteTitleTv, mNoteContentTv;
    Button saveBtn;

    //Vars
    private Bitmap bitmap;
    private Uri mImageUri;
    private final ArrayList<Uri> analyzedImages = new ArrayList<>();
    private final Handler handler = new Handler();
    private NoteEditorActivityViewModel noteEditorActivityViewModel;
    private boolean mIsNewNote;
    private Note mInitialNote;
    private Note mFinalNote;
    private final Runnable saveNoteRunnable = this::saveNoteChanges;
    ActivityResultLauncher<String> imagePickerActivityLauncher;
    private int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        //Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getColor(R.color.black)));

        noteEditorActivityViewModel = new ViewModelProvider(this).get(NoteEditorActivityViewModel.class);

        mNoteTitleTv = findViewById(R.id.note_title_tv);
        mNoteContentTv = findViewById(R.id.note_content_tv);
        saveBtn = findViewById(R.id.save_btn);
        setImage = findViewById(R.id.take_pic);
        imageView = findViewById(R.id.imageView);
        analyseImage = findViewById(R.id.analyse_pic);
        deleteNoteBtn = findViewById(R.id.delete_note);
        deleteNoteBtn.setOnClickListener(v -> {
            deleteNote();
        });
        exportNoteBtn = findViewById(R.id.export_note);
        exportNoteBtn.setOnClickListener(v -> createNoteFile(mFinalNote.getTitle() +".txt", mFinalNote.getTitle(), mFinalNote.getContents()));

        imagePickerActivityLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
//            mImageUri = uri;
//            if (uri != null) {
//                Glide
//                        .with(this)
//                        .load(mImageUri)
//                        .centerCrop()
//                        .into(imageView);
//
//                try {
//                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), mImageUri);
//                    bitmap = ImageDecoder.decodeBitmap(source);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        });

        if (checkForIntent()) {
            setInitialNoteProperties();
        } else {
            setNewNoteProperties();
        }

        mNoteTitleTv.addTextChangedListener(this);
        mNoteContentTv.addTextChangedListener(this);
        saveBtn.setOnClickListener(view -> {
            setEditedNoteProperties();
            saveNoteChanges();
        });

        setImage.setOnClickListener(v -> ImagePicker.with(NoteEditorActivity.this)
                .crop()
                .compress(10240)
                .maxResultSize(5000, 5000)
                .start());

        analyseImage.setOnClickListener(v -> analyzeImage());

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            requestCameraPermission();
        }

        noteEditorActivityViewModel.getAllImages(noteId).observe(this, this::updateImages);

    }

    private void createNoteFile(String fileName, String heading, String content) {
        try {
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ScanNote");
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    // Handle folder creation failure
                    Toast.makeText(this, "Failed to create folder.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            File file = new File(folder, fileName);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    // Handle file creation failure
                    Toast.makeText(this, "Failed to create file.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

            // Write note to file
            outputStreamWriter.write(heading + "\n\n");
            outputStreamWriter.write(content);

            // Close streams
            outputStreamWriter.close();
            fileOutputStream.close();
            Toast.makeText(this, fileName + " saved successfully to document/ScanNote", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any exceptions
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void deleteNote() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    if (!mIsNewNote) {
                        noteEditorActivityViewModel.deleteNote(mInitialNote);
                        this.finish();
                    }else {
                        Toast.makeText(this, "Can't delete a new note", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void updateImages(List<DBImage> dbImages) {
        for (DBImage dbImage : dbImages) {
            Glide
                .with(this)
                .load(new File(dbImage.getLocalPath()))
                .centerCrop()
                .into(imageView);
        }
    }

    // NOTE START
    private boolean checkForIntent() {
        if (getIntent().hasExtra(MainActivity.SELECTED_NOTE)) {
            mInitialNote = getIntent().getParcelableExtra(MainActivity.SELECTED_NOTE);
            mFinalNote = getIntent().getParcelableExtra(MainActivity.SELECTED_NOTE);
            mIsNewNote = false;
            noteId = mInitialNote.getId();
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
                this.noteId = mFinalNote.getId();
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

    // NOTE END


    // PERMISSION

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != CAMERA_PERMISSION_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
    }

    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, CAMERA_PERMISSION_CODE);
        }
    }

    // PERMISSION END

    public void requestImagePicker() {
        imagePickerActivityLauncher.launch("image/*");
    }


    //HUAWEI ML KIT
    public void analyzeImage() {
        if (bitmap == null) {
            Toast.makeText(this, "Couldn't analyze image. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        analyseImage.setVisibility(View.GONE);
        Context context = getApplicationContext();
        MLTextAnalyzer analyzer = new MLTextAnalyzer.Factory(context).setLocalOCRMode(MLLocalTextSetting.OCR_DETECT_MODE).setLanguage("zh").create();
        MLTextAnalyzer.Factory factory = new MLTextAnalyzer.Factory(context);
        factory.setLanguage("en");
        analyzer.setTransactor(new OcrDetectorProcessor());
        LensEngine lensEngine = new LensEngine.Creator(getApplicationContext(), analyzer).setLensType(LensEngine.BACK_LENS).applyDisplayDimension(1440, 1080).applyFps(30.0f).enableAutomaticFocus(true).create();
        try {
            analyzer.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (lensEngine != null) {
            lensEngine.release();
        }

        MLLocalTextSetting setting = new MLLocalTextSetting.Factory().setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE).setLanguage("en").create();
        MLTextAnalyzer analyzer1 = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(setting);

        MLFrame frame = MLFrame.fromBitmap(bitmap);
        Task<MLText> task = analyzer1.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(mlText -> {
            // Store initial data in the Note content editText
            String initialNoteContentTvText = mNoteContentTv.getText().toString();
            String textFromAnalyzedImage = mlText.getStringValue();
            String finalTextToShow = initialNoteContentTvText + " " + textFromAnalyzedImage;
            // update the editText
            mNoteContentTv.setText(finalTextToShow);
           // TODO: [Room] Store analyzed Image
            DBImage dbImage = new DBImage(noteId, mImageUri.getPath(),null);
            noteEditorActivityViewModel.saveImageToLocalDb(dbImage);
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Processing logic for recognition failure", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "testAnalyze: " + e.getMessage() );
            // Processing logic for recognition failure.
        });
    }

    public static class OcrDetectorProcessor implements MLAnalyzer.MLTransactor<MLText.Block> {

        @Override
        public void transactResult(MLAnalyzer.Result<MLText.Block> results) {
            SparseArray<MLText.Block> items = results.getAnalyseList();
            Log.d(TAG, "transactResult: " + items.toString());
            // Determine detection result processing as required. Note that only the detection results are processed.
            // Other detection-related APIs provided by ML Kit cannot be called.
        }

        @Override
        public void destroy() {
            Log.d(TAG, "destroy: OcrDetectorProcessor destroyed");
            // Callback method used to release resources when the detection ends.
        }
    }

    //HUAWEI ML KIT END



    // TEXT WATCHER
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
    // TEXT WATCHER END

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            saveNoteChanges();
            Uri uri = data.getData();
            mImageUri = uri;
            imageView.setImageURI(uri);
            if (uri != null) {
                try {
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), uri);
                    bitmap = ImageDecoder.decodeBitmap(source);
                    analyseImage.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
