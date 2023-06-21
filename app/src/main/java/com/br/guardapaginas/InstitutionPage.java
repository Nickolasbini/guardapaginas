package com.br.guardapaginas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.br.guardapaginas.databinding.ActivityMainBinding;

public class InstitutionPage extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_institution_page);
    }
}