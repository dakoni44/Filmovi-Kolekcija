package org.ftninformatika.filmovi_kolekcija.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.ftninformatika.filmovi_kolekcija.adapter.AdapterLista;
import org.ftninformatika.filmovi_kolekcija.db.DatabaseHelper;
import org.ftninformatika.filmovi_kolekcija.db.Filmovi;
import org.ftninformatika.filmovi_kolekcija.R;

import java.sql.SQLException;
import java.util.List;

import petarkitanovic.androidkurs.omiljeni.adapteri.AdapterLista;
import petarkitanovic.androidkurs.omiljeni.db.DatabaseHelper;
import petarkitanovic.androidkurs.omiljeni.db.model.Filmovi;

public class OmiljeniActivity extends AppCompatActivity implements AdapterLista.OnItemClickListener {

    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private AdapterLista adapterLista;
    private List<Filmovi> filmovi;
    private SharedPreferences prefs;

    public static String KEY = "KEY";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.omiljeni_activity);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        recyclerView = findViewById(R.id.rvRepertoarLista);
        setupToolbar();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        try {

            filmovi = getDataBaseHelper().getFilmoviDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        adapterLista = new AdapterLista(this, filmovi, this);
        recyclerView.setAdapter(adapterLista);


    }

    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_repertoar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.show();
        }
    }


    @Override
    public void onItemClick(int position) {
        Filmovi film = adapterLista.get(position);

        Intent i = new Intent(OmiljeniActivity.this, DetaljiOmiljeni.class);
        i.putExtra(KEY, film.getmImdbId());
        i.putExtra("id", film.getmId());
        startActivity(i);


    }

    private void refresh() {

        RecyclerView recyclerView = findViewById(R.id.rvRepertoarLista);
        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            List<Filmovi> film = null;
            try {

                film = getDataBaseHelper().getFilmoviDao().queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            AdapterLista adapter = new AdapterLista(this, film, this);
            recyclerView.setAdapter(adapter);

        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

    }

    public DatabaseHelper getDataBaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }


    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
}
