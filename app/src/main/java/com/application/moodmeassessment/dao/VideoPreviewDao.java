package com.application.moodmeassessment.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.application.moodmeassessment.entity.VideoPreview;

import java.util.List;

@Dao
public interface VideoPreviewDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(VideoPreview videoPreview);

    @Query("DELETE FROM video_preview_table")
    void deleteAll();

    @Query("SELECT * FROM video_preview_table")
    LiveData<List<VideoPreview>> getAllVideoPreviews();

    @Query("UPDATE video_preview_table SET tag=:tag WHERE id = :id")
    void update(String tag, int id);

}
