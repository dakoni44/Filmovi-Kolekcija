package org.ftninformatika.filmovi_kolekcija.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.ftninformatika.filmovi_kolekcija.adapter.AdapterOmiljeni;
import org.ftninformatika.filmovi_kolekcija.db.DatabaseHelper;
import org.ftninformatika.filmovi_kolekcija.db.Filmovi;
import org.ftninformatika.filmovi_kolekcija.net.model.Detalji;
import org.ftninformatika.filmovi_kolekcija.net.model2.Example;
import org.ftninformatika.filmovi_kolekcija.net.MyService;
import org.ftninformatika.filmovi_kolekcija.R;
import org.ftninformatika.filmovi_kolekcija.net.model2.Search;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterOmiljeni.OnItemClickListener {

    private RecyclerView recyclerView;
    private AdapterOmiljeni adapter;
    private RecyclerView.LayoutManager layoutManager;
    Button btnSearch;
    EditText movieName;
    int position = 0;

    List<String> drawerItems;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ListView drawerList;
    private RelativeLayout drawerPane;
    private ActionBarDrawerToggle drawerToggle;


    public static String KEY = "KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*fillData();
        setupToolbar();
        setupDrawer();*/

        btnSearch = findViewById(R.id.btn_search);
        movieName = findViewById(R.id.ime_filma);
        recyclerView = findViewById(R.id.rvLista);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMovieByName(movieName.getText().toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detalji_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /*public void setupToolbar() {
        toolbar = findViewById(R.id.toolbar_repertoar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu_drawer);
            actionBar.setHomeButtonEnabled(true);
            actionBar.show();
        }
    }

    private void fillData() {
        drawerItems = new ArrayList<>();
        drawerItems.add("Moji filmovi");
        drawerItems.add("Pretraga");
        drawerItems.add("Podesavanja");
        drawerItems.add("Obrisi sve");

    }

    private void setupDrawer() {
        drawerList = findViewById(R.id.left_drawer);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerPane = findViewById(R.id.drawerPane);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String title = "Unknown";
                switch (i) {
                    case 0:
                        title = "Moji filmovi";
                       *//* Intent settings = new Intent(OmiljeniActivity.this, OmiljeniActivity.class);
                        startActivity(settings);*//*
                        break;
                    case 1:
                        title = "Pretraga";
                        Intent pretraga = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(pretraga);
                        break;
                    case 2:
                        title = "Podesavanja";
                        Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(settings);
                        break;
                    case 3:
                        title = "Obrisi sve";
                        break;


                }
                drawerList.setItemChecked(i, true);
                setTitle(title);
                drawerLayout.closeDrawer(drawerPane);
            }
        });
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawerItems));


        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
    }*/

    private void getMovieByName(String name) {
        Map<String, String> query = new HashMap<>();
        //TODO unesi api key
        query.put("apikey", "bb578828");
        query.put("s", name.trim());

        Call<Example> call = MyService.apiInterface().getMovieByName(query);
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {

                if (response.code() == 200) {
                    try {
                        Example searches = response.body();

                        ArrayList<Search> search = new ArrayList<>();

                        for (Search e : searches.getSearch()) {

                            if (e.getType().equals("movie")) {
                                search.add(e);
                            }
                        }

                        layoutManager = new LinearLayoutManager(MainActivity.this);
                        recyclerView.setLayoutManager(layoutManager);

                        adapter = new AdapterOmiljeni(MainActivity.this, search, MainActivity.this);
                        recyclerView.setAdapter(adapter);

                        Toast.makeText(MainActivity.this, "Prikaz filmova.", Toast.LENGTH_SHORT).show();

                    } catch (NullPointerException e) {
                        Toast.makeText(MainActivity.this, "Ne postoji film sa tim nazivom", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(MainActivity.this, "Greska sa serverom", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onItemClick(int position) {
        Search movie = adapter.get(position);

        Intent i = new Intent(MainActivity.this, DetaljiActivity.class);
        i.putExtra(KEY, movie.getImdbID());
        startActivity(i);


    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onRestart() {

        super.onRestart();
    }

    @Override
    protected void onResume() {

        super.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("position", position);
    }


}