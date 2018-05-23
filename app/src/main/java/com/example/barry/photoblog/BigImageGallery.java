package com.example.barry.photoblog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BigImageGallery extends AppCompatActivity {

    private android.support.v7.widget.Toolbar bigImageToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image_gallery);

        bigImageToolbar = findViewById(R.id.big_image_toolbar);
        setSupportActionBar(bigImageToolbar);
        getSupportActionBar().setTitle("Post Big Image");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
