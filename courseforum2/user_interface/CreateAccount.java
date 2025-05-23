package com.example.courseforum2.user_interface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.courseforum2.R;
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


public class CreateAccount extends AppCompatActivity {

    String postUrl = "https://lamp.ms.wits.ac.za/home/s2826534/courseforum.php";
    OkHttpClient client;

    TextView textViewO;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        client = new OkHttpClient.Builder().build();

        //Please ignore this stay away from it
        LinearLayout l = new LinearLayout(this);
        TextView textView = new TextView(this);
        Button createAccount = new Button(this);
        EditText userNameInput = new EditText(this);
        EditText email = new EditText(this);
        EditText passwordEntered = new EditText(this);
        EditText confirmPassword = new EditText(this);



        textViewO = new TextView(this);
        //Add Ons to be edited soon
        //Buttons and edittexts To create account
        createAccount.setText("Create Account");
        userNameInput.setHint("Username");
        email.setHint("Enter Email");
        passwordEntered.setHint("Enter Password");
        confirmPassword.setHint("Confirm Password");

        //TextView for creating account

        textView.setText("Create Account");



        l.setOrientation(LinearLayout.VERTICAL);
        setContentView(l);
        l.addView(textView);
        l.addView(userNameInput);
        l.addView(email);
        l.addView(passwordEntered);
        l.addView(confirmPassword);
        l.addView(createAccount);
        l.addView(textViewO);
        System.out.println(hasStudentsInIt(email.getText().toString()) + email.getText().toString());

        //Button to create account

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordEntered.getText().toString().equals(confirmPassword.getText().toString())) {
                    //this is all you need
                    post(userNameInput.getText().toString(), email.getText().toString(), confirmPassword.getText().toString());
                    Intent intent = new Intent(CreateAccount.this, MainActivity.class);
                    startActivity(intent);
                } else {

                    textView.setText("Passwords dont match!");
                    passwordEntered.setText("");
                    confirmPassword.setText("");
                    textView.setText("Create Account");

                }
            }
        });
    }

    public void post(String username, String email, String password) {
        boolean c = hasStudentsInIt(email);
        User.setEmail(email);
        User.setUserName(username);
        User.setPassword(password);
        String s;
        if(c == false){
            s = "true";
        }
        else{
            s = "false";
        }
        RequestBody requestBody = new FormBody.Builder().add("username", User.getUserName()).add("email", User.getEmail()).add("password", User.getPassword()).add("isAdmin", s).build();
        Request request = new Request.Builder().post(requestBody).url(postUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String reaponseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewO.setText(reaponseData);
                    }
                });
            }
        });
    }

    public static boolean hasStudentsInIt(String email) {
        return email != null && email.toLowerCase().contains("students");
    }

}
