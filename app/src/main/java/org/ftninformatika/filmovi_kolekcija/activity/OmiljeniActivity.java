package org.ftninformatika.filmovi_kolekcija.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.ftninformatika.filmovi_kolekcija.adapter.AdapterLista;
import org.ftninformatika.filmovi_kolekcija.db.DatabaseHelper;
import org.ftninformatika.filmovi_kolekcija.db.Filmovi;
import org.ftninformatika.filmovi_kolekcija.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class OmiljeniActivity extends AppCompatActivity implements AdapterLista.OnItemClickListener {

    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private AdapterLista adapterLista;
    private List<Filmovi> filmovi;
    private SharedPreferences prefs;

    List<String> drawerItems;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ListView drawerList;
    private RelativeLayout drawerPane;
    private ActionBarDrawerToggle drawerToggle;

    public static String KEY = "KEY";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.omiljeni_activity);

        fillData();
        setupToolbar();
        setupDrawer();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        recyclerView = findViewById(R.id.rvRepertoarLista);

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

    public void setupToolbar() {
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
                       /* Intent settings = new Intent(OmiljeniActivity.this, OmiljeniActivity.class);
                        startActivity(settings);*/
                        break;
                    case 1:
                        title = "Pretraga";
                        Intent pretraga = new Intent(OmiljeniActivity.this, MainActivity.class);
                        startActivity(pretraga);
                        break;
                    case 2:
                        title = "Podesavanja";
                        Intent settings = new Intent(OmiljeniActivity.this, SettingsActivity.class);
                        startActivity(settings);
                        break;
                    case 3:
                        title = "Obrisi sve";
                        deleteAll();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detalji_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void deleteAll(){

        new androidx.appcompat.app.AlertDialog.Builder(OmiljeniActivity.this).setMessage("Obrisi sve filmove").setTitle("Obrisi")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Potvrdi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            getDataBaseHelper().getFilmoviDao().deleteBuilder();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),"Filmovi su obrisani",Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }


}
