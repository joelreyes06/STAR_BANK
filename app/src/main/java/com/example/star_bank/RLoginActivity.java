package com.example.star_bank;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.PatternsCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class RLoginActivity extends AppCompatActivity {

    EditText txtNombreR, txtEmailR, txtTelR, txtDirR;
    Button btn_rl_Cancelar, btn_rl_Ingresar;

    private int numToken = 0;
    private String cuentaCurrent = "";

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rlogin);

        txtNombreR = (EditText) findViewById(R.id.txtNombreR);
        txtEmailR = (EditText) findViewById(R.id.txtEmailR);
        txtTelR = (EditText) findViewById(R.id.txtTelR);
        txtDirR = (EditText) findViewById(R.id.txtDirR);

        btn_rl_Cancelar = (Button) findViewById(R.id.btn_rl_Cancelar);
        btn_rl_Ingresar = (Button) findViewById(R.id.btn_rl_Ingresar);

        progressDialog = new ProgressDialog(this);

        btn_rl_Ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarLogin();
            }
        });

        btn_rl_Cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelar();
            }
        });

    }

    //VALIDAMOS CORRECTO DATOS DEL LOGIN
    public void validarLogin() {
        boolean[] result = {
                validarNombre(),
                validarEmail(),
                validarTelefono(),
                validarDireccion()
        };

        if(result[0] && result[1] && result[2] && result[3]) {
            generarUsuario();
        }
    }

    //GENERAMOS ALEATORIAMENTE EL USUARIO BRINDADO POR EL BANCO
    private void generarUsuario() {
        progressDialog.setMessage("Cargando");
        progressDialog.show();

        final String[] id_registro = new String[1];

        StringRequest stringRequest = new StringRequest(Request.Method.POST, conexion.URL_GENERATE_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    id_registro[0] = jsonObject.getString("id_registro");
                    if(jsonObject.getString("message").equals("Usuario generado con exito")) {
                        crearUsuario(id_registro[0]);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RLoginActivity.this, "Ya existe un usuario con el numero de telefono o correo", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(RLoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String , String> getParams() throws AuthFailureError {
                Map<String , String> params = new HashMap<>();
                params.put("user", txtTelR.getText().toString().trim());
                return params;
            }
        };
        RequestHandler.getInstance(RLoginActivity.this).addToRequestQueue(stringRequest);
    }


    //SE CREA EL USUARIO CON SUS DATOS
    private void crearUsuario(String idUsuario) {
        progressDialog.setMessage("Cargando");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, conexion.URL_ADD_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("message").equals("Datos agregado con exito")) {
                        codigoVerificacion();

                        crearCuenta(idUsuario);

                        Intent intent = new Intent(getApplicationContext(), ActivityConfirmacionn.class);
                        intent.putExtra("correo", txtEmailR.getText().toString().trim());
                        intent.putExtra("token", numToken);
                        intent.putExtra("nombre", txtNombreR.getText().toString());
                        intent.putExtra("user", txtTelR.getText().toString());
                        startActivity(intent);

                        limpiar();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RLoginActivity.this, "Error(JSONException): " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(RLoginActivity.this, "Error(Volley): " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("Error(Volley)", "" + error.getMessage());
            }
        }){
            protected Map<String , String> getParams() throws AuthFailureError {
                Map<String , String> params = new HashMap<>();
                params.put("id_usuarios", idUsuario);
                params.put("nombre", txtNombreR.getText().toString());
                params.put("email", txtEmailR.getText().toString().trim());
                params.put("telefono", txtTelR.getText().toString().trim());
                params.put("direccion", txtDirR.getText().toString());
                return params;
            }
        };
        RequestHandler.getInstance(RLoginActivity.this).addToRequestQueue(stringRequest);
    }

    //SE CREA LA CUENTA DEL POSIBLE USUARIO A REGISTRARSE
    private void crearCuenta(String idUsuario) {
        double cantidad = 0.00;

        Date d = new Date();
        CharSequence s = DateFormat.format("dd/MM/yyyy ", d.getTime());

        generarCuenta();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, conexion.URL_ADD_CUENTA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("message").equals("Cuenta creada con exito")) {
                        //
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RLoginActivity.this, "Error(JSONException): " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(RLoginActivity.this, "Error(Volley): " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("Error(Volley)", "" + error.getMessage());
            }
        }){
            protected Map<String , String> getParams() throws AuthFailureError {
                Map<String , String> params = new HashMap<>();
                params.put("id_usuarios", idUsuario);
                params.put("ncuenta", cuentaCurrent);
                params.put("fecha", (String) s);
                params.put("cantidad", String.valueOf(cantidad));
                return params;
            }
        };
        RequestHandler.getInstance(RLoginActivity.this).addToRequestQueue(stringRequest);
    }

    //GENERAMOS LA CUENTA
    private void generarCuenta() {
        Random rand = new Random();
        int randCuenta = rand.nextInt(90000) + 10000;

        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss" + randCuenta);
        cuentaCurrent = sdf.format(new Date());
    }


    private void limpiar() {
        txtNombreR.setText("");
        txtEmailR.setText("");
        txtTelR.setText("");
        txtDirR.setText("");
    }

//VALIDAMOS TODOS LOS CAMPOS DE NOMBRE, EMAIL, TEL, DIRECCION, ETC.
    private boolean validarNombre() {
        String nombre = txtNombreR.getText().toString();
        if (nombre.isEmpty()) {
            txtNombreR.setError("El campo nombre no puede estar vacío");
            return false;

        } else {
            txtNombreR.setError(null);
            return true;
        }
    }
    //VALIDAMOS TODOS LOS CAMPOS DE NOMBRE, EMAIL, TEL, DIRECCION, ETC.
    private boolean validarEmail() {
        String email = txtEmailR.getText().toString();
        if (email.isEmpty()) {
            txtEmailR.setError("El campo email no puede estar vacío");
            return false;

        } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmailR.setError("Por favor, introduzca una dirección de correo electrónico válida");
            return false;

        } else {
            txtEmailR.setError(null);
            return true;
        }
    }
    //VALIDAMOS TODOS LOS CAMPOS DE NOMBRE, EMAIL, TEL, DIRECCION, ETC.
    private boolean validarTelefono() {
        String telefono = txtTelR.getText().toString();
        if (telefono.isEmpty()) {
            txtTelR.setError("El campo telefono no puede estar vacío");
            return false;

        } else {
            txtTelR.setError(null);
            return true;
        }
    }
    //VALIDAMOS TODOS LOS CAMPOS DE NOMBRE, EMAIL, TEL, DIRECCION, ETC.
    private boolean validarDireccion() {
        String direccion = txtDirR.getText().toString();
        if (direccion.isEmpty()) {
            txtDirR.setError("El campo direccion no puede estar vacío");
            return false;

        } else {
            txtDirR.setError(null);
            return true;
        }
    }
//CANCELAMOS Y LIMPIAMOS CAMPOS
    private void cancelar() {
        txtNombreR.setText("");
        txtEmailR.setText("");
        txtTelR.setText("");
        txtDirR.setText("");
        finish();
    }


    //ENVIAMOS EN CODIGO DE VERIFICACION  AL CORREO QUE EL USUARIO ASIGNO
    private void codigoVerificacion() {
        generarToken();

        String sEmail, sPassword;
        String aTo = txtEmailR.getText().toString().trim(),
                aSubject = "Verificacion de Correo en Star Bank";
        String aMessage = "Hola,<br>" +
                "<br>" +
                "Gracias por registrarse en nuestra app. Para completar su registro, copie el token en la app para verificar su cuenta:<br>" +
                "<br>" +
                "[<b>" + numToken + "</b>]<br>" +
                "<br>" +
                "Gracias,<br>" +
                "<br>" +
                "[<i>" + txtNombreR.getText().toString() + "</i>]";

        sEmail = "coopersteam5@gmail.com";
        sPassword = "otcxomogcjbrsjbc";

        Properties properties =  new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sEmail, sPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sEmail));

            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(aTo.toString().trim()));

            message.setSubject(aSubject.toString().trim());

            message.setContent(aMessage.toString().trim(), "text/html");

            new SenMail().execute(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    //SE GENERA TOKEN
    private void generarToken() {
        Random rand = new Random();
        numToken = rand.nextInt(90000) + 10000;
    }

    private class SenMail extends AsyncTask<Message, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(RLoginActivity.this,
                    "Por favor espere",
                    "Enviando Email de token.....",
                    true,
                    false);
        }

        @Override
        protected String doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
                return "Success";

            } catch (MessagingException e) {
                e.printStackTrace();
                return e.toString();
            }
        }


        //TOKEN RECIBIDO CON EXITO
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
            if (s.equals("Success")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RLoginActivity.this);
                builder.setCancelable(false);
                builder.setTitle(Html.fromHtml("<font color='#FEB536'>Token</font>"));
                builder.setMessage("El token se ha enviado con exito.");
                builder.setPositiveButton("Listo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            } else {
                Toast.makeText(RLoginActivity.this, "¿Algo salio mal?", Toast.LENGTH_SHORT).show();
//                Toast.makeText(RLoginActivity.this, "Error: " + s.toString(), Toast.LENGTH_SHORT).show();
                Log.i("Error", "" + s);
            }
        }
    }

}