package com.example.courseforum2.models;

public class Answer {
    private Question question;
    private String userName;
    private String Image_Url;
    private String Answer;

    public Answer(Question q, String answer, String image_Url, String userName){
        this.question = q;
        this.userName = userName;
        this.Answer = answer;
        this.Image_Url = image_Url;
    }

    public String getAnswer(){
        return Answer;
    }

    public String getUserName(){
        return userName;
    }

    public String getImage_Url(){return Image_Url;}

    public Question getQuestion() {
        return question;
    }
}

