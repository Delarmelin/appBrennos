package com.example.vaguinho.brennosbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by vaguinho on 21/10/17.
 */

public class EventoListAdapter extends BaseAdapter {


    Context context;
    List<Evento> eventos;
    private static LayoutInflater inflater = null;
MainActivity activity;

    public EventoListAdapter(Context context, List<Evento> eventos, MainActivity activity) {

        this.context = context;
        this.eventos = eventos;
        this.activity = activity;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return eventos.size();
    }

    @Override
    public Object getItem(int position) {
        return eventos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.item_evento, null);

        TextView textNome = (TextView) vi.findViewById(R.id.evento_nome);
        textNome.setText(eventos.get(position).getNome());

        TextView textDescricao = (TextView) vi.findViewById(R.id.evento_descricao);
        textDescricao.setText(eventos.get(position).getDescricao());

        TextView textData = (TextView) vi.findViewById(R.id.evento_data);
        textData.setText(eventos.get(position).getData());

        ImageView img = (ImageView) vi.findViewById(R.id.evento_foto);
        img.setImageBitmap(decodeBase64(eventos.get(position).getFoto_perfil()));

        Button btnExcluir = (Button) vi.findViewById(R.id.excluir);
        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.requestDelete(eventos.get(position).getId());
                EventoListAdapter.this.notifyDataSetChanged();
            }
        });

        return vi;
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
