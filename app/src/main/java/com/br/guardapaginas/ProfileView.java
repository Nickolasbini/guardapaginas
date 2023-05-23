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

import com.br.guardapaginas.classes.Institution;
import com.br.guardapaginas.classes.User;
import com.br.guardapaginas.databinding.ActivityMainBinding;
import com.br.guardapaginas.helpers.Functions;
import com.br.guardapaginas.helpers.SessionManagement;

public class ProfileView extends AppCompatActivity {

    ActivityMainBinding binding;
    EditText userNameInput;
    EditText userEmailInput;
    EditText userCPFInput;
    EditText institutionNameInput;
    User userObj;
    ImageView goBackBtn;
    Button saveProfileBtn;
    Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_profile_view);
        Functions.setSystemColors(this);

        userNameInput        = (EditText) findViewById(R.id.userNameInput);
        userEmailInput       = (EditText) findViewById(R.id.userEmailInput);
        userCPFInput         = (EditText) findViewById(R.id.userCPFInput);
        institutionNameInput = (EditText) findViewById(R.id.institutionNameInput);

        Button goToInsitutionPageBtn = (Button) findViewById(R.id.goToInsitutionPageBtn);
        Button changePasswordBtn     = (Button) findViewById(R.id.changePasswordBtn);


        fillFieds();

        saveProfileBtn = (Button) findViewById(R.id.saveProfileBtn);
        saveProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean result = updateProfile();
                if(!result){
                    addMessageToToast("Um problema ocorreu, tente novamente");
                    return;
                }
                addMessageToToast("Perfil atualizado");
                SessionManagement session = new SessionManagement(getApplicationContext());
                session.killSession();
                session.saveSession(userObj);
            }
        });

        goBackBtn = (ImageView) findViewById(R.id.goBackBtn);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                setResult(Activity.RESULT_OK, i);
                finishActivity(1);
                finish();
            }
        });

        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManagement session = new SessionManagement(getApplicationContext());
                session.killSession();
                Intent i = new Intent(getApplicationContext(), loginActivity.class);
                startActivity(i);
            }
        });
    }

    public void fillFieds(){
        userObj = new User(getApplicationContext());
        if(userObj.getUserId() == null) {
            addMessageToToast("Um problema ocorreu, por favor tente novamente");
            goBackBtn.callOnClick();
            return;
        }
        userObj = userObj.findById(Functions.parseToInteger(userObj.getUserId()));
        if(userObj == null){
            addMessageToToast("Um problema ocorreu, por favor tente novamente");
            goBackBtn.callOnClick();
            return;
        }
        userNameInput.setText(userObj.getName());
        userEmailInput.setText(userObj.getEmail());
        userCPFInput.setText(userObj.getCpf());
        Institution institutionObj = userObj.getInstitutionObj();
        institutionNameInput.setVisibility(View.GONE);
        if(institutionObj != null) {
            institutionNameInput.setText(institutionObj.getName());
            institutionNameInput.setVisibility(View.VISIBLE);
        }
    }

    public Boolean updateProfile(){
        String name     = userNameInput.getText().toString().trim();
        Boolean persist = false;
        if(!name.equals(userObj.getName()))
            persist = true;
        String email = userEmailInput.getText().toString().trim();
        if(!email.equals(userObj.getEmail())){
            if(!isEmailAvaliable(email) || isEmailValid(email)){
                addMessageToToast("Email inválido");
                return false;
            }
            persist = true;
        }
        String cpf = userCPFInput.getText().toString().trim();
        if(!cpf.equals(userObj.getCpf())){
            if(!Functions.isCPFValid(cpf)){
                addMessageToToast("CPF inválido");
                return false;
            }
        }
        Integer result = 1;
        if(persist)
            result = userObj.saveAdmin();
        return (result > 0 ? true : false);
    }

    public void addMessageToToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public Boolean isEmailAvaliable(String email){
        User obj = new User(getApplicationContext());
        return obj.isEmailAvaliable(email, userObj.getId());
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}