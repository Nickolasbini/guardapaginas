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
    public final Integer INACTIVE = 0;
    public final Integer ACTIVE   = 1;

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

    public ArrayList<Gender> fetchAll(Integer status){
        ArrayList<Gender> emptyList = new ArrayList<Gender>(0);
        if(status != this.ACTIVE && status != this.INACTIVE)
            return emptyList;
        //String query = "SELECT * FROM genders WHERE status = "+status.toString();
        String query = "SELECT * FROM genders";
        if(!execQuery(query))
            return emptyList;
        Cursor results = getResults();
        results.moveToFirst();
        Integer total = results.getCount();
        ArrayList<Gender> data = new ArrayList<Gender>(total);
        System.out.println(Functions.getNowDate());
        for(Integer i = 0; i < total; i++){
            Integer idIndex        = results.getColumnIndex("id");
            Integer nameIndex      = results.getColumnIndex("name");
            Integer createdAtIndex = results.getColumnIndex("createAt");
            results.moveToNext();
        }
        return data;
    }
}
