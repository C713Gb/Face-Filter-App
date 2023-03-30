package com.application.moodmeassessment.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.application.moodmeassessment.R;
import com.application.moodmeassessment.entity.VideoPreview;
import com.application.moodmeassessment.ui.activities.AllRecordingsActivity;
import com.application.moodmeassessment.utils.ImageBitmapString;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecordingsRVAdapter extends RecyclerView.Adapter<RecordingsRVAdapter.ViewHolder> {

    Context context;
    List<VideoPreview> recordingsList;
    AllRecordingsActivity activity;

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(VideoPreview item);
    }

    public RecordingsRVAdapter(Context context, List<VideoPreview> recordingsList, OnItemClickListener listener) {
        this.context = context;
        this.recordingsList = recordingsList;
        this.listener = listener;
    }

    public void setRecordingsList(List<VideoPreview> updatedRecordingsList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new RecordingsDiffUtilCallback(recordingsList, updatedRecordingsList));
        recordingsList.clear();
        recordingsList.addAll(updatedRecordingsList);
        diffResult.dispatchUpdatesTo(this);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecordingsRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recording_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordingsRVAdapter.ViewHolder holder, int position) {
        activity = (AllRecordingsActivity) context;
        holder.bind(recordingsList.get(position));

        holder.editTagButton.setOnClickListener(view -> {
            activity.showTagDialog(recordingsList.get(position));
        });

        holder.itemView.setOnClickListener(view -> {
            listener.onItemClick(recordingsList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return recordingsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public VideoView videoView;
        public TextView tagText;
        public TextView durationText;
        public Button editTagButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            tagText = itemView.findViewById(R.id.tagText);
            durationText = itemView.findViewById(R.id.durationText);
            editTagButton = itemView.findViewById(R.id.editTagButton);
        }

        public void bind(VideoPreview videoPreview) {

            try {

                Bitmap thumbnail = ImageBitmapString.StringToBitMap(videoPreview.getBitmap());
                BitmapDrawable bitmapDrawable = new BitmapDrawable(thumbnail);
                videoView.setBackground(bitmapDrawable);

                tagText.setText("Tag\n"+videoPreview.getTag());

                long minutes = TimeUnit.MILLISECONDS.toMinutes(videoPreview.getDuration());
                long seconds = TimeUnit.MILLISECONDS.toSeconds(videoPreview.getDuration());
                String minStr = minutes<10 ? "0"+minutes : String.valueOf(minutes);
                String secStr = seconds<10 ? "0"+seconds : String.valueOf(seconds);
                String duration = minStr + ":" + secStr;

                durationText.setText("Duration\n"+duration);

            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
