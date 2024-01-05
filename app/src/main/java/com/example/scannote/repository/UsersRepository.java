package com.example.scannote.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.scannote.database.AppDatabase;
import com.example.scannote.database.dao.UsersDao;
import com.example.scannote.database.entity.User;

import java.util.ArrayList;

public class UsersRepository {
    private final UsersDao usersDao;
    private static final String TAG = "UsersRepository";
    public UsersRepository(Context context) {
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        usersDao = appDatabase.usersDao();
    }

    public void insertUser(User user) {
        Log.d(TAG, "insertUser: " + user.getEmail());
        new Thread(() -> usersDao.insert(user)).start();
    }

    public LiveData<User> getUserByEmailAndPassword(String email, String password) {
        return usersDao.getUserByEmailAndPassword(email, password);
    }

}
