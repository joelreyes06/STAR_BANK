package com.example.star_bank;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.star_bank.Conexion.conexion;
import com.example.star_bank.Modelos.rlogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText txtUser, txtPass;

    Button btnIngresar;
    TextView btnRegistrar, btnRecordarPass;

    List<rlogin> usuario;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnIngresar = (Button) findViewById(R.id.btnIngresar);
        btnRegistrar = (TextView) findViewById(R.id.btnRegistrar);
        btnRecordarPass = (TextView) findViewById(R.id.btnRecordarPass);

        txtUser = (EditText) findViewById(R.id.txtUser);
        txtPass = (EditText) findViewById(R.id.txtPass);

        progressDialog = new ProgressDialog(this);
        usuario = new ArrayList<>();

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarLogin();
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RLoginActivity.class);
                startActivity(intent);
            }
        });

        btnRecordarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecuperarActivity.class);
                startActivity(intent);
            }
        });
    }


    //VALIDAMOS EL LOGIN CON LOS CAMPOS QUE SE REQUIEREN
    public void validarLogin() {

        boolean[] result = {
                validarcampoUser(),
                validarcampoPassword(),
                validarcampoUser(),
                validarcampoPassword()
        };

        if(result[0] && result[1] && result[2] && result[3]) {
            usuario_query(txtUser.getText().toString().trim());
        }
    }

    //VALIDAMOS QUE NO QUEDEN CAMPOS VACIOS
    private boolean validarcampoUser() {
        String user = txtUser.getText().toString();

        if (user.isEmpty()) {
            txtUser.setError("El campo usuario no puede estar vacío");
            return false;

        } else {
            txtUser.setError(null);
            return true;
        }
    }
    //VALIDAMOS QUE NO QUEDEN CAMPOS VACIOS
    private boolean validarcampoPassword() {
        String password = txtPass.getText().toString();

        if (password.isEmpty()) {
            txtPass.setError("El campo contraseña no puede estar vacio");
            return false;

        } else {
            txtPass.setError(null);
            return true;
        }
    }

    //VERIFICAMOS QUE EL USUARIO CONCUERDE CON LOS DATOS CORRECTOS
    public void usuario_query(String user) {
        usuario.clear();

        progressDialog.setMessage("Cargando");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, conexion.URL_SELECT_ID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try{
                    progressDialog.hide();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    if (jsonObject.getString("total").equals("0")) {
                        txtUser.setError("Usuario incorrecto");
                        txtPass.setError("Contraseña incorrecta");
                    } else {
//                        Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                    for (int i = 0; i<jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);
                        rlogin item = new rlogin(
                                o.getString("id_usuarios"),
                                o.getString("user"),
                                o.getString("pass"),
                                o.getString("nombre"),
                                o.getString("ncuenta")
                        );

                        usuario.add(item);
                        int position = 0;
                        final rlogin queryItem = usuario.get(position);

                        if (txtUser.getText().toString().equals(queryItem.getUser()) && txtPass.getText().toString().equals(queryItem.getPass())) {
                            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                            intent.putExtra("id_usuario_Emisor", queryItem.getId_usuarios());
                            intent.putExtra("numCuenta", queryItem.getNcuenta());
                            intent.putExtra("nombre", queryItem.getNombre());
                            startActivity(intent);

                            limpiarTextos();

                        } else {
                            txtUser.setError("Usuario incorrecto");
                            txtPass.setError("Contraseña incorrecta");
                            Toast.makeText(getApplicationContext(), "Vuelva a intentar en otro momento", Toast.LENGTH_SHORT).show();
                            limpiarTextos();
                        }

                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("Error_log", error.getMessage());
            }
        }){
            protected Map<String , String> getParams() throws AuthFailureError {
                Map<String , String> params = new HashMap<>();
                params.put("user", user);
                return params;
            }
        };
        RequestHandler.getInstance(MainActivity.this).addToRequestQueue(stringRequest);
    }

    //LIMIPIAMOS LOS CAMPOS YA CONFIRMADOS
    private void limpiarTextos() {
        txtUser.setText("");
        txtPass.setText("");
    }

}