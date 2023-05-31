package com.br.guardapaginas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.br.guardapaginas.classes.BookBorrowing;
import com.br.guardapaginas.classes.holders.GenderRecycleViewInterface;
import com.br.guardapaginas.databinding.ActivityMainBinding;
import com.br.guardapaginas.helpers.Functions;

import java.util.List;

public class BookBorrowingView extends AppCompatActivity implements GenderRecycleViewInterface {

    ActivityMainBinding binding;

    EditText searchInput;
    List<BookBorrowing> listOfBookBorrows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_book_borrowing);
        Functions.setSystemColors(this);

        setTitle("Empréstimos");

        searchInput = (EditText) findViewById(R.id.searchInput);

        ImageView addNewBorrowingBtn = (ImageView) findViewById(R.id.addNewBorrowingBtn);
        addNewBorrowingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent saveBookBorrowingView = new Intent(getApplicationContext().getApplicationContext(), SaveBookBorrowingView.class);
                saveBookBorrowingView.putExtra("BOOK_BORROW_ID", "0");
                startActivityForResult(saveBookBorrowingView, 1);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent saveBookBorrowingView = new Intent(getApplicationContext().getApplicationContext(), SaveBookBorrowingView.class);
        saveBookBorrowingView.putExtra("BOOK_BORROW_ID", Functions.parseToString(listOfBookBorrows.get(position).getId()));
        startActivityForResult(saveBookBorrowingView, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    fetchBookBorrowings(true, false);
                    searchInput.setText("");
                }
                break;
        }
    };

    public void fetchBookBorrowings(Boolean status, Boolean search){

    }

    public void retrieveAndSetMonthsAndYears(){

    }
}