package com.br.guardapaginas;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.br.guardapaginas.classes.BookBorrowing;
import com.br.guardapaginas.classes.DBHandler;
import com.br.guardapaginas.classes.Gender;
import com.br.guardapaginas.classes.Institution;
import com.br.guardapaginas.classes.Notification;
import com.br.guardapaginas.classes.User;
import com.br.guardapaginas.helpers.Functions;
import com.br.guardapaginas.helpers.MailService;
import com.br.guardapaginas.helpers.SessionManagement;
import com.br.guardapaginas.views.APIListOfBooks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class loginActivity extends AppCompatActivity {

    Boolean  startActivity    = true;
    EditText loginEmailInput;
    EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(startActivity) {

            Functions.setSystemColors(this);

            buildBaseData();

            loginEmailInput = (EditText) findViewById(R.id.loginEmailInput);
            passwordInput = (EditText) findViewById(R.id.passwordInput);

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
                    Intent i = new Intent(getApplicationContext(), NewAccount.class);
                    startActivity(i);
                }
            });

            TextView forgotPasswordBtn = (TextView) findViewById(R.id.forgotPasswordBtn);
            forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            new loginActivity.EmailTasks().execute();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        Intent thisIntent = getIntent();
        String flag = thisIntent.getStringExtra("SPLASH_SCREEN_EXECUTED");
        flag        = (flag == null ? "" : flag);
        SessionManagement sessionManagement = new SessionManagement(this);
        if(sessionManagement.isSessionActive()) {
            moveToMainActivity();
        }else if(flag.equals("")){
            Intent i = new Intent(getApplicationContext(), TheInitialScreen.class);
            startActivity(i);
            startActivity = false;
        }else{
            startActivity = true;
        }
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

        String[] defaultGender = {"drama", "romance", "terror", "misterio", "aventura", "ação"};
        for(Integer i = 0; i < defaultGender.length; i++){
            Gender obj = new Gender(getApplicationContext());
            List<Gender> result = obj.fetchByName(defaultGender[i]);
            if(result.size() > 0)
                continue;
            obj.setName(defaultGender[i]);
            obj.setDefaultGender(1);
            obj.save();
        }
    }

    private class EmailTasks extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            BookBorrowing bookBorrowingObj = new BookBorrowing(getApplicationContext());
            bookBorrowingObj.fetchAndSendEmailToDelayedBorrowings();
            bookBorrowingObj.fetchAndSendEmailWarningLicenseAlmostEnding();
            return null;
        }
    }
}