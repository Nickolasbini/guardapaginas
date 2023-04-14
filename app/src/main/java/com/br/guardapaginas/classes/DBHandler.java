package com.br.guardapaginas.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.br.guardapaginas.MainActivity;

public class DBHandler {

    SQLiteDatabase dbConnetion;

    Cursor results;

    Context currentContext;

    /**
     * Creates DB connection and DB TABLES if necessary
     */
    public DBHandler(Context context)
    {
        currentContext = context;
        openConnection();
    }

    public void openConnection(){
        try{
            SQLiteDatabase db = currentContext.openOrCreateDatabase("DB_GUARDAPAGINAS", currentContext.MODE_PRIVATE, null);
            setDBConnetion(db);
            // Create Table
            db.execSQL("CREATE TABLE IF NOT EXISTS genders(id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar(300), createdAt varchar(50))");
            db.execSQL("CREATE TABLE IF NOT EXISTS books(id INTEGER PRIMARY KEY AUTOINCREMENT, title varchar(100), synopsis varchar(300), author varchar(60), releaseDate varchar(50), editorName varchar(60), gender INTEGER, FOREIGN KEY (gender) REFERENCES genders (id))");
            db.execSQL("CREATE TABLE IF NOT EXISTS institutions(id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar(200), address varchar(300), number varchar(50), telephone varchar(30), email varchar(30))");
            db.execSQL("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar(200), cpf varchar(10), email varchar(30), password varchar(65), status varchar(2), institution INTEGER NULL, FOREIGN KEY (institution) REFERENCES institutions (id))");
            db.execSQL("CREATE TABLE IF NOT EXISTS bookBorrowings(id INTEGER PRIMARY KEY AUTOINCREMENT, book INTEGER, institution INTEGER, lendData varchar(50), expectedDelivery varchar(50), realDelivery varchar(50), reader INTEGER, FOREIGN KEY (reader) REFERENCES users (id), FOREIGN KEY (book) REFERENCES books (id),FOREIGN KEY (institution) REFERENCES institutions (id))");
        } catch (Exception exception) {
            System.out.println("Error connecting to BD");
        }
    }

    public void closeConnection(){
        if(dbConnetion == null)
            return;
        dbConnetion.close();
        setDBConnetion(null);
    }

    public void setResults(Cursor results){
        this.results = results;
    }

    public Cursor getResults(){
        return this.results;
    }

    /**
     * Sets a value to the connection object
     * param SQLiteDatabase obj
     */
    public void setDBConnetion(SQLiteDatabase obj)
    {
        this.dbConnetion = obj;
    }

    /**
     * Return the value of the current data base connection
     * @return SQLiteDatabase connection
     */
    public SQLiteDatabase getDBConnection()
    {
        return this.dbConnetion;
    }

    /**
     * Execute sent query, returning True if it was executed
     * param   queryToExecute
     * @return Boolean
     */
    public Boolean execQuery(String queryToExecute){
        try {
            Cursor result = dbConnetion.rawQuery(queryToExecute, null);
            setResults(result);
            return true;
        } catch (Exception e){
            System.out.println("Error at DBHandler line 85: "+e);
            return false;
        }
    }
}
