package com.example.courseforum2.user_interface;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.courseforum2.helpers.AnswerNumCallback;
import com.example.courseforum2.models.User;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AnswerInterface extends AppCompatActivity {
    String questionNum;
    OkHttpClient client;
    TextView textViewToViewResultsOfOperation;
    TextView questionTextView,displayAnswer;
    LinearLayout l;

    String answerNum;

    EditText answerEditText;
    private String postUrlAnswer = "https://lamp.ms.wits.ac.za/~s2826534/postAnswer.php";
    private String getUrlAnswer = "https://lamp.ms.wits.ac.za/~s2826534/getAnswer.php";
    Button answerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the layout
        l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        setContentView(l);

        // Get the question from the Intent
        String questionText = getIntent().getStringExtra("Question");
        if (questionText == null) {
            questionText = "No question found"; // Fallback
        }


        String imageUrl = "";
        // Initialize client
        client = new OkHttpClient.Builder().build();

        // Initialize TextViews
        textViewToViewResultsOfOperation = new TextView(this);
        questionTextView = new TextView(this);
        displayAnswer = new TextView(this);
        questionTextView.setText("Question: " + questionText);
        l.addView(questionTextView);

        // Initialize answer EditText
        answerEditText = new EditText(this);
        answerEditText.setHint("Enter your answer");
        l.addView(displayAnswer);
        l.addView(answerEditText);

        // Initialize the answer button

        answerButton = new Button(this);
        answerButton.setText("Post Answer To Question");


        String text = questionText;
        String afterColon = text.split(":\\s*", 2)[1];

        getQuestionNum(afterColon);

        l.addView(answerButton);
        // Add the result TextView
        l.addView(textViewToViewResultsOfOperation);
    }
    public void postAnswer(String answer,String imageUrl,String qNum, String userId) {
        RequestBody requestBody = new FormBody.Builder()
                .add("answer", answer)
                .add("imageUrl", imageUrl)
                .add("qNum", qNum)
                .add("userId", userId)
                .build();

        Request request = new Request.Builder()
                .post(requestBody)
                .url(postUrlAnswer)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    textViewToViewResultsOfOperation.setText("Failed to post question: " + e.getMessage());
                    l.addView(textViewToViewResultsOfOperation);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseBody = response.body().string();
                runOnUiThread(() -> {
                    answerEditText.setText("");
                });
            }
        });
    }


    private void voteAnswer(String answer, String userId,String target_id, String vote_value){
        RequestBody requestBody = new FormBody.Builder().add("target_type", answer).add("user_id", User.getUserId()).add("target_id",target_id).add("vote_value", vote_value).build();
        Request request = new Request.Builder().post(requestBody).url("https://lamp.ms.wits.ac.za/~s2826534/Vote.php").build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });

    }

    private void getAnswerNum(String answer, AnswerNumCallback callback) {
        RequestBody requestBody = new FormBody.Builder().add("answer", answer).build();
        Request request = new Request.Builder()
                .post(requestBody)
                .url("https://lamp.ms.wits.ac.za/~s2826534/getAnswerNum.php")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String ansNum = response.body().string().trim();
                runOnUiThread(() -> callback.onAnswerNumReceived(ansNum));
            }
        });
    }
    private void getQuestionNum(String question){
        RequestBody requestBody = new FormBody.Builder().add("question", question).build();
        Request request = new Request.Builder().post(requestBody).url("https://lamp.ms.wits.ac.za/~s2826534/getQuestionNum.php").build();
        client.newCall(request).enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {}

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            questionNum = response.body().string().trim(); // ✅ this gets the actual response text
                            answerButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    postAnswer(answerEditText.getText().toString(), "", questionNum, User.getUserId());
                                    String answerInput = answerEditText.getText().toString();
                                    getAnswerNum(answerInput, new AnswerNumCallback() {
                                        @Override
                                        public void onAnswerNumReceived(String answerNum) {
                                            if (!answerInput.isEmpty()) {
                                                Button up = new Button(AnswerInterface.this);
                                                Button down = new Button(AnswerInterface.this);

                                                up.setText("thumbs up");
                                                down.setText("thumbs down");

                                                up.setOnClickListener(v -> voteAnswer("answer", User.getUserId(), answerNum, "1"));
                                                down.setOnClickListener(v -> voteAnswer("answer", User.getUserId(), answerNum, "-1"));

                                                TextView t = new TextView(AnswerInterface.this);
                                                t.setText(answerInput);
                                                l.addView(t);
                                                l.addView(up);
                                                l.addView(down);
                                            }
                                        }
                                    });

                                }
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }
}