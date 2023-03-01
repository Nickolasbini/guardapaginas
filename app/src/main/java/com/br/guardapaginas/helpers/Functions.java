package com.br.guardapaginas.helpers;

import java.util.Calendar;
import java.util.Date;

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
}
