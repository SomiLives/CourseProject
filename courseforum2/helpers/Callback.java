package com.example.courseforum2.helpers;

import org.json.JSONException;

import java.io.IOException;

public interface Callback {
    void onReceived(String userId) throws JSONException;
    void uploaded();
    void onError(IOException e);  // More specific to IOException

    void onError(Exception e);
}


