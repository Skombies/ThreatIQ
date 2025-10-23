package com.example.threatiq;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.threatiq.SignUpActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // --- START OF FIX ---

        // Find the "Sign Up" link using the CORRECT ID from your activity_login.xml.
        TextView signupLink = findViewById(R.id.sign_up_link);

        // Find the "Sign In" button from your activity_login.xml.
        Button signInButton = findViewById(R.id.sign_in_button);

        // --- END OF FIX ---

        // Set a listener for the "Sign Up" link.
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // The class name must be EXACTLY "SignUpActivity" (case-sensitive).
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        // Set a listener for the "Sign In" button.
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add your login logic here (e.g., Firebase Auth)

                // For now, let's navigate to the main app screen
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close the login activity so the user can't go back to it
            }
        });
    }
}
