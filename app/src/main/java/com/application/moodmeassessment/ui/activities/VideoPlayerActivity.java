package com.application.moodmeassessment.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.application.moodmeassessment.R;

public class VideoPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        VideoView videoView = findViewById(R.id.videoView);
        ImageView back = findViewById(R.id.back);

        back.setOnClickListener(view -> {
            onBackPressed();
        });

        if (getIntent() != null){
            if (getIntent().getExtras() != null){
                String path = getIntent().getStringExtra("path");

                Uri videoUri = Uri.parse(path);

                videoView.setVideoURI(videoUri);
                videoView.start();

                MediaController mediaController =  new MediaController(this);
                videoView.setMediaController(mediaController);
                mediaController.setAnchorView(videoView);
            }
        }
    }
}