package com.application.moodmeassessment.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.application.moodmeassessment.dao.VideoPreviewDao;
import com.application.moodmeassessment.db.VideoPreviewRoomDatabase;
import com.application.moodmeassessment.entity.VideoPreview;

import java.util.List;

public class VideoPreviewRepository {

    private VideoPreviewDao videoPreviewDao;
    private LiveData<List<VideoPreview>> mAllVideoPreviews;

    public VideoPreviewRepository(Application application) {
        VideoPreviewRoomDatabase db = VideoPreviewRoomDatabase.getDatabase(application);
        videoPreviewDao = db.videoPreviewDao();
        mAllVideoPreviews = videoPreviewDao.getAllVideoPreviews();
    }

    public LiveData<List<VideoPreview>> getAllVideoPreviews() {
        return mAllVideoPreviews;
    }

    public void insert(VideoPreview videoPreview) {
        VideoPreviewRoomDatabase.databaseWriteExecutor.execute(() -> {
            videoPreviewDao.insert(videoPreview);
        });
    }

    public void update(String tag, int id) {
        VideoPreviewRoomDatabase.databaseWriteExecutor.execute(() -> {
            videoPreviewDao.update(tag, id);
        });
    }
}
