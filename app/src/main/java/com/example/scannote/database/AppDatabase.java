package com.example.scannote.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.scannote.database.dao.DBImageDao;
import com.example.scannote.database.dao.NotesDao;
import com.example.scannote.database.dao.UsersDao;
import com.example.scannote.database.entity.DBImage;
import com.example.scannote.database.entity.Note;
import com.example.scannote.database.entity.User;

@Database(entities = {Note.class, User.class, DBImage.class }, version = 3, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = "AppDatabase";
    private static AppDatabase instance;

    public static AppDatabase getDatabase(Context context) {
        Log.d(TAG, "getDatabase: Database is running");
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "app_database").fallbackToDestructiveMigration().build();
        }
        return instance;
    }

    public abstract UsersDao usersDao();
    public abstract NotesDao notesDao();
    public abstract DBImageDao dbImageDao();
}
