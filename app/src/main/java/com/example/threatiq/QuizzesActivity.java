package com.example.threatiq;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuizzesActivity extends AppCompatActivity {

    private RecyclerView quizzesRecyclerView;
    private QuizAdapter quizAdapter;
    private ArrayList<Quiz> quizList;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizzes);

        quizzesRecyclerView = findViewById(R.id.quizzes_recycler_view);
        quizzesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create dummy quiz data
        setupQuizData();

        // Set up the adapter
        quizAdapter = new QuizAdapter(this, quizList);
        quizzesRecyclerView.setAdapter(quizAdapter);

        // --- Handle the Bottom Navigation ---
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_quizzes);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(QuizzesActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_lessons) {
                startActivity(new Intent(QuizzesActivity.this, LessonsActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_quizzes) {
                return true; // Already here
            }
            else if (itemId == R.id.navigation_profile) {
                // Start ProfileActivity
                startActivity(new Intent(QuizzesActivity.this, ProfileActivity.class)); // Use 'this' qualified by the Activity name if needed
                finish(); // Close the current activity
            }
            return false;
        });
    }

    private void setupQuizData() {
        quizList = new ArrayList<>();

        // Quiz 1: Password Security
        List<Question> passwordQuestions = new ArrayList<>(Arrays.asList(
                new Question("What is the most important feature of a strong password?",
                        Arrays.asList("Length", "Using a pet's name", "Using your birthday", "A common word"), 0),
                new Question("What is '2FA'?",
                        Arrays.asList("Two-Factor Authentication", "Two-Friend Agreement", "Twice-Failed Access"), 1)
        ));
        quizList.add(new Quiz("Password Security Quiz", R.drawable.password_security, passwordQuestions));

        // Quiz 2: Phishing
        List<Question> phishingQuestions = new ArrayList<>(Arrays.asList(
                new Question("What should you do if you receive a suspicious email?",
                        Arrays.asList("Click the link to see", "Reply with personal info", "Delete it and report as spam"), 2)
        ));
        quizList.add(new Quiz("Phishing Quiz", R.drawable.phishing, phishingQuestions));

        // Add more quizzes here...
    }
}

