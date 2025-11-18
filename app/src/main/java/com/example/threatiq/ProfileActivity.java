package com.example.threatiq;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private MaterialCardView logoutButton, editProfileButton;
    private ShapeableImageView profileImage;
    private TextView profileName, profileEmail;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Uri cameraImageUri = null; // URI for camera-captured image

    // Activity result launchers (modern API)
    private ActivityResultLauncher<String[]> permissionLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // you provided this XML

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI refs
        profileImage = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);
        editProfileButton = findViewById(R.id.edit_profile_button);
        logoutButton = findViewById(R.id.logout_button);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Guard: logged in user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Not logged in -> go to login
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Show email, load user doc
        profileEmail.setText(currentUser.getEmail());
        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(this::loadUserProfile)
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
                    profileName.setText("User");
                });

        // Activity result handlers

        // Permissions: request CAMERA and READ (or READ_MEDIA_IMAGES on Android 13+)
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allGranted = true;
                    for (Boolean granted : result.values()) {
                        if (!granted) { allGranted = false; break; }
                    }
                    if (!allGranted) {
                        Toast.makeText(this, "Permissions required to use camera/gallery", Toast.LENGTH_SHORT).show();
                    } else {
                        // if cameraImageUri is non-null assume we wanted to take photo
                        if (cameraImageUri != null) {
                            takePictureLauncher.launch(cameraImageUri);
                        }
                    }
                }
        );

        // Camera: take picture into provided Uri
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                (Boolean success) -> {
                    if (success && cameraImageUri != null) {
                        // preview immediately
                        profileImage.setImageURI(cameraImageUri);
                        uploadImageToFirebase(cameraImageUri);
                    } else {
                        Toast.makeText(this, "Camera cancelled or failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Gallery: pick image
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                (Uri uri) -> {
                    if (uri != null) {
                        profileImage.setImageURI(uri);
                        uploadImageToFirebase(uri);
                    }
                }
        );

        // Click profile image -> choose source (camera or gallery)
        profileImage.setOnClickListener(v -> showImageSourceChooser());

        // Edit button: open EditProfileActivity
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Logout
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        bottomNavigationView.setOnItemSelectedListener((NavigationBarView.OnItemSelectedListener) item -> {
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
        });
    }

    private void loadUserProfile(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
            String username = documentSnapshot.getString("username");
            profileName.setText(username != null ? username : "User");

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

    // Show a simple chooser dialog (system chooser via intents)
    private void showImageSourceChooser() {
        // We'll show a simple native chooser using Intents: Camera and Gallery.
        // For better UX you can use a dialog — kept simple here.
        // 1. Camera
        // 2. Gallery

        // Build a camera URI first (for camera intent)
        cameraImageUri = createImageUriForCamera();
        // Build permission list
        if (needsCameraPermission()) {
            // Request permissions, then take picture in the permission callback
            requestCameraAndStoragePermissions();
        } else {
            // Directly show chooser Intent
            // We'll open a simple chooser Activity that offers both camera and gallery:
            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);

            // Camera intent
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraImageUri != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            // Gallery intent
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");

            // Put both into chooser
            chooserIntent.putExtra(Intent.EXTRA_INTENT, galleryIntent);
            Intent[] extraIntents = new Intent[]{cameraIntent};
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);

            try {
                startActivityForResult(chooserIntent, 101); // fallback route handled below
            } catch (ActivityNotFoundException e) {
                // Fallback: open gallery
                pickImageLauncher.launch("image/*");
            }
        }
    }

    // Create a file URI to store camera image via FileProvider
    private Uri createImageUriForCamera() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(null);
            File imageFile = File.createTempFile(fileName, ".jpg", storageDir);
            return FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider",
                    imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Determine if we need camera/storage permission
    private boolean needsCameraPermission() {
        boolean cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean readPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses READ_MEDIA_IMAGES
            readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return !(cameraPermission && readPermission);
    }

    // Request camera + read permissions
    private void requestCameraAndStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(new String[] { Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES });
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionLauncher.launch(new String[] { Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE });
        } else {
            // Older devices: permissions are granted at install time
            // Attempt to take picture directly
            if (cameraImageUri != null) takePictureLauncher.launch(cameraImageUri);
        }
    }

    // Fallback older onActivityResult (we still registered pickImageLauncher & takePictureLauncher)
    @Override
    @Deprecated
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If user used chooser started by startActivityForResult above
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                // If data is null, likely camera saved to cameraImageUri
                if (data == null || data.getData() == null) {
                    if (cameraImageUri != null) {
                        profileImage.setImageURI(cameraImageUri);
                        uploadImageToFirebase(cameraImageUri);
                    }
                } else {
                    Uri selected = data.getData();
                    profileImage.setImageURI(selected);
                    uploadImageToFirebase(selected);
                }
            }
        }
    }

    // Upload a chosen Uri to Firebase Storage and update Firestore document
    private void uploadImageToFirebase(@NonNull Uri uri) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("profile_images/" + userId + ".jpg");

        storageRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl()
                                .addOnSuccessListener(downloadUri ->
                                        saveImageUrlToFirestore(downloadUri.toString())
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(ProfileActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show()
                                )
                )
                .addOnFailureListener(e ->
                        Toast.makeText(ProfileActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // Save image URL
    private void saveImageUrlToFirestore(String imageUrl) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("users").document(currentUser.getUid())
                .update("profileImage", imageUrl)
                .addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Error saving image URL", Toast.LENGTH_SHORT).show());
    }
}
