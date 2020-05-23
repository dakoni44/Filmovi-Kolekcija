package org.ftninformatika.filmovi_kolekcija.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import org.ftninformatika.filmovi_kolekcija.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends Activity {

    private SharedPreferences prefs;
    private boolean splash;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        splash = prefs.getBoolean(getString(R.string.splash_key), false);

        if (splash) {
            setContentView(R.layout.splash_screen);

            ImageView imageView = findViewById(R.id.imageView);
            InputStream is = null;
            try {
                is = getAssets().open("repertoar_logo.png");
                Drawable drawable = Drawable.createFromStream(is, null);
                imageView.setImageDrawable(drawable);
            } catch (IOException e) {
                e.printStackTrace();
            }

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, OmiljeniActivity.class));
                    finish();
                }
            }, 5000);
        } else {
            startActivity(new Intent(SplashScreen.this, OmiljeniActivity.class));
            finish();
        }
    }
}
