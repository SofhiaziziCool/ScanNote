package com.example.scannote.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.scannote.database.entity.Note;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface NotesDao {
    //insert
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Note note);

    //update
    @Update
    void update(Note note);

    //delete
    @Query("DELETE FROM note_table WHERE id = :id")
    void delete(int id);

    //fetch
    @Query("SELECT * FROM note_table")
    LiveData<List<Note>> getAllNotes();

}
