package com.br.guardapaginas.classes;

import com.br.guardapaginas.helpers.Functions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ModelBase {
    private String   tableName;
    private String[] attributesFromModel;
    private Object   currentObject;

    public void setTableName(String name){
        tableName = name;
    }
    public void setAttributesFromModel(String[] attributes){
        attributesFromModel = attributes;
    }
    public void setCurrentObject(Object object){
        currentObject = object;
    }
    public Boolean save(HashMap<String, String> arrayData){
        String value = "";
        JSONObject jsonObject= new JSONObject();
        try {
            for (Object key : arrayData.keySet()) {
                String val = (String)arrayData.get(key);
                key = (key.equals(null) ? "" : key.toString());
                jsonObject.put("${key}", val);
            }
            value = jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            value = "";
        }
        System.out.println(value);
        return true;
    }

    public void verifyFileExistense(){
//        com.br.guardapaginas.files
    }
}
