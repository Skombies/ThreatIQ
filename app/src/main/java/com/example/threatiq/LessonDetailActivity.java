package com.example.threatiq;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LessonDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout contentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Intent data
        String lessonTitle = getIntent().getStringExtra("LESSON_TITLE");
        String lessonId = getIntent().getStringExtra("LESSON_ID");
        int lessonImageResId = getIntent().getIntExtra("LESSON_IMAGE_RES_ID", 0);

        // Views
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        ImageView lessonDetailImage = findViewById(R.id.lesson_detail_image);
        contentContainer = findViewById(R.id.cont_container);

        collapsingToolbar.setTitle(lessonTitle);
        lessonDetailImage.setImageResource(lessonImageResId);

        db = FirebaseFirestore.getInstance();

        if (lessonId != null) {
            loadLessonContent(lessonId);
        } else {
            Toast.makeText(this, "Lesson ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadLessonContent(String lessonId) {
        db.collection("lessons").document(lessonId)
                .collection("content")
                .orderBy("order")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "No content available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        addScenarioToView(doc);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load content", Toast.LENGTH_SHORT).show());
    }

    private void addScenarioToView(DocumentSnapshot doc) {
        String title = doc.getString("title");
        String story = doc.getString("story");
        java.util.List<String> questions = (java.util.List<String>) doc.get("questions");
        java.util.List<String> answers = (java.util.List<String>) doc.get("answers");

        // Create a new TextView for each part
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(20f);
        titleView.setTextColor(getResources().getColor(android.R.color.black));
        titleView.setPadding(0, 24, 0, 8);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView storyView = new TextView(this);
        storyView.setText(story);
        storyView.setTextSize(16f);
        storyView.setTextColor(getResources().getColor(android.R.color.black));
        storyView.setPadding(0, 0, 0, 16);

        contentContainer.addView(titleView);
        contentContainer.addView(storyView);

        }
    }


