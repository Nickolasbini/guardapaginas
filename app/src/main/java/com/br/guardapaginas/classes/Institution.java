package com.br.guardapaginas.classes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.br.guardapaginas.helpers.Functions;

import java.util.ArrayList;
import java.util.List;

public class Institution extends DBHandler{

    public static final Integer ACTIVE   = 0;
    public static final Integer INACTIVE = 1;

    public Institution(Context context){
        super(context);
        setTableName("institutions");
    }

    private int id;
    private String name;
    private String address;
    private String number;
    private String telephone;
    private String email;
    private String status;
    private int    owner;

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

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }

    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public int getOwner() {
        return owner;
    }
    public void setOwner(int owner) {
        this.owner = owner;
    }

    public Boolean save(){
        Institution institution = this;
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", institution.getName());
        contentValues.put("address", institution.getAddress());
        contentValues.put("number", institution.getNumber());
        contentValues.put("telephone", institution.getTelephone());
        contentValues.put("email", institution.getEmail());
        contentValues.put("owner", institution.getOwner());
        Integer result = 0;
        if(institution.getId() > 0) {
            contentValues.put("status", institution.getStatus());
            result = getDBConnection().update(getTableName(), contentValues, "id = ?", new String[]{Integer.toString(institution.getId())});
        }else{
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
        Institution institution;
        while(!cursor.isAfterLast()){
            institution = new Institution(currentContext);
            institution.setId(cursor.getInt(cursor.getColumnIndex("id")));
            institution.setName(cursor.getString(cursor.getColumnIndex("name")));
            institution.setAddress(cursor.getString(cursor.getColumnIndex("address")));
            institution.setNumber(cursor.getString(cursor.getColumnIndex("number")));
            institution.setTelephone(cursor.getString(cursor.getColumnIndex("telephone")));
            institution.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            institution.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            institution.setOwner(cursor.getInt(cursor.getColumnIndex("owner")));
            list.add(institution);
            cursor.moveToNext();
        }
        return list;
    }

    public Integer remove(Integer id){
        return getDBConnection().delete(getTableName(), "id = ?", new String[]{Integer.toString(id)});
    }

    @SuppressLint("Range")
    public Institution findById(Integer id){
        Cursor cursor = getDBConnection().rawQuery("SELECT * FROM "+getTableName()+" where id = " + id, null);
        cursor.moveToFirst();
        Institution institution = null;
        if(cursor.getCount() > 0) {
            institution = new Institution(currentContext);
            institution.setId(cursor.getInt(cursor.getColumnIndex("id")));
            institution.setName(cursor.getString(cursor.getColumnIndex("name")));
            institution.setAddress(cursor.getString(cursor.getColumnIndex("address")));
            institution.setNumber(cursor.getString(cursor.getColumnIndex("number")));
            institution.setTelephone(cursor.getString(cursor.getColumnIndex("telephone")));
            institution.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            institution.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            institution.setOwner(cursor.getInt(cursor.getColumnIndex("owner")));
        }
        return institution;
    }

    @SuppressLint("Range")
    public Institution getByOwner(String ownerId){
        if(ownerId == null)
            return null;
        Cursor cursor = getDBConnection().rawQuery("SELECT * FROM "+getTableName()+" where owner = ?", new String[]{ownerId});
        cursor.moveToFirst();
        Institution institution = null;
        if(cursor.getCount() > 0) {
            institution = new Institution(currentContext);
            institution.setId(cursor.getInt(cursor.getColumnIndex("id")));
            institution.setName(cursor.getString(cursor.getColumnIndex("name")));
            institution.setAddress(cursor.getString(cursor.getColumnIndex("address")));
            institution.setNumber(cursor.getString(cursor.getColumnIndex("number")));
            institution.setTelephone(cursor.getString(cursor.getColumnIndex("telephone")));
            institution.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            institution.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            institution.setOwner(cursor.getInt(cursor.getColumnIndex("owner")));
        }
        return institution;
    }
}
