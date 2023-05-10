package com.br.guardapaginas.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.br.guardapaginas.R;
import com.br.guardapaginas.classes.Gender;
import com.br.guardapaginas.databinding.ActivityMainBinding;
import com.br.guardapaginas.fragments.BookFragment;
import com.br.guardapaginas.fragments.HomeFragment;
import com.br.guardapaginas.fragments.ReaderFragment;

import java.util.List;

public class saveBookView extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}
