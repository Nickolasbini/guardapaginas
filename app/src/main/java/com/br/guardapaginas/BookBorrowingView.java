package com.br.guardapaginas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.br.guardapaginas.classes.BookBorrowing;
import com.br.guardapaginas.classes.holders.BookAdapter;
import com.br.guardapaginas.classes.holders.BookBorrowAdapter;
import com.br.guardapaginas.classes.holders.BookBorrowRecycleViewInterface;
import com.br.guardapaginas.classes.holders.GenderRecycleViewInterface;
import com.br.guardapaginas.databinding.ActivityMainBinding;
import com.br.guardapaginas.helpers.Functions;

import java.util.List;

public class BookBorrowingView extends AppCompatActivity implements BookBorrowRecycleViewInterface {

    ActivityMainBinding binding;

    Spinner statusSpinner;
    EditText searchInput;
    List<BookBorrowing> listOfBookBorrows;
    BookBorrowing bookBorrowingObj;
    TextView noResultLabel;
    RecyclerView listOfBookBorrowings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_book_borrowing);
        Functions.setSystemColors(this);

        setTitle("Empréstimos");

        searchInput      = (EditText) findViewById(R.id.searchInput);
        bookBorrowingObj = new BookBorrowing(getApplicationContext());
        noResultLabel    = (TextView) findViewById(R.id.noResultLabel);
        listOfBookBorrowings = (RecyclerView) findViewById(R.id.listOfBookBorrowings);

        fetchBookBorrowings(true, false);

        ImageView addNewBorrowingBtn = (ImageView) findViewById(R.id.addNewBorrowingBtn);
        addNewBorrowingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent saveBookBorrowingView = new Intent(getApplicationContext().getApplicationContext(), SaveBookBorrowingView.class);
                saveBookBorrowingView.putExtra("BOOK_BORROW_ID", "0");
                startActivityForResult(saveBookBorrowingView, 1);
            }
        });

        ImageView refreshButton = (ImageView) findViewById(R.id.refreshList);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchBookBorrowings(true, false);
                searchInput.setText("");
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
        String statusName = null;
        if(status) {
            //statusName = (String) statusSpinner.getSelectedItem();
            //statusName = (statusName.equals("Ativo") ? "1" : "0");
        }
        String searchValue = null;
        if(search)
            searchValue = (String) searchInput.getText().toString();
        listOfBookBorrows = bookBorrowingObj.fetchAll(statusName, searchValue);
        if(listOfBookBorrows.size() > 0) {
            noResultLabel.setVisibility(View.GONE);
        }else{
            noResultLabel.setVisibility(View.VISIBLE);
        }
        listOfBookBorrowings.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        listOfBookBorrowings.setAdapter(new BookBorrowAdapter(getApplicationContext(), listOfBookBorrows, this));
    }

    public void retrieveAndSetMonthsAndYears(){

    }
}