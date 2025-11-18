package com.example.threatiq;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class QuizActivity extends AppCompatActivity {

    private Quiz selectedQuiz;
    private List<Question> questions = new ArrayList<>();

    private int currentQuestionIndex = 0;
    private int score = 0;

    private TextView questionTextView;
    private LinearLayout optionsContainer;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionTextView = findViewById(R.id.question_text);
        optionsContainer = findViewById(R.id.options_container);
        nextButton = findViewById(R.id.next_button);

        // Get the quiz from the intent
        selectedQuiz = (Quiz) getIntent().getSerializableExtra("SELECTED_QUIZ");


        if (selectedQuiz != null) {
            getSupportActionBar().setTitle(selectedQuiz.getQuizTitle());
            loadQuestionsFromFirestore();
        }

        nextButton.setOnClickListener(v -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                displayQuestion();
            } else {
                // End of quiz
                Toast.makeText(this, "Quiz finished! Your score: " + score, Toast.LENGTH_LONG).show();
                finish(); // Go back to the quiz list
            }
        });
    }



    private void loadQuestionsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("quizzes")
                .document(selectedQuiz.getQuizId())
                .collection("questions")
                .orderBy("order") // optional
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query) {
                        Question q = new Question(
                                doc.getString("questionText"),
                                (List<String>) doc.get("options"),
                                doc.getLong("correctAnswerIndex").intValue()
                        );
                        questions.add(q);
                    }

                    if (questions.isEmpty()) {
                        Toast.makeText(this, "No questions found", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        displayQuestion();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load questions", Toast.LENGTH_SHORT).show());
    }

    /**
     * Display a question and options
     */
    private void displayQuestion() {
        Question currentQuestion = questions.get(currentQuestionIndex);
        questionTextView.setText(currentQuestion.getQuestionText());

        optionsContainer.removeAllViews();

        List<String> options = currentQuestion.getOptions();
        for (int i = 0; i < options.size(); i++) {
            MaterialButton optionButton = new MaterialButton(
                    this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle
            );
            optionButton.setText(options.get(i));
            optionButton.setTag(i);
            optionButton.setOnClickListener(v -> checkAnswer((int) v.getTag()));
            optionsContainer.addView(optionButton);
        }
    }

    /**
     * Handle answer and increase score
     */
    private void checkAnswer(int selectedOptionIndex) {
        Question currentQuestion = questions.get(currentQuestionIndex);

        if (selectedOptionIndex == currentQuestion.getCorrectAnswerIndex()) {
            score++;
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
        }

        // Disable all option buttons after selection
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            optionsContainer.getChildAt(i).setEnabled(false);
        }
    }

    /**
     * Save quiz score to Firestore per user
     */
    private void saveQuizScore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) return;

        Map<String, Object> result = new HashMap<>();
        result.put("quizId", selectedQuiz.getQuizId());
        result.put("score", score);
        result.put("total", questions.size());
        result.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users")
                .document(user.getUid())
                .collection("quizResults")
                .add(result);
    }
}