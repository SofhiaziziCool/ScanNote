package com.example.scannote.database.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "title_column")
    String title;

    @ColumnInfo(name = "contents_column")
    String contents;

    @ColumnInfo(name = "timeStamp_column")
    String timeStamp;

    @Ignore
    public Note() {
    }

    @Ignore
    public Note(Note note){
        id = note.getId();
        title = note.getTitle();
        contents = note.getContents();
        timeStamp = note.getTimeStamp();
    }

    public Note(String title, String contents, String timeStamp) {
        this.title = title;
        this.contents = contents;
        this.timeStamp = timeStamp;
    }

    protected Note(Parcel in) {
        id = in.readInt();
        title = in.readString();
        contents = in.readString();
        timeStamp = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

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

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(contents);
        parcel.writeString(timeStamp);
    }

    @NonNull
    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}
