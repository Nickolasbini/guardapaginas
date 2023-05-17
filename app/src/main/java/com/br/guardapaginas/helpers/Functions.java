package com.br.guardapaginas.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
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

    public static Integer parseToInteger(String val){
        if(val == null)
            return 0;
        return Integer.parseInt(val);
    }

    public static String parseToString(Integer val){
        if(val == null)
            return "0";
        return String.valueOf(val);
    }

    public static Bitmap parseByteArrayToBitMap(byte[] binary){
        Bitmap result = BitmapFactory.decodeByteArray(binary, 0, binary.length);
        return result;
    }

    public static String formatDate(String dateString) {
        System.out.println("A data enviada: "+dateString);
        if (dateString == null)
            return "";
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        return format.format(Date.parse(dateString));
    }

    public static String parsePtDateToEn(String ptDate){
        String[] array        = Functions.explode(ptDate, "-");
        String enFormatedDate = array[2] + "-" + array[1] + "-" + array[0];
        return enFormatedDate;
    }

    public static String implode(String[] array, String glue){
        if(array == null || array.length < 1)
            return "";
        if(glue == null)
            glue = "!@!";
        String val    = "";
        Integer total = array.length;
        Integer last  = total - 1;
        for(Integer i = 0; i < total; i++){
            if(i == last){
                val += array[i];
            }else{
                val += array[i] + glue;
            }
        }
        return val;
    }

    public static String[] explode(String value, String glue){
        if(glue == null)
            glue = "!@!";
        String[] array = value.split(glue);
        return array;
    }
}
