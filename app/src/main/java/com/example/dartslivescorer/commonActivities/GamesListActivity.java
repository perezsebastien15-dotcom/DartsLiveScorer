package com.example.dartslivescorer.commonActivities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.dartslivescorer.R;
import com.example.dartslivescorer.adapters.GameItemAdapter;

import java.util.ArrayList;
import java.util.List;

import com.example.dartslivescorer.enums.eGames;

import models.commonModels.GameItem;
import models.commonModels.MusicService;

public class GamesListActivity extends AppCompatActivity {

    private Button retour;
    private WindowInsetsControllerCompat windowInsetsController;

    private Vibrator vibrator;
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
        setContentView(R.layout.activity_games_list);

        String musicServiceId = getIntent().getStringExtra("MusicServiceId");

        if (musicServiceId != null) {
            Intent serviceIntent = new Intent(this, MusicService.class);
            bindService(serviceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }

        // Initialize the WindowInsetsControllerCompat
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // Hide the system bars
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        List<GameItem> games = new ArrayList<>();
        games.add(new GameItem(eGames.Standard301,"301", 10,301));
        games.add(new GameItem(eGames.Standard501,"501", 15,501));
        games.add(new GameItem(eGames.Standard701,"701", 15,701));
        games.add(new GameItem(eGames.OriginalCricket,"Original Cricket", 20,0));
        games.add(new GameItem(eGames.HiddenCricket,"Hidden Cricket", 20,0));
        games.add(new GameItem(eGames.RandomCricket,"Random Cricket", 20,0));
        games.add(new GameItem(eGames.UnderTheHat,"Under The Hat", 8,0));
        games.add(new GameItem(eGames.ShootOut,"Shoot Out", 8,0));
        games.add(new GameItem(eGames.MasterMind,"Master Mind", 20,0));

        GridView gameGridView = findViewById(R.id.game_grid_view);
        gameGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                vibrate();
                GameItem selectedGameItem = (GameItem) parent.getItemAtPosition(position);
                // Lancer PlayerSelectionActivity pour choisir les joueurs
                Intent playerSelectionIntent = new Intent(getApplicationContext(), SelectPlayersActivity.class);

                // Ajouter le jeu choisi à l'intent
                playerSelectionIntent.putExtra("selectedGame", selectedGameItem);
                playerSelectionIntent.putExtra("MusicServiceId", "uniqueMusicServiceId");
                startActivity(playerSelectionIntent);
                finish();
            }
        });
        gameGridView.setAdapter(new GameItemAdapter(this, games));

        this.retour = (Button) findViewById(R.id.jeuretour);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), IntroGameActivity.class);
                intent.putExtra("MusicServiceId", "uniqueMusicServiceId");
                startActivity(intent);
                finish();
            }
        });
    }
    // Fonction pour faire vibrer le téléphone
    private void vibrate() {
        if (vibrator != null && vibrator.hasVibrator()) {
            // Vibration de 100 millisecondes
            vibrator.vibrate(100);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Arrête la vibration lors de la destruction de l'activité
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}
