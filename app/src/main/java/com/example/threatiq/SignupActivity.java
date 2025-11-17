package com.example.threatiq;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    // UI elements
    private EditText etEmail, etPassword;
    private Button btnSignUp;
    private TextView tvLoginRedirect;
    private ProgressBar progressBar;

    // Firebase Authentication instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup); // Make sure you have an activity_sign_up.xml layout file

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Bind UI elements from the layout
        etEmail = findViewById(R.id.et_email); // Replace with your EditText ID for email
        etPassword = findViewById(R.id.et_password); // Replace with your EditText ID for password
        btnSignUp = findViewById(R.id.btn_sign_up); // Replace with your Button ID
        tvLoginRedirect = findViewById(R.id.tv_login_redirect); // Replace with your TextView ID for login redirection
        progressBar = findViewById(R.id.progress_bar); // Replace with your ProgressBar ID

        // Set up the listener for the Sign Up button
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });

        // Set up the listener for the "Already have an account? Login" text
        tvLoginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to navigate to LoginActivity
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class); // Ensure you have a LoginActivity
                startActivity(intent);
                finish(); // Optional: finish this activity so the user can't go back
            }
        });
    }

    private void registerNewUser() {
        String email, password;
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required.");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required.");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters long.");
            etPassword.requestFocus();
            return;
        }

        // Show the progress bar and disable the button
        progressBar.setVisibility(View.VISIBLE);
        btnSignUp.setEnabled(false);

        // Create a new user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Hide the progress bar and re-enable the button
                        progressBar.setVisibility(View.GONE);
                        btnSignUp.setEnabled(true);

                        if (task.isSuccessful()) {
                            // Sign up success
                            Toast.makeText(SignupActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                            // You can add user data to Firestore here if needed

                            // Navigate to the Login activity or Main activity
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish(); // Finish current activity
                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(SignupActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
