package com.example.threatiq;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private Quiz selectedQuiz;
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
            displayQuestion();
        }

        nextButton.setOnClickListener(v -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < selectedQuiz.getQuestions().size()) {
                displayQuestion();
            } else {
                // End of quiz
                Toast.makeText(this, "Quiz finished! Your score: " + score, Toast.LENGTH_LONG).show();
                finish(); // Go back to the quiz list
            }
        });
    }

    private void displayQuestion() {
        Question currentQuestion = selectedQuiz.getQuestions().get(currentQuestionIndex);
        questionTextView.setText(currentQuestion.getQuestionText());

        optionsContainer.removeAllViews(); // Clear old options

        List<String> options = currentQuestion.getOptions();
        for (int i = 0; i < options.size(); i++) {
            MaterialButton optionButton = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
            optionButton.setText(options.get(i));
            optionButton.setTag(i); // Use tag to identify the option index
            optionButton.setOnClickListener(v -> {
                checkAnswer((int) v.getTag());
            });
            optionsContainer.addView(optionButton);
        }
    }

    private void checkAnswer(int selectedOptionIndex) {
        Question currentQuestion = selectedQuiz.getQuestions().get(currentQuestionIndex);
        if (selectedOptionIndex == currentQuestion.getCorrectAnswerIndex()) {
            score++;
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
        }
        // Disable all option buttons after an answer is selected
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            optionsContainer.getChildAt(i).setEnabled(false);
        }
    }
}

