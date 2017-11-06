package com.example.vaguinho.brennosbar;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends CadastroEvento {


    protected ListView eventoList;
    EventoListAdapter eventoListAdapter;

    public int quantos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MainActivity activity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        eventoList = (ListView) findViewById(R.id.evento_list);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, CadastroEvento.class);
                intent.putExtra("quantos", quantos);
                startActivity(intent);
            }
        });

        requestGet(new callback() {
            @Override
            public List<Evento> calback(List<Evento> list) {
                for(int i = 0; i < list.size(); i++){
                    Log.d("teste ",list.get(i).descricao);
                }
                //chama o adapter personalizado feito EventoListAdapter
                eventoList.setAdapter(new EventoListAdapter(getApplicationContext(), list,activity));
                return list;
            }
        });
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventoListResult(List<Evento> eventos) {


        Log.d("MainAct", "ta VOLTANDO o caminhao do event BUZAO");

        quantos = eventos.size();

        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(quantos);
        String strI = sb.toString();

        Log.d("MainAct", strI);


        Log.d("MainAct", "chego aqui na acativity");

    }


    void requestDelete(int id){
        JSONObject objectEvent = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, "http://192.168.43.198:8080/evento/"+id, objectEvent, new Response.Listener<JSONObject>() {
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

}
