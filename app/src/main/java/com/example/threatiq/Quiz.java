package com.example.threatiq;

import java.io.Serializable;
import java.util.List;

// Implement Serializable to allow passing this object via Intent
public class Quiz implements Serializable {
    private String quizTitle;
    private int imageResourceId;
    private List<Question> questions;

    public Quiz(String quizTitle, int imageResourceId, List<Question> questions) {
        this.quizTitle = quizTitle;
        this.imageResourceId = imageResourceId;
        this.questions = questions;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
