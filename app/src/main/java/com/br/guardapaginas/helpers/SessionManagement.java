package com.br.guardapaginas.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.br.guardapaginas.classes.Institution;
import com.br.guardapaginas.classes.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SessionManagement {

    private Context currentContext;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    final String SHARED_PREF_NAME = "session";
    String SESSION_KEY            = "user_session";

    public SessionManagement(Context context){
        currentContext = context;
        System.out.println("Contexto atual: "+context);
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,  Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /*
     * Save user ID to session
     */
    public void saveSession(User user){
        Integer id      = user.getId();
        String idString = id.toString();
        editor.putString("userId", idString).commit();
        editor.putString("userName", user.getName()).commit();
        editor.putString("userEmail", user.getEmail()).commit();
        editor.putString("currentIdSession", idString).commit();
        Institution institutionObj = new Institution(currentContext);
        institutionObj = institutionObj.getByOwner(Integer.toString(user.getId()));
        String institutionId = null;
        if(institutionObj != null)
            institutionId = Integer.toString(institutionObj.getId());
        editor.putString("userInstitutionId", institutionId).commit();
    }


    /*
    * Return ID of logged in user
    */
    public List<String> getSessionUser(){
        List<String> data = new ArrayList<>(4);
        if(!isSessionActive())
            return null;
        String currentSession = getSession("currentIdSession");
        data.add(getSession("userId"));
        data.add(getSession("userName"));
        data.add(getSession("userEmail"));
        data.add(getSession("userInstitutionId"));
        return data;
    }

    public String getSession(String key){
        return sharedPreferences.getString(key, "");
    }

    public Boolean isSessionActive(){
        String val = getSession("currentIdSession");
        if(val.equals("") || val == null)
            return false;
        return true;
    }

    public String getSessionId(){
        if(isSessionActive())
            return getSession("currentIdSession");
        return null;
    }

    public void killSession(){
        sharedPreferences.edit().clear().commit();
    }
}
