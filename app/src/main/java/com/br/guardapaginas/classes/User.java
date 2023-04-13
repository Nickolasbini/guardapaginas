package com.br.guardapaginas.classes;

import android.content.Context;
import android.database.Cursor;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    String[] salt = {"e38183138#", "-q18283138189"};

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
        this.password = hashMake(password);
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
        String[] fillable = {"name", "cpf", "email", "password", "status", "institution"};
        return fillable;
    }

    public Boolean findByEmail(String email){

        return true;
    }
    public String hashMake(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            String generatedHash = hexString.toString();
            System.out.println("Hashed Salt: "+generatedHash);
            generatedHash = salt[0]+generatedHash+salt[1];
            return generatedHash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Boolean hashCheck(String hashed, String nonHashed){
        if(!hashed.equals(hashMake(nonHashed)))
            return false;
        return true;
    }

    public Boolean saveUser(User obj){
        String query = "";
        if(obj.getId() > 0){
            query = "UPDATE user SET name = "+obj.getName()+", email = "+obj.getEmail()+", password = "+obj.getPassword()+", institution = "+obj.getInstitution()+" WHERE id = "+obj.getId();
        }else{
            query = "INSERT into users (name, email, password, institution) VALUES ('"+obj.getName()+"','"+obj.getEmail()+"','"+obj.getPassword()+"',"+obj.getInstitution()+")";
        }
        User val = getByObject(obj.getEmail());
        System.out.println("Obj resultado: "+val.getId()+"  Name: " + val.getName());
        return execQuery(query);
    }

    public User getByObject(String email){
        String query = "SELECT * FROM users where email = '"+email+"'";
        Boolean response = execQuery(query);
        if(!response)
            return null;
        Cursor result = getResults();
        if(result.getCount() < 1)
            return null;
        result.moveToFirst();
        User obj = new User(currentContext);
        for(Integer i = 0; i < 1; i++){
            for(String attribute : this.getFillable()){
                Integer position = result.getColumnIndex(attribute);
                if(position < 1)
                    continue;
                String value = result.getString(position);
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
                        obj.setInstitution(Integer.parseInt(value));
                    break;
                }
            }
        }
        return obj;
    }

}
