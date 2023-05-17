package com.br.guardapaginas.classes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

import com.br.guardapaginas.helpers.Functions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Book extends DBHandler{
    public final String INACTIVE = "0";
    public final String ACTIVE   = "1";

    private int id;

    private String title;

    private String synopsis;

    private String author;

    private String releaseDate;

    private String editorName;

    private String status;

    private String[] genders;

    private String bookLanguage;

    private String numberOfPages;

    private byte[] bookCover;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynopsis() {
        return synopsis;
    }
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getEditorName() {
        return editorName;
    }
    public void setEditorName(String editorName) {
        this.editorName = editorName;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getGenders() {
        return genders;
    }
    public void setGenders(String[] genders) {
        this.genders = genders;
    }

    public String getBookLanguage() {
        return bookLanguage;
    }
    public void setBookLanguage(String bookLanguage) {
        this.bookLanguage = bookLanguage;
    }

    public String getNumberOfPages() {
        return numberOfPages;
    }
    public void setNumberOfPages(String numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public byte[] getBookCover() {
        return bookCover;
    }
    public void setBookCover(byte[] bookCover) {
        this.bookCover = bookCover;
    }

    public String attributes[] = {"id","title","synopsis","author","releaseDate","editorName","status","gender","bookLanguage","numberOfPages"};

    public Book(Context context){
        super(context);
        setTableName("books");
        setAttributesFromModel(attributes);
    }

    public Boolean save(Book book){
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", book.getTitle());
        contentValues.put("synopsis", book.getSynopsis());
        contentValues.put("author", book.getAuthor());
        contentValues.put("releaseDate", book.getReleaseDate());
        contentValues.put("editorName", book.getEditorName());
        contentValues.put("bookLanguage", book.getBookLanguage());
        contentValues.put("numberOfPages", book.getNumberOfPages());
        contentValues.put("bookCover", book.getBookCover());
        Integer result = 0;
        if(book.getId() > 0) {
            result = getDBConnection().update(getTableName(), contentValues, "id = ?", new String[]{Integer.toString(book.getId())});
        }else{
            contentValues.put("status", this.ACTIVE);
            result = Math.toIntExact(getDBConnection().insert(getTableName(), null, contentValues));
        }
        Boolean saveResult = (result > 0 ? true : false);
        if(!saveResult)
            return false;
        BookGenders bookGendersObj = new BookGenders(currentContext);
        return bookGendersObj.updateBookGender(Functions.parseToString(book.getId()), book.getGenders());
    }

    @SuppressLint("Range")
    public List<Book> fetchAll(String status) {
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
        Book book;
        while(!cursor.isAfterLast()){
            book = new Book(currentContext);
            book.setId(cursor.getInt(cursor.getColumnIndex("id")));
            book.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            book.setSynopsis(cursor.getString(cursor.getColumnIndex("synopsis")));
            book.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
            book.setReleaseDate(cursor.getString(cursor.getColumnIndex("releaseDate")));
            book.setEditorName(cursor.getString(cursor.getColumnIndex("editorName")));
            book.setStatus(cursor.getString(cursor.getColumnIndex("status")));
//            book.setGender(cursor.getInt(cursor.getColumnIndex("gender")));
            book.setBookLanguage(cursor.getString(cursor.getColumnIndex("bookLanguage")));
            book.setNumberOfPages(cursor.getString(cursor.getColumnIndex("numberOfPages")));
            book.setBookCover(cursor.getBlob(cursor.getColumnIndex("bookCover")));
            list.add(book);
            System.out.println("ID do livro  "+book.getId());
            cursor.moveToNext();
        }
        return list;
    }

    public Integer remove(Integer id){
        return getDBConnection().delete(getTableName(), "id = ?", new String[]{Integer.toString(id)});
    }

    @SuppressLint("Range")
    public Book findById(Integer id){
        Cursor cursor = getDBConnection().rawQuery("SELECT * FROM "+getTableName()+" where id = ?",new String[]{Integer.toString(id)});
        cursor.moveToFirst();
        System.out.println("Quantidade achada  "+cursor.getCount());
        Book book = new Book(currentContext);
        book.setId(cursor.getInt(cursor.getColumnIndex("id")));
        book.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        book.setSynopsis(cursor.getString(cursor.getColumnIndex("synopsis")));
        book.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
        book.setReleaseDate(cursor.getString(cursor.getColumnIndex("releaseDate")));
        book.setEditorName(cursor.getString(cursor.getColumnIndex("editorName")));
        book.setStatus(cursor.getString(cursor.getColumnIndex("status")));
//        book.setGender(cursor.getInt(cursor.getColumnIndex("gender")));
        book.setBookLanguage(cursor.getString(cursor.getColumnIndex("bookLanguage")));
        book.setNumberOfPages(cursor.getString(cursor.getColumnIndex("numberOfPages")));
        book.setBookCover(cursor.getBlob(cursor.getColumnIndex("bookCover")));
        return book;
    }

    public String parseToString(List<Book> listOfBooks){
        if(listOfBooks.size() < 1)
            return null;
        String result = "";
        for(Book obj : listOfBooks){
            result += "-----";
            result += "Id: "+obj.getId()+" | Title: "+obj.getTitle()+" | Synopsis: "+obj.getSynopsis()+" | Author: "+obj.getAuthor();
            result += "-----\n";
        }
        return result;
    }

    @SuppressLint("Range")
    public String getGendersName(){
        String gendersString = "";
        StringBuilder stringBuilderQuery = new StringBuilder();
        stringBuilderQuery.append("SELECT g.name AS genderName FROM bookGenders AS bg LEFT JOIN genders AS g ON g.id = bg.gender WHERE bg.book = "+getId());
        Cursor cursor = getDBConnection().rawQuery(stringBuilderQuery.toString(), null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            String genderName = cursor.getString(cursor.getColumnIndex("genderName"));
            if(cursor.isLast()){
                gendersString += " " + genderName;
            }else {
                gendersString += " " + genderName + " |";
            }
            cursor.moveToNext();
        }
        return gendersString;
    }

    @SuppressLint("Range")
    public List<String[]> getGendersIdsAndNames(){
        List<String[]> results = new ArrayList<String[]>();
        StringBuilder stringBuilderQuery = new StringBuilder();
        stringBuilderQuery.append("SELECT g.id AS genderId, g.name AS genderName FROM bookGenders AS bg LEFT JOIN genders AS g ON g.id = bg.gender WHERE bg.book = "+this.getId());
        Cursor cursor = getDBConnection().rawQuery(stringBuilderQuery.toString(), null);
        cursor.moveToFirst();
        String ids   = "";
        String names = "";
        while(!cursor.isAfterLast()){
            String genderId   = cursor.getString(cursor.getColumnIndex("genderId"));
            String genderName = cursor.getString(cursor.getColumnIndex("genderName"));
            if(cursor.isLast()){
                ids   += genderId;
                names += genderName;
            }else {
                ids   += genderId   + ",";
                names += genderName + ",";
            }
            cursor.moveToNext();
        }
        results.add(Functions.explode(ids,   ","));
        results.add(Functions.explode(names, ","));
        return results;
    }

    public String[] getAvaliableLanguages(){
        return new String[]{"Português","Inglês","Espanhol"};
    }

    public Integer getLanguagePosition(){
        String[] languageItems = this.getAvaliableLanguages();
        String language        = this.getBookLanguage();
        System.out.println("Minha linguagem " + language);
        Integer position       = null;
        for(Integer i = 0; i < languageItems.length; i++){
            if(languageItems[i].equals(language)) {
                position = i;
                break;
            }
        }
        return position;
    }

    public String getFormatedReleasedDate(){
        if(this.getReleaseDate() == null)
            return "";
        return this.getReleaseDate();
    }

    public Boolean saveGenders(String[] gendersId){
        BookGenders bg = new BookGenders(currentContext);
        return bg.updateBookGender(Functions.parseToString(this.getId()), gendersId);
    }
}
