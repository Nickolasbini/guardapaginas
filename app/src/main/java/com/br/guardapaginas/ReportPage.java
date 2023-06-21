package com.br.guardapaginas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.br.guardapaginas.classes.BookBorrowing;
import com.br.guardapaginas.databinding.ActivityMainBinding;
import com.br.guardapaginas.helpers.Functions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReportPage extends AppCompatActivity {

    ActivityMainBinding binding;

    final Calendar fromCalendar = Calendar.getInstance();
    final Calendar toCalendar   = Calendar.getInstance();
    String dateInputInUse       = "fromDate";

    EditText      fromDateInput;
    EditText      toDateInput;
    Button        generateLendBooksBtn;
    Button        generateDelayedBooksBtn;
    Button        generateBestReadersBtn;
    Button        generateWorstLendBooksBtn;
    Button        generateBestLendBooksBtn;
    BookBorrowing object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_report_page);

        Functions.setSystemColors(this);
        setTitle("Relatórios");

        initTags();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
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

        fromDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateInputInUse = "fromDate";
                new DatePickerDialog(ReportPage.this, date, fromCalendar.get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH), fromCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        toDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateInputInUse = "toDate";
                new DatePickerDialog(ReportPage.this, date, toCalendar.get(Calendar.YEAR), toCalendar.get(Calendar.MONTH), toCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        generateLendBooksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateReport(1);
            }
        });

        generateDelayedBooksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateReport(2);
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
    }

    public void initTags(){
        object                    = new BookBorrowing(getApplicationContext());
        fromDateInput             = (EditText) findViewById(R.id.fromDateInput);
        toDateInput               = (EditText) findViewById(R.id.toDateInput);
        generateLendBooksBtn      = (Button) findViewById(R.id.generateLendBooksBtn);
        generateDelayedBooksBtn   = (Button) findViewById(R.id.generateDelayedBooksBtn);
        generateBestReadersBtn    = (Button) findViewById(R.id.generateBestReadersBtn);
        generateWorstLendBooksBtn = (Button) findViewById(R.id.generateWorstLendBooksBtn);
        generateBestLendBooksBtn  = (Button) findViewById(R.id.generateBestLendBooksBtn);
    }

    private void updateDateOfInput(String inputName) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat);
        if(inputName.equals("fromDate")) {
            fromDateInput.setText(dateFormat.format(fromCalendar.getTime()));
        }else{
            toDateInput.setText(dateFormat.format(toCalendar.getTime()));
        }
    }

    private void generateReport(Integer flag){
        String[] dateInputs = new String[2];
        dateInputs[0] = fromDateInput.getText().toString();
        dateInputs[1] = toDateInput.getText().toString();
        String dataToUse = object.createReport(flag, dateInputs);
        createPDF(dataToUse);
    }

    public void addMessageToToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void createPDF(String textToUse){
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1080, 1920, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canva = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        paint.setTextAlign(Paint.Align.CENTER);

        float x = (canva.getWidth() / 2);
        float y = (int) ((canva.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;
        canva.drawText(textToUse, x, y, paint);
        document.finishPage(page);

        File dowloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName  = Functions.getUniqueText() + "-" + object.getUserName() + ".pdf";
        File file = new File(dowloadsDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            addMessageToToast("Arquivo Gerado. Visite sua pasta de downloads");
        } catch (FileNotFoundException e) {
            addMessageToToast("Um problema ocorreu, tente novamente");
        } catch (IOException e) {
            addMessageToToast("Um problema ocorreu, tente novamente");
        }
    }
}