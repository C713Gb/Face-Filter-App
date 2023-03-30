package com.application.moodmeassessment.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.application.moodmeassessment.entity.VideoPreview;
import com.application.moodmeassessment.repository.VideoPreviewRepository;

import java.util.List;

public class VideoPreviewViewModel extends AndroidViewModel {

    private VideoPreviewRepository repository;

    public final LiveData<List<VideoPreview>> allVideoPreviews;

    public VideoPreviewViewModel(Application application){
        super(application);
        repository = new VideoPreviewRepository(application);
        allVideoPreviews = repository.getAllVideoPreviews();
    }

    LiveData<List<VideoPreview>> getAllVideoPreviews() { return allVideoPreviews; }

    public void insert(VideoPreview videoPreview) { repository.insert(videoPreview); }

    public void update(String tag, int id) { repository.update(tag, id); }
}
