package com.example.threatiq;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class LessonsActivity extends AppCompatActivity {

    private RecyclerView lessonsRecyclerView;
    private LessonAdapter lessonAdapter;
    private ArrayList<Lesson> lessonList;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        lessonsRecyclerView = findViewById(R.id.lessons_recycler_view);

        // --- THIS IS THE KEY CHANGE ---
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        lessonsRecyclerView.setLayoutManager(gridLayoutManager);

        // List of lessons
        lessonList = new ArrayList<>();
        lessonList.add(new Lesson("Password Security", R.drawable.password_security));
        lessonList.add(new Lesson("Phishing", R.drawable.phishing));
        lessonList.add(new Lesson("Social Engineering", R.drawable.social_engineering));
        lessonList.add(new Lesson("Malware Protection", R.drawable.malware));

        // Set up the adapter
        lessonAdapter = new LessonAdapter(this, lessonList);
        lessonsRecyclerView.setAdapter(lessonAdapter);

        // Handles for the Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_lessons);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    Intent intent = new Intent(LessonsActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.navigation_lessons) {
                    return true;
                } else if (itemId == R.id.navigation_quizzes) {
                    Intent intent = new Intent(LessonsActivity.this, QuizzesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    Intent intent = new Intent(LessonsActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
}
