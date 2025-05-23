package com.example.courseforum2.user_interface;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.courseforum2.R;
import com.example.courseforum2.helpers.Callback;
import com.example.courseforum2.helpers.QuestionNumCallback;
import com.example.courseforum2.helpers.postQuestionCallback;
import com.example.courseforum2.models.User;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QuestionInterface extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;

    private Uri selectedImageUri;
    private Bitmap capturedImage;

    Button askQuestionButton, getQuestionButton, getAllQuestionsButton,getImage;

    private TextView textViewToViewResultsOfOperation,newQ;
    OkHttpClient client;


    private String postUrlQuestion = "https://lamp.ms.wits.ac.za/~s2826534/postQuestion.php";
    private String getUrlQuestion = "https://lamp.ms.wits.ac.za/~s2826534/getQuestion.php" , getQuestionNumUrl = "https://lamp.ms.wits.ac.za/~s2826534/getQuestionNum.php", getAllUsersAndQuestionsUrl = "https://lamp.ms.wits.ac.za/~s2826534/getAllUserQuestions.php";

    EditText answerText, questionText;
    ImageView imageView;

    TextView textView;

    private LinearLayout l;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_interface);

        l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        client = new OkHttpClient.Builder().build();
        setContentView(l);

        //Buttons
        askQuestionButton = new Button(this);
        getAllQuestionsButton = new Button(this);
        getQuestionButton = new Button(this);
        getImage = new Button(this);

        //Image
        imageView = new ImageView(this);

        //TextViews
        textView = new TextView(this);
        textViewToViewResultsOfOperation = new TextView(this);
        newQ = new TextView(this);

        //EditText
        answerText = new EditText(this);
        questionText = new EditText(this);


        getImage.setText("Image");
        getAllQuestionsButton.setText("See All User Questions");
        askQuestionButton.setText("Ask Question");
        getQuestionButton.setText("Get Question");
        answerText.setHint("Answer");
        questionText.setHint("Question");
        textView.setText(User.getUserId());

        //adding Views
        l.addView(textView);
        l.addView(questionText);
        l.addView(answerText);
        l.addView(askQuestionButton);
        l.addView(getImage);
        l.addView(imageView);
        l.addView(getQuestionButton);
        l.addView(getAllQuestionsButton);

        String imageUrl = "";

        getAllQuestionsAndUserNames(new Callback() {
            @Override
            public void onReceived(String responseFromJson) {
                try {
                    JSONArray array = new JSONArray(responseFromJson);
                    for(int i = 0; i < array.length();i++){
                        JSONObject question = array.getJSONObject(i);

                        String q = question.getString("UserName") + " : "+ question.getString("Q_Text");

                        TextView resultingQuestion = new TextView(QuestionInterface.this);
                        resultingQuestion.setText(q);


                        resultingQuestion.setOnClickListener(v -> {
                            Intent intent = new Intent(QuestionInterface.this, AnswerInterface.class);
                            intent.putExtra("Question", q);
                            startActivity(intent);
                        });

                        String text = resultingQuestion.getText().toString();
                        String afterColon = text.split(":\\s*", 2)[1];

                        getQuestionNum(afterColon, new QuestionNumCallback() {
                            @Override
                            public void onReceiveNum(String questionNum) {
                                Button up = new Button(QuestionInterface.this);
                                Button down = new Button(QuestionInterface.this);

                                up.setText("thumbs up");
                                down.setText("thumbs down");

                                up.setOnClickListener(v -> voteAnswer("question", User.getUserId(), questionNum, "1"));
                                down.setOnClickListener(v -> voteAnswer("question", User.getUserId(), questionNum, "-1"));

                                l.addView(resultingQuestion);
                                l.addView(up);
                                l.addView(down);
                            }
                        });
                    }
                } catch (JSONException e) {
                    textViewToViewResultsOfOperation.setText("Error has approached");
                    l.addView(textViewToViewResultsOfOperation);
                }
            }

            @Override
            public void uploaded() {

            }

            @Override
            public void onError(IOException e) {

            }

            @Override
            public void onError(Exception e) {

            }
        });


        askQuestionButton.setOnClickListener(view -> {
            postQuestion(questionText.getText().toString(), imageUrl, User.getUserId());
        });
    }



    private void postQuestion(String question, String imageUrl, String userId) {
        RequestBody requestBody = new FormBody.Builder().add("question", question).add("imageUrl", imageUrl).add("userId", userId).build();
        Request request = new Request.Builder().post(requestBody).url(postUrlQuestion).build();
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

                    questionText.setText("");

                    getQuestionNum(question, new QuestionNumCallback() {
                        @Override
                        public void onReceiveNum(String questionNum) {
                            Button up = new Button(QuestionInterface.this);
                            Button down = new Button(QuestionInterface.this);

                            up.setText("thumbs up");
                            down.setText("thumbs down");

                            up.setOnClickListener(v -> voteAnswer("question", User.getUserId(), questionNum, "1"));
                            down.setOnClickListener(v -> voteAnswer("question", User.getUserId(), questionNum, "-1"));


                            newQ.setText(question);

                            newQ.setOnClickListener(v -> {
                                Intent intent = new Intent(QuestionInterface.this, AnswerInterface.class);
                                intent.putExtra("Question", question);
                                startActivity(intent);
                            });

                            l.addView(newQ);
                            l.addView(up);
                            l.addView(down);
                        }
                    });
                });
            }
        });
    }


    public void getAllQuestionsAndUserNames(Callback callback) {

        Request request = new Request.Builder()
                .url(getAllUsersAndQuestionsUrl)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> callback.onError(e));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string().trim();
                runOnUiThread(() -> {
                    try {
                        callback.onReceived(json);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
    }

    private void voteAnswer(String question, String userId,String target_id, String vote_value){
        RequestBody requestBody = new FormBody.Builder().add("target_type", question).add("user_id", User.getUserId()).add("target_id",target_id).add("vote_value", vote_value).build();
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

    private void getQuestionNum(String question, QuestionNumCallback callback) {
        RequestBody requestBody = new FormBody.Builder().add("question", question).build();
        Request request = new Request.Builder()
                .post(requestBody)
                .url("https://lamp.ms.wits.ac.za/~s2826534/getQuestionNum.php")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String qNum = response.body().string().trim();
                runOnUiThread(() -> callback.onReceiveNum(qNum));
            }
        });
    }

    private void toIntent(String q, postQuestionCallback callback){
        runOnUiThread(()->callback.onIntent());
    }


}