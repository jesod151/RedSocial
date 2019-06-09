package com.example.redsocial.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Calendar;

/**
 * Created by pablolopezfallas on 3/12/18.
 */

public class UserPreferences {

    public static final String KEY_USER_ID = "USER_CODE";
    public static final String KEY_NOMBRE = "NOMBRE";
    public static final String KEY_APELLIDO1 = "APELLIDO1";
    public static final String KEY_APELLIDO2 = "APELLIDO2";
    public static final String KEY_EMAIL = "EMAIL";
    public static final String KEY_PHOTO = "PHOTO";


    public static final String KEY_LAST_LOGIN = "LAST_LOGIN";
    public static final String KEY_USERNAME = "USERNAME";

    public static final String KEY_RESEND_QUEUE = "RESEND_QUEUE";

    private final static String PREFERENCE_KEY = "com.example.redsocial.PREFERENCES_KEY";



    private SharedPreferences prefs;


    public UserPreferences(Context context) {
        //this.context = context;
        this.prefs = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
    }

    public void clear() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public boolean isEmpty() {
        return !prefs.contains(KEY_USER_ID);
    }

    public void saveUser(String id, String nombre, String email, String photo) {
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString(KEY_USER_ID, id);
            editor.putString(KEY_NOMBRE, nombre);
            editor.putString(KEY_EMAIL, email);
            editor.putString(KEY_PHOTO, photo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        editor.apply();
    }

    public void saveUser(JSONObject json) {
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString(KEY_USER_ID, json.getString("ID"));
            editor.putString(KEY_NOMBRE, json.getString("NOMBRE"));
            editor.putString(KEY_APELLIDO1, json.getString("APELLIDO1"));
            editor.putString(KEY_APELLIDO2, json.getString("APELLIDO2"));
            editor.putString(KEY_EMAIL, json.getString("EMAIL"));
            editor.putString(KEY_PHOTO, json.getString("PHOTO"));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        editor.apply();
    }

    public void saveData(JSONObject json) {
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString(KEY_NOMBRE, json.getString("NOMBRE"));
            editor.putString(KEY_APELLIDO1, json.getString("APELLIDO1"));
            editor.putString(KEY_APELLIDO2, json.getString("APELLIDO2"));
            editor.putString(KEY_EMAIL, json.getString("EMAIL"));
            editor.putString(KEY_PHOTO, json.getString("PHOTO"));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        editor.apply();
    }



    public void saveLastLogin() {
        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_LAST_LOGIN, dayOfYear);
        editor.apply();
    }

    public void saveUsername(String username) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public boolean requiresLogin () {
        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        return (prefs.getInt(KEY_LAST_LOGIN, 0) != dayOfYear);
    }



    public void saveFailedQueue(JSONObject json) {
        JSONArray array;
        try {
            array = new JSONArray(prefs.getString(KEY_RESEND_QUEUE, ""));
        } catch (JSONException ex) {
            ex.printStackTrace();
            array = new JSONArray();
        }

        array.put(json);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_RESEND_QUEUE, array.toString());
        editor.apply();
    }

    public JSONArray getFailedQueue() {
        try {
            return new JSONArray(prefs.getString(KEY_RESEND_QUEUE, ""));
        } catch (JSONException ex) {
            return new JSONArray();
        }
    }

    public void clearFailedQueue() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_RESEND_QUEUE);
        editor.apply();
    }

    public String getUserCode () {
        return prefs.getString(KEY_USER_ID, "");
    }

    public String getNombre () {
        return prefs.getString(KEY_NOMBRE, "");
    }

    public String getNombreCompleto () { return this.getNombre()+" "+getApellido1()+" "+getApellido2(); }

    public String getApellido1 () {
        return prefs.getString(KEY_APELLIDO1, "");
    }

    public String getApellido2 () {
        return prefs.getString(KEY_APELLIDO2, "");
    }

    public String getEmail () { return prefs.getString(KEY_EMAIL, ""); }


    public static JSONObject[] arrayFix(JSONArray array, boolean hasBlank) {
        int offset = hasBlank ? 1 : 0;
        JSONObject[] result = new JSONObject[array.length()+offset];

        if (hasBlank) result[0] = new JSONObject();
        for (int x = 0; x < array.length(); x++) {
            try {
                result[x+offset] = array.getJSONObject(x);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public String getPhoto () {
        return prefs.getString(KEY_PHOTO, "");
    }

    public String getPref (String key) {
        return prefs.getString(key, "");
    }


    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return urlEncode(capitalize(model));
        } else {
            return urlEncode(capitalize(manufacturer) + " " + model);
        }
    }

    private static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (Exception ex) {
            return "";
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
