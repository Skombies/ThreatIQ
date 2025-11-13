package com.example.threatiq;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView profileName;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

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


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        profileName = findViewById(R.id.textView);

        FirebaseUser currentUser = mAuth.getCurrentUser();


        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        if (username != null && !username.isEmpty()) {
                            profileName.setText(username.toUpperCase());
                        } else {
                            profileName.setText("USER");
                        }
                    } else {
                        profileName.setText("USER");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
                    profileName.setText("USER");
                });


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
            malwareCard.setOnClickListener(v -> openLessonDetail("Malware Protection", R.drawable.advanced_persistent_threat));
        }
    }

    private void openLessonDetail(String title, int imageResId) {
        Intent intent = new Intent(this, LessonDetailActivity.class);
        intent.putExtra("LESSON_TITLE", title);
        intent.putExtra("LESSON_IMAGE_RES_ID", imageResId);

        // Map Firestore document names
        switch (title) {
            case "Password Security":
                intent.putExtra("LESSON_ID", "password-security");
                break;
            case "Phishing":
                intent.putExtra("LESSON_ID", "phishing");
                break;
            case "Social Engineering":
                intent.putExtra("LESSON_ID", "social-engineering");
                break;
            case "Malware Protection":
                intent.putExtra("LESSON_ID", "malware-protection");
                break;
        }

        startActivity(intent);
    }

}
