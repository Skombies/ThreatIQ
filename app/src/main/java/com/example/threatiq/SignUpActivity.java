package com.example.threatiq;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button; // <-- Add this import
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        TextView signInTab = findViewById(R.id.sign_in_tab);
        Button signUpButton = findViewById(R.id.sign_up_button); // <-- Find the button

        signInTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to LoginActivity
                finish(); // Closes this activity to go back
            }
        });

        // --- THIS IS THE MISSING PART ---
        // Set the OnClickListener for the sign-up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add your user registration logic here (e.g., save to Firebase)

                // For now, navigate to the main app screen after "signing up"
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                // Clear the activity stack so the user can't go back to the login/signup flow
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        // ------------------------------------
    }
}
