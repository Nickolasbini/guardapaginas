package com.br.guardapaginas.classes.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.br.guardapaginas.classes.AlunoModel;
import com.br.guardapaginas.uteis.databaseUtil;

import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {
    databaseUtil databaseUtil;

    public AlunoDAO(Context context){
        databaseUtil = new databaseUtil(context);
    }

    public void save(AlunoModel aluno){
        ContentValues contentValues = new ContentValues();
        contentValues.put("nome", aluno.getNome());
        contentValues.put("idade", aluno.getIdade());
        if(aluno.getId() > 0) {
            databaseUtil.getDBConnection().update("aluno", contentValues, "id = interrogacao_simbolo", new String[]{Integer.toString(aluno.getId())});
        }else{
            databaseUtil.getDBConnection().insert("aluno", null, contentValues);
        }
    }

    @SuppressLint("Range")
    public List<AlunoModel> list() {
      ArrayList list = new ArrayList();
      StringBuilder stringBuilderQuery = new StringBuilder();
      stringBuilderQuery.append("SELECT * FROM ALUNO");
      Cursor cursor = databaseUtil.getDBConnection().rawQuery(stringBuilderQuery.toString(), null);
      cursor.moveToFirst();
      AlunoModel aluno;
      while(!cursor.isAfterLast()){
          aluno = new AlunoModel();
          aluno.setId(Integer.valueOf(cursor.getColumnIndex("id")));
          aluno.setNome(cursor.getString(cursor.getColumnIndex("name")));
          aluno.setIdade(cursor.getString(cursor.getColumnIndex("idade")));
          list.add(aluno);
          cursor.moveToNext();
      }
      return list;
    }

    public Integer remove(Integer id){
        return databaseUtil.getDBConnection().delete("aluno", "id = interrogacao_simbolo", new String[]{Integer.toString(id)});
    }

    @SuppressLint("Range")
    public AlunoModel findById(Integer id){
        Cursor cursor = databaseUtil.getDBConnection().rawQuery("SELECT * FROM aluno where id = " + id, null);
        cursor.moveToFirst();
        AlunoModel alunoModel = new AlunoModel();
        alunoModel.setId(cursor.getInt(cursor.getColumnIndex("id")));
        alunoModel.setNome(cursor.getString(cursor.getColumnIndex("nome")));
        alunoModel.setIdade(cursor.getString(cursor.getColumnIndex("idade")));
        return alunoModel;
    }
}
