package com.example.courseforum2.models;

import java.util.ArrayList;

public class Question {
    private static String question;
    private static String imageUrl;
    private static String userId;
    private static ArrayList<Answer> answersToQuestion;

    public Question(){

    }

    public static String getQuestion() {
        return question;
    }

    public static String getUserId(){
        return userId;
    }

    public static String getImageUrl(){
        return imageUrl;
    }

    public static void putAnswerForQuestion(Answer answer){
        answersToQuestion.add(answer);
    }

    public static void setQuestion(String question){
        question = question;
    }

    public static void setUserId(String userId) {
        Question.userId = userId;
    }

    public static void setAnswersToQuestion(ArrayList<Answer> answers){
        answersToQuestion = answers;
    }

    public static ArrayList<Answer> getAnswersToQuestion() {
        return answersToQuestion;
    }

    public static void returnAnswers(){
        for(Answer answer:answersToQuestion){
            System.out.println(answer.getUserName() +" : " + answer.getAnswer());
        }
    }
}

