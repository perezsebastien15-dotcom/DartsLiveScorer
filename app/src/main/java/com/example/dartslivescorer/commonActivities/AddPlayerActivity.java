package com.example.dartslivescorer.commonActivities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.text.InputFilter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.dartslivescorer.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import models.DartScorerDatabase;
import models.commonModels.Joueur;
import models.commonModels.MusicService;

public class AddPlayerActivity extends AppCompatActivity {

    private EditText    nomEditText;
    private MusicService musicService;
    private boolean     isMusicBound = false;

    private final ExecutorService executor   = Executors.newSingleThreadExecutor();
    private final Handler         mainHandler = new Handler(Looper.getMainLooper());

    private final ServiceConnection musicConnection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.LocalBinder) service).getService();
            musicService.startMusic();
            isMusicBound = true;
        }
        @Override public void onServiceDisconnected(ComponentName name) { isMusicBound = false; }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_add);

        String musicServiceId = getIntent().getStringExtra("MusicServiceId");
        if (musicServiceId != null)
            bindService(new Intent(this, MusicService.class), musicConnection, Context.BIND_AUTO_CREATE);

        WindowInsetsControllerCompat wic = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        wic.hide(WindowInsetsCompat.Type.systemBars());

        nomEditText = findViewById(R.id.nomEditText);
        nomEditText.setFilters(new InputFilter[]{ new InputFilter.AllCaps() });

        Button retour = findViewById(R.id.jeuretour);
        retour.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), PlayersActivity.class)
                    .putExtra("MusicServiceId", "uniqueMusicServiceId"));
            finish();
        });

        Button ajout = findViewById(R.id.ajouter_joueur);
        ajout.setOnClickListener(v -> ajouterJoueur());
    }

    private void ajouterJoueur() {
        String nom = nomEditText.getText().toString().trim();
        if (nom.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir un nom", Toast.LENGTH_SHORT).show();
            return;
        }

        Button ajout = findViewById(R.id.ajouter_joueur);
        ajout.setEnabled(false);

        executor.execute(() -> {
            try {
                Joueur joueur = new Joueur();
                joueur.nom = nom;
                DartScorerDatabase.getDatabase(getApplicationContext())
                        .dartScorerDao().insertJoueur(joueur);

                mainHandler.post(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        startActivity(new Intent(this, PlayersActivity.class)
                                .putExtra("MusicServiceId", "uniqueMusicServiceId"));
                        finish();
                    }
                });
            } catch (Exception e) {
                Log.e("AddPlayer", "Erreur insertion joueur : " + e.getClass().getSimpleName() + " — " + e.getMessage(), e);
                String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                mainHandler.post(() -> {
                    ajout.setEnabled(true);
                    Toast.makeText(this, "Erreur : " + msg, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
        if (isMusicBound) { unbindService(musicConnection); isMusicBound = false; }
    }
}
