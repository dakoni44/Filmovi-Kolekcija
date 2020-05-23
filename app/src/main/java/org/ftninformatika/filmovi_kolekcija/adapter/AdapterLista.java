package org.ftninformatika.filmovi_kolekcija.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.ftninformatika.filmovi_kolekcija.db.Filmovi;
import org.ftninformatika.filmovi_kolekcija.R;

import java.util.List;


public class AdapterLista extends RecyclerView.Adapter<AdapterLista.MyViewHolder> {

    private Context context;
    private List<Filmovi> film;
    private OnItemClickListener listener;



    public AdapterLista(Context context, List<Filmovi> film, OnItemClickListener listener) {
        this.context = context;
        this.film = film;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_prikaz, parent, false);

        return new MyViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


            holder.tvNaziv.setText(film.get(position).getmNaziv());
            holder.tvGodina.setText(film.get(position).getmGodina());
            Picasso.with(context).load(film.get(position).getmImage()).into(holder.ivSlika);



    }

    @Override
    public int getItemCount() {

       return film.size();
    }

    public Filmovi get(int position) {
        return film.get(position);
    }

    public void removeAll() {
        film.clear();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private TextView tvNaziv;
        private TextView tvGodina;
        private ImageView ivSlika;
        private OnItemClickListener vhListener;


        MyViewHolder(@NonNull View itemView, OnItemClickListener vhListener) {
            super(itemView);

            ivSlika = itemView.findViewById(R.id.ivSlika);
            tvNaziv = itemView.findViewById(R.id.tvNazivFilma);
            tvGodina = itemView.findViewById(R.id.godinaFilma);
            this.vhListener = vhListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            vhListener.onItemClick(getAdapterPosition());
        }


    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
