package com.application.moodmeassessment.ui.adapters;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.application.moodmeassessment.entity.VideoPreview;

import java.util.List;

public class RecordingsDiffUtilCallback extends DiffUtil.Callback{

    private List<VideoPreview> oldList;
    private List<VideoPreview> newList;

    public RecordingsDiffUtilCallback(List<VideoPreview> recordingsList, List<VideoPreview> updatedRecordingsList) {
        oldList = recordingsList;
        newList = updatedRecordingsList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        if (oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId()){
            return true;
        } else if (oldList.get(oldItemPosition).getDuration() == newList.get(newItemPosition).getDuration()){
            return true;
        } else if (oldList.get(oldItemPosition).getTag().equals(newList.get(newItemPosition).getTag())){
            return true;
        } else return oldList.get(oldItemPosition).getBitmap().equals(newList.get(newItemPosition).getBitmap());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
