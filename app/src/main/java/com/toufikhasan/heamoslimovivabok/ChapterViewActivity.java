package com.toufikhasan.heamoslimovivabok;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ChapterViewActivity extends AppCompatActivity {
    private WebView chapterView;

    private String chapterTitle;
    private int chapterId;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_view);

        Intent myIntent = getIntent();
        chapterTitle = myIntent.getStringExtra("cp_title");
        chapterId = myIntent.getIntExtra("cp_id", 0);

        Objects.requireNonNull(getSupportActionBar()).setTitle(chapterTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String url = "chapter/" + chapterId + ".html";

        chapterView = findViewById(R.id.webViewChapter);
        chapterView.setBackgroundColor(Color.TRANSPARENT);
        chapterView.setOnLongClickListener(v -> false);
        chapterView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Long press detected, prevent screenshot
                return event.getEventTime() - event.getDownTime() > ViewConfiguration.getLongPressTimeout();
            }
            return false;
        });
        chapterView.getSettings().setBuiltInZoomControls(false);


        AssetManager assetManager = this.getAssets();
        String html;
        try {
            InputStream inputStream = assetManager.open(url);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            html = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            html = "<html><body><h4 style='color:red'>Error loading chapter file!</h1></body></html>";
        }

        String htmlContent = "<html><head><link rel='stylesheet' type='text/css' href='file:///android_asset/css/style.css'></head><body><img class='header_img' src='file:///android_asset/images/"+chapterId+".jpg'><h1 style='text-align:centre;'>" + chapterTitle + "</h1>" + html + "<br/></body></html>";

        String baseUrl = "file:///android_asset/";
        chapterView.loadDataWithBaseURL(baseUrl, htmlContent, "text/html", "UTF-8", null);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}