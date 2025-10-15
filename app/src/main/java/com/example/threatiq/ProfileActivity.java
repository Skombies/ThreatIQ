package com.example.threatiq;

import android.content.Intent;
import android.os.Bundle;import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationBarView;

public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private MaterialCardView logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logoutButton = findViewById(R.id.logout_button);

        // --- Handle Logout Button Click ---
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add proper logout logic here (e.g., clear session, sign out from Firebase)

                // For now, show a toast and navigate to the login screen
                Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                // Clear the activity stack so the user can't go back to the app
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // --- Handle the Bottom Navigation ---
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile); // Set "Profile" as selected

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
                    // Already here
                    return true;
                }
                return false;
            }
        });
    }
}
