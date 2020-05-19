package com.example.medicationapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;
import java.util.HashMap;

public class SessionHelper {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "loginSession";
    private static final String LOGIN = "isLogin";
    public static final String USERID = "userID";
    public static final String FIRST_NAME = "firstName";
    public static final String AUTHORISATION = "authorisation";

    public SessionHelper(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }
	
	//Store session details
    public void createSession(String userID, String name, String authorisation){

        Date currentDate = new Date();

        long expiryDate = currentDate.getTime() + (7 * 24 * 60 * 60 * 1000);

        editor.putLong(LOGIN, expiryDate);
        editor.putString(USERID, userID);
        editor.putString(FIRST_NAME, name);
        editor.putString(AUTHORISATION, authorisation);
        editor.apply();
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        user.put(USERID, sharedPreferences.getString(USERID, null));
        user.put(FIRST_NAME, sharedPreferences.getString(FIRST_NAME, null));
        user.put(AUTHORISATION, sharedPreferences.getString(AUTHORISATION, null));

        return user;
    }

	//Check if user is logged in and not past expiry date
   public boolean isLogin(){
       Date currentDate = new Date();
       long expiryDate = sharedPreferences.getLong(LOGIN, 0);

       if(expiryDate == 0){
           return false;
       }

       Date sessionExpiryDate = new Date(expiryDate);
       return currentDate.before(sessionExpiryDate);
   }

   public void logout(){
       editor.clear();
       editor.commit();
   }
}
