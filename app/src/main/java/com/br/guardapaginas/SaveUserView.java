package com.br.guardapaginas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.br.guardapaginas.classes.User;
import com.br.guardapaginas.databinding.ActivityMainBinding;
import com.br.guardapaginas.helpers.Functions;

import java.util.Locale;

public class SaveUserView extends AppCompatActivity {

    ActivityMainBinding binding;

    EditText nameInput;
    EditText cpfInput;
    EditText emailInput;
    EditText registrationInput;

    Integer numberOfId;
    String userId;
    String userType;
    String userTypePt;
    User userObj;

    Button inactiveUserBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_save_user_view);
        Functions.setSystemColors(this);

        nameInput         = (EditText) findViewById(R.id.nameInput);
        cpfInput          = (EditText) findViewById(R.id.cpfInput);
        emailInput        = (EditText) findViewById(R.id.emailInput);
        registrationInput = (EditText) findViewById(R.id.registrationInput);

        Button saveUserButton  = (Button) findViewById(R.id.saveUserButton);
        inactiveUserBtn = (Button) findViewById(R.id.inactiveUserBtn);

        Intent intent = getIntent();
        userId     = intent.getStringExtra("USER_ID");
        numberOfId = Integer.parseInt(userId);
        userType   = intent.getStringExtra("USER_TYPE");
        userTypePt = (userType.equals("READER") ? "Leitor" : "Administrador");
        if(!userId.equals("0")){
            setTitle("Editar " + userTypePt);
            saveUserButton.setText("Salvar");
            inactiveUserBtn.setVisibility(View.VISIBLE);
        }else{
            setTitle("Cadastrar " + userTypePt);
            saveUserButton.setText("Cadastrar");
            inactiveUserBtn.setVisibility(View.GONE);
        }

        fillFields();

        ImageView goBackBtn = (ImageView) findViewById(R.id.goBackBtn);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(Activity.RESULT_OK, i);
                finishActivity(1);
                finish();
            }
        });

        saveUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verifyInputs() == true){
                    Boolean result = saveUser();
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

        inactiveUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userObj == null || userObj.getId() < 1){
                    addMessageToToast("Ação não permitida");
                    return;
                }
                Boolean result = userObj.inactiveActiveUser();
                if(!result){
                    addMessageToToast("Um problema ocorreu, tente novamente");
                    return;
                }
                addMessageToToast("Status do "+userTypePt.toLowerCase(Locale.ROOT)+" atualizado");
                fillFields();
            }
        });
    }

    public Boolean verifyInputs(){
        String name         = nameInput.getText().toString();
        String email        = emailInput.getText().toString();
        String registration = registrationInput.getText().toString();
        String institution  = userObj.getUserInstitution();
        String cpf          = cpfInput.getText().toString();
        if(name == null || name.equals("")){
            addMessageToToast("Informe o nome do " + userTypePt);
            return false;
        }
        if(email == null || email.equals("") || !isEmailValid(email) || !isEmailAvaliable(email, numberOfId)){
            addMessageToToast("Email não informado ou inválido");
            return false;
        }
        if(registration == null || registration.equals("")){
            addMessageToToast("Informe o número de registro");
            return false;
        }
        if(institution == null || institution.equals("")){
            addMessageToToast("Informe a instituição");
            return false;
        }
        if(cpf != null && !Functions.isCPFValid(cpf)){
            addMessageToToast("CPF inválido");
            return false;
        }
        return true;
    }

    public void fillFields(){
        userObj = new User(getApplicationContext());
        if(numberOfId > 0){
            User obj = new User(getApplicationContext());
            obj = obj.findById(numberOfId);
            System.out.println("Objeto: "+obj);
            if(obj == null){
                userObj = null;
                return;
            }
            userObj = obj;
            nameInput.setText(userObj.getName());
            cpfInput.setText(userObj.getCpf());
            registrationInput.setText(userObj.getRegistration());
            emailInput.setText(userObj.getEmail());

            String typeOfUser               = userObj.getUserType();
            String titleToSetOnStatusButton = "";
            if(typeOfUser.equals("READER")){
                titleToSetOnStatusButton = (userObj.getStatus().equals(userObj.ACTIVE_READER.toString()) ? "Inativar" : "Ativar");
            }else if(typeOfUser.equals("ADMINISTRATOR")){
                titleToSetOnStatusButton = (userObj.getStatus().equals(userObj.ACTIVE_ADMINISTRATOR.toString()) ? "Inativar" : "Ativar");
            }
            inactiveUserBtn.setText(titleToSetOnStatusButton);
        }
    }

    public Boolean saveUser(){
        if(userObj == null){
            addMessageToToast("Um problema ocorreu, tente novamente");
            finish();
            return false;
        }
        userObj.setName(nameInput.getText().toString());
        userObj.setEmail(emailInput.getText().toString());
        userObj.setRegistration(registrationInput.getText().toString());
        userObj.setCpf(cpfInput.getText().toString());
        userObj.setInstitution(Integer.parseInt(userObj.getUserInstitution()));
        Integer result = 0;
        if(userType.equals("READER")){
            result = userObj.saveReader();
        }else{
            result = userObj.saveAdmin();
        }
        return (result == 0 ? false : true);
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void addMessageToToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public Boolean isEmailAvaliable(String email, Integer id){
        User obj = new User(getApplicationContext());
        return obj.isEmailAvaliable(email, id);
    }
}