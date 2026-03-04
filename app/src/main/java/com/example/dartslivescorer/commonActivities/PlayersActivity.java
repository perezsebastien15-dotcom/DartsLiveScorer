package com.example.dartslivescorer.commonActivities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.dartslivescorer.R;
import com.example.dartslivescorer.adapters.PlayerItemAdapter;

import java.util.List;

import models.DartScorerDatabase;
import models.asyncTasks.DeleteJoueurAsyncTask;
import models.asyncTasks.GetJoueursAsyncTask;
import models.commonModels.MusicService;
import models.gamesModels.PlayerItem;

public class PlayersActivity extends AppCompatActivity implements GetJoueursAsyncTask.OnJoueursLoadedListener {

    private DartScorerDatabase db;
    private GridView gridView;
    private WindowInsetsControllerCompat windowInsetsController;
    private MusicService musicService;
    private boolean isMusicBound = false;

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

        db = DartScorerDatabase.getDatabase(this);
        new GetJoueursAsyncTask(this, db).execute();

        gridView = findViewById(R.id.player_grid_view);

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
    }

    private void populateGridView(List<PlayerItem> joueurs) {
        gridView = findViewById(R.id.player_grid_view);
        gridView.setAdapter(new PlayerItemAdapter(this, joueurs,
                (playerItem, clickType) -> handlePlayerItemClick(playerItem, clickType),
                playerItem -> { /* Stats : non implémenté */ }
        ));
    }

    private void handlePlayerItemClick(PlayerItem playerItem, String type) {
        if ("Suppr".equals(type)) {
            new DeleteJoueurAsyncTask(this, db, playerItem.getId()).execute();
            startActivity(new Intent(getApplicationContext(), PlayersActivity.class)
                    .putExtra("MusicServiceId", "uniqueMusicServiceId"));
            finish();
        } else if ("Modif".equals(type)) {
            startActivity(new Intent(getApplicationContext(), ModifyPlayerActivity.class)
                    .putExtra("playerId", playerItem.getId())
                    .putExtra("playerName", playerItem.getName())
                    .putExtra("MusicServiceId", "uniqueMusicServiceId"));
            finish();
        }
    }

    @Override
    public void onJoueursLoaded(List<PlayerItem> joueurs) {
        populateGridView(joueurs);
    }
}
