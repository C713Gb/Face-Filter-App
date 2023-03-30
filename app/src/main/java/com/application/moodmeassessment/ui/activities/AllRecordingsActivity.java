package com.application.moodmeassessment.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.application.moodmeassessment.R;
import com.application.moodmeassessment.ui.adapters.RecordingsRVAdapter;
import com.application.moodmeassessment.entity.VideoPreview;
import com.application.moodmeassessment.viewmodel.VideoPreviewViewModel;

import java.util.ArrayList;
import java.util.List;

public class AllRecordingsActivity extends AppCompatActivity {

    private RecyclerView recordingsRV;
    private Button newRecording;

    private Dialog dialog;

    private VideoPreviewViewModel viewModel;

    RecordingsRVAdapter adapter;

    public static final String TAG = "ABCD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_recordings);

        viewModel =new ViewModelProvider(this).get(VideoPreviewViewModel.class);

        recordingsRV = findViewById(R.id.recordingsRV);
        newRecording = findViewById(R.id.newRecording);

        newRecording.setOnClickListener(view -> {
            startActivity(new Intent(AllRecordingsActivity.this, MainActivity.class));
            finish();
        });

        List<VideoPreview> recordingsList = new ArrayList<>();
        adapter = new RecordingsRVAdapter(AllRecordingsActivity.this, recordingsList,  (item) -> {
            if (item != null){
                String path = item.getPath();

                Intent intent = new Intent(AllRecordingsActivity.this, VideoPlayerActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });
        recordingsRV.setAdapter(adapter);

        viewModel.allVideoPreviews.observe(this, recordings -> {
            if (!recordings.isEmpty()){
                adapter.setRecordingsList(recordings);
            }
        });

    }

    public void showTagDialog(VideoPreview videoPreview) {
        dialog = new Dialog(AllRecordingsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.tag_dialog);
        dialog.show();

        EditText tagText = dialog.findViewById(R.id.tagText);
        Button saveButton = dialog.findViewById(R.id.saveButton);

        final String[] tag = {videoPreview.getTag()};

        tagText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null){
                    String s = charSequence.toString().trim();
                    if (!s.equals("")){
                        tag[0] = s;
                    } else {
                        tag[0] = "";
                    }
                } else {
                    tag[0] = "";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        tagText.setText(videoPreview.getTag());

        saveButton.setOnClickListener(view -> {
            if (tag[0] != ""){
                dialog.dismiss();
                viewModel.update(tag[0], videoPreview.getId());
            }
        });
    }
}