package com.br.guardapaginas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.br.guardapaginas.classes.User;
import com.br.guardapaginas.helpers.SessionManagement;

public class loginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(view);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        SessionManagement sessionManagement = new SessionManagement(this);
        if(sessionManagement.isSessionActive())
            moveToMainActivity();
    }

    public boolean login(View view){
        User user = new User(getApplicationContext());
        user      = user.findById(1);
        if(user == null)
            return false;
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
}