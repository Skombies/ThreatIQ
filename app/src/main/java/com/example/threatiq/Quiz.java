package com.example.threatiq;

import java.io.Serializable;

/**
 * Quiz model. Default constructor for Firestore.
 */
public class Quiz implements Serializable {
    private String quizTitle;
    private int imageResourceId;
    private String quizId;

    public Quiz() { }

    public Quiz(String quizTitle, int imageResourceId, String quizId) {
        this.quizTitle = quizTitle;
        this.imageResourceId = imageResourceId;
        this.quizId = quizId;
    }

    public String getQuizTitle() { return quizTitle; }
    public int getImageResourceId() { return imageResourceId; }
    public String getQuizId() { return quizId; }
}
