package com.br.guardapaginas;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.br.guardapaginas.classes.Gender;
import com.br.guardapaginas.classes.Institution;
import com.br.guardapaginas.classes.User;
import com.br.guardapaginas.helpers.Functions;
import com.br.guardapaginas.helpers.SessionManagement;

import java.util.List;

public class loginActivity extends AppCompatActivity {

    EditText loginEmailInput;
    EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Functions.setSystemColors(this);

        buildBaseData();

        loginEmailInput = (EditText) findViewById(R.id.loginEmailInput);
        passwordInput   = (EditText) findViewById(R.id.passwordInput);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        Button createAccountBtn = (Button) findViewById(R.id.createAccountBtn);
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        TextView forgotPasswordBtn = (TextView) findViewById(R.id.forgotPasswordBtn);
        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        System.out.println("Bora checar a sessão");
        SessionManagement sessionManagement = new SessionManagement(this);
        if(sessionManagement.isSessionActive())
            moveToMainActivity();
    }

    public boolean login(){
        String email = loginEmailInput.getText().toString().trim();
        if(email.equals("") || email == null){
            addMessageToToast("Informe um email");
            return false;
        }
        String password = passwordInput.getText().toString().trim();
        if(password.equals("") || password == null){
            addMessageToToast("Informe uma senha");
            return false;
        }
        User user = new User(getApplicationContext());
        user = user.fetchByEmail(loginEmailInput.getText().toString());
        if(user == null) {
            addMessageToToast("Email inválido");
            return false;
        }
        System.out.println("Suas credenciais:  "+email+ " | Senha: "+password + " - MD5 "+Functions.md5(password)+"  |  "+user.getPassword());
        if(!user.getPassword().equals(Functions.md5(password))){
            addMessageToToast("Email ou senha não conferem");
            return false;
        }
        SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
        sessionManagement.saveSession(user);
        moveToMainActivity();
        return true;
    }

    public void moveToMainActivity(){
        Intent intent = new Intent(loginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void addMessageToToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void buildBaseData(){
        User user = new User(getApplicationContext());
        User adminObj = user.findById(1);
        if(adminObj == null) {
            user.setEmail("nickolasbini@hotmail.com");
            user.setPassword(Functions.md5("123456"));
            user.setName("Nickolas Bini");
            user.saveAdmin();
        }
        Institution institution = new Institution(getApplicationContext());
        if(institution.findById(1) == null){
            institution.setName("Livraria Nickolas");
            institution.setEmail("livrarias_nick@hotmail.com");
            institution.setOwner(1);
            institution.save();
        }
        if(adminObj != null && (adminObj.getInstitution() == null || adminObj.getInstitution() == 0)){
            // Setting the institution
            adminObj.setInstitution(1);
            adminObj.saveAdmin();
        }

        Gender genderObj = new Gender(getApplicationContext());
        String[] defaultGender = {"drama", "romance", "terror", "misterio", "aventura", "ação"};
        for(Integer i = 0; i < defaultGender.length; i++){
            Gender obj = new Gender(getApplicationContext());
            List<Gender> result = obj.fetchByName(defaultGender[i]);
            if(result.size() != 0)
                continue;
            obj.setName(defaultGender[i]);
            obj.save(obj);
        }
    }
}