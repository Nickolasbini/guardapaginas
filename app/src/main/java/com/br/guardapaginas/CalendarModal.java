package com.br.guardapaginas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.br.guardapaginas.databinding.ActivityMainBinding;
import com.br.guardapaginas.helpers.Functions;

public class CalendarModal extends AppCompatActivity {

    ActivityMainBinding binding;
    String chosenDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.date_modal);

//        here gather the EXTRA and later sent it back

        ImageView closeButton = (ImageView) findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        CalendarView calendarInput = (CalendarView) findViewById(R.id.calendarInput);
        calendarInput.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                chosenDate = Functions.parseToString(day) + "/" + Functions.parseToString(month) + "/" + Functions.parseToString(year);

                finish();
            }
        });
    };
}
