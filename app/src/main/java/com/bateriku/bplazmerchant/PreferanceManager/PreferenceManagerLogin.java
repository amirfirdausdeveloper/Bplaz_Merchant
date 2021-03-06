package com.bateriku.bplazmerchant.PreferanceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.bateriku.bplazmerchant.Activity.Login.LoginActivity;

import java.util.HashMap;


public class PreferenceManagerLogin {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "BplazMerchant";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_EMAIL = "KEY_EMAIL";
    public static final String KEY_TOKEN = "KEY_TOKEN";
    public static final String KEY_ID = "KEY_ID";
    public static final String KEY_ROLE = "HAVE_ROLE_ID";
    public static final String USER_ID = "USER_ID";


    public PreferenceManagerLogin(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String email, String token,String id,String role, String user_id){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_ROLE, role);
        editor.putString(USER_ID, user_id);
        editor.commit();
    }

    public boolean checkLogin(){
        if(!this.isLoggedIn()){
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            return true;
        }
        return false;
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_ROLE, pref.getString(KEY_ROLE, null));
        user.put(USER_ID, pref.getString(USER_ID, null));
        return user;
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}