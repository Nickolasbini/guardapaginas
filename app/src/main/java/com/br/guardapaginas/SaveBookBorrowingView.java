package com.br.guardapaginas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
    TextView      delayedDaysLabel;
    Button        returnedBookButton;

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
            setTitle("Editar Empréstimo");
            saveBookBorrowButton.setText("Salvar");
            inactiveActiveBookBorrow.setVisibility(View.VISIBLE);
            bookBorrowingObj = bookBorrowingObj.findById(numberOfId);
            String messageToInactiveActiveButton = "Inativar";
            if(bookBorrowingObj.getStatus().equals(bookBorrowingObj.INACTIVE))
                messageToInactiveActiveButton = "Ativar";
            inactiveActiveBookBorrow.setText(messageToInactiveActiveButton);
            allowEdition(false);
            handleButtons(bookBorrowingObj.getStatus());
        }else{
            setTitle("Cadastrar Empréstimo");
            saveBookBorrowButton.setText("Cadastrar");
            inactiveActiveBookBorrow.setVisibility(View.GONE);
            handleButtons("");
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
                if(!bookBorrowingObj.isBookAvaliableForBorrowing()){
                    addMessageToToast("O livro selecionado não está disponível para empréstimo");
                    return;
                }
                bookBorrowingObj.setStatus(bookBorrowingObj.BORROWED);
                String delayedDaysCalculated = bookBorrowingObj.calculateDelayedDays();
                if(!delayedDaysCalculated.equals("0"))
                    bookBorrowingObj.setStatus(bookBorrowingObj.DELAYED);
                Integer resultFromSave = bookBorrowingObj.save();
                if(resultFromSave > 0){
                    Book myBook = bookBorrowingObj.getMyBookObject();
                    if(myBook != null)
                        myBook.decreaseQuantityInStock();
                    new SaveBookBorrowingView.EmailTasks(resultFromSave, "new").execute();
                    String message = (numberOfId > 0 ? "Empréstimo atualizado" : "Empréstimo cadastrado");
                    addMessageToToast(message);
                    Intent i = new Intent();
                    setResult(Activity.RESULT_OK, i);
                    finishActivity(1);
                    finish();
                }else{
                    addMessageToToast("Um problema ocorreu, tente novamente");
                }
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

        returnedBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bookBorrowingObj == null){
                    addMessageToToast("Um problema ocorreu, tente novamente");
                    return;
                }
                bookBorrowingObj.setStatus(bookBorrowingObj.RETURNED);
                bookBorrowingObj.setRealDelivery(bookBorrowingObj.getNowDate());
                bookBorrowingObj.setDelayedDays(delayedDaysInput.getText().toString());
                if(bookBorrowingObj.save() < 1){
                    addMessageToToast("Um problema ocorreu ao salvar, tente novamente");
                    return;
                }
                Book myBook = bookBorrowingObj.getMyBookObject();
                myBook.increaseQuantityInStock();
                new SaveBookBorrowingView.EmailTasks(bookBorrowingObj.getId(), "returned").execute();
                allowEdition(false);
                addMessageToToast("Empréstimo finalizado com sucesso");
                Intent i = new Intent();
                setResult(Activity.RESULT_OK, i);
                finishActivity(1);
                finish();
            }
        });

        inactiveActiveBookBorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = bookBorrowingObj.getNextStatus();
                bookBorrowingObj.setStatus(status);
                if(bookBorrowingObj.save() < 1){
                    addMessageToToast("Um problema ocorreu, tente novamente");
                    return;
                }
                addMessageToToast("Status atualizado");
                String messageToInactiveActiveButton = "Inativar";
                if(bookBorrowingObj.getStatus().equals(bookBorrowingObj.INACTIVE))
                    messageToInactiveActiveButton = "Ativar";
                inactiveActiveBookBorrow.setText(messageToInactiveActiveButton);
            }
        });
    }

    private class EmailTasks extends AsyncTask<String, Void, String> {
        Integer borrowingId;
        String  emailType;
        public EmailTasks(Integer id, String type){
            borrowingId = id;
            emailType   = type;
        }

        @Override
        protected String doInBackground(String... params) {
            BookBorrowing bookBorrowingObj = new BookBorrowing(getApplicationContext());
            BookBorrowing borrowObj = bookBorrowingObj.findById(borrowingId);
            if(borrowObj == null)
                return null;
            if(emailType.equals("new")) {
                borrowObj.fetchAndSendEmailToNewBorrowing();
            }else{
                borrowObj.fetchAndSendEmailToReturnedBorrowing();
            }
            return null;
        }
    }

    private void updateDateOfRelease(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat);
        lendDateInput.setText(dateFormat.format(myCalendar.getTime()));
        SimpleDateFormat enDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        expectedDeliveryDateInput.setText(Functions.addDaysToDate(15, enDateFormat.format(myCalendar.getTime())));
    }

    public void initTags(){
        saveBookBorrowButton     = (Button)   findViewById(R.id.saveBookBorrowButton);
        inactiveActiveBookBorrow = (Button)   findViewById(R.id.inactiveBookBorrowBtn);
        bookSelect               = (Spinner)  findViewById(R.id.bookSelect);
        readerSelect             = (Spinner)  findViewById(R.id.readerSelect);
        lendDateInput            = (EditText) findViewById(R.id.lendDateInput);
        expectedDeliveryDateInput= (TextView) findViewById(R.id.expectedDeliveryDateInput);
        realDeliveryDateLabel    = (TextView) findViewById(R.id.realDeliveryDateLabel);
        realDeliveryDateInput    = (TextView) findViewById(R.id.realDeliveryDateInput);
        delayedDaysLabel         = (TextView) findViewById(R.id.delayedDaysLabel);
        delayedDaysInput         = (TextView) findViewById(R.id.delayedDaysInput);
        bookBorrowingObj         = new BookBorrowing(getApplicationContext());
        returnedBookButton       = (Button)   findViewById(R.id.returnedBookButton);

        if(!fillSpinners())
            finish();
    }

    public void allowEdition(Boolean enable){
        bookSelect.setEnabled(enable);
        readerSelect.setEnabled(enable);
        lendDateInput.setEnabled(enable);
    }

    public void handleButtons(String status){
        switch (status){
            case "0":
                saveBookBorrowButton.setVisibility(View.GONE);
                inactiveActiveBookBorrow.setVisibility(View.VISIBLE);
                returnedBookButton.setVisibility(View.GONE);
                allowEdition(false);
            break;
            case "1":
                saveBookBorrowButton.setVisibility(View.GONE);
                inactiveActiveBookBorrow.setVisibility(View.VISIBLE);
                returnedBookButton.setVisibility(View.GONE);
                allowEdition(false);
            break;
            case "2":
                saveBookBorrowButton.setVisibility(View.GONE);
                inactiveActiveBookBorrow.setVisibility(View.GONE);
                returnedBookButton.setVisibility(View.VISIBLE);
                allowEdition(false);
            break;
            case "3":
                saveBookBorrowButton.setVisibility(View.GONE);
                inactiveActiveBookBorrow.setVisibility(View.GONE);
                returnedBookButton.setVisibility(View.VISIBLE);
                allowEdition(false);
            break;
            default:
                saveBookBorrowButton.setVisibility(View.VISIBLE);
                inactiveActiveBookBorrow.setVisibility(View.GONE);
                returnedBookButton.setVisibility(View.GONE);
                allowEdition(true);
            break;
        }
    }

    public void feedObject(){
        Integer positionBook = bookSelect.getSelectedItemPosition();
        bookBorrowingObj.setBook(Functions.parseToInteger(booksId[positionBook]));
        Integer positionReader = readerSelect.getSelectedItemPosition();
        bookBorrowingObj.setReader(Functions.parseToInteger(readersId[positionReader]));
        bookBorrowingObj.setLendDate(lendDateInput.getText().toString());
        bookBorrowingObj.setExpectedDelivery(expectedDeliveryDateInput.getText().toString());
        bookBorrowingObj.setRealDelivery(realDeliveryDateInput.getText().toString());
    }

    public void fillInputs(){
        if(bookBorrowingObj.getId() < 1){
            lendDateInput.setText(Functions.parseEnToPt(Functions.getNowDate()));
            expectedDeliveryDateInput.setText(Functions.addDaysToDate(15, Functions.getNowDate()));
            realDeliveryDateLabel.setVisibility(View.INVISIBLE);
            realDeliveryDateInput.setVisibility(View.INVISIBLE);
            delayedDaysLabel.setVisibility(View.INVISIBLE);
            delayedDaysInput.setVisibility(View.INVISIBLE);
            return;
        }
        realDeliveryDateLabel.setVisibility(View.VISIBLE);
        realDeliveryDateInput.setVisibility(View.VISIBLE);
        delayedDaysLabel.setVisibility(View.VISIBLE);
        delayedDaysInput.setVisibility(View.VISIBLE);
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
        delayedDaysInput.setText(bookBorrowingObj.calculateDelayedDays() + " dias");
        realDeliveryDateInput.setText(bookBorrowingObj.getRealDelivery());
        if(bookBorrowingObj.getRealDelivery() == null)
            return;
        realDeliveryDateLabel.setVisibility(View.VISIBLE);
        realDeliveryDateInput.setVisibility(View.VISIBLE);
        delayedDaysInput.setText(bookBorrowingObj.calculateDelayedDays() + " dias");
    }

    public Boolean fillSpinners(){
        List<String[]> data = bookBorrowingObj.getDataForSpinner();
        booksId     = data.get(0);
        booksName   = data.get(1);
        readersId   = data.get(2);
        readersName = data.get(3);

        if(booksId == null){
            addMessageToToast("Nenhum livro cadastrado");
            finish();
            return false;
        }
        if(readersId == null){
            addMessageToToast("Nenhum leitor cadastrado");
            finish();
            return false;
        }

        ArrayAdapter<String> bookSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, booksName);
        bookSelect.setAdapter(bookSpinnerAdapter);

        ArrayAdapter<String> readerSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, readersName);
        readerSelect.setAdapter(readerSpinnerAdapter);

        return true;
    }

    public void addMessageToToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}