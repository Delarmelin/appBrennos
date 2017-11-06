package com.example.vaguinho.brennosbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CadastroEvento extends AppCompatActivity {

    public static final int PICK_IMAGE = 1555;
    protected ListView eventoList;
    protected int idAtual;
    protected ImageView imageView;
    protected Bitmap bitmap;
    protected EditText tituloEvento;
    protected EditText dataEvento;
    protected EditText descricaoEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_evento);

        imageView = (ImageView) findViewById(R.id.evento_foto);
        tituloEvento = (EditText) findViewById(R.id.evento_titulo);
        dataEvento = (EditText) findViewById(R.id.evento_data);
        descricaoEvento = (EditText) findViewById(R.id.evento_descricao);
        imageView.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                if (v.equals(imageView)) {

                    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getIntent.setType("image/*");

                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                    startActivityForResult(chooserIntent, PICK_IMAGE);
                }
            }
        });

        AppCompatButton salvaBtn = (AppCompatButton) findViewById(R.id.salvar_btn);

        salvaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPost();
                finish();
            }
        });

    }

    void requestPost(){
        JSONObject objectEvent = new JSONObject();
        try {
            objectEvent.put("nome",tituloEvento.getText().toString());
            objectEvent.put("foto_perfil", retornaStringDaImagem());
            objectEvent.put("data_evento",dataEvento.getText().toString());
            objectEvent.put("descricao",descricaoEvento.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://192.168.43.198:8080/evento", objectEvent, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> hash = new HashMap<>();
                hash.put("Content-type","application/json");
                return hash;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    public String retornaStringDaImagem(){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 5, byteArrayOutputStream);

        Log.d("tester ",encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG,5));

        return encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG,50);
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    void requestGet(final callback callback){


        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.43.198:8080/evento", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("entrou ","sim");
                    List<Evento> list = new ArrayList<>();
                    JSONObject OBJECT = new JSONObject(response);
                    JSONObject jsonObject = OBJECT.getJSONObject("_embedded");
                    JSONArray jsonArray = jsonObject.getJSONArray("evento");
                    Log.d("tam ",jsonArray.length()+"");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String nome = jsonObject1.getString("nome");
                        String foto_perfil = jsonObject1.getString("foto_perfil");
                        String data_evento = jsonObject1.getString("data_evento");
                        String descricao = jsonObject1.getString("descricao");
                        int _id = jsonObject1.getInt("id");
                        Log.d("teste ", foto_perfil);
                        Evento evento = new Evento();
                        evento.setId(_id);
                        evento.setNome(nome);
                        evento.setFoto_perfil(foto_perfil);
                        evento.setDescricao(descricao);
                        evento.setData(data_evento);
                        list.add(evento);
                        callback.calback(list);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> hash = new HashMap<>();
                hash.put("Content-type","application/json");
                hash.put("Accept","application/json");
                return hash;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1555 && resultCode == -1
                && null != data) {

            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
