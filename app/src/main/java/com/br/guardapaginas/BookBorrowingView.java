package com.br.guardapaginas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.br.guardapaginas.classes.BookBorrowing;
import com.br.guardapaginas.classes.holders.BookBorrowAdapter;
import com.br.guardapaginas.classes.holders.BookBorrowRecycleViewInterface;
import com.br.guardapaginas.databinding.ActivityMainBinding;
import com.br.guardapaginas.helpers.Functions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class BookBorrowingView extends AppCompatActivity implements BookBorrowRecycleViewInterface {

    ActivityMainBinding binding;

    Spinner statusSpinner;
    EditText searchInput;
    List<BookBorrowing> listOfBookBorrows;
    BookBorrowing bookBorrowingObj;
    TextView noResultLabel;
    RecyclerView listOfBookBorrowings;
    LinearLayout filterDropdownOptions;
    ImageView filterDropdown;
    Boolean filterOptionsOpen = false;
    EditText fromDate;
    EditText toDate;

    final Calendar fromCalendar = Calendar.getInstance();
    final Calendar toCalendar   = Calendar.getInstance();
    String dateInputInUse       = "fromDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_book_borrowing);
        Functions.setSystemColors(this);

        setTitle("Empréstimos");

        fromDate             = (EditText) findViewById(R.id.toDate);
        toDate               = (EditText) findViewById(R.id.fromDate);
        bookBorrowingObj     = new BookBorrowing(getApplicationContext());
        noResultLabel        = (TextView) findViewById(R.id.noResultLabel);
        listOfBookBorrowings = (RecyclerView) findViewById(R.id.listOfBookBorrowings);
        statusSpinner        = (Spinner) findViewById(R.id.statusSpinner);
        ArrayAdapter<String> genderSpinnerAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, bookBorrowingObj.getAvaliableStatus());
        statusSpinner.setAdapter(genderSpinnerAdapter);
        statusSpinner.setSelection(1, true);

        filterDropdownOptions = (LinearLayout) findViewById(R.id.filterDropdownOptions);
        filterDropdownOptions.setVisibility(View.GONE);
        filterDropdown        = (ImageView) findViewById(R.id.showFilterOptions);
        filterDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filterOptionsOpen){
                    filterOptionsOpen = false;
                    filterDropdownOptions.setVisibility(View.GONE);
                }else{
                    filterOptionsOpen = true;
                    filterDropdownOptions.setVisibility(View.VISIBLE);
                }
            }
        });

        fetchBookBorrowings(true, false, null);

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
                String[] data = new String[2];
                data[0] = fromDate.getText().toString();
                data[1] = toDate.getText().toString();
                fetchBookBorrowings(true, true, data);
            }
        });

        ImageView goBackBtn = (ImageView) findViewById(R.id.goBackBtn);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(Activity.RESULT_OK, i);
                finishActivity(1);
                finish();
            }
        });

        ImageView clearBtn = (ImageView) findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDate.setText("");
                toDate.setText("");
                fetchBookBorrowings(true, false, null);
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                System.out.println("Tipo::  "+dateInputInUse);
                if(dateInputInUse.equals("fromDate")) {
                    fromCalendar.set(Calendar.YEAR, year);
                    fromCalendar.set(Calendar.MONTH, month);
                    fromCalendar.set(Calendar.DAY_OF_MONTH, day);
                }else{
                    toCalendar.set(Calendar.YEAR, year);
                    toCalendar.set(Calendar.MONTH, month);
                    toCalendar.set(Calendar.DAY_OF_MONTH, day);
                }
                updateDateOfInput(dateInputInUse);
            }
        };

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateInputInUse = "fromDate";
                new DatePickerDialog(BookBorrowingView.this, date, fromCalendar.get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH), fromCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateInputInUse = "toDate";
                new DatePickerDialog(BookBorrowingView.this, date, toCalendar.get(Calendar.YEAR), toCalendar.get(Calendar.MONTH), toCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateDateOfInput(String inputName) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat);
        if(inputName.equals("fromDate")) {
            fromDate.setText(dateFormat.format(fromCalendar.getTime()));
        }else{
            toDate.setText(dateFormat.format(toCalendar.getTime()));
        }
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
                    fetchBookBorrowings(true, false, null);
                    fromDate.setText("");
                    toDate.setText("");
                }
                break;
        }
    };

    public void fetchBookBorrowings(Boolean status, Boolean search, String[] dataToUse){
        String statusName = null;
        if(status) {
            statusName = bookBorrowingObj.parseStatusNameToNumber((String)statusSpinner.getSelectedItem());
        }
        String searchValue = null;
        if(search) {
            if(dataToUse == null){
                addMessageToToast("Por favor informe uma data de Início e de Fim");
                return;
            }
            String[] dates = new String[2];
            dates[0] = fromDate.getText().toString();
            dates[1] = toDate.getText().toString();
            searchValue = Functions.implode(dates, "!@!");
            if(searchValue.equals("!@!"))
                searchValue = null;
        }
        listOfBookBorrows = bookBorrowingObj.fetchAll(statusName, searchValue);
        if(listOfBookBorrows.size() > 0) {
            noResultLabel.setVisibility(View.GONE);
        }else{
            noResultLabel.setVisibility(View.VISIBLE);
        }
        listOfBookBorrowings.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        listOfBookBorrowings.setAdapter(new BookBorrowAdapter(getApplicationContext(), listOfBookBorrows, this));
    }

    public void addMessageToToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}