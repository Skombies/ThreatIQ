//---Developer - David J---
package com.example.threatiq;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private EditText usernameInput, emailInput, passwordInput;
    private Button signUpButton;
    private Button signInTab;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // --- Firebase instances ---
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // --- UI Elements ---
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        signUpButton = findViewById(R.id.sign_up_button);
        signInTab = findViewById(R.id.sign_in_tab);

        // --- Switch to Login ---
        signInTab.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // --- Sign Up Button ---
        signUpButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(SignupActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create new user in Firebase Auth
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                // --- Save username & email to Firestore ---
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("username", username);
                                userData.put("email", email);
                                userData.put("uid", user.getUid());

                                db.collection("users").document(user.getUid())
                                        .set(userData)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(SignupActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                                            // Go to main or profile activity
                                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(SignupActivity.this, "Failed to save user info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }

                        } else {
                            Toast.makeText(SignupActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}

//---Developer - David J---
