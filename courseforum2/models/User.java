package com.example.courseforum2.models;

public class User {
    private static String userId;
    private static String UserName;
    private static String Password;
    private static String Email;
    private static boolean Is_Admin = false;

    public User(){

    }

    public static void setEmail(String email) {
        Email = email;
    }

    public static void setPassword(String password){
        Password = password;
    }

    public static void setIs_Admin(boolean is_Admin){
        Is_Admin = is_Admin;
    }

    public static void setUserId(String userId) {
        User.userId = userId;
    }

    public static void setUserName(String userName){
        UserName = userName;
    }
    public static String getEmail() {
        return Email;
    }

    public static String getPassword() {
        return Password;
    }

    public static String getUserName() {
        return UserName;
    }

    public static String getUserId(){return userId;}

    public String getIsAdmin(){
        if(Is_Admin == false){
            return "false";
        }
        else {
            return "true";
        }
    }
}

