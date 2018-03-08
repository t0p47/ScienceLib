package com.t0p47.sciencelib.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by 01 on 16.10.2017.
 */

public class SessionManager {

    private static String TAG = "LOG_TAG";

    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;


    private static final String PREF_NAME = "ScienceLibPrefs";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_AUTH_TOKEN = "authToken";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_PATH_TO_FILE = "pathToFile";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setAuthToken(String authToken){
        editor.putString(KEY_AUTH_TOKEN, authToken);

        editor.commit();
        //Log.d(TAG,"SessionManager: token changed");
    }

    public String getAuthToken(){
        return pref.getString(KEY_AUTH_TOKEN, "false");
    }

    public void setUID(int uid){
        editor.putInt(KEY_USER_ID,uid);

        editor.commit();
    }

    public int getUID(){
        return pref.getInt(KEY_USER_ID,0);
    }

    public void setPathToFile(String pathToFile){
        editor.putString(KEY_PATH_TO_FILE, pathToFile);

        editor.commit();
    }

    public String getPathToFile(){

        return pref.getString(KEY_PATH_TO_FILE, null);

    }

    public void setLogin(boolean isLoggedIn){
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        editor.commit();

        Log.d(TAG,"User login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN,false);
    }



}
