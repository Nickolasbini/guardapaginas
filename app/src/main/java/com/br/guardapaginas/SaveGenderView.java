package com.br.guardapaginas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.br.guardapaginas.classes.Gender;
import com.br.guardapaginas.databinding.ActivityMainBinding;
import com.br.guardapaginas.helpers.Functions;

public class SaveGenderView extends AppCompatActivity {

    ActivityMainBinding binding;
    EditText nameInput;
    Integer numberOfId;
    String  genderId;
    Gender genderObj;
    Button saveGenderButton;
    Button inactiveActiveGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_save_gender_view);
        Functions.setSystemColors(this);

        saveGenderButton     = (Button) findViewById(R.id.saveBookBorrowButton);
        inactiveActiveGender = (Button) findViewById(R.id.inactiveBookBorrowBtn);
        nameInput            = (EditText) findViewById(R.id.nameInput);

        Intent intent = getIntent();
        genderId   = intent.getStringExtra("GENDER_ID");
        numberOfId = Integer.parseInt(genderId);
        if(!genderId.equals("0")){
            setTitle("Editar Gênero");
            saveGenderButton.setText("Salvar");
            inactiveActiveGender.setVisibility(View.VISIBLE);
        }else{
            setTitle("Cadastrar Gênero");
            saveGenderButton.setText("Cadastrar");
            inactiveActiveGender.setVisibility(View.GONE);
        }

        fillFields();

        saveGenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verifyInputs() == true){
                    Boolean result = saveGender();
                    if(result){
                        Intent i = new Intent();
                        setResult(Activity.RESULT_OK, i);
                        finishActivity(1);
                        finish();
                    }else{
                        addMessageToToast("Um problema ocorreu, tente novamente");
                    }
                }
            }
        });

        inactiveActiveGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(genderObj == null || genderObj.getId() < 1){
                    addMessageToToast("Ação não permitida");
                    return;
                }
                Boolean result = genderObj.inactiveActiveGender();
                if(!result){
                    addMessageToToast("Um problema ocorreu, tente novamente");
                    return;
                }
                addMessageToToast("Status do gênero atualizado");
                fillFields();
            }
        });

        ImageView goBackBtn = (ImageView) findViewById(R.id.goBackBtn);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(Activity.RESULT_OK, i);
                finishActivity(1);
                finish();
            }
        });
    }

    public void addMessageToToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void fillFields(){
        genderObj = new Gender(getApplicationContext());
        if(numberOfId > 0){
            Gender obj = new Gender(getApplicationContext());
            obj = obj.findById(numberOfId);
            if(obj == null){
                genderObj = null;
                return;
            }
            genderObj = obj;
            nameInput.setText(genderObj.getName());
            String titleToSetOnStatusButton = "";
            if(obj.getDefaultGender() != 1){
                titleToSetOnStatusButton = (genderObj.getStatus().equals(genderObj.ACTIVE) ? "Inativar" : "Ativar");
                System.out.println("Diferente de Null");
            }else{
                addMessageToToast("Gênero padrão. Não pode ser alterado");
                finish();
            }
            inactiveActiveGender.setText(titleToSetOnStatusButton);
        }
    }

    public Boolean verifyInputs(){
        if(nameInput.getText().toString().equals("")){
            addMessageToToast("Informe um nome");
            return false;
        }
        return true;
    }

    public Boolean saveGender(){
        if(genderObj == null){
            addMessageToToast("Um problema ocorreu, tente novamente");
            finish();
            return false;
        }
        genderObj.setName(nameInput.getText().toString());
        Integer result = genderObj.save();
        return (result == 0 ? false : true);
    }
}