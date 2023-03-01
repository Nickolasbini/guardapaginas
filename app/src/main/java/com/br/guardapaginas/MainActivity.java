package com.br.guardapaginas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.br.guardapaginas.classes.Gender;
import com.br.guardapaginas.helpers.Functions;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "nickolas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Gender genderObj = new Gender();
//        genderObj.setName("Romance");
//        genderObj.setCreatedAt(Functions.now().toString());
//        genderObj.saveGender(genderObj);

        Button btn = (Button) findViewById(R.id.btnEntrar);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToHomePage();
            }
        });
    }

    private void goToHomePage(){
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra(EXTRA_MESSAGE, "user name");
        startActivity(intent);
    }
}