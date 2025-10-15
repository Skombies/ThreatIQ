package com.example.threatiq;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;

public class LessonDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);

        // Setup the toolbar and back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get the data passed from MainActivity
        Intent intent = getIntent();
        String lessonTitle = intent.getStringExtra("LESSON_TITLE");
        int lessonImageResId = intent.getIntExtra("LESSON_IMAGE_RES_ID", 0);

        // Find views
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        ImageView lessonDetailImage = findViewById(R.id.lesson_detail_image);
        TextView lessonDetailContent = findViewById(R.id.lesson_detail_content);

        // Set the data
        collapsingToolbar.setTitle(lessonTitle);
        lessonDetailImage.setImageResource(lessonImageResId);

        // TODO: In a real app, you would load specific content based on the lessonTitle
        // For now, we'll just show the title in the content area as a placeholder.
        lessonDetailContent.setText("Detailed content about " + lessonTitle + " goes here.");
    }

    // Handle the back button press in the toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish(); // Closes the current activity and goes back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
