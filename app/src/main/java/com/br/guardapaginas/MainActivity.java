package com.br.guardapaginas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.br.guardapaginas.classes.DBHandler;
import com.br.guardapaginas.classes.Gender;

import com.br.guardapaginas.AulaClass;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "nickolas";
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Gender genderObj = new Gender();
        genderObj.setName("romance");
        genderObj.setCreatedAt("2022-0307");
        genderObj.saveGender(genderObj);*/

        //DBHandler connectionObj = new DBHandler(MainActivity.this);
        Gender genderObj = new Gender(this);
        System.out.println("Oi");
        System.out.println(genderObj.getDBConnection());
        /*
        SQLiteDatabase db = connectionObj.getConnection();

        String query   = "SELECT * FROM gender";
        Cursor results = db.rawQuery(query, null);
        // Index of values
        int indexName = results.getColumnIndex("name");
        int indexDate = results.getColumnIndex("date");
        results.moveToFirst();
        String[] data = null;
        int position = 0;
        while(results.equals(null)){
            String name = results.getString(indexName);
            String date = results.getString(indexDate);
            data[position] = name + date;
            position++;
            results.moveToNext();
        }
        System.out.println(data);
        */

        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);

        Button btn = (Button) findViewById(R.id.btnEntrar);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //goToHomePage();



                // whatsAPP();
                // openPhoneContact();
                // openWebSite("https://unifacear.com.br");
            }
        });
    }

    private void goToHomePage(){
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra(EXTRA_MESSAGE, "user name");
        startActivity(intent);
    }

    private void whatsAPP(){
        Intent view = new Intent(Intent.ACTION_VIEW);
        view.setData(Uri.parse("https://api.whatsapp.com/send?text=Olá&phone=5541984320432"));
        startActivity(view);
    }
    private void openPhoneContact(){
        Intent view = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:991019001"));
        startActivity(view);
    }

    private void openWebSite(String url){
        Intent view = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(view);
    }
}