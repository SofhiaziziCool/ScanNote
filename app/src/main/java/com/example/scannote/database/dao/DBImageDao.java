package com.example.scannote.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.scannote.database.entity.DBImage;
import com.example.scannote.database.entity.Note;

import java.util.List;

@Dao
public interface DBImageDao {
    //insert
    @Insert
    long insert(DBImage DBImage);

    //update
    @Update
    int update(DBImage... DBImage);

    //delete
    @Delete
    void delete(DBImage DBImage);

    //fetch
    @Query("SELECT * FROM DBImage_table WHERE note_id_column = :noteId AND note_id_column > 0")
    LiveData<List<DBImage>> getAllImagesByNoteId(int noteId);

}
