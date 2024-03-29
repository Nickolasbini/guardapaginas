package com.br.guardapaginas.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.br.guardapaginas.MainActivity;
import com.br.guardapaginas.helpers.SessionManagement;

import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    private static final String NAME_DB = "DB_GUARDAPAGINAS";
    private static final int DB_VERSION = 1;

    Cursor results;

    Context currentContext;

    private String tableName;
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    private String[] attributesFromModel;
    public String[] getAttributesFromModel() {
        return attributesFromModel;
    }
    public void setAttributesFromModel(String[] attributesFromModel) {
        this.attributesFromModel = attributesFromModel;
    }

    private String userId;
    private String userName;
    private String userEmail;
    private String userInstitution;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserInstitution() {
        return (userInstitution == null ? "" : userInstitution);
    }
    public void setUserInstitution(String userInstitution) {
        this.userInstitution = userInstitution;
    }

    /**
     * Creates DB connection and DB TABLES if necessary
     */
    public DBHandler(Context context)
    {
        super(context, NAME_DB, null, DB_VERSION);
        currentContext = context;
        try{
            SQLiteDatabase db = currentContext.openOrCreateDatabase(this.NAME_DB, currentContext.MODE_PRIVATE, null);
            // Create Table
            db.execSQL("CREATE TABLE IF NOT EXISTS genders(id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar(300), createdAt varchar(50), status varchar(2), defaultGender INTEGER default 0, institution INTEGER, FOREIGN KEY (institution) REFERENCES institutions (id))");
            db.execSQL("CREATE TABLE IF NOT EXISTS books(id INTEGER PRIMARY KEY AUTOINCREMENT, title varchar(100), synopsis varchar(300), author varchar(60), releaseDate varchar(50), quantity varchar(10) default 0, editorName varchar(60), status varchar(2), bookCover BLOB, bookLanguage varchar(2), numberOfPages varchar(50), institution INTEGER, FOREIGN KEY (institution) REFERENCES institutions (id))");
            db.execSQL("CREATE TABLE IF NOT EXISTS institutions(id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar(200), address varchar(300), number varchar(50), telephone varchar(30), email varchar(30), status varchar(2), owner INTEGER, FOREIGN KEY (owner) REFERENCES users (id))");
            db.execSQL("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar(200), cpf varchar(10), email varchar(30), password varchar(65), status varchar(2), registration varchar(200) UNIQUE, institution INTEGER, FOREIGN KEY (institution) REFERENCES institutions (id))");
            db.execSQL("CREATE TABLE IF NOT EXISTS bookBorrowings(id INTEGER PRIMARY KEY AUTOINCREMENT, book INTEGER, institution INTEGER, lendDate varchar(50), delayedDays varchar(100) default 0, expectedDelivery varchar(50), status varchar(2), realDelivery varchar(50), reader INTEGER, FOREIGN KEY (reader) REFERENCES users (id), FOREIGN KEY (book) REFERENCES books (id),FOREIGN KEY (institution) REFERENCES institutions (id))");
            db.execSQL("CREATE TABLE IF NOT EXISTS bookGenders(id INTEGER PRIMARY KEY AUTOINCREMENT, book INTEGER, gender INTEGER, FOREIGN KEY (book) REFERENCES books (id), FOREIGN KEY (gender) REFERENCES genders (id))");
            db.execSQL("CREATE TABLE IF NOT EXISTS notifications(id INTEGER PRIMARY KEY AUTOINCREMENT, sentDate varchar(50), type varchar(10), bookBorrowing INTEGER, FOREIGN KEY (bookBorrowing) REFERENCES bookBorrowings)");
        } catch (Exception exception) {
            System.out.println("Error connecting to BD");
        }
        setSessionOfUserToVariables();
    }

    /**
     * Return the value of the current data base connection
     * @return SQLiteDatabase connection
     */
    public SQLiteDatabase getDBConnection(){
        return this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try{
            SQLiteDatabase db = currentContext.openOrCreateDatabase(this.NAME_DB, currentContext.MODE_PRIVATE, null);
            // Create Table
            db.execSQL("CREATE TABLE IF NOT EXISTS genders(id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar(300), createdAt varchar(50), status varchar(2), defaultGender INTEGER default 0, institution INTEGER, FOREIGN KEY (institution) REFERENCES institutions (id))");
            db.execSQL("CREATE TABLE IF NOT EXISTS books(id INTEGER PRIMARY KEY AUTOINCREMENT, title varchar(100), synopsis varchar(300), author varchar(60), releaseDate varchar(50), quantity varchar(10) default 0, editorName varchar(60), status varchar(2), bookCover BLOB, bookLanguage varchar(2), numberOfPages varchar(50), institution INTEGER, FOREIGN KEY (institution) REFERENCES institutions (id))");
            db.execSQL("CREATE TABLE IF NOT EXISTS institutions(id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar(200), address varchar(300), number varchar(50), telephone varchar(30), email varchar(30), status varchar(2), owner INTEGER, FOREIGN KEY (owner) REFERENCES users (id))");
            db.execSQL("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar(200), cpf varchar(10), email varchar(30), password varchar(65), status varchar(2), registration varchar(200) UNIQUE, institution INTEGER, FOREIGN KEY (institution) REFERENCES institutions (id))");
            db.execSQL("CREATE TABLE IF NOT EXISTS bookBorrowings(id INTEGER PRIMARY KEY AUTOINCREMENT, book INTEGER, institution INTEGER, lendDate varchar(50), delayedDays varchar(100) default 0, expectedDelivery varchar(50), status varchar(2), realDelivery varchar(50), reader INTEGER, FOREIGN KEY (reader) REFERENCES users (id), FOREIGN KEY (book) REFERENCES books (id),FOREIGN KEY (institution) REFERENCES institutions (id))");
            db.execSQL("CREATE TABLE IF NOT EXISTS bookGenders(id INTEGER PRIMARY KEY AUTOINCREMENT, book INTEGER, gender INTEGER, FOREIGN KEY (book) REFERENCES books (id), FOREIGN KEY (gender) REFERENCES genders (id))");
            db.execSQL("CREATE TABLE IF NOT EXISTS notifications(id INTEGER PRIMARY KEY AUTOINCREMENT, sentDate varchar(50), type varchar(10), bookBorrowing INTEGER, FOREIGN KEY (bookBorrowing) REFERENCES bookBorrowings)");
        } catch (Exception exception) {
            System.out.println("Error connecting to BD");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS genders");
        db.execSQL("DROP TABLE IF EXISTS books");
        db.execSQL("DROP TABLE IF EXISTS institutions");
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS bookBorrowings");
        db.execSQL("DROP TABLE IF EXISTS bookGnders");
        db.execSQL("DROP TABLE IF EXISTS notifications");
        onCreate(db);
    }

    public void recyclyBD(){
        SQLiteDatabase db = currentContext.openOrCreateDatabase(this.NAME_DB, currentContext.MODE_PRIVATE, null);
        db.execSQL("DROP TABLE IF EXISTS genders");
        db.execSQL("DROP TABLE IF EXISTS books");
        db.execSQL("DROP TABLE IF EXISTS institutions");
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS bookBorrowings");
        db.execSQL("DROP TABLE IF EXISTS bookGenders");
        db.execSQL("DROP TABLE IF EXISTS notifications");
        onCreate(db);
    }

    public void insertImage(byte[] imageBytes){
        ContentValues cv = new ContentValues();
    }

    public void setSessionOfUserToVariables(){
        SessionManagement sessionManagement = new SessionManagement(currentContext);
        List<String> data                   = sessionManagement.getSessionUser();
        if(data == null)
            return;
        Integer      dataSize               = data.size();
        if(dataSize < 1)
            return;
        userId          = data.get(0);
        userName        = data.get(1);
        userEmail       = data.get(2);
        userInstitution = data.get(3);
    }
}
