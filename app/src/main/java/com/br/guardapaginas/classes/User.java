package com.br.guardapaginas.classes;

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

@Entity(tableName = "users")
public class User extends DBHandler{
    static final Integer INACTIVE      = 0;
    static final Integer ADMINISTRATOR = 1;
    static final Integer READER        = 2;

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "institution")
    public Integer institution;

    @ColumnInfo(name = "cpf")
    public String cpf;

    @ColumnInfo(name = "status")
    public String status;

    public User(Context context){
        super(context);
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

    public ArrayList<User> fetchAll(){
//        String query = "SELECT * FROM users";
//        Boolean response = execQuery(query);
//        ArrayList<User> emptyList = new ArrayList<User>(0);
//        if(!response)
//            return emptyList;
//        Cursor results = this.getResults();
//        Integer total  = results.getCount();
//        if(results.equals(null) && total > 0)
//            return emptyList;
//        ArrayList<User> usersArray = new ArrayList<User>(total);
//        results.moveToFirst();
//        for(Integer i = 0; i < total; i++){
//            User objFetched = buildUserByCursorResult(results);
//            usersArray.add(objFetched);
//            results.moveToNext();
//        }
//        return usersArray;
        return null;
    }

    public User findById(Integer id){
//        String query = "SELECT * FROM users WHERE id = "+id;
//        Boolean response = execQuery(query);
//        if(!response)
//            return null;
//        Cursor results = this.getResults();
//        Integer total  = results.getCount();
//        if(results.equals(null) && total > 0)
//            return null;
//        results.moveToFirst();
//        User objFetched = buildUserByCursorResult(results);
//        return objFetched;
        return null;
    }

    public ArrayList<User> findBy(String attributeName, String attributeValue, Boolean onlyFirst){
//        String query = "SELECT * FROM users WHERE "+attributeName+" = '"+attributeValue+"'";
//        Boolean response = getDBConnection.raw(query);
//        ArrayList<User> emptyList = new ArrayList<User>(0);
//        if(!response)
//            return emptyList;
//        Cursor results = this.getResults();
//        Integer total  = results.getCount();
//        if(results.equals(null) && total > 0)
//            return emptyList;
//        results.moveToFirst();
//        ArrayList<User> objects = new ArrayList<User>(total);
//        for(Integer i = 0; i < total; i++) {
//            User objFetched = buildUserByCursorResult(results);
//            objects.add(objFetched);
//            results.moveToNext();
//            if(onlyFirst)
//                break;
//        }
//        return objects;
        return null;
    }

    public User buildUserByCursorResult(Cursor data){
        User obj = new User(currentContext);
        for(String attribute : this.getFillable()){
            Integer position = data.getColumnIndex(attribute);
            String value = data.getString(position);
            switch(attribute){
                case "id":
                    obj.setId(Integer.parseInt(value));
                    break;
                case "name":
                    obj.setName(value);
                    break;
                case "email":
                    obj.setEmail(value);
                    break;
                case "password":
                    obj.setPassword(value);
                    break;
                case "cpf":
                    obj.setCpf(value);
                    break;
                case "status":
                    obj.setStatus(value);
                    break;
                case "institution":
                    if(value == null){
                        obj.setInstitution(null);
                    }else{
                        obj.setInstitution(Integer.parseInt(value));
                    }
                    break;
            }
        }
        return obj;
    }
}
