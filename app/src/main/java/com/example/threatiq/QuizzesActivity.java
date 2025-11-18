package com.example.threatiq;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("quizzes").get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query) {
                        String title = doc.getString("title");
                        String image = doc.getString("image");
                        String quizId = doc.getId();

                        int imageRes = getResources().getIdentifier(image, "drawable", getPackageName());

                        quizList.add(new Quiz(title, imageRes, quizId));
                    }
                    quizAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load quizzes", Toast.LENGTH_SHORT).show()
                );
    }
}

