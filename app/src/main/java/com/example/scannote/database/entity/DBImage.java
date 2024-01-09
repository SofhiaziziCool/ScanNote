package com.example.scannote.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "DBImage_table")
public class DBImage {
    @PrimaryKey(autoGenerate = true)
    int id;
    @ColumnInfo(name = "note_id_column")
    int noteId;
    @ColumnInfo(name = "local_path_column")
    String localPath;
    @ColumnInfo(name = "remote_path_column")
    String remotePath;

    @Ignore
    public DBImage() {

    }

    public DBImage(int noteId, String localPath, String remotePath) {
        this.noteId = noteId;
        this.localPath = localPath;
        this.remotePath = remotePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }
}
