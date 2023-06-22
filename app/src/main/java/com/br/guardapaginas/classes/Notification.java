package com.br.guardapaginas.classes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.br.guardapaginas.helpers.Functions;
import com.br.guardapaginas.helpers.MailService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Notification extends DBHandler{

    public final String START_OF_BORROW = "1";
    public final String END_OF_BORROW   = "2";
    public final String DELAYED_BORROW  = "3";

    public Notification(Context context){
        super(context);
        setTableName("notifications");
    }

    public int id;
    public String sentDate;
    public Integer bookBorrowing;
    public String type;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getSentDate() {
        return sentDate;
    }
    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public Integer getBookBorrowing() {
        return bookBorrowing;
    }
    public void setBookBorrowing(Integer bookBorrowing) {
        this.bookBorrowing = bookBorrowing;
    }

    public String getType() {
        return type;
    }
    public void setType(String typeVal) {
        this.type = typeVal;
    }

    @SuppressLint("Range")
    public List<Notification> fetchAll(){
        ArrayList list = new ArrayList();
        Cursor cursor = getDBConnection().rawQuery("SELECT * FROM "+getTableName(), null);
        cursor.moveToFirst();
        Notification notification;
        while(!cursor.isAfterLast()){
            notification = new Notification(currentContext);
            notification.setId(cursor.getInt(cursor.getColumnIndex("id")));
            notification.setSentDate(cursor.getString(cursor.getColumnIndex("sentDate")));
            notification.setBookBorrowing(cursor.getInt(cursor.getColumnIndex("bookBorrowing")));
            list.add(notification);
            cursor.moveToNext();
        }
        return list;
    }

    @SuppressLint("Range")
    public List<Notification> fetchByBookBorrowingAndDate(Integer bookBorrowingId, String sentDate){
        List<Notification> list = new ArrayList<>();
        Cursor cursor           = getDBConnection().rawQuery("SELECT * FROM "+getTableName()+" WHERE bookBorrowing = ? AND sentDate = ? AND type = ?", new String[]{bookBorrowingId.toString(), sentDate, DELAYED_BORROW});
        if(cursor.getCount() < 1)
            return null;
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Notification notification = new Notification(currentContext);
            notification.setId(cursor.getInt(cursor.getColumnIndex("id")));
            notification.setSentDate(cursor.getString(cursor.getColumnIndex("sentDate")));
            notification.setBookBorrowing(cursor.getInt(cursor.getColumnIndex("bookBorrowing")));
            list.add(notification);
            cursor.moveToNext();
        }
        return list;
    }

    public Boolean sendEmail(String emailType, BookBorrowing bookBorrowingObj){
        Boolean result = false;
        if(bookBorrowingObj == null)
            return false;
        List<Notification> notificationList = this.fetchByBookBorrowingAndDate(bookBorrowingObj.getId(), new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()));
        System.out.println("O resultado: "+notificationList);
        if(notificationList != null && notificationList.size() > 0)
            return true;
        System.out.println("Estou aquiii com status: "+emailType);
        Book bookObj         = bookBorrowingObj.getMyBookObject();
        User readerObj       = bookBorrowingObj.getMyReaderObject();
        Institution instiObj = new Institution(currentContext).findById(Functions.parseToInteger(getUserInstitution()));
        MailService mailer = new MailService("unirank.adm@gmail.com",readerObj.getEmail(),"Notificação do aplicativo Guarda Páginas", "", "");
        String type = null;
        switch (emailType){
            case START_OF_BORROW:
                mailer.setHtmlBody("<h1>Empréstimo cadastrado em nosso sistema!</h1><p>O livro "+bookObj.getTitle()+" foi emprestado na data de "+bookBorrowingObj.getLendDate()+" (<small>Este é apenas um email com a confirmação e formalização dos dados de seu empréstimo)</small>).</p><br><br>A "+instiObj.getName()+" agradece sua preferência e deseja a você um excelente dia.");
                type   = START_OF_BORROW;
                result = true;
            break;
            case END_OF_BORROW:
                mailer.setHtmlBody("<h1>Seu livro foi entregue com sucesso!</h1><p>Obrigado pela devolução do livro "+bookObj.getTitle()+".</p><br><br>A "+instiObj.getName()+" agradece sua preferência e deseja a você um excelente dia.");
                type   = END_OF_BORROW;
                result = true;
            break;
            case DELAYED_BORROW:
                String delayedDays = bookBorrowingObj.calculateDelayedDays();
                mailer.setHtmlBody("<h1>Seu livro está atrasado a "+delayedDays+" dias!</h1><p>Evite futuras multas e realize a devolução ainda hoje.</p><br><br>A "+instiObj.getName()+" agradece sua preferência e deseja a você um excelente dia.");
                type   = DELAYED_BORROW;
                result = true;
            break;
            default:
                result = false;
            break;
        }
        System.out.println("Resultado:: "+result);
        if(!result)
            return result;
        try {
            mailer.sendAuthenticated();
            Notification notObj = new Notification(currentContext);
            notObj.createReference(bookBorrowingObj, type);
            System.out.println("Email sent");
        } catch (Exception e) {
            System.out.println("Failed sending email. "+  e);
        }
        return result;
    }

    public void createReference(BookBorrowing bookBorrowing, String type){
        Notification obj = new Notification(currentContext);
        obj.setBookBorrowing(bookBorrowing.getId());
        obj.setSentDate(bookBorrowing.getNowDate());
        obj.setType(type);
        obj.save();
    }

    public Integer save(){
        ContentValues contentValues = new ContentValues();
        contentValues.put("bookBorrowing", this.getBookBorrowing());
        contentValues.put("sentDate", this.getSentDate());
        contentValues.put("type", this.getType());
        Integer result = 0;
        if(this.getId() > 0) {
            result = getDBConnection().update(getTableName(), contentValues, "id = ?", new String[]{Integer.toString(this.getId())});
        }else{
            result = Math.toIntExact(getDBConnection().insert(getTableName(), null, contentValues));
        }
        return result;
    }
}
