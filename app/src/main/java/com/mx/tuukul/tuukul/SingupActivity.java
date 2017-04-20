package com.mx.tuukul.tuukul;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SingupActivity extends AppCompatActivity {

    private EditText nombre;
    private EditText contra;
    private EditText contra2;
    private EditText edad;
    private String genero;
    private EditText correo;
    private EditText usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        usuario = (EditText) findViewById(R.id.editText);
        correo = (EditText) findViewById(R.id.editText2);
        contra = (EditText) findViewById(R.id.editText4);
        contra2 = (EditText) findViewById(R.id.editText3);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.Radio_Man:
                if (checked)
                    genero = "m";
                    break;
            case R.id.Radio_Woman:
                if (checked)
                    genero = "f";
                    break;
        }
    }

    public String confContra(String a, String b){
        if (a.equals(b) ){
            String cc = a.toString();
            return cc;
        }
        else {
            Toast.makeText(SingupActivity.this, "las contraseñas no coinciden", Toast.LENGTH_LONG).show();
            return "error";
        }
    }

    public void checkRegistro(View arg0) {

        // Get text from email and passord field
        final String user = usuario.getText().toString();
        final String email = correo.getText().toString();
        final String gen =  genero;
        final String pass = confContra(contra.getText().toString(),contra2.getText().toString());

        if (pass != "error") {
            // Initialize  AsyncLogin() class with email and password
            new AsyncRegistro().execute(user, email, gen, pass);
        }

    }
    private class AsyncRegistro extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(SingupActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tCargando...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://tecnaat.com/Tuukul_PHP/registro.php");
                //url = new URL("C:\\Users\\Ale\\Documents\\Technovation\\Codigo\\login.inc.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("user", params[0])
                        .appendQueryParameter("email", params[1])
                        .appendQueryParameter("gen", params[2])
                        .appendQueryParameter("pass", params[3]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }
        @Override
        protected void onPostExecute(String result) {


            pdLoading.dismiss();
            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */

                Intent intent = new Intent(SingupActivity.this,MenuActivity.class);
                startActivity(intent);
                SingupActivity.this.finish();

            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(SingupActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();

            } else if ( result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(SingupActivity.this, "Unsuccessful.", Toast.LENGTH_LONG).show();

            }else if (result.equalsIgnoreCase("exception") ){
                Toast.makeText(SingupActivity.this, "Connection Problem.", Toast.LENGTH_LONG).show();
            }
        }

    }
}