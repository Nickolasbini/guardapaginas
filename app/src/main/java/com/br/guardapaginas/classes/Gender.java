package com.br.guardapaginas.classes;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.br.guardapaginas.helpers.Functions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
@Entity(tableName = "genders")
public class Gender extends DBHandler{
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "created at")
    public String createdAt;

    public int institution;

    public String attributes[] = {"id","name","date","institution"};

    public Gender(Context context){
        super(context);
        //setTableName("gender");
        //setAttributesFromModel(attributes);
        //setCurrentObject(this);
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
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

    public int getInstitution() {
        return institution;
    }
    public void setInstitution(int institution) {
        this.institution = institution;
    }

    public Boolean saveGender(Gender obj){
        String query = "";
        if(obj.getId() > 0){
            query = "UPDATE gender SET NAME = "+obj.getName()+" WHERE id = "+obj.getId();
        }else{
            query = "INSERT into gender (name, date, institution) VALUES ("+obj.getName()+","+obj.getCreatedAt()+","+obj.getInstitution()+")";
        }
        Boolean result = execQuery(query);

        /*
        HashMap<String, String> dataToSave =new HashMap<String, String>();
        dataToSave.put("id",        obj.getId());
        dataToSave.put("name",      obj.getName());
        dataToSave.put("createdAt", obj.getCreatedAt());
        for (Object key : dataToSave.keySet()) {
            String value=(String)dataToSave.get(key);
            System.out.println("Key: " +key+ "  |  Value: " + value);
        }
        System.out.println(dataToSave);
         */
        return true;
        //return save(dataToSave);
    }
}
