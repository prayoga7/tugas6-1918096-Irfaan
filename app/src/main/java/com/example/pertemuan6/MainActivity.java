package com.example.pertemuan6;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "Upload ###";

    private static final int IMAGE_REQ = 1;
    private Uri imagePath;

    Button button;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initConfig();

        imageView = findViewById(R.id.imageView);
        Picasso.get().load("https://www.globalpharmatek.com/wp-content/uploads/2016/10/orionthemes-placeholder-image.jpg").into(imageView);
        button = findViewById(R.id.button);

        imageView.setOnClickListener(view -> requestPermissions());

        button.setOnClickListener(v -> {
            Log.d(TAG,"test");

            MediaManager.get().upload(imagePath).callback(new UploadCallback() {
                @Override
                public void onStart(String requestId) {
                    Log.d(TAG, "onStart:" + " started");
                }

                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {
                    Log.d(TAG, "onProgress:" + " uploading");

                }

                @Override
                public void onSuccess(String requestId, Map resultData) {
                    Log.d(TAG, "onSuccess:" + " success");
                    final OneTimeWorkRequest request = new
                            OneTimeWorkRequest.Builder(com.example.pertemuan6.MyWorker.class).build();
                    WorkManager.getInstance().enqueueUniqueWork(
                            "Notifikasi",
                            ExistingWorkPolicy.REPLACE,
                            request);
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {
                    Log.d(TAG, "onError:" + " error upload");
                }

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {
                    Log.d(TAG, "onReschedule:" + " reschedule upload");
                }
            }).dispatch();
        });


    }
    private void initConfig() {
        Map config = new HashMap();
        config.put("cloud_name", "dvunscppx");
        config.put("api_key", "712911362233377");
        config.put("api_secret", "ddOisezgj4O6i0-R-CmYiavOO6w");
        MediaManager.init(this, config);
    }
    private void requestPermissions() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            selectImage();
        }else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, IMAGE_REQ);
        }
    }
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQ);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);

        if(requestCode==IMAGE_REQ && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
            imagePath = data.getData();
            Picasso.get().load(imagePath).into(imageView);
        }
    }

}