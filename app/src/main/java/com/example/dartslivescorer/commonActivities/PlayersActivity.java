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

    private static final String TAG = "PlayersActivity";

    private GridView          gridView;
    private PlayerItemAdapter adapter;
    private WindowInsetsControllerCompat windowInsetsController;
    private MusicService musicService;
    private boolean isMusicBound = false;

    private final ExecutorService executor    = Executors.newSingleThreadExecutor();
    private final Handler         mainHandler = new Handler(Looper.getMainLooper());

    private final ServiceConnection musicConnection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                musicService = ((MusicService.LocalBinder) service).getService();
                musicService.startMusic();
                isMusicBound = true;
            } catch (Exception e) { Log.e(TAG, "MusicService erreur", e); }
        }
        @Override public void onServiceDisconnected(ComponentName name) { isMusicBound = false; }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_list);

        try {
            windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
            if (windowInsetsController != null)
                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        } catch (Exception e) { Log.e(TAG, "WindowInsets erreur", e); }

        try {
            String musicServiceId = getIntent().getStringExtra("MusicServiceId");
            if (musicServiceId != null)
                bindService(new Intent(this, MusicService.class), musicConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) { Log.e(TAG, "MusicService bind erreur", e); }

        gridView = findViewById(R.id.player_grid_view);

        adapter = new PlayerItemAdapter(
                this,
                new ArrayList<>(),
                (playerItem, clickType) -> handlePlayerItemClick(playerItem, clickType),
                playerItem -> startActivity(
                        new Intent(getApplicationContext(), StatsActivity.class)
                                .putExtra("playerId",    playerItem.getId())
                                .putExtra("playerName",  playerItem.getName()))
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

        chargerJoueurs();
    }

    private void chargerJoueurs() {
        executor.execute(() -> {
            try {
                Log.d(TAG, "Chargement joueurs...");
                DartScorerDatabase db = DartScorerDatabase.getDatabase(getApplicationContext());
                Log.d(TAG, "DB obtenue");

                List<PlayerItem> joueurs = new ArrayList<>();
                List<Joueur> liste = db.dartScorerDao().getAllJoueurs();
                Log.d(TAG, "Nb joueurs en base : " + liste.size());

                for (Joueur j : liste)
                    joueurs.add(new PlayerItem((long) j.id, j.nom != null ? j.nom : "", 0));

                mainHandler.post(() -> {
                    if (!isFinishing() && !isDestroyed())
                        afficherJoueurs(joueurs);
                });
            } catch (Exception e) {
                Log.e(TAG, "Erreur chargement joueurs", e);
                mainHandler.post(() -> {
                    if (!isFinishing() && !isDestroyed())
                        afficherJoueurs(new ArrayList<>());
                });
            }
        });
    }

    private void afficherJoueurs(List<PlayerItem> joueurs) {
        try {
            TextView vide = findViewById(R.id.tv_aucun_joueur);
            if (vide != null)
                vide.setVisibility(joueurs.isEmpty() ? View.VISIBLE : View.GONE);
            adapter.updatePlayerList(joueurs);
            Log.d(TAG, "Affichage de " + joueurs.size() + " joueurs.");
        } catch (Exception e) {
            Log.e(TAG, "Erreur affichage joueurs", e);
        }
    }

    private void handlePlayerItemClick(PlayerItem playerItem, String type) {
        if ("Suppr".equals(type)) {
            executor.execute(() -> {
                try {
                    DartScorerDatabase.getDatabase(getApplicationContext())
                            .dartScorerDao().deleteJoueurById(playerItem.getId());
                } catch (Exception e) { Log.e(TAG, "Erreur suppression", e); }
                mainHandler.post(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        startActivity(new Intent(getApplicationContext(), PlayersActivity.class)
                                .putExtra("MusicServiceId", "uniqueMusicServiceId"));
                        finish();
                    }
                });
            });
        } else if ("Modif".equals(type)) {
            startActivity(new Intent(getApplicationContext(), ModifyPlayerActivity.class)
                    .putExtra("playerId",    playerItem.getId())
                    .putExtra("playerName",  playerItem.getName())
                    .putExtra("MusicServiceId", "uniqueMusicServiceId"));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try { executor.shutdown(); } catch (Exception ignored) {}
        try { if (isMusicBound) { unbindService(musicConnection); isMusicBound = false; } }
        catch (Exception ignored) {}
    }
}
