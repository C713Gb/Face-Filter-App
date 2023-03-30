package com.application.moodmeassessment.ui.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.application.moodmeassessment.R;
import com.application.moodmeassessment.entity.VideoPreview;
import com.application.moodmeassessment.ui.CustomArFragment;
import com.application.moodmeassessment.ui.adapters.ImagesRVAdapter;
import com.application.moodmeassessment.utils.ImageBitmapString;
import com.application.moodmeassessment.utils.VideoRecorder;
import com.application.moodmeassessment.viewmodel.VideoPreviewViewModel;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.AugmentedFace;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.AugmentedFaceNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "ABCD";

    private final HashMap<AugmentedFace, AugmentedFaceNode> facesNodes = new HashMap<>();

    ArrayList<String> permissionsList;
    ActivityResultLauncher<String[]> permissionsLauncher;
    int permissionsCount = 0;
    String[] permissionsStr = {android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private Button recordButton;
    private Integer image = null;

    Node mustache;

    private VideoRecorder videoRecorder;
    private MediaMetadataRetriever retriever;

    private Dialog dialog;

    private VideoPreviewViewModel viewModel;

    private String tag = "";
    private long timeInMilliSec = 0;
    private String bitmap;
    private String filePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(VideoPreviewViewModel.class);

        permissionsLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                        result -> {
                            ArrayList<Boolean> list = new ArrayList<>(result.values());
                            permissionsList = new ArrayList<>();
                            permissionsCount = 0;
                            for (int i = 0; i < list.size(); i++) {
                                if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                                    permissionsList.add(permissionsStr[i]);
                                } else if (!hasPermission(this, permissionsStr[i])) {
                                    permissionsCount++;
                                }
                            }
                            if (permissionsList.size() > 0) {
                                //Some permissions are denied and can be asked again.
                                askForPermissions(permissionsList);
                            } else if (permissionsCount > 0) {
                                //Show alert dialog
//                                showPermissionDialog();
                            } else {
                                //All permissions granted. Do your stuff 🤞
                                askForCameraPermission();
                            }
                        });

        permissionsList = new ArrayList<>();
        permissionsList.addAll(Arrays.asList(permissionsStr));
        askForPermissions(permissionsList);

        CustomArFragment arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        recordButton = findViewById(R.id.recordButton);
        Button allRecordingsButton = findViewById(R.id.allRecordingsButton);
        RecyclerView imagesRV = findViewById(R.id.imagesRV);

        if (savedInstanceState == null) {
            ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
            if (!availability.isSupported()) {
                return;
            }
        }

        ArSceneView sceneView = arFragment.getArSceneView();

        // This is important to make sure that the camera stream renders first so that
        // the face mesh occlusion works correctly.
        sceneView.setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);

        Scene scene = sceneView.getScene();

        scene.addOnUpdateListener(frameTime -> {
            Collection<AugmentedFace> faceList = sceneView.getSession().getAllTrackables(AugmentedFace.class);
            // Make new AugmentedFaceNodes for any new faces.
            for (AugmentedFace face : faceList) {
                if (!facesNodes.containsKey(face)) {
                    AugmentedFaceNode faceNode = new AugmentedFaceNode(face);
                    faceNode.setParent(scene);

                    mustache = new Node();
                    Vector3 localPosition = new Vector3();
                    Vector3 localSize = new Vector3();
                    localPosition.set(0.0f, -0.07f, 0.08f);
                    localSize.set(0.2f, 0.2f, 1f);
                    mustache.setLocalPosition(localPosition);
                    mustache.setLocalScale(localSize);
                    mustache.setParent(faceNode);

                    loadImage();

                    facesNodes.put(face, faceNode);
                }
            }

            // Remove any AugmentedFaceNodes associated with an AugmentedFace that stopped tracking.
            Iterator<Map.Entry<AugmentedFace, AugmentedFaceNode>> faceIterator = facesNodes.entrySet().iterator();
            while (faceIterator.hasNext()) {
                Map.Entry<AugmentedFace, AugmentedFaceNode> entry = faceIterator.next();
                AugmentedFace face = entry.getKey();
                if (face.getTrackingState() == TrackingState.STOPPED) {
                    AugmentedFaceNode faceNode = entry.getValue();
                    faceNode.setParent(null);
                    faceNode.getChildren().clear();
                    faceIterator.remove();
                }
            }
        });

        recordButton.setOnClickListener(view -> {
            if (videoRecorder == null) {
                videoRecorder = new VideoRecorder();
                videoRecorder.setSceneView(sceneView);

                int orientation = getResources().getConfiguration().orientation;

                videoRecorder.setVideoQuality(CamcorderProfile.QUALITY_HIGH, orientation);
            }

            boolean isRecording = videoRecorder.onToggleRecord();

            if (isRecording) {
                recordButton.setText("Stop Recording");
            } else {
                recordButton.setText("Start Recording");

                String videoFile = videoRecorder.getVideoPath().toString();
                Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoFile,
                        MediaStore.Images.Thumbnails.MINI_KIND);

                bitmap = ImageBitmapString.BitMapToString(thumbnail);

                retriever = new MediaMetadataRetriever();
                retriever.setDataSource(this, Uri.fromFile(videoRecorder.getVideoPath()));
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                timeInMilliSec = Long.parseLong(time);

                filePath = videoRecorder.getVideoPath().toString();

                try {
                    retriever.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                showTagDialog();

            }
        });


        ImagesRVAdapter adapter = new ImagesRVAdapter(this, fetchImagesList(), (image) -> {
            this.image = image;
            loadImage();
        });
        imagesRV.setAdapter(adapter);

        allRecordingsButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, AllRecordingsActivity.class));
            finish();
        });

    }

    private void askForCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, 1);
        }
    }

    private List<Integer> fetchImagesList() {
        List<Integer> imagesList = new ArrayList<>();

        imagesList.add(R.drawable.mus1);
        imagesList.add(R.drawable.mus2);
        imagesList.add(R.drawable.mus3);
        imagesList.add(R.drawable.mus4);
        imagesList.add(R.drawable.mus5);
        imagesList.add(R.drawable.mus6);
        imagesList.add(R.drawable.mus7);

        return imagesList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsList = new ArrayList<>();
        permissionsList.addAll(Arrays.asList(permissionsStr));
        askForPermissions(permissionsList);
    }

    private void showTagDialog() {
        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.tag_dialog);
        dialog.show();

        EditText tagText = dialog.findViewById(R.id.tagText);
        Button saveButton = dialog.findViewById(R.id.saveButton);

        tagText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null) {
                    String s = charSequence.toString().trim();
                    if (!s.equals("")) {
                        tag = s;
                    } else {
                        tag = "";
                    }
                } else {
                    tag = "";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        saveButton.setOnClickListener(view -> {
            if (tag != "") {
                dialog.dismiss();

                VideoPreview model = new VideoPreview();
                model.setTag(tag);
                model.setBitmap(bitmap);
                model.setDuration(timeInMilliSec);
                model.setPath(filePath);

                viewModel.insert(model);

            }
        });
    }

    private void loadImage() {

        LayoutInflater inflater = (LayoutInflater) getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.mustache_view, null);
        ImageView mustacheImage = (ImageView) frameLayout.findViewById(R.id.mustache_image);

        if (image != null)
            mustacheImage.setImageResource(image);
        else
            return;

        ViewRenderable.builder().setView(this, frameLayout).build()
                .thenAccept(model -> {
                    mustache.setRenderable(model);
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    Toast.makeText(this, "Unable to load renderable", Toast.LENGTH_LONG).show();
                    return null;
                });
    }

    private boolean hasPermission(Context context, String permissionStr) {
        return ContextCompat.checkSelfPermission(context, permissionStr) == PackageManager.PERMISSION_GRANTED;
    }

    private void askForPermissions(ArrayList<String> permissionsList) {
        String[] newPermissionStr = new String[permissionsList.size()];
        for (int i = 0; i < newPermissionStr.length; i++) {
            newPermissionStr[i] = permissionsList.get(i);
        }
        if (newPermissionStr.length > 0) {
            permissionsLauncher.launch(newPermissionStr);
        } else {
        /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
        which will lead them to app details page to enable permissions from there. */
//            showPermissionDialog();
        }
    }

}