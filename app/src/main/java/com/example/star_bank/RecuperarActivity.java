package com.example.star_bank;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
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
import com.example.star_bank.Modelos.lrecu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class RecuperarActivity extends AppCompatActivity {

    Button btnRecuperar;
    EditText txtrecuUser;

    private int numToken = 0;

    List<lrecu> usuario;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);

        txtrecuUser = (EditText) findViewById(R.id.txtrecuUser);
        btnRecuperar = (Button) findViewById(R.id.btnRecuperar);

        progressDialog = new ProgressDialog(this);
        usuario = new ArrayList<>();

        btnRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarLogin();
            }
        });
    }

    //VALIDAMOS QUE LOS CAMPOS DEL USUARIO ESTEN CORRECTO PARA LA RECUPERACION
    public void validarLogin() {

        boolean[] result = {
                validarcampoUser(),
        };

        if(result[0]) {
            usuario_query(txtrecuUser.getText().toString().trim());
        }
    }

    //VALIDAMOS QUE NO PUEDAN ESTAR VACIOS
    private boolean validarcampoUser() {
        String user = txtrecuUser.getText().toString();

        if (user.isEmpty()) {
            txtrecuUser.setError("El campo usuario no puede estar vacío");
            return false;

        } else {
            txtrecuUser.setError(null);
            return true;
        }
    }

    //CONFIRMAMOS QUE COINCIDAN CON LOS SQL DE LA BASE DE DATOS
    public void usuario_query(String user) {
        usuario.clear();

        progressDialog.setMessage("Cargando");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, conexion.URL_RECU_ID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try{
                    progressDialog.hide();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    if (jsonObject.getString("total").equals("0")) {
                        txtrecuUser.setError("Usuario no existente");
                    }

                    for (int i = 0; i<jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        lrecu item = new lrecu(
                                o.getString("id_usuarios"),
                                o.getString("user"),
                                o.getString("pass"),
                                o.getString("email"),
                                o.getString("nombre")
                        );

                        usuario.add(item);
                        int position = 0;
                        final lrecu queryItem = usuario.get(position);

                        if (txtrecuUser.getText().toString().equals(queryItem.getUser())) {
                            codigoVerificacion(queryItem.getEmail());

                            Intent intent = new Intent(getApplicationContext(), ActivityConfirmacionn.class);
                            intent.putExtra("id_usuarios", queryItem.getId_usuarios());
                            intent.putExtra("user", queryItem.getUser());
                            intent.putExtra("correo", queryItem.getEmail());
                            intent.putExtra("nombre", queryItem.getNombre());
                            intent.putExtra("token", numToken);
                            startActivity(intent);

                            Toast.makeText(getApplicationContext(), "Usuario Correcto", Toast.LENGTH_SHORT).show();

                            limpiarTextos();

                        } else {
                            txtrecuUser.setError("Usuario incorrecto");
                            Toast.makeText(getApplicationContext(), "Vuelva a intentar en otro momento", Toast.LENGTH_SHORT).show();
                            limpiarTextos();
                            finish();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(RecuperarActivity.this, "Error(Volley): " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String , String> getParams() throws AuthFailureError {
                Map<String , String> params = new HashMap<>();
                params.put("user", user);
                return params;
            }
        };
        RequestHandler.getInstance(RecuperarActivity.this).addToRequestQueue(stringRequest);
    }

    private void limpiarTextos() {
        txtrecuUser.setText("");
    }

    //ENVIO DEL CODIGO DE VERIFICACION PARA LA RECUPERACION
    private void codigoVerificacion(String emailRecu) {
        generarToken();

        String sEmail, sPassword;
        String aTo = emailRecu,
                aSubject = "Recuperacion de cuenta en Star Bank";
        String aMessage = "Hola,<br>" +
                "<br>" +
                "Reenvio de token para recuperacion del usuario. Para completar su recuperacion, copie el token en la app para verificar su cuenta:<br>" +
                "<br>" +
                "[<b>" + numToken + "</b>]<br>" +
                "<br>" +
                "Gracias usuario con el numero,<br>" +
                "<br>" +
                "[<i>" + txtrecuUser.getText().toString() + "</i>]";

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

            new RecuperarActivity.SenMail().execute(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    //FUNCION DE GENERAR TOKEN O CODIGO DE CONFIRMACION
    private void generarToken() {
        Random rand = new Random();
        numToken = rand.nextInt(90000) + 10000;
    }

    private class SenMail extends AsyncTask<Message, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(RecuperarActivity.this,
                    "Por favor espere",
                    "Enviando Email de token.....",
                    true,
                    false);
        }


        //ENVIO SATISFACTORIO DEL TOKEN
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


        //ENVIO CORRECTAMENTE AL CORREO
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
            if (s.equals("Success")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RecuperarActivity.this);
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
                Toast.makeText(RecuperarActivity.this, "¿Algo salio mal?", Toast.LENGTH_SHORT).show();
//                Toast.makeText(RLoginActivity.this, "Error: " + s.toString(), Toast.LENGTH_SHORT).show();
                Log.i("Error", "" + s);
            }
        }
    }
}