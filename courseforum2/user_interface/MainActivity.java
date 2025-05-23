package com.example.courseforum2.user_interface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.courseforum2.R;
import com.example.courseforum2.models.User;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final int PCIK_IMAGE_REQUEST = 1;
    private OkHttpClient client;
    private String getUrl = "https://lamp.ms.wits.ac.za/~s2826534/login.php",getUserUrl = "https://lamp.ms.wits.ac.za/~s2826534/getUserId.php";

    private TextView checkForLoginOrNot;

    private String UserId;
    Button loginAccount;

    EditText username, password;
    LinearLayout l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new OkHttpClient.Builder().build();

        l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        setContentView(l);

        Button createAccount = new Button(this);
        loginAccount = new Button(this);
        username = new EditText(this);
        password = new EditText(this);

        loginAccount.setText("Login");
        createAccount.setText("Create Account");
        username.setHint("Username");
        password.setHint("Password");

        l.addView(username);
        l.addView(password);
        l.addView(loginAccount);
        l.addView(createAccount);

        checkForLoginOrNot = new TextView(this);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateAccount.class);
                startActivity(intent);
            }
        });
        loginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User.setUserName(username.getText().toString());
                get(username.getText().toString(), password.getText().toString());
                getUserId(username.getText().toString());

            }
        });
    }

    public void get(String Username,String Password){
        String urlWithParams = getUrl + "?username=" + Username + "&password=" + Password;
        Request request = new Request.Builder().url(urlWithParams).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseFromGet = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject json = new JSONObject(responseFromGet);
                            if (json.getBoolean("success")){
                                Intent intent = new Intent(MainActivity.this, QuestionInterface.class);
                                startActivity(intent);
                            }
                            else{
                                checkForLoginOrNot.setText("User does not exist please create account.");
                                l.addView(checkForLoginOrNot);
                                getUserId(username.getText().toString());
                                username.setText("");
                                password.setText("");
                            }
                        }
                        catch (JSONException e){
                            checkForLoginOrNot.setText("Error :" + e);
                        }
                    }
                });
            }
        });
    }

    private void getUserId(String userName) {
        RequestBody requestBody = new FormBody.Builder().add("userName", userName).build();
        Request request = new Request.Builder().post(requestBody).url(getUserUrl).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            UserId = response.body().string().trim(); // ✅ this gets the actual response text
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        User.setUserId(UserId);
                    }
                });
            }
        });
    }
}