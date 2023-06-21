package com.br.guardapaginas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.br.guardapaginas.databinding.ActivityMainBinding;
import com.br.guardapaginas.helpers.Functions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

public class HelpPage extends AppCompatActivity {

    ActivityMainBinding binding;

    String[] questions = {"Cadastrar/Editar livro", "Cadastrar/Editar gênero", "Cadastrar/Editar leitor", "Alterar os dados de minha conta", "Cadastrar empréstimo", "Gerar relatórios"};
    String[] answers   = {"Partindo da tela inicial selecione o ícone de livro localizado no canto inferior do menu > selecione o icone de '+' | para alteração clique em um item da lista", "Partindo da tela inicial selecione o ícone de três pontinhos localizado no canto inferior do menu > selecione o icone de '+' | para alteração clique em um item da lista", "Partindo da tela inicial selecione o ícone de três pontinhos localizado no canto inferior do menu > selecione o icone de '+' | para alteração clique em um item da lista"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_help_page);

        Functions.setSystemColors(this);

        Spinner questionSelector = (Spinner) findViewById(R.id.questionSelector);
        ArrayAdapter<String> genderSpinnerAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, questions);
        questionSelector.setAdapter(genderSpinnerAdapter);

        TextView tipText = (TextView) findViewById(R.id.tipText);

        questionSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Integer index = questionSelector.getSelectedItemPosition();
                tipText.setText(answers[index]);
                tipText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ImageView goBackBtn = (ImageView) findViewById(R.id.goBackBtn);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}