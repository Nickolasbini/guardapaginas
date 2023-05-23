package com.br.guardapaginas.classes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import androidx.room.Entity;

import com.br.guardapaginas.helpers.Functions;

import java.util.ArrayList;
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
        this.password = password;
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
        if(user.getInstitution() == null || user.getInstitution() < 1) {
            contentValues.put("institution", user.getUserInstitution());
        }else{
            contentValues.put("institution", user.getInstitution());
        }
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
    public List<User> fetchAll(String status, String name) {
        ArrayList list = new ArrayList();
        StringBuilder stringBuilderQuery = new StringBuilder();
        Cursor cursor = null;
        if(status == null && name == null) {
            stringBuilderQuery.append("SELECT * FROM " + getTableName() + " WHERE institution = ?");
            cursor = getDBConnection().rawQuery(stringBuilderQuery.toString(), new String[]{getUserInstitution()});
        }else if(status != null && name == null) {
            stringBuilderQuery.append("SELECT * FROM " + getTableName() + " WHERE status = ? AND institution = ?");
            System.out.println("Aqui na query status");
            cursor = getDBConnection().rawQuery(stringBuilderQuery.toString(), new String[]{status, getUserInstitution()});
        }else if(status == null && name != null) {
            String nameFormated = "%" + name + "%";
            stringBuilderQuery.append("SELECT * FROM " + getTableName() + " WHERE (name LIKE ? OR email LIKE ? OR cpf like ? OR registration LIKE ?) AND institution = ?");
            cursor = getDBConnection().rawQuery(stringBuilderQuery.toString(), new String[]{nameFormated, nameFormated, nameFormated, nameFormated, getUserInstitution()});
        }else if(status != null && name != null){
            String nameFormated = "%" + name + "%";
            stringBuilderQuery.append("SELECT * FROM " + getTableName() + " WHERE status = ? AND (name LIKE ? OR email LIKE ? OR cpf like ? OR registration LIKE ?) AND institution = ?");
            cursor = getDBConnection().rawQuery(stringBuilderQuery.toString(), new String[]{status, nameFormated, nameFormated, nameFormated, nameFormated, getUserInstitution()});
        }else{
            cursor = getDBConnection().rawQuery("SELECT * FROM "+getTableName()+ " WHERE status = ? AND institution = ?", new String[]{status, getUserInstitution()});
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

    public Boolean inactiveActiveUser(){
        if(this.getId() < 1)
            return false;
        String nextStatus = getNextStatus();
        if(nextStatus == null)
            return false;
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", nextStatus);
        Integer result = getDBConnection().update(getTableName(), contentValues, "id = ?", new String[]{Integer.toString(this.getId())});
        return (result > 0 ? true : false);
    }

    public String getUserType(){
        String status = this.getStatus();
        if(status.equals(ACTIVE_ADMINISTRATOR.toString()) || status.equals(INACTIVE_ADMINISTRATOR.toString()))
            return "ADMINISTRATOR";
        if(status.equals(ACTIVE_READER.toString()) || status.equals(INACTIVE_READER.toString()))
            return "READER";
        return "UNKNOW";
    }

    public String getNextStatus(){
        String status     = this.getStatus();
        String userType   = this.getUserType();
        if(userType.equals("READER")){
            return (status.equals(ACTIVE_READER.toString()) ? INACTIVE_READER.toString() : ACTIVE_READER.toString());
        }else if(userType.equals("ADMINISTRATOR")){
            return (status.equals(ACTIVE_READER.toString()) ? INACTIVE_READER.toString() : ACTIVE_READER.toString());
        }else{
            return null;
        }
    }

    @SuppressLint("Range")
    public User fetchByEmail(String email){
        System.out.println("Email: "+email);
        Cursor cursor = getDBConnection().rawQuery("SELECT * FROM "+getTableName()+" where email = ?", new String[]{email});
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

    public Boolean isEmailAvaliable(String email, Integer id){
        User response = this.fetchByEmail(email);
        if(response == null)
            return true;
        if(response.getId() == id)
            return true;
        return false;
    }

    public Institution getInstitutionObj(){
        Institution obj = new Institution(currentContext);
        obj = obj.getByOwner(Functions.parseToString(this.getId()));
        return obj;
    }
}
