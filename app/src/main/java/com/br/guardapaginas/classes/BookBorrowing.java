package com.br.guardapaginas.classes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.br.guardapaginas.helpers.Functions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BookBorrowing extends DBHandler{
    public final String INACTIVE = "0";
    public final String RETURNED = "1";
    public final String BORROWED = "2";
    public final String DELAYED  = "3";

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
    public String delayedDays;

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
        if(realDelivery == null || realDelivery.length() < 1)
            realDelivery = null;
        this.realDelivery = realDelivery;
    }

    public int getReader() {
        return reader;
    }
    public void setReader(int reader) {
        this.reader = reader;
    }

    public String getDelayedDays() {
        return delayedDays;
    }
    public void setDelayedDays(String delayedDays) {
        this.delayedDays = delayedDays;
    }

    @SuppressLint("Range")
    public List<String> getBookBorrowData(){
        List<String> data = new ArrayList<>(4);
        String query      = "SELECT u.name as readerName, u.email as readerEmail, b.title as bookName, bb.lendDate as borrowDate, bb.realDelivery as delivery FROM "+getTableName()+" as bb LEFT JOIN users as u on u.id = bb.reader LEFT JOIN books as b on b.id = bb.book WHERE bb.id = ?";
        Cursor cursor     = getDBConnection().rawQuery(query, new String[]{Functions.parseToString(this.getId())});
        if(cursor.getCount() < 1)
            return data;
        cursor.moveToNext();
        data.add(cursor.getString(cursor.getColumnIndex("readerName")));
        data.add(cursor.getString(cursor.getColumnIndex("readerEmail")));
        data.add(cursor.getString(cursor.getColumnIndex("bookName")));
        data.add(cursor.getString(cursor.getColumnIndex("borrowDate")));
        data.add(cursor.getString(cursor.getColumnIndex("delivery")));
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
        String daysOfDiference    = "0";
        Date   expectedDateObject = Functions.parseStringToDateWithFormat(this.getExpectedDelivery(), null);
        if(expectedDateObject == null)
            return daysOfDiference;
        Date nowDateObject = Functions.getNowDateObject();
        if(nowDateObject == null)
            return daysOfDiference;
        long diffInMillies = Math.abs(nowDateObject.getTime() - expectedDateObject.getTime());
        int daysDiff = (int) (diffInMillies / (1000 * 60 * 60 * 24));
        if(expectedDateObject.equals(nowDateObject) || expectedDateObject.after(nowDateObject))
            return daysOfDiference;
        daysOfDiference = Functions.parseToString(daysDiff);
        return daysOfDiference;
    }

    public Integer save(){
        ContentValues contentValues = new ContentValues();
        contentValues.put("book", this.getBook());
        contentValues.put("reader", this.getReader());
        contentValues.put("lendDate", this.getLendDate());
        contentValues.put("expectedDelivery", this.getExpectedDelivery());
        contentValues.put("realDelivery", this.getRealDelivery());
        contentValues.put("delayedDays", this.getDelayedDays());
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
            String defaultStatus = this.RETURNED;
            if(this.getStatus() != null)
                defaultStatus = this.getStatus();
            contentValues.put("status", defaultStatus);
            result = Math.toIntExact(getDBConnection().insert(getTableName(), null, contentValues));
        }
        return result;
    }

    @SuppressLint("Range")
    public List<BookBorrowing> fetchAll(String status, String date) {
        List<BookBorrowing> list = new ArrayList();
        StringBuilder stringBuilderQuery = new StringBuilder();
        Cursor cursor = null;
        if(status == null) {
            stringBuilderQuery.append("SELECT * FROM " + getTableName() + " WHERE institution = ?");
            cursor = getDBConnection().rawQuery(stringBuilderQuery.toString(), new String[]{getUserInstitution()});
        }else if(status != null) {
            stringBuilderQuery.append("SELECT * FROM " + getTableName() + " WHERE status = ? AND institution = ?");
            cursor = getDBConnection().rawQuery(stringBuilderQuery.toString(), new String[]{status, getUserInstitution()});
        }
        cursor.moveToFirst();
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
        if(date != null) {
            String[] array = Functions.explode(date, "!@!");
            list = parseByDate(array, list);
        }
        return list;
    }

    public String[] getAvaliableStatus(){
        return new String[]{"Inativo", "Devolvidos", "Em empréstimo", "Em atraso"};
    }

    public String parseStatusNameToNumber(String statusName){
        String statusIndex = null;
        switch(statusName){
            case "Inativo":
                statusIndex = this.INACTIVE;
            break;
            case "Devolvidos":
                statusIndex = this.RETURNED;
            break;
            case "Em empréstimo":
                statusIndex = this.BORROWED;
            break;
            case "Em atraso":
                statusIndex = this.DELAYED;
            break;
        }
        return statusIndex;
    }

    public Boolean isBookAvaliableForBorrowing(){
        Book bookObj = getMyBookObject();
        return bookObj.isAvaliableInStock();
    }

    public Book getMyBookObject(){
        Integer idOfBook = this.getBook();
        Book bookObj     = new Book(currentContext);
        bookObj          = bookObj.findById(idOfBook);
        if(bookObj == null)
            return null;
        return bookObj;
    }

    public User getMyReaderObject(){
        Integer idOfReader = this.getReader();
        User readerObj     = new User(currentContext);
        readerObj          = readerObj.findById(idOfReader);
        if(readerObj == null)
            return null;
        return readerObj;
    }

    public List<BookBorrowing> parseByDate(String[] dateArray, List<BookBorrowing> results){
        List<BookBorrowing> emptyList = new ArrayList<>();
        if(dateArray == null || dateArray.length < 2)
            return emptyList;
        List<BookBorrowing> myList = new ArrayList<>();
        String fromDate = dateArray[1];
        Date fromDateDateObj = Functions.parseStringToDateWithFormat(fromDate, null);
        String toDate   = dateArray[0];
        Date toDateDateObj = Functions.parseStringToDateWithFormat(toDate, null);
        if(fromDateDateObj == null || toDateDateObj == null)
            return emptyList;
        for(Integer i = 0; i < results.size(); i++){
            String myDate = results.get(i).getLendDate();
            if(myDate.length() < 1 || myDate.equals(""))
                continue;
            Date lendDateDateObj = null;
            lendDateDateObj      = Functions.parseStringToDateWithFormat(myDate, null);
            if(lendDateDateObj == null)
                continue;
            if((myDate.equals(fromDate) || myDate.equals(toDate)) || (lendDateDateObj.after(fromDateDateObj) && lendDateDateObj.before(toDateDateObj))){
                myList.add(results.get(i));
            }
        }
        return myList;
    }

    public String getNowDate(){
        return new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
    }

    public String getNextStatus(){
        return (this.getStatus().equals(this.INACTIVE) ? this.RETURNED : this.INACTIVE);
    }

    @SuppressLint("Range")
    public List<BookBorrowing> fetchAndSendEmailToDelayedBorrowings(){
        Cursor cursor = getDBConnection().rawQuery("SELECT * FROM " + getTableName() + " WHERE institution = ? AND realDelivery IS NULL", new String[]{getUserInstitution()});
        List<BookBorrowing> list = new ArrayList<>();
        cursor.moveToFirst();
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
        if(list.size() < 1)
            return null;
        for(Integer i = 0; i < list.size(); i++){
            BookBorrowing obj  = list.get(i);
            String delayedDays = obj.calculateDelayedDays();
            if(delayedDays.equals("0"))
                continue;
            obj.setStatus(obj.DELAYED);
            if(obj.save() > 0) {
                Notification notificationObj = new Notification(currentContext);
                notificationObj.sendEmail(notificationObj.DELAYED_BORROW, obj);
            }
        }
        return list;
    }

    public Boolean fetchAndSendEmailToNewBorrowing(){
        Notification notObj = new Notification(currentContext);
        return notObj.sendEmail(notObj.START_OF_BORROW, this);
    }

    public Boolean fetchAndSendEmailToReturnedBorrowing(){
        Notification notObj = new Notification(currentContext);
        return notObj.sendEmail(notObj.END_OF_BORROW, this);
    }

    @SuppressLint("Range")
    public Boolean fetchAndSendEmailWarningLicenseAlmostEnding(){
        Cursor cursor = getDBConnection().rawQuery("SELECT * FROM " + getTableName() + " WHERE institution = ? AND realDelivery IS NULL", new String[]{getUserInstitution()});
        List<BookBorrowing> list = new ArrayList<>();
        cursor.moveToFirst();
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
        if(list.size() < 1)
            return null;
        for(Integer i = 0; i < list.size(); i++){

        }
        return true;
    }

    @SuppressLint("Range")
    public List<BookBorrowing> fetchAllBorrowings(){
        Cursor cursor = getDBConnection().rawQuery("SELECT * FROM " + getTableName() + " WHERE institution = ?", new String[]{getUserInstitution()});
        List<BookBorrowing> list = new ArrayList<>();
        cursor.moveToFirst();
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

    public String createReport(Integer reportType, String[] dates){
        String fromDate = dates[0];
        String toDate   = dates[1];
        String response = "";
        String dataGathered;
        String query = "";
        Integer size = 0;
        Date fromDateObject = Functions.parseStringToDateWithFormat(fromDate, null);
        Date toDateObject   = Functions.parseStringToDateWithFormat(toDate, null);
        List<Object> data   = new ArrayList<>();
        List<BookBorrowing> all = fetchAllBorrowings();
        switch(reportType){
            case 1:
                for(Integer i = 0; i < all.size(); i++){
                    BookBorrowing obj   = all.get(i);
                    Date lendDateObject = Functions.parseStringToDateWithFormat(obj.getLendDate(), null);
                    if((lendDateObject.equals(fromDateObject) || lendDateObject.equals(toDateObject)) || (lendDateObject.after(fromDateObject) || lendDateObject.before(toDateObject)))
                        size++;
                }
                response = "No período entre "+fromDate+" até "+toDate+" foram realizados "+size+" empréstimos";
            break;
            case 2:
                for(Integer i = 0; i < all.size(); i++){
                    BookBorrowing obj   = all.get(i);
                    Date lendDateObject = Functions.parseStringToDateWithFormat(obj.getLendDate(), null);
                    if(!obj.getStatus().equals(obj.DELAYED))
                        continue;
                    if((lendDateObject.equals(fromDateObject) || lendDateObject.equals(toDateObject)) || (lendDateObject.after(fromDateObject) || lendDateObject.before(toDateObject)))
                        size++;
                }
                response = "No período entre "+fromDate+" até "+toDate+" houveram "+size+" empréstimos atrasados";
            break;
            case 3:
                List<String> readers = new ArrayList<>();
                String[] ids = new String[3];
                String top1 = "";
                String top2 = "";
                String top3 = "";

                for(Integer i = 0; i < all.size(); i++){
                    BookBorrowing obj   = all.get(i);
                    Date lendDateObject = Functions.parseStringToDateWithFormat(obj.getLendDate(), null);
                    if((lendDateObject.equals(fromDateObject) || lendDateObject.equals(toDateObject)) || (lendDateObject.after(fromDateObject) || lendDateObject.before(toDateObject))) {
                        String readerId = Functions.parseToString(obj.getReader());
                        if(readers.contains(readerId)){

                        }else{

                        }
                    }
                }
            break;
            case 4:

            break;
            case 5:

            break;
        }
        return response;
    }
}
