package com.example.scannote.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.scannote.database.entity.Note;

import java.util.List;

@Dao
public interface NotesDao {
    //insert
    @Insert
    long insert(Note note);

    //update
    @Update
    int update(Note... note);

    //delete
    @Delete
    void delete(Note note);

    //fetch
    @Query("SELECT * FROM note_table")
    LiveData<List<Note>> getAllNotes();

}
