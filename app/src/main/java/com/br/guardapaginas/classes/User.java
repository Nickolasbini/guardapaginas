package com.br.guardapaginas.classes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import java.util.Base64;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.br.guardapaginas.helpers.Functions;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Entity(tableName = "users")
public class User extends DBHandler{
    public static final Integer INACTIVE_ADMINISTRATOR = 0;
    public static final Integer ACTIVE_ADMINISTRATOR   = 1;
    public static final Integer ACTIVE_READER          = 2;
    public static final Integer INACTIVE_READER        = 3;

    public int id;
    public String name;
    public String cpf;
    public String email;
    public String password;
    public String status;
    public String registration;
    public Integer institution;

    public User(Context context){
        super(context);
        setTableName("users");
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

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = Functions.hashMake(password);
    }

    public Integer getInstitution() {
        return institution;
    }
    public void setInstitution(Integer institution) {
        this.institution = institution;
    }

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRegistration() {
        return registration;
    }
    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getFillable(){
        String[] fillable = {"id", "name", "cpf", "email", "password", "status", "institution"};
        return fillable;
    }

    public Integer save(User user, String typeOfUser){
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", user.getName());
        contentValues.put("cpf", user.getCpf());
        contentValues.put("email", user.getEmail());
        contentValues.put("password", user.getPassword());
        contentValues.put("registration", user.getRegistration());
        contentValues.put("institution", user.getInstitution());
        Integer result = 0;
        if(user.getId() > 0) {
            contentValues.put("status", user.getStatus());
            result = getDBConnection().update(getTableName(), contentValues, "id = ?", new String[]{Integer.toString(user.getId())});
        }else{
            String activeStatus = (typeOfUser.equals("ADMIN") ? ACTIVE_ADMINISTRATOR.toString() : ACTIVE_READER.toString());
            contentValues.put("status", activeStatus);
            result = Math.toIntExact(getDBConnection().insert(getTableName(), null, contentValues));
        }
        return result;
    }

    public Integer saveAdmin(){
        return this.save(this, "ADMIN");
    }

    public Integer saveReader(){
        return this.save(this, "READER");
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
        User user;
        while(!cursor.isAfterLast()){
            user = new User(currentContext);
            user.setId(cursor.getInt(cursor.getColumnIndex("id")));
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
            user.setCpf(cursor.getString(cursor.getColumnIndex("cpf")));
            user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            user.setRegistration(cursor.getString(cursor.getColumnIndex("registration")));
            user.setInstitution(cursor.getInt(cursor.getColumnIndex("institution")));
            list.add(user);
            cursor.moveToNext();
        }
        return list;
    }

    public Integer remove(Integer id){
        return getDBConnection().delete(getTableName(), "id = ?", new String[]{Integer.toString(id)});
    }

    @SuppressLint("Range")
    public User findById(Integer id){
        Cursor cursor = getDBConnection().rawQuery("SELECT * FROM "+getTableName()+" where id = " + id, null);
        cursor.moveToFirst();
        User user = null;
        if(cursor.getCount() > 0) {
            user = new User(currentContext);
            user.setId(cursor.getInt(cursor.getColumnIndex("id")));
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
            user.setCpf(cursor.getString(cursor.getColumnIndex("cpf")));
            user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            user.setRegistration(cursor.getString(cursor.getColumnIndex("registration")));
            user.setInstitution(cursor.getInt(cursor.getColumnIndex("institution")));
        }
        return user;
    }
}
