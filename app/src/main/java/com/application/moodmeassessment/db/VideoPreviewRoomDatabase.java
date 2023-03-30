package com.application.moodmeassessment.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.application.moodmeassessment.dao.VideoPreviewDao;
import com.application.moodmeassessment.entity.VideoPreview;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {VideoPreview.class}, version = 1, exportSchema = false)
public abstract  class VideoPreviewRoomDatabase extends RoomDatabase {

    public abstract VideoPreviewDao videoPreviewDao();

    private static volatile VideoPreviewRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static VideoPreviewRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (VideoPreviewRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    VideoPreviewRoomDatabase.class, "video_preview_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
