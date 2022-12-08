package com.example.star_bank;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

public class CuentasActivity extends AppCompatActivity {
    TextView tvNumeroCuenta, tvNombreUser;
    EditText date;
    DatePickerDialog datePickerDialog;
    private String numCuenta;
    private String nombreUser;
    private String id_usuario_Receptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas);

       /* tvNumeroCuenta = (TextView) findViewById(R.id.tvNumeroCuenta);
        tvNombreUser = (TextView) findViewById(R.id.tvNombreUser);

        id_usuario_Receptor = getIntent().getExtras().getString("id_usuario_Emisor");
        numCuenta = getIntent().getExtras().getString("numCuenta");
        nombreUser = getIntent().getExtras().getString("nombre");

        tvNumeroCuenta.setText(numCuenta);
        tvNombreUser.setText(nombreUser);*/

        date = (EditText) findViewById(R.id.date);
        // perform click event on edit text
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(CuentasActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                date.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

    }

}