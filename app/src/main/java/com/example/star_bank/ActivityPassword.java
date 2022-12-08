package com.example.star_bank;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.star_bank.Conexion.conexion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityPassword extends AppCompatActivity {

    //ASIGNAMOS LAS VARIABLES
    EditText txtPassNueva, txtPassConfirmacion;
    Button btnConfirmarPass;

    String user;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        txtPassNueva = (EditText) findViewById(R.id.txtPassNueva);
        txtPassConfirmacion = (EditText) findViewById(R.id.txtPassConfirmacion);

        btnConfirmarPass = (Button) findViewById(R.id.btnConfirmarPass);


        progressDialog = new ProgressDialog(this);

        user = getIntent().getExtras().getString("user");


        btnConfirmarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarPass();
            }
        });
    }

//CON ESTA FUNCION VALIDAMOS LA CONTRASEÑA DEL USUARIO COMO UN BOLEANO PARA EVITAR QUE LAS CONTRASEÑAS SEAN DIFERENTES
    public void validarPass() {
        boolean[] result = {
                validarPassNueva(),
                validarPassConfirmacion(),
        };

        if(result[0] && result[1]) {
            if (txtPassNueva.getText().toString().trim().equals(txtPassConfirmacion.getText().toString().trim())) {
                actualizarPass(txtPassConfirmacion.getText().toString().trim());

            } else {
                Toast.makeText(ActivityPassword.this, "Error, las contraseñas no son iguales", Toast.LENGTH_SHORT).show();
                limpiar();
            }

        }
    }

    //LIMPIAMOS UNA VEZ CONFIRMADO LOS DATOS
    private void limpiar() {
        txtPassNueva.setText("");
        txtPassConfirmacion.setText("");
        txtPassNueva.requestFocus();
    }


    //ACTUALIZAMOS LA CONTRASEÑA CUANDO SE CONFIRME EL TOKEN
    private void actualizarPass(String newPass) {
        progressDialog.setMessage("Cargando");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, conexion.URL_UPDATE_PASS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("message").equals("Dato actualizado con exito")){
                        Toast.makeText(ActivityPassword.this, "Exito", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);

                        finish();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ActivityPassword.this, "Error(JSONException): " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(ActivityPassword.this, "Error en los datos: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String , String> getParams() throws AuthFailureError {
                Map<String , String> params = new HashMap<>();
                params.put("user", user);
                params.put("pass", newPass);
                return params;
            }
        };
        RequestHandler.getInstance(ActivityPassword.this).addToRequestQueue(stringRequest);
    }

    //VOLVEMOS A VALIDAR SI LAS CONTRASEÑAS SON IGUALES
    private boolean validarPassNueva() {
        String passNueva = txtPassNueva.getText().toString();
        if (passNueva.isEmpty()) {
            txtPassNueva.setError("El campo nueva contraseña no puede estar vacío");
            return false;

        } else {
            txtPassNueva.setError(null);
            return true;
        }
    }

    //CONFIRMAMOS QUE NO QUEDEN CAMPOS VACIOS
    private boolean validarPassConfirmacion() {
        String passConfi = txtPassConfirmacion.getText().toString();
        if (passConfi.isEmpty()) {
            txtPassConfirmacion.setError("El campo confirmacion contraseña no puede estar vacío");
            return false;

        } else {
            txtPassConfirmacion.setError(null);
            return true;
        }
    }
}