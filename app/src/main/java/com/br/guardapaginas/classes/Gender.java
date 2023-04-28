package com.br.guardapaginas.classes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.br.guardapaginas.helpers.Functions;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "genders")
public class Gender extends DBHandler{
    public final String INACTIVE = "0";
    public final String ACTIVE   = "1";

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "created at")
    public String createdAt;

    public int institution;

    public String status;

    public String attributes[] = {"id","name","date","institution"};

    public Gender(Context context){
        super(context);
        setTableName("genders");
        setAttributesFromModel(attributes);
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

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean save(Gender gender){
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", gender.getName());
        Integer result = 0;
        if(gender.getId() > 0) {
            contentValues.put("createdAt", gender.getCreatedAt());
            result = getDBConnection().update(getTableName(), contentValues, "id = ?", new String[]{Integer.toString(gender.getId())});
        }else{
            contentValues.put("createdAt", Functions.now().toString());
            contentValues.put("status", this.ACTIVE);
            result = Math.toIntExact(getDBConnection().insert(getTableName(), null, contentValues));
        }
        return (result > 0 ? true : false);
    }

    @SuppressLint("Range")
    public List<Gender> fetchAll(String status) {
        ArrayList list = new ArrayList();
        StringBuilder stringBuilderQuery = new StringBuilder();
        if(status == null) {
            stringBuilderQuery.append("SELECT * FROM " + getTableName());
        }else{
            getDBConnection().rawQuery("SELECT * FROM "+getTableName()+ " WHERE status = ?", new String[]{status});
        }
        Cursor cursor = getDBConnection().rawQuery(stringBuilderQuery.toString(), null);
        cursor.moveToFirst();
        Gender gender;
        while(!cursor.isAfterLast()){
            gender = new Gender(currentContext);
            gender.setId(cursor.getInt(cursor.getColumnIndex("id")));
            gender.setName(cursor.getString(cursor.getColumnIndex("name")));
            gender.setCreatedAt(cursor.getString(cursor.getColumnIndex("createdAt")));
            gender.setName(cursor.getString(cursor.getColumnIndex("status")));
            list.add(gender);
            cursor.moveToNext();
        }
        return list;
    }

    public Integer remove(Integer id){
        return getDBConnection().delete(getTableName(), "id = ?", new String[]{Integer.toString(id)});
    }

    @SuppressLint("Range")
    public Gender findById(Integer id){
        Cursor cursor = getDBConnection().rawQuery("SELECT * FROM aluno where id = " + id, null);
        cursor.moveToFirst();
        Gender gender = new Gender(currentContext);
        gender.setId(cursor.getInt(cursor.getColumnIndex("id")));
        gender.setName(cursor.getString(cursor.getColumnIndex("name")));
        gender.setCreatedAt(cursor.getString(cursor.getColumnIndex("createdAt")));
        gender.setName(cursor.getString(cursor.getColumnIndex("status")));
        return gender;
    }

    public String parseToString(List<Gender> listOfGenders){
        if(listOfGenders.size() < 1)
            return null;
        String result = "";
        for(Gender obj : listOfGenders){
            result += "-----";
            result += "Id: "+obj.getId()+" | Name: "+obj.getName()+" | CreatedAt: "+obj.getCreatedAt()+" | Status: "+obj.getStatus();
            result += "-----\n";
        }
        return result;
    }
}
