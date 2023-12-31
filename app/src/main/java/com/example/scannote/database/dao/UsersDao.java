package com.example.scannote.database.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.scannote.database.entity.Note;
import com.example.scannote.database.entity.User;

import java.util.ArrayList;

@Dao
public interface UsersDao {
    //insert
    @Insert
    void insert(User user);

    //update
    @Update
    void update(User user);

    //delete
    @Query("DELETE FROM user_table WHERE id = :id")
    void delete(int id);

    //fetch
    @Query("SELECT * FROM user_table WHERE email_column = :email AND password_column = :password")
    LiveData<User> getUserByEmailAndPassword(String email, String password);
}
