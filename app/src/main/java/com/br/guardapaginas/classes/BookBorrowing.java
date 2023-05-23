package com.br.guardapaginas.classes;

import android.content.Context;

public class BookBorrowing extends DBHandler{
    public final String INACTIVE = "0";
    public final String ACTIVE   = "1";

    public BookBorrowing(Context context){
        super(context);
        setTableName("bookBorrowings");
    }

}
