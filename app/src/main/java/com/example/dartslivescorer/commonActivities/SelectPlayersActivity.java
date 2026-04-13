package com.example.dartslivescorer.commonActivities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.dartslivescorer.R;
import com.example.dartslivescorer.adapters.PlayerItemAdapter;
import com.example.dartslivescorer.enums.eGames;
import com.example.dartslivescorer.gamesActivities.CricketGameActivity;
import com.example.dartslivescorer.gamesActivities.MasterMindActivity;
import com.example.dartslivescorer.gamesActivities.ShootOutActivity;
import com.example.dartslivescorer.gamesActivities.StandardGameActivity;
import com.example.dartslivescorer.gamesActivities.UnderHatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import models.DartScorerDatabase;
import models.commonModels.GameItem;
import models.commonModels.Joueur;
import models.commonModels.MusicService;
import models.gamesModels.PlayerItem;

public class SelectPlayersActivity extends AppCompatActivity {

    private List<PlayerItem>  selectedPlayers  = new ArrayList<>();
    private List<PlayerItem>  displayedPlayers = new ArrayList<>();
    private GameItem          selectedGameItem;
    private GridView          gridView;
    private PlayerItemAdapter playerAdapter;
    private Vibrator          vibrator;
    private MusicService      musicService;
    private boolean           isMusicBound = false;

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
        setContentView(R.layout.activity_players_selection);

        String musicServiceId = getIntent().getStringExtra("MusicServiceId");
        if (musicServiceId != null)
            bindService(new Intent(this, MusicService.class), musicConnection, Context.BIND_AUTO_CREATE);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        WindowInsetsControllerCompat wic = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        wic.hide(WindowInsetsCompat.Type.systemBars());

        selectedGameItem = getIntent().getParcelableExtra("selectedGame");

        playerAdapter = new PlayerItemAdapter(this, displayedPlayers, null, playerItem -> {
            vibrate();
            togglePlayerSelection(playerItem);
            playerAdapter.updateSelectedPlayers(selectedPlayers);
        });

        gridView = findViewById(R.id.player_grid_view);
        gridView.setAdapter(playerAdapter);

        Button retour = findViewById(R.id.jeuretour);
        retour.setOnClickListener(v -> {
            vibrate();
            startActivity(new Intent(getApplicationContext(), GamesListActivity.class)
                    .putExtra("MusicServiceId", "uniqueMusicServiceId"));
            finish();
        });

        Button lancer = findViewById(R.id.plateau_jeu);
        lancer.setOnClickListener(v -> {
            vibrate();
            if (isMusicBound) { unbindService(musicConnection); isMusicBound = false; }
            lancerJeu();
        });

        chargerJoueurs();
    }

    private void chargerJoueurs() {
        executor.execute(() -> {
            try {
                List<PlayerItem> joueurs = new ArrayList<>();
                for (Joueur j : DartScorerDatabase.getDatabase(getApplicationContext())
                        .dartScorerDao().getAllJoueurs()) {
                    joueurs.add(new PlayerItem((long) j.id, j.nom != null ? j.nom : "", 0));
                }
                mainHandler.post(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        displayedPlayers.clear();
                        displayedPlayers.addAll(joueurs);
                        playerAdapter.notifyDataSetChanged();
                    }
                });
            } catch (Exception e) { /* liste vide */ }
        });
    }

    private void lancerJeu() {
        if (selectedGameItem == null) return;
        eGames type = selectedGameItem.getType();

        if (type == eGames.Standard301 || type == eGames.Standard501 || type == eGames.Standard701) {
            if (!selectedPlayers.isEmpty()) startGame(StandardGameActivity.class);
            else Toast.makeText(this, "Sélectionnez au moins un joueur", Toast.LENGTH_SHORT).show();

        } else if (type == eGames.OriginalCricket || type == eGames.HiddenCricket || type == eGames.RandomCricket) {
            if (selectedPlayers.size() > 1) startGame(CricketGameActivity.class);
            else Toast.makeText(this, "Sélectionnez au moins deux joueurs", Toast.LENGTH_SHORT).show();

        } else if (type == eGames.UnderTheHat) {
            if (selectedPlayers.size() > 1 && selectedPlayers.size() < 10) startGame(UnderHatActivity.class);
            else if (selectedPlayers.size() < 2) Toast.makeText(this, "Sélectionnez au moins deux joueurs", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "Sélectionnez moins de dix joueurs", Toast.LENGTH_SHORT).show();

        } else if (type == eGames.ShootOut) {
            if (selectedPlayers.size() > 1) startGame(ShootOutActivity.class);
            else Toast.makeText(this, "Sélectionnez au moins deux joueurs", Toast.LENGTH_SHORT).show();

        } else if (type == eGames.MasterMind) {
            if (!selectedPlayers.isEmpty()) startGame(MasterMindActivity.class);
            else Toast.makeText(this, "Sélectionnez au moins un joueur", Toast.LENGTH_SHORT).show();
        }
    }

    private void startGame(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass)
                .putExtra("selectedGame",    selectedGameItem)
                .putExtra("selectedPlayers", new ArrayList<>(selectedPlayers)));
    }

    private void togglePlayerSelection(PlayerItem playerItem) {
        if (selectedPlayers.contains(playerItem)) selectedPlayers.remove(playerItem);
        else selectedPlayers.add(playerItem);
    }

    private void vibrate() {
        if (vibrator != null && vibrator.hasVibrator()) vibrator.vibrate(100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
        if (vibrator != null) vibrator.cancel();
        if (isMusicBound) { unbindService(musicConnection); isMusicBound = false; }
    }
}
