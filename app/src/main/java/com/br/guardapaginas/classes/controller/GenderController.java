package com.br.guardapaginas.classes.controller;

import android.content.Context;

import com.br.guardapaginas.classes.Gender;

import java.util.ArrayList;

public class GenderController {

    Context currentContext;

    public GenderController(Context context){
        currentContext = context;
    }

    public ArrayList<Gender> getAllGenders(){
        Gender genderObj = new Gender(currentContext);
        genderObj.fetchAll(genderObj.ACTIVE);

        ArrayList<Gender> data = new ArrayList<Gender>();
        return data;
    }
}
