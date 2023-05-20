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

public class Gender extends DBHandler{
    public final String INACTIVE = "0";
    public final String ACTIVE   = "1";

    public int id;

    public String name;

    public String createdAt;

    public String institution;

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

    public String getInstitution() {
        return institution;
    }
    public void setInstitution(String institution) {
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
            contentValues.put("createdAt", Functions.getNowDate());
            contentValues.put("status", this.ACTIVE);
            result = Math.toIntExact(getDBConnection().insert(getTableName(), null, contentValues));
        }
        return (result > 0 ? true : false);
    }

    @SuppressLint("Range")
    public List<Gender> fetchAll(String status) {
        ArrayList list = new ArrayList();
        StringBuilder stringBuilderQuery = new StringBuilder();
        Cursor cursor = null;
        if(status == null) {
            stringBuilderQuery.append("SELECT * FROM " + getTableName());
            cursor = getDBConnection().rawQuery(stringBuilderQuery.toString(), null);
        }else{
            cursor = getDBConnection().rawQuery("SELECT * FROM "+getTableName()+ " WHERE status = ?", new String[]{status});
        }
        cursor.moveToFirst();
        Gender gender;
        while(!cursor.isAfterLast()){
            gender = new Gender(currentContext);
            gender.setId(cursor.getInt(cursor.getColumnIndex("id")));
            gender.setName(cursor.getString(cursor.getColumnIndex("name")));
            gender.setCreatedAt(cursor.getString(cursor.getColumnIndex("createdAt")));
            gender.setStatus(cursor.getString(cursor.getColumnIndex("status")));
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
        Cursor cursor = getDBConnection().rawQuery("SELECT * FROM "+getTableName()+" where id = " + id, null);
        cursor.moveToFirst();
        Gender gender = new Gender(currentContext);
        gender.setId(cursor.getInt(cursor.getColumnIndex("id")));
        gender.setName(cursor.getString(cursor.getColumnIndex("name")));
        gender.setCreatedAt(cursor.getString(cursor.getColumnIndex("createdAt")));
        gender.setName(cursor.getString(cursor.getColumnIndex("status")));
        return gender;
    }

    @SuppressLint("Range")
    public List<Gender> getGendersByStatusWithoutThisIds(String status, String ids){
        if(status == null)
            status = "1";
        ArrayList list = new ArrayList();
        StringBuilder stringBuilderQuery = new StringBuilder();
        Cursor cursor = getDBConnection().rawQuery("SELECT * FROM "+getTableName()+ " WHERE status = ? AND ID NOT IN("+ids+") AND (institution IS NULL OR institution = ?)", new String[]{status, getUserInstitution()});
        cursor.moveToFirst();
        Gender gender;
        while(!cursor.isAfterLast()){
            gender = new Gender(currentContext);
            gender.setId(cursor.getInt(cursor.getColumnIndex("id")));
            gender.setName(cursor.getString(cursor.getColumnIndex("name")));
            gender.setCreatedAt(cursor.getString(cursor.getColumnIndex("createdAt")));
            gender.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            list.add(gender);
            cursor.moveToNext();
        }
        return list;
    }

    @SuppressLint("Range")
    public List<Gender> fetchByName(String name){
        ArrayList list = new ArrayList();
        StringBuilder stringBuilderQuery = new StringBuilder();
        Cursor cursor = getDBConnection().rawQuery("SELECT * FROM "+getTableName()+ " WHERE name = ?", new String[]{name});
        cursor.moveToFirst();
        Gender gender;
        while(!cursor.isAfterLast()){
            gender = new Gender(currentContext);
            gender.setId(cursor.getInt(cursor.getColumnIndex("id")));
            gender.setName(cursor.getString(cursor.getColumnIndex("name")));
            gender.setCreatedAt(cursor.getString(cursor.getColumnIndex("createdAt")));
            gender.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            list.add(gender);
            cursor.moveToNext();
        }
        return list;
    }

    @SuppressLint("Range")
    public List<Gender> getByIds(String[] ids){
        String idsString = Functions.implode(ids, ",");
        ArrayList list   = new ArrayList();
        Cursor cursor   = getDBConnection().rawQuery("SELECT * FROM "+getTableName()+ " WHERE id IN ("+idsString+") AND (institution IS NULL OR institution = ?)", new String[]{getUserInstitution()});
        cursor.moveToFirst();
        Gender gender;
        while(!cursor.isAfterLast()){
            gender = new Gender(currentContext);
            gender.setId(cursor.getInt(cursor.getColumnIndex("id")));
            gender.setName(cursor.getString(cursor.getColumnIndex("name")));
            gender.setCreatedAt(cursor.getString(cursor.getColumnIndex("createdAt")));
            gender.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            list.add(gender);
            cursor.moveToNext();
        }
        return list;
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
