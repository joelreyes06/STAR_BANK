package com.example.star_bank;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class RealizarTranActivity extends AppCompatActivity {

    EditText id_emisor, cuenta_emisor;
    EditText cuenta_Receptor;

    EditText cantidad, fecha, descripcion;
    Button btnConfirmar;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_tran);

        cuenta_emisor = (EditText) findViewById(R.id.txtEmisor);
        cuenta_Receptor = (EditText) findViewById(R.id.txtReceptor);
        cantidad = (EditText) findViewById(R.id.txtCanti);
        fecha = (EditText) findViewById(R.id.txtFec);
        descripcion = (EditText) findViewById(R.id.txtDescrip);

        btnConfirmar = (Button) findViewById(R.id.btnConf);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearTrans();
            }
        });
    }




    private void crearTrans() {
        /*cuenta_emisor.getText().toString();
        cuenta_Receptor.getText().toString();
        cantidad.getText().toString();
        fecha.getText().toString();
        descripcion.getText().toString();*/


                progressDialog.setMessage("Cargando");
                progressDialog.show();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, conexion.URL_ADD_TRAN, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(RealizarTranActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            if(jsonObject.getString("message").equals("Transaccion realizada con exito")) {
//                                    ListarActivity.ma.refresh_list();
//                                    finish();
                                //limpiar();
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RealizarTranActivity.this,  e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(RealizarTranActivity.this, "Error en los datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
                    protected Map<String , String> getParams() throws AuthFailureError {
                        Map<String , String> params = new HashMap<>();
                        params.put("cuenta_Emisor", String.valueOf(cuenta_emisor));
                        params.put("cuenta_Receptor", String.valueOf(cuenta_Receptor));
                        params.put("cantidad", String.valueOf(cantidad));
                        params.put("fecha", String.valueOf(fecha));
                        params.put("descripcion", String.valueOf(descripcion));
                        return params;
                    }
                };
                RequestHandler.getInstance(RealizarTranActivity.this).addToRequestQueue(stringRequest);

        }

}