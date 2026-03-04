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

    private Button retour;
    private Button ajouter;
    private DartScorerDatabase db;
    private GridView gridView;
    private WindowInsetsControllerCompat windowInsetsController;


    private MusicService musicService;
    private boolean isMusicBound = false;

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            musicService.startMusic();
            isMusicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isMusicBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_list);

        // Liez votre activité au service de musique
        String musicServiceId = getIntent().getStringExtra("MusicServiceId");

        if (musicServiceId != null) {
            Intent serviceIntent = new Intent(this, MusicService.class);
            bindService(serviceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }

        // Initialize the WindowInsetsControllerCompat
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // Hide the system bars
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        // Récupération de l'instance db
        db = DartScorerDatabase.getDatabase(this);

        // Récupération de la liste des joueurs en bdd
        new GetJoueursAsyncTask(this, db).execute();

        // Retour à l'accueil
        retour = findViewById(R.id.jeuretour);
        retour.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), IntroGameActivity.class);
            intent.putExtra("MusicServiceId", "uniqueMusicServiceId");
            startActivity(intent);
            finish();
        });

        // Création d'un joueur
        ajouter = findViewById(R.id.ajouter_joueur);
        ajouter.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddPlayerActivity.class);
            intent.putExtra("MusicServiceId", "uniqueMusicServiceId");
            startActivity(intent);
            finish();
        });

        gridView = findViewById(R.id.player_grid_view);
    }

    public void populateGridView(List<PlayerItem> joueurs, String type) {
        gridView = findViewById(R.id.player_grid_view);
        PlayerItemAdapter playerAdapter = new PlayerItemAdapter(this, joueurs,
                (playerItem, clickType) -> {
                    // Gérer le clic long sur le joueur
                    System.out.println("item : " + playerItem.getId());
                    handlePlayerItemClick(playerItem, clickType);
                },
                playerItem -> {
                    // Gérer le clic sur le joueur (stats dans votre cas)
                    /*Intent intent = new Intent(getApplicationContext(), StatsActivity.class);
                    startActivity(intent);
                    finish();*/
                }
        );
        gridView.setAdapter(playerAdapter);
    }

    private void handlePlayerItemClick(PlayerItem playerItem, String type) {
        System.out.println("type : " + type);
        if (type.equals("Suppr")) {
            // Supprimer le joueur de la base de données
            new DeleteJoueurAsyncTask(this, db, playerItem.getId()).execute();

            Intent intent = new Intent(getApplicationContext(), PlayersActivity.class);
            intent.putExtra("MusicServiceId", "uniqueMusicServiceId");
            startActivity(intent);
            finish();
        }
        if (type.equals("Modif")) {
            Intent intent = new Intent(getApplicationContext(), ModifyPlayerActivity.class);

            intent.putExtra("playerId", playerItem.getId());
            intent.putExtra("playerName", playerItem.getName());
            intent.putExtra("MusicServiceId", "uniqueMusicServiceId");

            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onJoueursLoaded(List<PlayerItem> joueurs) {
        populateGridView(joueurs, "");
    }
}
