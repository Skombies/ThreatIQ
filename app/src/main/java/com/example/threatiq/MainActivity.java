package com.example.threatiq;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- SETUP LESSON CARD CLICKS ---
        setupLessonCardClicks();
        // -------------------------------

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    // Already on the home screen
                    return true;
                } else if (itemId == R.id.navigation_lessons) {
                    startActivity(new Intent(MainActivity.this, LessonsActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_quizzes) {
                    // --- FIX IS HERE ---
                    // Start QuizzesActivity
                    startActivity(new Intent(MainActivity.this, QuizzesActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    // Start ProfileActivity
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class)); // Use 'this' qualified by the Activity name if needed
                    finish(); // Close the current activity
                    return true;
                }
                return false;
            }
        });
    }

    private void setupLessonCardClicks() {
        // Find the cards using the correct IDs you added to the card layouts.
        // NOTE: The ID below might be a typo. It should probably be R.id.lesson_card_malware
        View passwordCard = findViewById(R.id.lesson_card_password);
        View phishingCard = findViewById(R.id.lesson_card_phishing);
        View socialCard = findViewById(R.id.lesson_card_social);
        View malwareCard = findViewById(R.id.lesson_card_advanced);

        if (passwordCard != null) {
            passwordCard.setOnClickListener(v -> openLessonDetail("Password Security", R.drawable.password_security));
        }
        if (phishingCard != null) {
            phishingCard.setOnClickListener(v -> openLessonDetail("Phishing", R.drawable.phishing));
        }
        if (socialCard != null) {
            socialCard.setOnClickListener(v -> openLessonDetail("Social Engineering", R.drawable.social_engineering));
        }
        if (malwareCard != null) {
            malwareCard.setOnClickListener(v -> openLessonDetail("Malware Protection", R.drawable.malware));
        }
    }

    private void openLessonDetail(String title, int imageResId) {
        Intent intent = new Intent(this, LessonDetailActivity.class);
        intent.putExtra("LESSON_TITLE", title);
        intent.putExtra("LESSON_IMAGE_RES_ID", imageResId);
        startActivity(intent);
    }
}
