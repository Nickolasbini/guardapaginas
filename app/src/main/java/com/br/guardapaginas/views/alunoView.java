package com.br.guardapaginas.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.br.guardapaginas.MainActivity;
import com.br.guardapaginas.R;
import com.br.guardapaginas.classes.AlunoModel;
import com.br.guardapaginas.classes.DAO.AlunoDAO;

import java.util.List;

public class alunoView extends AppCompatActivity {

    EditText idInput;
    EditText nameInput;
    EditText ageInput;
    Button saveAluno;

    ListView listOfaluno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aluno_view);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        idInput   = (EditText) findViewById(R.id.idInput);
        nameInput = (EditText) findViewById(R.id.nameInput);
        ageInput  = (EditText) findViewById(R.id.ageInput);
        saveAluno = (Button) findViewById(R.id.saveAluno);

        listOfaluno = (ListView) findViewById(R.id.alunoList);
        listar();

        saveAluno.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onCLick(View v){
                saveAluno();
            }
        });
    }

    public void saveAluno(){
        if(nameInput.getText().toString().trim().equals("")){
            mensagem.Alert(this, "Nome é de preenchimento obrigatório");
            this.nameInput.requestFocus();
        }else if(ageInput.getText().toString().trim().equals("")){
            mensagem.ALert(this, "Idade é de preenchimento obrigatório");
            this.ageInput.requestFocus();
        }else{
            AlunoModel alunoModel = new AlunoModel();
            alunoModel.setNome(this.nameInput.getText().toString().trim());
            alunoModel.setIdade(this.ageInput.getText().toString().trim());
            new AlunoDAO(getApplicationContext()).save(alunoModel);

            AlertDialog.Builder confirmDialog = new AlertDialog.Builder(alunoView.this);
            confirmDialog.setCancelable(false);
            if(alunoModel.getId() > 0) {
                confirmDialog.setTitle("Aluno editado");
            }else{
                confirmDialog.setTitle("Aluno cadastrado");
            }
            confirmDialog.setMessage("Deseja cadastrar/editar outro aluno interrogacao_simbolo");

            confirmDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(alunoView.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            confirmDialog.show();
        }
    }

    private void listar(){
        try{
            AlunoDAO alunoDao = new AlunoDAO(this);
            List<AlunoModel> alunoModel = alunoDao.list();
            listOfaluno.setAdapter(new Adapter(this, alunoModel));
        }catch (Exception e){

        }
    }
}