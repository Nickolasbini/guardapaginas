package com.br.guardapaginas.classes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Base64;

import com.br.guardapaginas.helpers.Functions;
import com.br.guardapaginas.views.APIListOfBooks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private String quantity;

    private String editorName;

    private String status;

    private String[] genders;

    private String bookLanguage;

    private String numberOfPages;

    private byte[] bookCover;

    public String genderStringArray;

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

    public String getQuantity() { return quantity; };
    public void setQuantity(String quantity){ this.quantity = quantity; }

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
        String[] avaliableLanguages = {"pt", "en", "es"};
        Boolean isSupportedLanguage = false;
        for(Integer i = 0; i < avaliableLanguages.length; i++){
            if(avaliableLanguages[i].equals(bookLanguage))
                isSupportedLanguage = true;
        }
        this.bookLanguage = (isSupportedLanguage ? bookLanguage : "pt");
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

    public String getGenderStringArray() {
        return genderStringArray;
    }
    public void setGenderStringArray(String genderStringArray) {
        this.genderStringArray = genderStringArray;
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
        contentValues.put("quantity", book.getQuantity());
        contentValues.put("institution", getUserInstitution());
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
        book.setId(result);
        BookGenders bookGendersObj = new BookGenders(currentContext);
        return bookGendersObj.updateBookGender(Functions.parseToString(book.getId()), book.getGenders());
    }

    @SuppressLint("Range")
    public List<Book> fetchAll(String status, String name) {
        ArrayList list = new ArrayList();
        StringBuilder stringBuilderQuery = new StringBuilder();
        Cursor cursor = null;
        if(status == null && name == null) {
            stringBuilderQuery.append("SELECT * FROM " + getTableName() + " WHERE (institution IS NULL OR institution = ?) ORDER BY title");
            cursor = getDBConnection().rawQuery(stringBuilderQuery.toString(), new String[]{getUserInstitution()});
        }else if(status != null && name == null) {
            cursor = getDBConnection().rawQuery("SELECT * FROM " + getTableName() + " WHERE status = ? AND (institution IS NULL OR institution = ?) ORDER BY title", new String[]{status, getUserInstitution()});
        }else if(status == null && name != null){
            String likeParam = "%"+name+"%";
            cursor = getDBConnection().rawQuery("SELECT * FROM " + getTableName() + " WHERE title LIKE ? AND (institution IS NULL OR institution = ?) ORDER BY title", new String[]{likeParam, getUserInstitution()});
        }else{
            String likeParam = "%"+name+"%";
            cursor = getDBConnection().rawQuery("SELECT * FROM "+getTableName()+ " WHERE status = ? AND title LIKE ? AND (institution IS NULL OR institution = ?) ORDER BY title", new String[]{status, likeParam, getUserInstitution()});
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
            book.setQuantity(cursor.getString(cursor.getColumnIndex("quantity")));
            book.setBookCover(cursor.getBlob(cursor.getColumnIndex("bookCover")));
            list.add(book);
            cursor.moveToNext();
        }
        return list;
    }

    @SuppressLint("Range")
    public List<Book> listPaginated(String status, String search, Integer page){
        page                  = (page == null ? 1 : page);
        ArrayList list        = new ArrayList();
        Cursor cursor         = null;
        search                = (search == null ? "" : search);
        String likeParam      = "%"+search+"%";
        cursor                = getDBConnection().rawQuery("SELECT * FROM " + getTableName() + " WHERE status = ? AND title LIKE ?", new String[]{status, likeParam});
        Integer total         = cursor.getCount();
        Integer perPage       = 10;
        perPage               = 10 * page;
        cursor                = getDBConnection().rawQuery("SELECT * FROM " + getTableName() + " WHERE status = ? AND title LIKE ? LIMIT ?", new String[]{status, likeParam, Integer.toString(perPage)});
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
            book.setQuantity(cursor.getString(cursor.getColumnIndex("quantity")));
            book.setBookCover(cursor.getBlob(cursor.getColumnIndex("bookCover")));
            list.add(book);
            cursor.moveToNext();
        }
        System.out.println("Numero de resultados: "+cursor.getCount());
        System.out.println("Pagina "+page);
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
        book.setQuantity(cursor.getString(cursor.getColumnIndex("quantity")));
        book.setBookCover(cursor.getBlob(cursor.getColumnIndex("bookCover")));
        return book;
    }

    public String parseListToString(List<Book> listOfBooks){
        if(listOfBooks.size() < 1)
            return null;
        String result = "";
        for(Book obj : listOfBooks){
            result += parseToString(obj);
        }
        return result;
    }

    public String parseToString(Book obj){
        String result = "-----";
        result += "Id: "+obj.getId()+" | Title: "+obj.getTitle()+" | Synopsis: "+obj.getSynopsis()+" | Author: "+obj.getAuthor() + " | Cover: "+obj.getBookCover() + " | NumberOfPages: "+obj.getNumberOfPages() + " | ReleaseDate: "+obj.getReleaseDate();
        result += "-----\n";
        return result;
    }

    @SuppressLint("Range")
    public String getGendersName(){
        String gendersString = "";
        StringBuilder stringBuilderQuery = new StringBuilder();
        stringBuilderQuery.append("SELECT g.name AS genderName FROM bookGenders AS bg LEFT JOIN genders AS g ON g.id = bg.gender WHERE bg.book = "+getId());
        Cursor cursor = getDBConnection().rawQuery(stringBuilderQuery.toString(), null);
        cursor.moveToFirst();
        System.out.println("Tamanho cursor: "+cursor.getCount());
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
        if(language == null)
            return 0;
        switch(language){
            case "pt":
                language = "Português";
            break;
            case "en":
                language = "Inglês";
            break;
            case "es":
                language = "Espanhol";
            break;
        }
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
        System.out.println("A data: "+this.getReleaseDate());
        if(this.getReleaseDate() == null)
            return "";
        return this.getReleaseDate();
    }

    public Boolean saveGenders(String[] gendersId){
        BookGenders bg = new BookGenders(currentContext);
        return bg.updateBookGender(Functions.parseToString(this.getId()), gendersId);
    }

    public String getQuantityAsNumber(){
        String quantity = this.getQuantity();
        if(quantity == null || quantity.equals(""))
            return "0";
        return quantity;
    }

    public List<String> gatherStatistics(){
        List<String> response = new ArrayList<String>(3);
        Cursor totalBooks     = getDBConnection().rawQuery("SELECT * FROM " +getTableName() + " WHERE institution = ? AND status = ?", new String[]{getUserInstitution(), ACTIVE});
        response.add(Integer.toString(totalBooks.getCount()));
        Cursor totalBorrowed  = getDBConnection().rawQuery("SELECT * FROM " + getTableName() + " AS b LEFT JOIN bookBorrowings AS bb WHERE b.institution = ? AND bb.status = ?", new String[]{getUserInstitution(), ACTIVE});
        response.add(Integer.toString(totalBorrowed.getCount()));
        Cursor totalDelayed   = getDBConnection().rawQuery("SELECT * FROM " + getTableName() + " AS b LEFT JOIN bookBorrowings AS bb WHERE b.institution = ? AND bb.status = ? AND expectedDelivery < ?", new String[]{getUserInstitution(), ACTIVE, Functions.getNowDate()});
        response.add(Integer.toString(totalDelayed.getCount()));
        return response;
    }

    public Boolean inactiveActiveBook(){
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

    public String getNextStatus(){
        String status = this.getStatus();
        return (status.equals(ACTIVE) ? INACTIVE : ACTIVE);
    }

    public String parseToJSON(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", this.getTitle());
            jsonObject.put("editorName", this.getEditorName());
            jsonObject.put("releaseDate", this.getReleaseDate());
            jsonObject.put("synopsis", this.getSynopsis());
            jsonObject.put("bookLanguage", this.getBookLanguage());
            jsonObject.put("numberOfPages", this.getNumberOfPages());
            jsonObject.put("author", this.getAuthor());
            jsonObject.put("bookCover", Base64.encodeToString(this.getBookCover(), Base64.NO_WRAP));
            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }
}
