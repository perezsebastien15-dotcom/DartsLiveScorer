package com.example.dartslivescorer.commonActivities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
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

public class ModifyPlayerActivity extends AppCompatActivity {

    private EditText nom;
    private WindowInsetsControllerCompat windowInsetsController;
    private MusicService musicService;
    private boolean isMusicBound = false;

    private final ExecutorService executor    = Executors.newSingleThreadExecutor();
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
        setContentView(R.layout.activity_players_modification);

        String musicServiceId = getIntent().getStringExtra("MusicServiceId");
        if (musicServiceId != null)
            bindService(new Intent(this, MusicService.class), musicConnection, Context.BIND_AUTO_CREATE);

        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        long   playerId   = getIntent().getLongExtra("playerId", 0);
        String playerName = getIntent().getStringExtra("playerName");

        nom = findViewById(R.id.nomEditText);
        nom.setText(playerName);
        nom.setFilters(new InputFilter[]{ new InputFilter.AllCaps() });

        Button retour = findViewById(R.id.jeuretour);
        retour.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), PlayersActivity.class)
                    .putExtra("MusicServiceId", "uniqueMusicServiceId"));
            finish();
        });

        Button modifier = findViewById(R.id.modifier_joueur);
        modifier.setOnClickListener(v -> updateJoueur(playerId, nom.getText().toString().trim()));
    }

    private void updateJoueur(long id, String nomSaisi) {
        if (nomSaisi.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir un nom", Toast.LENGTH_SHORT).show();
            return;
        }

        Button modifier = findViewById(R.id.modifier_joueur);
        modifier.setEnabled(false);

        executor.execute(() -> {
            try {
                DartScorerDatabase db = DartScorerDatabase.getDatabase(this);
                Joueur joueur = new Joueur();
                joueur.id  = (int) id;
                joueur.nom = nomSaisi;
                db.dartScorerDao().updateJoueur(joueur);

                mainHandler.post(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        startActivity(new Intent(this, PlayersActivity.class)
                                .putExtra("MusicServiceId", "uniqueMusicServiceId"));
                        finish();
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    modifier.setEnabled(true);
                    Toast.makeText(this, "Erreur lors de la modification", Toast.LENGTH_SHORT).show();
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
