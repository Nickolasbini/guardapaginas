package com.br.guardapaginas.classes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.br.guardapaginas.helpers.Functions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookBorrowing extends DBHandler{
    public final String INACTIVE = "0";
    public final String ACTIVE   = "1";

    public BookBorrowing(Context context){
        super(context);
        setTableName("bookBorrowings");
    }

    public int id;
    public int book;
    public int institution;
    public String lendDate;
    public String expectedDelivery;
    public String status;
    public String realDelivery;
    public int reader;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getBook() {
        return book;
    }
    public void setBook(int book) {
        this.book = book;
    }

    public int getInstitution() {
        return institution;
    }
    public void setInstitution(int institution) {
        this.institution = institution;
    }

    public String getLendDate() {
        return lendDate;
    }
    public void setLendDate(String lendDate) {
        this.lendDate = lendDate;
    }

    public String getExpectedDelivery() {
        return expectedDelivery;
    }
    public void setExpectedDelivery(String expectedDelivery) {
        this.expectedDelivery = expectedDelivery;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getRealDelivery() {
        return realDelivery;
    }
    public void setRealDelivery(String realDelivery) {
        if(realDelivery.length() < 1)
            realDelivery = null;
        this.realDelivery = realDelivery;
    }

    public int getReader() {
        return reader;
    }
    public void setReader(int reader) {
        this.reader = reader;
    }

    @SuppressLint("Range")
    public List<String> getBookBorrowData(){
        List<String> data = new ArrayList<>(4);
        String query      = "SELECT u.name as readerName, u.email as readerEmail, b.title as bookName, bb.lendDate as borrowDate FROM "+getTableName()+" as bb LEFT JOIN users as u on u.id = bb.reader LEFT JOIN books as b on b.id = bb.book WHERE bb.id = ?";
        Cursor cursor     = getDBConnection().rawQuery(query, new String[]{Functions.parseToString(this.getId())});
        if(cursor.getCount() < 1)
            return data;
        cursor.moveToNext();
        data.add(cursor.getString(cursor.getColumnIndex("readerName")));
        data.add(cursor.getString(cursor.getColumnIndex("readerEmail")));
        data.add(cursor.getString(cursor.getColumnIndex("bookName")));
        data.add(cursor.getString(cursor.getColumnIndex("borrowDate")));
        return data;
    }

    @SuppressLint("Range")
    public BookBorrowing findById(Integer id){
        Cursor cursor = getDBConnection().rawQuery("SELECT * FROM "+getTableName()+" where id = ?",new String[]{Integer.toString(id)});
        cursor.moveToFirst();
        BookBorrowing bookBorrowing = new BookBorrowing(currentContext);
        bookBorrowing.setId(cursor.getInt(cursor.getColumnIndex("id")));
        bookBorrowing.setBook(cursor.getInt(cursor.getColumnIndex("book")));
        bookBorrowing.setInstitution(cursor.getInt(cursor.getColumnIndex("institution")));
        bookBorrowing.setLendDate(cursor.getString(cursor.getColumnIndex("lendDate")));
        bookBorrowing.setExpectedDelivery(cursor.getString(cursor.getColumnIndex("expectedDelivery")));
        bookBorrowing.setStatus(cursor.getString(cursor.getColumnIndex("status")));
        bookBorrowing.setRealDelivery(cursor.getString(cursor.getColumnIndex("realDelivery")));
        bookBorrowing.setReader(cursor.getInt(cursor.getColumnIndex("reader")));
        return bookBorrowing;
    }

    public List<String[]> getDataForSpinner(){
        List<String[]> data = new ArrayList<String[]>(4);
        List<Book> bookData = getBooks();
        String ids   = "";
        String names = "";
        Integer size = 0;
        size         = bookData.size();
        for(Integer count = 0; count < bookData.size(); count++){
            if(count == 0){
                ids   += bookData.get(count).getId();
                names += bookData.get(count).getTitle();
            }else{
                ids   += "@!@" + bookData.get(count).getId();
                names += "@!@" + bookData.get(count).getTitle();
            }
        }
        data.add(!ids.equals("")   ? Functions.explode(ids, "@!@")   : null);
        data.add(!names.equals("") ? Functions.explode(names, "@!@") : null);

        ids   = "";
        names = "";
        List<User> readerData = getReaders();
        size                  = readerData.size();
        for(Integer count = 0; count < readerData.size(); count++){
            if(count == 0){
                ids   += readerData.get(count).getId();
                names += readerData.get(count).getName();
            }else{
                ids   += "@!@" + readerData.get(count).getId();
                names += "@!@" + readerData.get(count).getName();
            }
        }
        data.add(!ids.equals("")   ? Functions.explode(ids, "@!@")   : null);
        data.add(!names.equals("") ? Functions.explode(names, "@!@") : null);

        return data;
    }

    public List<Book> getBooks(){
        Book b = new Book(currentContext);
        List<Book> data = b.fetchAll(b.ACTIVE, null);
        return data;
    }

    public List<User> getReaders(){
        User u = new User(currentContext);
        List<User> data = u.fetchAll(u.ACTIVE_READER.toString(), null);
        return data;
    }

    public String calculateDelayedDays(){
        String data = "";
        String expectedDateString = Functions.parsePtDateToEn(this.getExpectedDelivery(), "/");
        System.out.println("Data: "+expectedDateString);
//        Date expectedDate = Functions.parseStringToDate();
        return data;
    }

    public Integer save(){
        ContentValues contentValues = new ContentValues();
        contentValues.put("book", this.getBook());
        contentValues.put("reader", this.getReader());
        contentValues.put("lendDate", this.getLendDate());
        contentValues.put("expectedDelivery", this.getExpectedDelivery());
        contentValues.put("realDelivery", this.getRealDelivery());
        if(this.getInstitution() < 1) {
            contentValues.put("institution", this.getUserInstitution());
        }else{
            contentValues.put("institution", this.getInstitution());
        }
        Integer result = 0;
        if(this.getId() > 0) {
            contentValues.put("status", this.getStatus());
            result = getDBConnection().update(getTableName(), contentValues, "id = ?", new String[]{Integer.toString(this.getId())});
        }else{
            contentValues.put("status", this.ACTIVE);
            result = Math.toIntExact(getDBConnection().insert(getTableName(), null, contentValues));
        }
        return result;
    }

    @SuppressLint("Range")
    public List<BookBorrowing> fetchAll(String status, String date) {
        ArrayList list = new ArrayList();
        StringBuilder stringBuilderQuery = new StringBuilder();
        Cursor cursor = null;
        if(status == null && date == null) {
            stringBuilderQuery.append("SELECT * FROM " + getTableName() + " WHERE institution = ?");
            cursor = getDBConnection().rawQuery(stringBuilderQuery.toString(), new String[]{getUserInstitution()});
        }else if(status != null && date == null) {
            stringBuilderQuery.append("SELECT * FROM " + getTableName() + " WHERE status = ? AND institution = ?");
            cursor = getDBConnection().rawQuery(stringBuilderQuery.toString(), new String[]{status, getUserInstitution()});
        }else if(status == null && date != null) {
            String nameFormated = "%" + date + "%";
            stringBuilderQuery.append("SELECT * FROM " + getTableName() + " WHERE institution = ?");
            cursor = getDBConnection().rawQuery(stringBuilderQuery.toString(), new String[]{nameFormated, nameFormated, nameFormated, nameFormated, getUserInstitution()});
        }else if(status != null && date != null){
            String nameFormated = "%" + date + "%";
            stringBuilderQuery.append("SELECT * FROM " + getTableName() + " WHERE status = ? AND institution = ?");
            cursor = getDBConnection().rawQuery(stringBuilderQuery.toString(), new String[]{status, nameFormated, nameFormated, nameFormated, nameFormated, getUserInstitution()});
        }else{
            cursor = getDBConnection().rawQuery("SELECT * FROM "+getTableName()+ " WHERE status = ? AND institution = ? ORDER BY name", new String[]{status, getUserInstitution()});
        }
        cursor.moveToFirst();
        System.out.println("Tamanho: "+cursor.getCount());
        while(!cursor.isAfterLast()){
            BookBorrowing bookBorrowing = new BookBorrowing(currentContext);
            bookBorrowing.setId(cursor.getInt(cursor.getColumnIndex("id")));
            bookBorrowing.setBook(cursor.getInt(cursor.getColumnIndex("book")));
            bookBorrowing.setInstitution(cursor.getInt(cursor.getColumnIndex("institution")));
            bookBorrowing.setLendDate(cursor.getString(cursor.getColumnIndex("lendDate")));
            bookBorrowing.setExpectedDelivery(cursor.getString(cursor.getColumnIndex("expectedDelivery")));
            bookBorrowing.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            bookBorrowing.setRealDelivery(cursor.getString(cursor.getColumnIndex("realDelivery")));
            bookBorrowing.setReader(cursor.getInt(cursor.getColumnIndex("reader")));
            list.add(bookBorrowing);
            cursor.moveToNext();
        }
        return list;
    }
}
