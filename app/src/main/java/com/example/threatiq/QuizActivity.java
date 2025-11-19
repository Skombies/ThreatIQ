package com.example.threatiq;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {

    private Quiz selectedQuiz;
    private List<Question> questions = new ArrayList<>();

    private int currentQuestionIndex = 0;
    private int score = 0;

    private TextView questionTextView, quizPageTitle;
    private LinearLayout optionsContainer;
    private Button nextButton;
    private ImageView backButton;

    // track whether current question has been answered
    private boolean answered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionTextView = findViewById(R.id.question_text);
        optionsContainer = findViewById(R.id.options_container);
        nextButton = findViewById(R.id.next_button);
        backButton = findViewById(R.id.back_button);
        quizPageTitle = findViewById(R.id.quiz_page_title);

        nextButton.setEnabled(false); // disabled until user answers

        selectedQuiz = (Quiz) getIntent().getSerializableExtra("SELECTED_QUIZ");

        if (selectedQuiz == null) {
            Toast.makeText(this, "No quiz selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        quizPageTitle.setText(selectedQuiz.getQuizTitle());

        backButton.setOnClickListener(v -> finish());

        loadQuestionsFromFirestore();

        nextButton.setOnClickListener(v -> {
            if (!answered) {
                Toast.makeText(this, "Please select an answer first", Toast.LENGTH_SHORT).show();
                return;
            }

            // move to next question or finish
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                displayQuestion();
            } else {
                saveQuizScore();
                Toast.makeText(this, "Quiz finished! Your score: " + score + " / " + questions.size(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void loadQuestionsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("quizzes")
                .document(selectedQuiz.getQuizId())
                .collection("questions")
                .orderBy("order") // optional field for ordering questions
                .get()
                .addOnSuccessListener(query -> {
                    questions.clear();
                    for (DocumentSnapshot doc : query) {
                        // defensive: verify fields exist
                        String qText = doc.getString("questionText");
                        List<String> opts = (List<String>) doc.get("options");
                        Long correct = doc.getLong("correctAnswerIndex");

                        if (qText == null || opts == null || correct == null) continue;

                        Question q = new Question(qText, opts, correct.intValue());
                        questions.add(q);
                    }

                    if (questions.isEmpty()) {
                        Toast.makeText(this, "No questions found for this quiz.", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    // start quiz
                    currentQuestionIndex = 0;
                    score = 0;
                    displayQuestion();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load questions", Toast.LENGTH_SHORT).show();
                });
    }

    private void displayQuestion() {
        answered = false;
        nextButton.setEnabled(false);

        optionsContainer.removeAllViews();

        Question q = questions.get(currentQuestionIndex);
        questionTextView.setText((currentQuestionIndex + 1) + ". " + q.getQuestionText());
        questionTextView.setTextColor(ContextCompat.getColor(this, R.color.black));

        List<String> opts = q.getOptions();
        for (int i = 0; i < opts.size(); i++) {
            MaterialButton btn = new MaterialButton(this, null,
                    com.google.android.material.R.attr.materialButtonOutlinedStyle);
            btn.setText(opts.get(i));
            btn.setTag(i);
            btn.setAllCaps(false);
            btn.setOnClickListener(v -> onOptionSelected((MaterialButton) v));
            // layout params: match parent width, wrap content height, margin
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 12, 0, 0);
            optionsContainer.addView(btn, lp);
        }
    }

    private void onOptionSelected(MaterialButton selectedBtn) {
        if (answered) return; // ignore double taps

        answered = true;
        nextButton.setEnabled(true); // allow moving forward

        int selectedIndex = (int) selectedBtn.getTag();
        Question current = questions.get(currentQuestionIndex);
        int correctIndex = current.getCorrectAnswerIndex();

        // color buttons: green for correct, red for selected wrong
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            View child = optionsContainer.getChildAt(i);
            child.setEnabled(false);
            if (!(child instanceof MaterialButton)) continue;
            MaterialButton b = (MaterialButton) child;

            if ((int) b.getTag() == correctIndex) {
                b.setBackgroundColor(Color.parseColor("#4CAF50")); // green
                b.setTextColor(Color.WHITE);
            } else if ((int) b.getTag() == selectedIndex) {
                b.setBackgroundColor(Color.parseColor("#F44336")); // red
                b.setTextColor(Color.WHITE);
            } else {
                // keep default outlined style for others
            }
        }

        if (selectedIndex == correctIndex) {
            score++;
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveQuizScore() {
        if (selectedQuiz == null || questions.isEmpty()) return;
        var user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        Map<String, Object> result = new HashMap<>();
        result.put("quizId", selectedQuiz.getQuizId());
        result.put("score", score);
        result.put("total", questions.size());
        result.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .collection("quizResults")
                .add(result);
    }
}
