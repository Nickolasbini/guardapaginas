package com.br.guardapaginas.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.br.guardapaginas.databinding.ActivityMainBinding;

public class BookDetails extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}