package com.example.pm1e2grupo4;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityEdit extends AppCompatActivity {

    EditText txtNombre, txtTelefono, txtLongitud, txtLatitud;
    String id;
    Contacto contactoBuscado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        txtNombre = (EditText) findViewById(R.id.txtNombreContacto2);
        txtTelefono = (EditText) findViewById(R.id.txtTelefonoContacto2);
        txtLongitud = (EditText) findViewById(R.id.txtLongitudContacto2);
        txtLatitud = (EditText) findViewById(R.id.txtLatitudContacto2);
        Button btnVolver2 = (Button) findViewById(R.id.btnVolver2);
        Button btnELiminar = (Button) findViewById(R.id.btnEliminar);

        Intent intent = getIntent();
        id = intent.getStringExtra("idCont");
        buscarContacto(id);

        btnVolver2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pantallaVolver = new Intent(getApplicationContext(), ActivityListContactos.class);
                startActivity(pantallaVolver);
            }
        });

        btnELiminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EliminarContacto();
            }
        });
    }

    private void buscarContacto(String id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = RestApiMethods.ApiGetID + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray ContactoArray = obj.getJSONArray("Contactos");

                    for (int i = 0; i < ContactoArray.length(); i++) {
                        //getting the json object of the particular index inside the array
                        JSONObject contactoObject = ContactoArray.getJSONObject(i);

                        //creating a hero object and giving them the values from json object
                        contactoBuscado = new Contacto(contactoObject.getString("ID"),
                                contactoObject.getString("NOMBRE"),
                                contactoObject.getString("TELEFONO"),
                                contactoObject.getString("LATITUD"),
                                contactoObject.getString("LONGITUD"),
                                contactoObject.getString("FOTO"),
                                contactoObject.getString("ARCHIVO"));
                    }
                    /*String[] obtencion = contactoBuscado.getFoto().split("\\[");
                    String[] obtencionBytes = obtencion[1].split("]");
                    byte[] foto = Base64.decode(obtencionBytes[0].getBytes(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
                    imgFoto2.setImageBitmap(bitmap);*/

                    txtNombre.setText(contactoBuscado.getNombre());
                    txtTelefono.setText(contactoBuscado.getTelefono());
                    txtLatitud.setText(contactoBuscado.getLatitud());
                    txtLongitud.setText(contactoBuscado.getLongitud());

                } catch (JSONException ex) {
                    Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error en Response", "onResponse: " +  error.getMessage().toString() );
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void EliminarContacto() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmación de Eliminación")
                .setMessage("¿Desea eliminar el contacto de " + txtNombre.getText() + "?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        RequestQueue queue = Volley.newRequestQueue(ActivityEdit.this);
                        String url = RestApiMethods.ApiDeleteUrl + id;
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    JSONArray ContactoArray = obj.getJSONArray("Contactos");
                                    for (int i = 0; i < ContactoArray.length(); i++) {
                                        //getting the json object of the particular index inside the array
                                        JSONObject contactoObject = ContactoArray.getJSONObject(i);

                                        //creating a hero object and giving them the values from json object
                                        contactoBuscado = new Contacto(contactoObject.getString("ID"),
                                                contactoObject.getString("NOMBRE"),
                                                contactoObject.getString("TELEFONO"),
                                                contactoObject.getString("LATITUD"),
                                                contactoObject.getString("LONGITUD"),
                                                contactoObject.getString("FOTO"),
                                                contactoObject.getString("ARCHIVO"));
                                    }

                                } catch (JSONException ex) {
                                    Toast.makeText(getApplicationContext(), "Error al eliminar el contacto", Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("Error en Response", "onResponse: " +  error.getMessage().toString() );
                            }
                        });
                        // Add the request to the RequestQueue.
                        queue.add(stringRequest);


                        Toast.makeText(getApplicationContext(), "Dato Eliminado", Toast.LENGTH_LONG).show();
                        Intent pantallaRegresoList = new Intent(getApplicationContext(), ActivityListContactos.class);
                        startActivity(pantallaRegresoList);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "Se canceló la eliminación", Toast.LENGTH_LONG).show();
                    }
                }).show();
    }

}