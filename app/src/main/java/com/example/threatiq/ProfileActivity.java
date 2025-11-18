package com.example.threatiq;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private MaterialCardView logoutButton, editProfileButton;
    private ShapeableImageView profileImage;
    private TextView profileName, profileEmail;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final int PICK_IMAGE_REQUEST = 100;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI references
        profileImage = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);
        editProfileButton = findViewById(R.id.edit_profile_button);
        logoutButton = findViewById(R.id.logout_button);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Check user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        profileEmail.setText(currentUser.getEmail());

        // Load profile data
        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> loadUserProfile(documentSnapshot))
                .addOnFailureListener(e ->
                        Toast.makeText(ProfileActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show()
                );

        // Click to change picture
        profileImage.setOnClickListener(v -> openImageChooser());

        // Edit Profile button (you can expand later)
        editProfileButton.setOnClickListener(v ->
                Toast.makeText(ProfileActivity.this, "Edit Profile coming soon!", Toast.LENGTH_SHORT).show()
        );

        // Logout
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Navigation bar
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_lessons) {
                Intent intent = new Intent(ProfileActivity.this, LessonsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_quizzes) {
                Intent intent = new Intent(ProfileActivity.this, QuizzesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                return true;
            }
            return false;
        });
    }

    private void loadUserProfile(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
            // Load username
            String username = documentSnapshot.getString("username");
            profileName.setText(username != null ? username : "User");

            // Load profile image
            String imageUrl = documentSnapshot.getString("profileImage");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(ProfileActivity.this)
                        .load(imageUrl)
                        .into(profileImage);
            }
        } else {
            profileName.setText("User");
        }
    }

    // Open gallery to pick image
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST);
    }

    // Handle image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Set preview immediately
            profileImage.setImageURI(imageUri);

            // Upload to Firebase Storage
            uploadImageToFirebase();
        }
    }

    // Upload image to Firebase Storage
    private void uploadImageToFirebase() {
        if (imageUri == null) return;

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("profile_images/" + userId + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(uri ->
                                saveImageUrlToFirestore(uri.toString())
                        )
                )
                .addOnFailureListener(e ->
                        Toast.makeText(ProfileActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // Save image URL to Firestore
    private void saveImageUrlToFirestore(String imageUrl) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("users").document(currentUser.getUid())
                .update("profileImage", imageUrl)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(ProfileActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(ProfileActivity.this, "Error saving image URL", Toast.LENGTH_SHORT).show()
                );
    }
}

// --- Developer: David J ---
