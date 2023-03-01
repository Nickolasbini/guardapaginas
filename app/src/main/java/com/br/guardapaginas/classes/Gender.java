package com.br.guardapaginas.classes;

import android.widget.Toast;

import com.br.guardapaginas.helpers.Functions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Gender extends ModelBase{
    private String id;
    private String name;
    private String createdAt;
    private String institution;

    public String attributes[] = {"id","name","date","institution"};

    public Gender(){
        setTableName("gender");
        setAttributesFromModel(attributes);
        setCurrentObject(this);
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getInstitution() {
        return institution;
    }
    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public Boolean saveGender(Gender obj){
        HashMap<String, String> dataToSave =new HashMap<String, String>();
        dataToSave.put("id",        obj.getId());
        dataToSave.put("name",      obj.getName());
        dataToSave.put("createdAt", obj.getCreatedAt());
        for (Object key : dataToSave.keySet()) {
            String value=(String)dataToSave.get(key);
            System.out.println("Key: " +key+ "  |  Value: " + value);
        }

        return save(dataToSave);
    }
}
