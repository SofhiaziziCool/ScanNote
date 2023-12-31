package com.example.scannote.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "title_column")
    String title;

    @ColumnInfo(name = "contents_column")
    String contents;

    @ColumnInfo(name = "user_id_column")
    int userId;

    @ColumnInfo(name = "timeStamp_column")
    String timeStamp;

    @Ignore
    public Note() {

    }

    public Note(String title, String contents, int userId, String timeStamp) {
        this.title = title;
        this.contents = contents;
        this.userId = userId;
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
