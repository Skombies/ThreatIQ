package com.example.threatiq;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView profileName;
    private AutoCompleteTextView searchAutoComplete;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // --- SETUP LESSON CARD CLICKS ---
        setupLessonCardClicks();
        // -------------------------------

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        profileName = findViewById(R.id.textView);
        searchAutoComplete = findViewById(R.id.search_autocomplete);

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

        // --- SEARCH BAR --- 
        setupSearch();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    return true;
                } else if (itemId == R.id.navigation_lessons) {
                    Intent intent = new Intent(MainActivity.this, LessonsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.navigation_quizzes) {
                    Intent intent = new Intent(MainActivity.this, QuizzesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    private void setupSearch() {
        List<String> lessonTitles = Arrays.asList("Password Security", "Phishing", "Social Engineering", "Malware Protection");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, lessonTitles);
        searchAutoComplete.setAdapter(adapter);

        searchAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTitle = (String) parent.getItemAtPosition(position);
            openLessonDetail(selectedTitle, getResourceIdForLessonTitle(selectedTitle));
        });
    }

    private void setupLessonCardClicks() {
        // "Lessons For You" cards
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

        // "Most Popular" cards
        View shortcutPasswordCard = findViewById(R.id.shortcut_password_card);
        View shortcutMalwareCard = findViewById(R.id.shortcut_malware_card);

        if (shortcutPasswordCard != null) {
            shortcutPasswordCard.setOnClickListener(v -> openLessonDetail("Password Security", R.drawable.password_security));
        }
        if (shortcutMalwareCard != null) {
            shortcutMalwareCard.setOnClickListener(v -> openLessonDetail("Malware Protection", R.drawable.malware));
        }
    }

    private int getResourceIdForLessonTitle(String title) {
        switch (title) {
            case "Password Security":
                return R.drawable.password_security;
            case "Phishing":
                return R.drawable.phishing;
            case "Social Engineering":
                return R.drawable.social_engineering;
            case "Malware Protection":
                return R.drawable.malware;
            default:
                return 0; // Or a default image
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
