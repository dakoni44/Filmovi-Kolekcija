package org.ftninformatika.filmovi_kolekcija.activity;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
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
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.squareup.picasso.Picasso;

import org.ftninformatika.filmovi_kolekcija.R;
import org.ftninformatika.filmovi_kolekcija.db.DatabaseHelper;
import org.ftninformatika.filmovi_kolekcija.db.Filmovi;
import org.ftninformatika.filmovi_kolekcija.net.MyService;
import org.ftninformatika.filmovi_kolekcija.net.model.Detalji;

import java.sql.SQLException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetaljiActivity extends AppCompatActivity {

    private Detalji detalji;
    private DatabaseHelper databaseHelper;
    private SharedPreferences prefs;

    public static final String NOTIF_CHANNEL_ID = "notif_channel_543";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalji_activity);

        createNotificationChannel();

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


                        ImageView image = DetaljiActivity.this.findViewById(R.id.detalji_slika);

                        Picasso.with(DetaljiActivity.this).load(detalji.getPoster()).into(image);


                        TextView title = DetaljiActivity.this.findViewById(R.id.detalji_naziv);
                        title.setText(detalji.getTitle());

                        TextView year = DetaljiActivity.this.findViewById(R.id.detalji_godina);
                        year.setText("(" + detalji.getYear() + ")");

                        TextView runtime = DetaljiActivity.this.findViewById(R.id.detalji_runtime);
                        runtime.setText(detalji.getRuntime());

                        TextView genre = DetaljiActivity.this.findViewById(R.id.detalji_zanr);
                        genre.setText(detalji.getGenre());

                        TextView language = DetaljiActivity.this.findViewById(R.id.detalji_jezik);
                        language.setText(detalji.getLanguage());

                        TextView plot = DetaljiActivity.this.findViewById(R.id.detalji_plot);
                        plot.setText(detalji.getPlot());

                    }
                }
            }

            @Override
            public void onFailure(Call<Detalji> call, Throwable t) {
                Toast.makeText(DetaljiActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String imdbKey = getIntent().getStringExtra(MainActivity.KEY);
        getDetail(imdbKey);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addFav:
                addFilm();
                break;
        }
        return super.onOptionsItemSelected(item);
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



    public void addFilm() {

        Filmovi film = new Filmovi();
        film.setmNaziv(detalji.getTitle());
        film.setmGodina(detalji.getYear());
        film.setmImage(detalji.getPoster());
        film.setmImdbId(detalji.getImdbID());


        try {
            getDataBaseHelper().getFilmoviDao().create(film);


            boolean toast = prefs.getBoolean(getString(R.string.toast_key), false);
            boolean notif = prefs.getBoolean(getString(R.string.notif_key), false);

            if (toast) {
                Toast.makeText(DetaljiActivity.this, detalji.getTitle()+" uspesno dodat", Toast.LENGTH_LONG).show();

            }

            if (notif) {
                showNotification(detalji.getTitle()+" uspesno dodat");

            }

        } catch (NumberFormatException e) {
            Toast.makeText(DetaljiActivity.this, "Rating mora biti broj", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
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

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Description of My Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showNotification(String poruka) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(DetaljiActivity.this, NOTIF_CHANNEL_ID);
        builder.setSmallIcon(android.R.drawable.ic_input_add);
        builder.setContentTitle("Filmovi");
        builder.setContentText(poruka);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_movies);


        builder.setLargeIcon(bitmap);
        notificationManager.notify(1, builder.build());
    }
}

