package com.example.threatiq;

import java.io.Serializable;
import java.util.List;

/**
 * Simple model for a question.
 * Implements Serializable so it can be passed in intents if needed.
 * Default constructor required by Firestore.
 */
public class Question implements Serializable {
    private String questionText;
    private List<String> options;
    private int correctAnswerIndex;

    public Question() { }

    public Question(String questionText, List<String> options, int correctAnswerIndex) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }
}