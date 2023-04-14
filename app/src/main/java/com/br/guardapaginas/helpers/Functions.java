package com.br.guardapaginas.helpers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Functions {
    public static Date now(){
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime;
    }

    public static String ddArray(String[] array){
        String dataToReturn = "";
        for(Integer i = 0; i < array.length; i++){
            dataToReturn += "[";
            dataToReturn += array[i];
            dataToReturn += "]";
        }
        return dataToReturn;
    }

    public static String ucfirst(String val){
        if (val == null || val.trim().isEmpty()) {
            return val;
        }
        char c[] = val.trim().toLowerCase().toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }

    public static String getNowDate() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        return timeStamp;
    }

    public static String hashMake(String value){
        String generatedPassword = null;
        try{
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            // Add password bytes to digest
            md.update(value.getBytes());
            // Get the hash's bytes
            byte[] bytes = md.digest();
            // This bytes[] has bytes in decimal format. Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            // Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public static Boolean hashCheck(String hashed, String nonHashed){
        if(!hashed.equals(Functions.hashMake(nonHashed)))
            return false;
        return true;
    }
}
