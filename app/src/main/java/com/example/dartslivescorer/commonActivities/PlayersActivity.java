package com.example.dartslivescorer.commonActivities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.dartslivescorer.R;
import com.example.dartslivescorer.adapters.PlayerItemAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import models.DartScorerDatabase;
import models.commonModels.Joueur;
import models.commonModels.MusicService;
import models.gamesModels.PlayerItem;

public class PlayersActivity extends AppCompatActivity {

    private DartScorerDatabase db;
    private GridView gridView;
    private PlayerItemAdapter adapter;
    private WindowInsetsControllerCompat windowInsetsController;
    private MusicService musicService;
    private boolean isMusicBound = false;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

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
        setContentView(R.layout.activity_players_list);

        String musicServiceId = getIntent().getStringExtra("MusicServiceId");
        if (musicServiceId != null)
            bindService(new Intent(this, MusicService.class), musicConnection, Context.BIND_AUTO_CREATE);

        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        gridView = findViewById(R.id.player_grid_view);

        // Adapter initialisé avec une liste vide — sera rempli après chargement
        adapter = new PlayerItemAdapter(
                this,
                new ArrayList<>(),
                (playerItem, clickType) -> handlePlayerItemClick(playerItem, clickType),
                playerItem -> startActivity(
                        new Intent(getApplicationContext(), StatsActivity.class)
                                .putExtra("playerId", playerItem.getId())
                                .putExtra("playerName", playerItem.getName()))
        );
        gridView.setAdapter(adapter);

        Button retour = findViewById(R.id.jeuretour);
        retour.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), IntroGameActivity.class)
                    .putExtra("MusicServiceId", "uniqueMusicServiceId"));
            finish();
        });

        Button ajouter = findViewById(R.id.ajouter_joueur);
        ajouter.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), AddPlayerActivity.class)
                    .putExtra("MusicServiceId", "uniqueMusicServiceId"));
            finish();
        });

        // Chargement DB + joueurs entièrement en background
        chargerJoueurs();
    }

    private void chargerJoueurs() {
        executor.execute(() -> {
            try {
                // Ouvre la DB (+ migrations) hors thread principal
                db = DartScorerDatabase.getDatabase(this);

                List<PlayerItem> joueurs = new ArrayList<>();
                for (Joueur j : db.dartScorerDao().getAllJoueurs()) {
                    joueurs.add(new PlayerItem((long) j.id, j.nom, 0));
                }

                mainHandler.post(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        afficherJoueurs(joueurs);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        afficherJoueurs(new ArrayList<>());
                    }
                });
            }
        });
    }

    private void afficherJoueurs(List<PlayerItem> joueurs) {
        // Message si aucun joueur
        TextView vide = findViewById(R.id.tv_aucun_joueur);
        if (vide != null) {
            vide.setVisibility(joueurs.isEmpty() ? View.VISIBLE : View.GONE);
        }
        adapter.updatePlayerList(joueurs);
    }

    private void handlePlayerItemClick(PlayerItem playerItem, String type) {
        if ("Suppr".equals(type)) {
            executor.execute(() -> {
                db.dartScorerDao().deleteJoueurById(playerItem.getId());
                mainHandler.post(() -> {
                    startActivity(new Intent(getApplicationContext(), PlayersActivity.class)
                            .putExtra("MusicServiceId", "uniqueMusicServiceId"));
                    finish();
                });
            });
        } else if ("Modif".equals(type)) {
            startActivity(new Intent(getApplicationContext(), ModifyPlayerActivity.class)
                    .putExtra("playerId", playerItem.getId())
                    .putExtra("playerName", playerItem.getName())
                    .putExtra("MusicServiceId", "uniqueMusicServiceId"));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
        if (isMusicBound) { unbindService(musicConnection); isMusicBound = false; }
    }
}
