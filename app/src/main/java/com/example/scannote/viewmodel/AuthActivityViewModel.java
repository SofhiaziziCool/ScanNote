package com.example.scannote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.scannote.database.entity.User;
import com.example.scannote.repository.UsersRepository;

import java.util.ArrayList;

public class AuthActivityViewModel extends AndroidViewModel {

    UsersRepository usersRepository;

    public AuthActivityViewModel(@NonNull Application application) {
        super(application);
        usersRepository = new UsersRepository(application.getApplicationContext());
    }

    public void createNewUser(User user) {
        usersRepository.insertUser(user);
    }

    public LiveData<User> getUserByEmailAndPassword(String email, String password) {
       return usersRepository.getUserByEmailAndPassword(email, password);
    }

}
