package com.br.guardapaginas.classes;

import android.database.sqlite.SQLiteDatabase;

import com.br.guardapaginas.MainActivity;

public class DBHandler {

    SQLiteDatabase dbConnetion;

    /**
     * Creates DB connection and DB TABLES if necessary
     */
    public DBHandler(MainActivity context)
    {
        try{
            SQLiteDatabase db = context.openOrCreateDatabase("DB_GUARDAPAGINAS", context.MODE_PRIVATE, null);
            setDBConnetion(db);
            // Create Table
            db.execSQL("CREATE TABLE IF NOT EXISTS genders(id INTEGER PRIMARY KEY, name varchar(300), createdAt varchar(50))");
            db.execSQL("CREATE TABLE IF NOT EXISTS books(id INTEGER PRIMARY KEY, title varchar(100), synopsis varchar(300), author varchar(60), releaseDate varchar(50), editorName varchar(60), gender INTEGER, FOREIGN KEY (gender) REFERENCES genders (id))");
            db.execSQL("CREATE TABLE IF NOT EXISTS institutions(id INTEGER PRIMARY KEY, name varchar(200), address varchar(300), number varchar(50), telephone varchar(30), email varchar(30))");
            db.execSQL("CREATE TABLE IF NOT EXISTS users(id PRIMARY KEY, name varchar(200), cpf varchar(10), email varchar(30), password varchar(65), status varchar(2))");
            db.execSQL("CREATE TABLE IF NOT EXISTS bookBorrowings(id PRIMARY KEY, book INTEGER, institution INTEGER, lendData varchar(50), expectedDelivery varchar(50), realDelivery varchar(50), reader INTEGER, FOREIGN KEY (reader) REFERENCES users (id), FOREIGN KEY (book) REFERENCES books (id),FOREIGN KEY (institution) REFERENCES institutions (id))");
        } catch (Exception exception) {
            System.out.println("Error connecting to BD");
        }
    }

    /**
     * Sets a value to the connection object
     * @param SQLiteDatabase obj
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
}
