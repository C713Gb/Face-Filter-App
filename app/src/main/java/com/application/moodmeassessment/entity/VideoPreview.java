package com.application.moodmeassessment.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "video_preview_table")
public class VideoPreview {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String bitmap;

    @NonNull
    private long duration;

    @NonNull
    private String tag;

    @NonNull
    private String path;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(@NonNull String bitmap) {
        this.bitmap = bitmap;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @NonNull
    public String getTag() {
        return tag;
    }

    public void setTag(@NonNull String tag) {
        this.tag = tag;
    }

    @NonNull
    public String getPath() {
        return path;
    }

    public void setPath(@NonNull String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "VideoPreview{" +
                "id=" + id +
                ", duration=" + duration +
                ", tag='" + tag + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
