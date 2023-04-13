package com.br.guardapaginas.classes.controller;

import android.content.Context;

import com.br.guardapaginas.classes.User;

public class UserController {

    Context currentContext;

    public UserController(Context context){
        currentContext = context;
    }

    public User findByEmail(String email){
        return (new User(currentContext));
    }

}
