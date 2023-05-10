package com.br.guardapaginas;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.br.guardapaginas.classes.Book;
import com.br.guardapaginas.databinding.ActivityMainBinding;
import com.br.guardapaginas.fragments.BookFragment;
import com.br.guardapaginas.helpers.Functions;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SaveBookView extends AppCompatActivity {
    ActivityMainBinding binding;

    final Calendar myCalendar= Calendar.getInstance();

    Integer SELECT_IMAGE;
    ImageView bookCoverButton;
    Bitmap bookCoverBitMap;
    EditText dateOfReleaseInput;
    Book bookObj;
    TextView bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.save_book);

         setDefaultBookCover();

        Intent intent = getIntent();
        String idOfBook = intent.getStringExtra(BookFragment.BOOK_ID);
        bookId = (TextView) findViewById(R.id.bookId);
        bookId.setText(idOfBook);

        System.out.println("Id do livro: "+bookId.getText());


        ImageView goBackBtn = (ImageView) findViewById(R.id.goBackBtn);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        Spinner dropdown = findViewById(R.id.languageSelect);
        String[] items = new String[]{"Português","Inglês","Espanhol"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        bookCoverButton = (ImageView) findViewById(R.id.bookCoverButton);
        bookCoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), SELECT_IMAGE);
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                final int ACTIVITY_SELECT_IMAGE = 100;
                startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
            }
        });

        dateOfReleaseInput = (EditText) findViewById(R.id.releaseDateInput);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateDateOfRelease();
            }
        };
        dateOfReleaseInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(SaveBookView.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        Button saveButton = (Button) findViewById(R.id.saveBookButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verifyInputs() == false){

                }else{
                    saveBook();
                }
            }
        });
    }

    public Boolean verifyInputs(){

        return true;
    }

    public Book extractBookData(){
        bookObj = new Book(getApplicationContext());
        EditText title = (EditText) findViewById(R.id.titleInput);
        bookObj.setTitle(title.getText().toString());
        EditText synopsis = (EditText) findViewById(R.id.synopsisInput);
        bookObj.setSynopsis(synopsis.getText().toString());
        EditText author = (EditText) findViewById(R.id.authorInput);
        bookObj.setAuthor(author.getText().toString());
        EditText editor = (EditText) findViewById(R.id.editorNameInput);
        bookObj.setEditorName(editor.getText().toString());
        EditText numberOfPages = (EditText) findViewById(R.id.numberOfPagesInput);
        bookObj.setNumberOfPages(numberOfPages.getText().toString());
        Spinner language = findViewById(R.id.languageSelect);
        bookObj.setBookLanguage(language.getSelectedItem().toString());
        EditText gender = (EditText) findViewById(R.id.gendersInput);
        bookObj.setGender(Functions.parseToInteger(gender.getText().toString()));
        EditText releaseDate = (EditText) findViewById(R.id.releaseDateInput);
        bookObj.setReleaseDate(releaseDate.getText().toString());
        bookObj.setBookCover(bookCoverBitMap);
        return bookObj;
    }

    public Boolean saveBook(){

        return true;
    }

    public void listAndBuildGenderDropdown(){

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Response of Image");
        System.out.println(requestCode);
        switch(requestCode) {
            case 100:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    bookCoverBitMap = yourSelectedImage;
                    bookCoverButton.setImageBitmap(yourSelectedImage);
                    /* Now you have choosen image in Bitmap format in object "yourSelectedImage". You can use it in way you want! */
                }
        }
    };

    public void setDefaultBookCover(){
        bookCoverBitMap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.guardalivros_logo);
    }

    public String fetchCurrentInfo(){
//        here will fetch all data, the same method may be used to save the book with gathered data
        return "";
    }

    private void updateDateOfRelease(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat);
        dateOfReleaseInput.setText(dateFormat.format(myCalendar.getTime()));
    }
}
