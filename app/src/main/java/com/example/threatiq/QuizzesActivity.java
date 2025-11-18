package com.example.threatiq;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class QuizzesActivity extends AppCompatActivity {

    private RecyclerView quizzesRecyclerView;
    private QuizAdapter quizAdapter;
    private ArrayList<Quiz> quizList;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizzes);

        quizzesRecyclerView = findViewById(R.id.quizzes_recycler_view);
        quizzesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        quizList = new ArrayList<>();
        quizAdapter = new QuizAdapter(this, quizList);
        quizzesRecyclerView.setAdapter(quizAdapter);

        setupQuizData();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_quizzes);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(QuizzesActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_lessons) {
                Intent intent = new Intent(QuizzesActivity.this, LessonsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_quizzes) {
                return true; // Already here
            } else if (itemId == R.id.navigation_profile) {
                Intent intent = new Intent(QuizzesActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
            return false;
        });
    }

    private void setupQuizData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("quizzes").get()
                .addOnSuccessListener(query -> {
                    quizList.clear();
                    for (DocumentSnapshot doc : query) {
                        String title = doc.getString("title");
                        String image = doc.getString("image"); // name of drawable (optional)
                        String quizId = doc.getId();

                        int imageRes = 0;
                        if (image != null && !image.isEmpty()) {
                            imageRes = getResources().getIdentifier(image, "drawable", getPackageName());
                        }

                        quizList.add(new Quiz(title != null ? title : "Untitled", imageRes, quizId));
                    }
                    quizAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load quizzes", Toast.LENGTH_SHORT).show()
                );
    }
}
