package org.ftninformatika.filmovi_kolekcija.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.squareup.picasso.Picasso;

import org.ftninformatika.filmovi_kolekcija.db.DatabaseHelper;
import org.ftninformatika.filmovi_kolekcija.net.model.Detalji;
import org.ftninformatika.filmovi_kolekcija.net.MyService;
import org.ftninformatika.filmovi_kolekcija.R;

import java.sql.SQLException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetaljiOmiljeni extends AppCompatActivity {

    private Detalji detalji;
    private DatabaseHelper databaseHelper;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalji_activity);

        setupToolbar();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);


    }

    private void getDetail(String imdbKey) {
        HashMap<String, String> queryParams = new HashMap<>();
        //TODO unesi api key
        queryParams.put("apikey", "bb578828");
        queryParams.put("i", imdbKey);


        Call<Detalji> call = MyService.apiInterface().getMovieData(queryParams);
        call.enqueue(new Callback<Detalji>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Detalji> call, Response<Detalji> response) {
                if (response.code() == 200) {
                    Log.d("REZ", "200");

                    detalji = response.body();
                    if (detalji != null) {


                        ImageView image = DetaljiOmiljeni.this.findViewById(R.id.detalji_slika);

                        Picasso.with(DetaljiOmiljeni.this).load(detalji.getPoster()).into(image);


                        TextView title = DetaljiOmiljeni.this.findViewById(R.id.detalji_naziv);
                        title.setText(detalji.getTitle());

                        TextView year = DetaljiOmiljeni.this.findViewById(R.id.detalji_godina);
                        year.setText("(" + detalji.getYear() + ")");

                        TextView runtime = DetaljiOmiljeni.this.findViewById(R.id.detalji_runtime);
                        runtime.setText(detalji.getRuntime());

                        TextView genre = DetaljiOmiljeni.this.findViewById(R.id.detalji_zanr);
                        genre.setText(detalji.getGenre());

                        TextView language = DetaljiOmiljeni.this.findViewById(R.id.detalji_jezik);
                        language.setText(detalji.getLanguage());

                        TextView plot = DetaljiOmiljeni.this.findViewById(R.id.detalji_plot);
                        plot.setText(detalji.getPlot());

                    }
                }
            }

            @Override
            public void onFailure(Call<Detalji> call, Throwable t) {
                Toast.makeText(DetaljiOmiljeni.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String imdbKey = getIntent().getStringExtra(OmiljeniActivity.KEY);
        getDetail(imdbKey);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.omiljeni_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_detalji);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_filmove:
                deleteFilmove();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteFilmove() {

        int filmZaBrisanje = getIntent().getExtras().getInt("id", 0);
        try {
            getDataBaseHelper().getFilmoviDao().deleteById(filmZaBrisanje);
            finish();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String tekstNotifikacije = "Film je obrisan";

        Toast.makeText(DetaljiOmiljeni.this, tekstNotifikacije, Toast.LENGTH_LONG).show();

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
}
