package com.br.guardapaginas.classes;

import android.content.ContentValues;
import android.content.Context;

public class BookGenders extends DBHandler{
    public BookGenders(Context context){
        super(context);
        setTableName("bookGenders");
    }

    public Boolean updateBookGender(String bookId, String[] genderIdsArray){
        clearBookGenders(bookId);
        System.out.println("gendersId: "+genderIdsArray.length);
        if(genderIdsArray == null)
            return false;
        for(Integer i = 0; i < genderIdsArray.length; i++) {
            System.out.println("Dados: "+genderIdsArray[i]);
            ContentValues contentValues = new ContentValues();
            contentValues.put("book", bookId);
            contentValues.put("gender", genderIdsArray[i]);
            Integer result = Math.toIntExact(getDBConnection().insert(getTableName(), null, contentValues));
            System.out.println("Resultado:: "+result);
            if(result < 1)
                return false;
        }
        return true;
    }

    public Boolean clearBookGenders(String bookId){
        Integer response = getDBConnection().delete(getTableName(), "book = ?", new String[]{bookId});
        return response > 0 ? true : false;
    }
}
