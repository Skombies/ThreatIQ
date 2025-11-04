
package com.example.threatiq;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private MaterialCardView logoutButton, editProfileButton;
    private ShapeableImageView profileImage;
    private TextView profileName, profileEmail;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // --- Firebase instances ---
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // --- UI references ---
        profileImage = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);
        editProfileButton = findViewById(R.id.edit_profile_button);
        logoutButton = findViewById(R.id.logout_button);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Redirect to login if no user
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Display email
        profileEmail.setText(currentUser.getEmail());

        // Fetch username from Firestore
        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        profileName.setText(username != null ? username : "User");
                    } else {
                        profileName.setText("User");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
                    profileName.setText("User");
                });

        // Edit Profile button
        editProfileButton.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Edit Profile coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Logout button
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_lessons) {
                    startActivity(new Intent(ProfileActivity.this, LessonsActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_quizzes) {
                    startActivity(new Intent(ProfileActivity.this, QuizzesActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    return true;
                }
                return false;
            }
        });
    }
}

//---Developer - David J---