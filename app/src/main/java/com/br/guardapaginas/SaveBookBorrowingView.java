package com.br.guardapaginas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.br.guardapaginas.classes.Book;
import com.br.guardapaginas.classes.BookBorrowing;
import com.br.guardapaginas.databinding.ActivityMainBinding;
import com.br.guardapaginas.helpers.Functions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class SaveBookBorrowingView extends AppCompatActivity {

    ActivityMainBinding binding;

    final Calendar myCalendar= Calendar.getInstance();

    String        bookBorrowId;
    Integer       numberOfId;
    Button        saveBookBorrowButton;
    Button        inactiveActiveBookBorrow;
    BookBorrowing bookBorrowingObj;
    Spinner       bookSelect;
    Spinner       readerSelect;
    EditText      lendDateInput;
    TextView      expectedDeliveryDateInput;
    TextView      realDeliveryDateLabel;
    TextView      realDeliveryDateInput;
    TextView      delayedDaysInput;

    String[]      booksId;
    String[]      booksName;
    String[]      readersId;
    String[]      readersName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_save_book_borrowing_view);
        Functions.setSystemColors(this);

        initTags();

        Intent intent = getIntent();
        bookBorrowId   = intent.getStringExtra("BOOK_BORROW_ID");
        numberOfId = Integer.parseInt(bookBorrowId);
        if(!bookBorrowId.equals("0")){
            setTitle("Editar Emprêstimo");
            saveBookBorrowButton.setText("Salvar");
            inactiveActiveBookBorrow.setVisibility(View.VISIBLE);
            bookBorrowingObj = bookBorrowingObj.findById(numberOfId);
        }else{
            setTitle("Cadastrar Emprêstimo");
            saveBookBorrowButton.setText("Cadastrar");
            inactiveActiveBookBorrow.setVisibility(View.GONE);
        }

        fillInputs();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateDateOfRelease();
            }
        };
        lendDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(SaveBookBorrowingView.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        saveBookBorrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedObject();
                if(bookBorrowingObj.save()){
                    String message = (numberOfId > 0 ? "Empréstimo atualizado" : "Empréstimo cadastrado");
                    addMessageToToast(message);
                }else{
                    addMessageToToast("Um problema ocorreu, tente novamente");
                }
            }
        });
    }

    private void updateDateOfRelease(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat);
        lendDateInput.setText(dateFormat.format(myCalendar.getTime()));
    }

    public void initTags(){
        saveBookBorrowButton     = (Button) findViewById(R.id.saveBookBorrowButton);
        inactiveActiveBookBorrow = (Button)findViewById(R.id.inactiveBookBorrowBtn);
        bookSelect               = (Spinner) findViewById(R.id.bookSelect);
        readerSelect             = (Spinner) findViewById(R.id.readerSelect);
        lendDateInput            = (EditText) findViewById(R.id.lendDateInput);
        expectedDeliveryDateInput= (TextView) findViewById(R.id.expectedDeliveryDateInput);
        realDeliveryDateLabel    = (TextView) findViewById(R.id.realDeliveryDateLabel);
        realDeliveryDateInput    = (TextView) findViewById(R.id.releaseDateInput);
        delayedDaysInput         = (TextView) findViewById(R.id.delayedDaysInput);
        bookBorrowingObj         = new BookBorrowing(getApplicationContext());

        fillSpinners();
    }

    public void feedObject(){
        bookBorrowingObj.setBook((Integer) bookSelect.getSelectedItem());
        bookBorrowingObj.setReader((Integer) readerSelect.getSelectedItem());
        bookBorrowingObj.setLendDate(lendDateInput.getText().toString());
    }

    public void fillInputs(){
        if(bookBorrowingObj.getId() < 1){
            lendDateInput.setText(Functions.parseEnToPt(Functions.getNowDate()));
            return;
        }
        Integer myBookIndex = 0;
        String  myBookId    = Functions.parseToString(bookBorrowingObj.getBook());
        for(Integer i = 0 ; i < booksId.length; i++) {
            if (booksId[i].equals(myBookId)){
                myBookIndex = i;
                break;
            }
        }
        bookSelect.setSelection(myBookIndex);
        Integer myReaderIndex = 0;
        String  myReaderId    = Functions.parseToString(bookBorrowingObj.getReader());
        for(Integer i = 0 ; i < readersId.length; i++) {
            if (readersId[i].equals(myReaderId)){
                myReaderIndex = i;
                break;
            }
        }
        readerSelect.setSelection(myReaderIndex);
        lendDateInput.setText(bookBorrowingObj.getLendDate());

        expectedDeliveryDateInput.setText("Ainda no Prazo de Entrega");
        if(bookBorrowingObj.getExpectedDelivery() != null){
            expectedDeliveryDateInput.setText(bookBorrowingObj.getExpectedDelivery());
        }

        realDeliveryDateLabel.setVisibility(View.GONE);
        realDeliveryDateInput.setVisibility(View.GONE);
        delayedDaysInput.setText("0 Dias");
        if(bookBorrowingObj.getRealDelivery() != null)
            return;
        realDeliveryDateLabel.setVisibility(View.VISIBLE);
        realDeliveryDateInput.setVisibility(View.VISIBLE);
        delayedDaysInput.setText(bookBorrowingObj.calculateDelayedDays());
    }

    public void fillSpinners(){
        List<String[]> data = bookBorrowingObj.getDataForSpinner();
        booksId     = data.get(0);
        booksName   = data.get(1);
        readersId   = data.get(2);
        readersName = data.get(3);

        ArrayAdapter<String> bookSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, booksName);
        bookSelect.setAdapter(bookSpinnerAdapter);

        ArrayAdapter<String> readerSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, readersName);
        readerSelect.setAdapter(readerSpinnerAdapter);
    }

    public void addMessageToToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}