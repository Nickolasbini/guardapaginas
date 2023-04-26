package com.br.guardapaginas.uteis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class databaseUtil extends SQLiteOpenHelper {
    private static final String NAME_DB = "CRUD.db";

    private static final int DB_VERSION = 1;

    public databaseUtil(Context context){
        super(context, NAME_DB, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String sqlAluno = "CREATE TABLE ALUNO ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL, " +
                "idade TEXT NOT NULL);";
        db.execSQL(sqlAluno);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ALUNO");
        onCreate(db);
    }

    public SQLiteDatabase getDBConnection(){
        return this.getWritableDatabase();
    }
}
