package com.example.dass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class RearCameraActivity extends AppCompatActivity {

    private WebView displayScreen;
    private Button reverseButton;

    private static final String STREAM_URL = "http://192.168.43.77:5000/video_feed "; // Replace with your stream URL
    private static final String TAG = "StreamingActivity";

    // Firebase storage reference


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rear_camera);



        // Initialize views
        displayScreen = findViewById(R.id.displayScreen);

        reverseButton = findViewById(R.id.reverseButton);

        // Setup the WebView for live streaming
        setupWebView();



        // Reverse button click listener
        reverseButton.setOnClickListener(v -> {
            Intent intent = new Intent(RearCameraActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        displayScreen.setWebViewClient(new WebViewClient());
        WebSettings webSettings = displayScreen.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        displayScreen.loadUrl(STREAM_URL);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (displayScreen != null) {
            displayScreen.destroy();
        }
    }
}
