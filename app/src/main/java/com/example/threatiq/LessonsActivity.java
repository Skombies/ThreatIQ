package com.example.threatiq;

import android.content.Intent; // Import Intent
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView; // Import BottomNavigationView
import com.google.android.material.navigation.NavigationBarView; // Import NavigationBarView

import java.util.ArrayList;

public class LessonsActivity extends AppCompatActivity {

    private RecyclerView lessonsRecyclerView;
    private LessonAdapter lessonAdapter;
    private ArrayList<Lesson> lessonList;
    private BottomNavigationView bottomNavigationView; // Add this

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        // Remove the top action bar with the back button
        // if you want the UI to be cleaner with only the bottom bar.
        // ActionBar actionBar = getSupportActionBar();
        // if (actionBar != null) {
        //     actionBar.setDisplayHomeAsUpEnabled(true);
        // }

        lessonsRecyclerView = findViewById(R.id.lessons_recycler_view);
        lessonsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize and populate the list of lessons
        lessonList = new ArrayList<>();
        lessonList.add(new Lesson("Password Security", R.drawable.password_security));
        lessonList.add(new Lesson("Phishing", R.drawable.phishing));
        lessonList.add(new Lesson("Social Engineering", R.drawable.social_engineering));
        lessonList.add(new Lesson("Malware Protection", R.drawable.advanced_persistent_threat));
        // Add more lessons as needed

        // Set up the adapter
        lessonAdapter = new LessonAdapter(this, lessonList);
        lessonsRecyclerView.setAdapter(lessonAdapter);

        // --- Add this block to handle the Bottom Navigation ---
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_lessons); // Set "Lessons" as selected

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    // Go to Home Activity
                    startActivity(new Intent(LessonsActivity.this, MainActivity.class));
                    // Optional: finish current activity to prevent a stack of activities
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_lessons) {
                    // Already on the lessons screen
                    return true;
                } else if (itemId == R.id.navigation_quizzes) {
                    // Handle quizzes click (you'll create this activity later)
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    // Handle profile click (you'll create this activity later)
                    return true;
                }
                return false;
            }
        });

    }

}
