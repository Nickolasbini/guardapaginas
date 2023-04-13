package com.br.guardapaginas.classes.controller;

import android.content.Context;

import com.br.guardapaginas.classes.Book;

import java.util.ArrayList;

public class BookController {

    Context currentContext;

    public BookController(Context context){
        currentContext = context;
    }

    public ArrayList<Book> getBooks(){
        ArrayList<Book> a = new ArrayList<Book>();
        return a;
    }

}
