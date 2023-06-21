package com.br.guardapaginas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.br.guardapaginas.classes.Institution;
import com.br.guardapaginas.classes.User;
import com.br.guardapaginas.helpers.Functions;

public class NewAccount extends AppCompatActivity {

    EditText newNameInput;
    EditText newEmailInput;
    EditText newPasswordInput;
    EditText nameOfInstitutionInput;
    Button   btnCreateAccount;

    Institution institutionObj;
    User        userObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        Functions.setSystemColors(this);

        newNameInput           = (EditText) findViewById(R.id.newNameInput);
        newEmailInput          = (EditText) findViewById(R.id.newEmailInput);
        newPasswordInput       = (EditText) findViewById(R.id.newPasswordInput);
        nameOfInstitutionInput = (EditText) findViewById(R.id.nameOfInstitutionInput);
        btnCreateAccount       = (Button) findViewById(R.id.btnCreateAccount);
        userObject             = new User(getApplicationContext());
        institutionObj         = new Institution(getApplicationContext());

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedObjects();
                if(!userObject.isEmailAvaliable(userObject.getEmail(), null)){
                    addMessageToToast("O email é inválido ou já está em uso");
                    return;
                }
                Integer result = userObject.save(userObject, "ADMIN");
                if(result < 1){
                    addMessageToToast("Um problema ocorreu, tente novamente");
                    return;
                }
                institutionObj.setOwner(result);
                Integer resultFromInstitutionCreation = institutionObj.save();
                if(resultFromInstitutionCreation < 1){
                    userObject.remove(result);
                    addMessageToToast("Um problema ocorreu, tente novamente");
                    return;
                }
                userObject.setId(result);
                userObject.setInstitution(resultFromInstitutionCreation);
                userObject.save(userObject, "ADMIN");
                addMessageToToast("Conta criada com sucesso");
                Intent i = new Intent(getApplicationContext(), loginActivity.class);
                startActivity(i);
            }
        });
    }

    public void feedObjects(){
        institutionObj.setName(nameOfInstitutionInput.getText().toString());
        userObject.setName(newNameInput.getText().toString());
        userObject.setEmail(newEmailInput.getText().toString());
        userObject.setPassword(Functions.md5(newPasswordInput.getText().toString()));
    }

    public void addMessageToToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}