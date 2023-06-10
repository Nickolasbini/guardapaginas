package com.br.guardapaginas;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.br.guardapaginas.classes.Book;
import com.br.guardapaginas.databinding.ActivityMainBinding;
import com.br.guardapaginas.helpers.Functions;
import com.br.guardapaginas.views.APIListOfBooks;
import com.br.guardapaginas.views.CaptureScren;
import com.br.guardapaginas.views.GenderSelection;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class SaveBookView extends AppCompatActivity {
    ActivityMainBinding binding;

    final Calendar myCalendar = Calendar.getInstance();

    Integer SELECT_IMAGE;
    ImageView bookCoverButton;
    Bitmap bookCoverBitMap;
    EditText dateOfReleaseInput;
    TextView bookId;
    Integer idOfCurrentBook;
    byte[] bookCoverByte;
    EditText bookTitle;
    EditText bookSynopsis;
    EditText bookAuthor;
    EditText bookEditorName;
    EditText bookNumberOfPages;
    Spinner bookLanguage;
    EditText bookGenders;
    EditText bookReleaseDate;
    ImageView bookCover;
    String[] languageItems;
    Book bookObject;
    Spinner genderSelect;
    EditText quantityInput;

    String[] gendersId;
    String[] gendersName;
    Button inactiveUserBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.save_book);

        Functions.setSystemColors(this);
        initInputs();
        setDefaultBookCover();

        Intent intent = getIntent();
        String idOfBook = intent.getStringExtra("BOOK_ID");
        idOfCurrentBook = Functions.parseToInteger(idOfBook);
        Button getByCode = (Button) findViewById(R.id.fetchBookByCode);
        Button saveBookButton = (Button) findViewById(R.id.saveBookBorrowButton);
        inactiveUserBtn = (Button) findViewById(R.id.inactiveBookBorrowBtn);
        if (idOfCurrentBook > 0) {
            setTitle("Editar Livro");
            getByCode.setVisibility(View.GONE);
            saveBookButton.setText("Salvar");
            inactiveUserBtn.setVisibility(View.VISIBLE);
        } else {
            setTitle("Cadastrar Livro");
            getByCode.setVisibility(View.VISIBLE);
            saveBookButton.setText("Cadastrar");
            inactiveUserBtn.setVisibility(View.GONE);
        }
        fillFields();

        ImageView goBackBtn = (ImageView) findViewById(R.id.goBackBtn);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(Activity.RESULT_OK, i);
                finishActivity(1);
                finish();
            }
        });

        Spinner dropdown = findViewById(R.id.languageSelect);
        languageItems = new Book(getApplicationContext()).getAvaliableLanguages();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, languageItems);
        dropdown.setAdapter(adapter);

        bookCoverButton = (ImageView) findViewById(R.id.bookCoverButton);
        bookCoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                final int ACTIVITY_SELECT_IMAGE = 100;
                startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
            }
        });

        dateOfReleaseInput = (EditText) findViewById(R.id.releaseDateInput);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateDateOfRelease();
            }
        };
        dateOfReleaseInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(SaveBookView.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button saveButton = (Button) findViewById(R.id.saveBookBorrowButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyInputs() == true) {
                    Boolean result = saveBook();
                    if (result) {
                        Intent i = new Intent();
                        setResult(Activity.RESULT_OK, i);
                        finishActivity(1);
                        finish();
                    }
                }
            }
        });

        ImageView addGender = (ImageView) findViewById(R.id.addNewGender);
        addGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gendersId != null && gendersId.length >= 3) {
                    addMessageToToast("Número máximo de Gêneros selecionados");
                    return;
                }
                Intent genderIntent = new Intent(getApplicationContext(), GenderSelection.class);
                genderIntent.putExtra("CURRENT_GENDER_IDS", Functions.implode(gendersId, ","));
                genderIntent.putExtra("CURRENT_GENDER_NAMES", Functions.implode(gendersName, ","));
                genderIntent.putExtra("ACTION", "ADD");
                startActivityForResult(genderIntent, 200);
            }
        });

        ImageView removeGender = (ImageView) findViewById(R.id.removeLastGender);
        removeGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent genderIntent = new Intent(getApplicationContext(), GenderSelection.class);
                genderIntent.putExtra("CURRENT_GENDER_IDS", Functions.implode(gendersId, ","));
                genderIntent.putExtra("CURRENT_GENDER_NAMES", Functions.implode(gendersName, ","));
                genderIntent.putExtra("ACTION", "REMOVE");
                startActivityForResult(genderIntent, 200);
            }
        });

        inactiveUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookObject == null || bookObject.getId() < 1) {
                    addMessageToToast("Ação não permitida");
                    return;
                }
                Boolean result = bookObject.inactiveActiveBook();
                if (!result) {
                    addMessageToToast("Um problema ocorreu, tente novamente");
                    return;
                }
                addMessageToToast("Status do livro atualizado");
                fillFields();
            }
        });

        getByCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), APIListOfBooks.class);
                startActivityForResult(i, 29);
            }
        });
    }

    public void initInputs() {
        bookTitle = (EditText) findViewById(R.id.nameInput);
        bookSynopsis = (EditText) findViewById(R.id.lastNameInput);
        bookAuthor = (EditText) findViewById(R.id.cpfInput);
        bookEditorName = (EditText) findViewById(R.id.emailInput);
        bookNumberOfPages = (EditText) findViewById(R.id.numberOfPagesInput);
        bookLanguage = (Spinner) findViewById(R.id.languageSelect);
        bookGenders = (EditText) findViewById(R.id.gendersInput);
        bookReleaseDate = (EditText) findViewById(R.id.releaseDateInput);
        bookCover = (ImageView) findViewById(R.id.bookCoverButton);
        genderSelect = (Spinner) findViewById(R.id.genderSelect);
        quantityInput = (EditText) findViewById(R.id.quantityInput);
    }

    public void fillFields() {
        Book bookObj = new Book(getApplicationContext());
        if (idOfCurrentBook > 0) {
            Book obj = new Book(getApplicationContext());
            bookObj = obj.findById(idOfCurrentBook);
            if (bookObj == null) {
                // Empty Book Object
                bookObject = bookObj;
                return;
            }
            //  Feed Data By Found Book Object
            bookTitle.setText(bookObj.getTitle());
            bookSynopsis.setText(bookObj.getSynopsis());
            bookAuthor.setText(bookObj.getAuthor());
            bookEditorName.setText(bookObj.getEditorName());
            bookNumberOfPages.setText(bookObj.getNumberOfPages());
            Integer indexOfLanguage = bookObj.getLanguagePosition();
            if (indexOfLanguage != null)
                bookLanguage.setSelection(indexOfLanguage);
            bookGenders.setText(bookObj.getTitle());
            bookReleaseDate.setText(bookObj.getFormatedReleasedDate());
            quantityInput.setText(bookObj.getQuantityAsNumber());
            bookCover.setImageBitmap(Functions.parseByteArrayToBitMap(bookObj.getBookCover()));
            bookCoverByte = bookObj.getBookCover();
            bookObject = bookObj;
            List<String[]> gendersData = bookObj.getGendersIdsAndNames();
            gendersId = gendersData.get(0);
            gendersName = gendersData.get(1);
            if (gendersId.length > 0) {
                ArrayAdapter<String> genderSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, gendersName);
                genderSelect.setAdapter(genderSpinnerAdapter);
            }
            String inactiveActiveButtonText = (bookObj.getStatus().equals(bookObj.ACTIVE) ? "Inativar" : "Ativar");
            inactiveUserBtn.setText(inactiveActiveButtonText);
        } else {
            //  Empty Book Object
            bookObject = bookObj;
        }
    }

    public Boolean verifyInputs() {
        bookTitle = (EditText) findViewById(R.id.nameInput);
        bookGenders = (EditText) findViewById(R.id.gendersInput);
        if (bookTitle.getText().toString().equals("") || bookTitle.getText().toString() == null) {
            addMessageToToast("Informe o título do livro");
            return false;
        }
        if (bookGenders.getText().toString().equals("") || bookGenders.getText().toString() == null) {
            addMessageToToast("Informe ao menos um Gênero do livro");
            return false;
        }
        return true;
    }

    public Book extractBookData() {
        Book bookObj = new Book(getApplicationContext());
        if (idOfCurrentBook > 0)
            bookObj.setId(idOfCurrentBook);
        EditText title = (EditText) findViewById(R.id.nameInput);
        bookObj.setTitle(title.getText().toString());
        EditText synopsis = (EditText) findViewById(R.id.lastNameInput);
        bookObj.setSynopsis(synopsis.getText().toString());
        EditText author = (EditText) findViewById(R.id.cpfInput);
        bookObj.setAuthor(author.getText().toString());
        EditText editor = (EditText) findViewById(R.id.emailInput);
        bookObj.setEditorName(editor.getText().toString());
        EditText numberOfPages = (EditText) findViewById(R.id.numberOfPagesInput);
        bookObj.setNumberOfPages(numberOfPages.getText().toString());
        Spinner language = findViewById(R.id.languageSelect);
        bookObj.setBookLanguage(language.getSelectedItem().toString());
        bookObj.setGenders(gendersId);
        EditText releaseDate = (EditText) findViewById(R.id.releaseDateInput);
        bookObj.setReleaseDate(releaseDate.getText().toString());
        bookObj.setBookCover(bookCoverByte);
        bookObj.setQuantity(quantityInput.getText().toString());
        return bookObj;
    }

    public Boolean saveBook() {
        bookObject = extractBookData();
        Boolean result = bookObject.save(bookObject);
        if (!result) {
            addMessageToToast("Um problema ocorreu, tente novamente");
            return false;
        } else {
            String message = idOfCurrentBook > 0 ? "Livro atualizado com sucesso" : "Livro cadastrado com sucesso";
            addMessageToToast(message);
            return true;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    InputStream iStream = null;
                    try {
                        iStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        bookCoverByte = getBytes(iStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imageInByte = stream.toByteArray();
                    long lengthbmp = imageInByte.length;
                    if (lengthbmp >= 100000) {
                        addMessageToToast("Imagem muito grande!");
                        return;
                    }
                    bookCoverBitMap = yourSelectedImage;
                    bookCoverButton.setImageBitmap(yourSelectedImage);
                }
                break;
            case 200:
                if (data != null) {
                    String genderIdsString = data.getStringExtra("UPDATED_GENDER_IDS");
                    String genderNamesString = data.getStringExtra("UPDATED_GENDER_NAMES");
                    if (genderIdsString == null)
                        return;
                    gendersId = Functions.explode(genderIdsString, ",");
                    gendersName = Functions.explode(genderNamesString, ",");
                    bookGenders.setText(genderNamesString);
                    Integer sizeOfArray = gendersName.length;
                    String[] arrayToUse = new String[sizeOfArray];
                    for (Integer i = 0; i < sizeOfArray; i++) {
                        String value = gendersName[i];
                        arrayToUse[i] = value;
                    }
                    ArrayAdapter<String> genderSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayToUse);
                    genderSelect.setAdapter(genderSpinnerAdapter);
                }
                break;
            case 29:
                // Book api return
                if(data != null){
                    String selectedObjectJSON = data.getStringExtra("SELECTED_BOOK_FROM_API");
                    if(selectedObjectJSON == null || selectedObjectJSON.equals("")){
                        addMessageToToast("Um problema ocorreu, tente novamente");
                        return;
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(selectedObjectJSON);
                        bookTitle.setText(jsonObject.getString("title"));
                        bookSynopsis.setText(jsonObject.getString("synopsis"));
                        bookAuthor.setText(jsonObject.getString("author"));
                        bookEditorName.setText(jsonObject.getString("editorName"));
                        bookNumberOfPages.setText(jsonObject.getString("numberOfPages"));
                        bookObject.setBookLanguage(jsonObject.getString("bookLanguage"));
                        bookLanguage.setSelection(bookObject.getLanguagePosition());
                        bookObject.setReleaseDate(jsonObject.getString("releaseDate"));
                        if(bookObject.getReleaseDate() != null)
                            bookObject.setReleaseDate(Functions.parseEnToPt(bookObject.getReleaseDate()));
                        bookReleaseDate.setText(bookObject.getFormatedReleasedDate());
                        bookCoverByte   = Base64.decode(jsonObject.getString("bookCover"), Base64.NO_WRAP);
                        bookCoverBitMap = Functions.parseByteArrayToBitMap(bookCoverByte);
                        bookCover.setImageBitmap(bookCoverBitMap);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        addMessageToToast("Um problema ocorreu, tente novamente");
                    }
                }else{
                    addMessageToToast("Um problema ocorreu, tente novamente");
                }
                break;
        }
    }

    ;

    public void setDefaultBookCover() {
        bookCoverBitMap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.guardalivros_logo);
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.guardalivros_logo);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitMapData = stream.toByteArray();
        bookCoverByte = bitMapData;
    }

    private void updateDateOfRelease() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat);
        dateOfReleaseInput.setText(dateFormat.format(myCalendar.getTime()));
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void addMessageToToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
