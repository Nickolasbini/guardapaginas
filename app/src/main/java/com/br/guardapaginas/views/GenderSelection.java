package com.br.guardapaginas.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.br.guardapaginas.R;
import com.br.guardapaginas.classes.Book;
import com.br.guardapaginas.classes.Gender;
import com.br.guardapaginas.classes.holders.BookAdapter;
import com.br.guardapaginas.classes.holders.GenderAdapter;
import com.br.guardapaginas.classes.holders.GenderRecycleViewInterface;
import com.br.guardapaginas.databinding.ActivityMainBinding;
import com.br.guardapaginas.helpers.Functions;

import java.util.List;

public class GenderSelection extends AppCompatActivity implements GenderRecycleViewInterface {

    ActivityMainBinding binding;
    List<Gender> listOfGenders;
    String idOfGenders;
    String nameOfGenders;
    String action;
    String[] gendersIdArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_gender_selection);

        Intent intent = getIntent();
        idOfGenders   = intent.getStringExtra("CURRENT_GENDER_IDS");
        nameOfGenders = intent.getStringExtra("CURRENT_GENDER_NAMES");
        action        = intent.getStringExtra("ACTION");

        gendersIdArray = Functions.explode(idOfGenders, ",");

        RecyclerView recyclerView = findViewById(R.id.listOfGenders);
        listOfGenders = getGenders("1", idOfGenders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(new GenderAdapter(getApplicationContext(), listOfGenders, this));

        ImageView close = (ImageView) findViewById(R.id.goBackButton);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public List<Gender> getGenders(String status, String alreadyUsedIds){
        if(status == null)
            status = "1";
        Gender genderObj = new Gender(getApplicationContext());
        if(action.equals("ADD")) {
            return genderObj.getGendersByStatusWithoutThisIds(status, alreadyUsedIds);
        }else{
            return genderObj.getByIds(gendersIdArray);
        }
    }

    @Override
    public void onItemClick(int position) {
        if(listOfGenders.size() < position){
            Toast.makeText(this, "Gênero não encontrado", Toast.LENGTH_LONG).show();
            return;
        }

        Intent returnIntent = getIntent();
        if(action.equals("ADD")) {
            addItemToList(position, returnIntent);
        }else{
            removeItemOfList(position, returnIntent);
        }
        setResult(Activity.RESULT_OK, returnIntent);
        finishActivity(200);
        finish();
    }

    public void addItemToList(Integer position, Intent intent){
        Gender obj              = listOfGenders.get(position);
        String[] formerIdArray  = Functions.explode(idOfGenders, ",");
        String[] formerNameArray= Functions.explode(nameOfGenders, ",");
        String newArrayOfId     = "";
        String newArrayOfName   = "";
        Boolean firstLoop       = true;
        for(Integer i = 0; i < formerIdArray.length; i++){
            if(firstLoop){
                newArrayOfId   += formerIdArray[i];
                newArrayOfName += formerNameArray[i];
                firstLoop = false;
                continue;
            }
            newArrayOfId   += ","+formerIdArray[i];
            newArrayOfName += ","+formerNameArray[i];
        }
        String separator = newArrayOfName == "" ? "" : ",";
        newArrayOfId   += separator+ Functions.parseToString(obj.getId());
        newArrayOfName += separator+ obj.getName();
        intent.putExtra("UPDATED_GENDER_IDS",   newArrayOfId);
        intent.putExtra("UPDATED_GENDER_NAMES", newArrayOfName);
    }

    public void removeItemOfList(Integer position, Intent intent){
        Gender obj              = listOfGenders.get(position);
        String[] formerIdArray  = Functions.explode(idOfGenders, ",");
        String[] formerNameArray= Functions.explode(nameOfGenders, ",");
        String newArrayOfId     = "";
        String newArrayOfName   = "";
        Boolean firstLoop       = true;
        for(Integer i = 0; i < formerIdArray.length; i++){
            if(formerIdArray[i].equals(Functions.parseToString(obj.getId())))
                continue;
            if(firstLoop){
                newArrayOfId   += formerIdArray[i];
                newArrayOfName += formerNameArray[i];
                firstLoop = false;
                continue;
            }
            newArrayOfId   += ","+formerIdArray[i];
            newArrayOfName += ","+formerNameArray[i];
        }
        intent.putExtra("UPDATED_GENDER_IDS",   newArrayOfId);
        intent.putExtra("UPDATED_GENDER_NAMES", newArrayOfName);
    }
}