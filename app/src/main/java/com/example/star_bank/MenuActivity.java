package com.example.star_bank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {
    TextView tvNumeroCuenta, tvNombreUser;

    Button btnAdminCuentas, btnPagarServicios, btnTranferencia, btnReportes, btnSalir;

    private String numCuenta;
    private String nombreUser;
    private String id_usuario_Receptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        tvNumeroCuenta = (TextView) findViewById(R.id.tvNumeroCuenta);
        tvNombreUser = (TextView) findViewById(R.id.tvNombreUser);

        btnAdminCuentas = (Button) findViewById(R.id.btnAdminCuentas);
        btnPagarServicios = (Button) findViewById(R.id.btnPagarServicios);
        btnTranferencia = (Button) findViewById(R.id.btnTranferencia);
        btnReportes = (Button) findViewById(R.id.btnReportes);
        btnSalir = (Button) findViewById(R.id.btnSalir);

        id_usuario_Receptor = getIntent().getExtras().getString("id_usuario_Emisor");
        numCuenta = getIntent().getExtras().getString("numCuenta");
        nombreUser = getIntent().getExtras().getString("nombre");

        tvNumeroCuenta.setText(numCuenta);
        tvNombreUser.setText(nombreUser);

        btnAdminCuentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CuentasActivity.class);
                startActivity(intent);
            }
        });

        btnPagarServicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PagosServicioActivity.class);
                startActivity(intent);
            }
        });

        btnTranferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RealizarTranActivity.class);
                startActivity(intent);

                // Enviar el dato del id y el numero de cuenta
            }
        });

        btnReportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), CuentasActivity.class);
//                startActivity(intent);
            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }
}