package com.example.star_bank;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class ActivityConfirmacionn extends AppCompatActivity {

    TextView textViewCorreo, btnReenviarCodigo, textViewTiempo;
    EditText txtCodigoVeri;
    Button btnConfirmarVeri;

    private String correo, nombreR, user;
    private int numToken;
    private int acum = 0;

    final EsperaToken esperaToken = new EsperaToken(60000,1000);
    final ReenviarToken reenviarToken = new ReenviarToken(30000,1000);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion);

        textViewCorreo = (TextView) findViewById(R.id.textViewCorreo);

        btnReenviarCodigo = (TextView) findViewById(R.id.btnReenviarCodigo);
        btnReenviarCodigo.setEnabled(false);
        btnReenviarCodigo.setTextColor(Color.GRAY);

        textViewTiempo = (TextView) findViewById(R.id.textViewTiempo);

        txtCodigoVeri = (EditText) findViewById(R.id.txtCodigoVeri);

        btnConfirmarVeri = (Button) findViewById(R.id.btnConfirmarVeri);


        correo = getIntent().getExtras().getString("correo");
        numToken = getIntent().getExtras().getInt("token");
        nombreR = getIntent().getExtras().getString("nombre");
        user = getIntent().getExtras().getString("user");

        textViewCorreo.setText(correo);

        esperaToken.start();


        btnReenviarCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acum++;
                reenviarToken.start();
                btnReenviarCodigo.setEnabled(false);
                btnReenviarCodigo.setTextColor(Color.GRAY);

                if (acum == 3) {
                    btnReenviarCodigo.setEnabled(false);
                    btnReenviarCodigo.setTextColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "No puede realizar mas de 3 intentos. Intente mas tarde.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnConfirmarVeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtCodigoVeri.getText().toString().trim().equals(String.valueOf(numToken))) {
                    Intent intent = new Intent(getApplicationContext(), ActivityPassword.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    limpiar();

                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Token invalido. Intente nuevamente.", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void limpiar() {
        textViewCorreo.setText("Correo Electronico");
        txtCodigoVeri.setText("");
        btnReenviarCodigo.setEnabled(false);
    }

    public class EsperaToken extends CountDownTimer {
        public EsperaToken(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            btnReenviarCodigo.setEnabled(true);
            btnReenviarCodigo.setTextColor(Color.parseColor("#FF3700B3"));
        }

        @Override
        public void onTick(long millisUntilFinished) {
            textViewTiempo.setText("0:" + (millisUntilFinished/1000 + ""));
        }
    }

    public class ReenviarToken extends CountDownTimer {

        public ReenviarToken(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            btnReenviarCodigo.setEnabled(true);
            btnReenviarCodigo.setTextColor(Color.parseColor("#FF3700B3"));
            esperaToken.start();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            textViewTiempo.setText("0:" + (millisUntilFinished/1000 + ""));
            if ((millisUntilFinished/1000) == 5) {
                nuevoToken();
                txtCodigoVeri.setText("" + String.valueOf(numToken));
            }
        }
    }



    private void nuevoToken() {
        generarToken();

        String sEmail, sPassword;

        String aTo = textViewCorreo.getText().toString().trim(),
                aSubject = "Nuevo token de Verificacion de Correo";
        String aMessage = "¡Hola!<br>" +
                "<br>" +
                "Recientemente solicitaste un nuevo token de verificación para tu cuenta [<i>" + nombreR + "</i>]. Aquí está el token que necesitas:<br>\n" +
                "<br>" +
                "[<b>" + numToken + "</b>]<br>" +
                "<br>" +
                "Este token caducará en [<b>1 minuto</b>]. ¡No lo compartas con nadie!" +
                "<br>" +
                "¡Gracias por usar nuestro servicio!";

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

            new ActivityConfirmacionn.SenMail().execute(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void generarToken() {
        Random rand = new Random();
        numToken = rand.nextInt(90000) + 10000;
    }

    private class SenMail extends AsyncTask<Message, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ActivityConfirmacionn.this,
                    "Por favor espere",
                    "Enviando Email de nuevo token.....",
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
            if (s.equals("Success")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityConfirmacionn.this);
                builder.setCancelable(false);
                builder.setTitle(Html.fromHtml("<font color='#FEB536'>Token</font>"));
                builder.setMessage("El nuevo token se ha enviado con exito.");
                builder.setPositiveButton("Listo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
//                        txtNombreR.setText("Hola");
                    }
                });
                builder.show();

            } else {
                Toast.makeText(ActivityConfirmacionn.this, "¿Algo salio mal?", Toast.LENGTH_SHORT).show();
            }
        }
    }
}