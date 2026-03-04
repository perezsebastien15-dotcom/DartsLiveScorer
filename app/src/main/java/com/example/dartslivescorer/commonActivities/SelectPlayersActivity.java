package com.example.dartslivescorer.commonActivities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.View;
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

import models.DartScorerDatabase;
import models.asyncTasks.GetJoueursAsyncTask;
import models.commonModels.GameItem;
import models.commonModels.MusicService;
import models.gamesModels.PlayerItem;

// Import des classes nécessaires
// ...

public class SelectPlayersActivity extends AppCompatActivity implements GetJoueursAsyncTask.OnJoueursLoadedListener {

    private List<PlayerItem> allPlayers;
    private List<PlayerItem> selectedPlayers;
    private List<PlayerItem> displayedPlayers;
    private Button retour;
    private Button lancer;
    private DartScorerDatabase db;
    private GridView gridView;
    private PlayerItemAdapter playerAdapter;
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
        setContentView(R.layout.activity_players_selection);

        // Liez votre activité au service de musique
        String musicServiceId = getIntent().getStringExtra("MusicServiceId");

        if (musicServiceId != null) {
            Intent serviceIntent = new Intent(this, MusicService.class);
            bindService(serviceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Initialize the WindowInsetsControllerCompat
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // Hide the system bars
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        // Récupérer le jeu choisi depuis l'intent
        GameItem selectedGameItem = getIntent().getParcelableExtra("selectedGame");

        db = DartScorerDatabase.getDatabase(this);

        retour = findViewById(R.id.jeuretour);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate();
                Intent intent = new Intent(getApplicationContext(), GamesListActivity.class);
                intent.putExtra("MusicServiceId", "uniqueMusicServiceId");
                startActivity(intent);
                finish();
            }
        });

        lancer = findViewById(R.id.plateau_jeu);
        lancer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                vibrate();
                if (isMusicBound) {
                    unbindService(musicConnection);
                    isMusicBound = false;
                }

                if(selectedGameItem.getType().equals(eGames.Standard301)
                        || selectedGameItem.getType().equals(eGames.Standard501)
                        || selectedGameItem.getType().equals(eGames.Standard701)) {
                    if(selectedPlayers.size()>0) {
                        Intent intent = new Intent(SelectPlayersActivity.this, StandardGameActivity.class);
                        intent.putExtra("selectedGame", selectedGameItem);
                        intent.putExtra("selectedPlayers", new ArrayList<>(selectedPlayers));
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(SelectPlayersActivity.this, "Sélectionnez au moins un joueur", Toast.LENGTH_SHORT).show();

                }
                if(selectedGameItem.getType().equals(eGames.OriginalCricket)
                        || selectedGameItem.getType().equals(eGames.HiddenCricket)
                        || selectedGameItem.getType().equals(eGames.RandomCricket)) {
                    if(selectedPlayers.size()>1) {
                        Intent intent = new Intent(SelectPlayersActivity.this, CricketGameActivity.class);
                        intent.putExtra("selectedGame", selectedGameItem);
                        intent.putExtra("selectedPlayers", new ArrayList<>(selectedPlayers));
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(SelectPlayersActivity.this, "Sélectionnez au moins deux joueurs", Toast.LENGTH_SHORT).show();

                }
                if(selectedGameItem.getType().equals(eGames.UnderTheHat)) {
                    if(selectedPlayers.size()>1 && selectedPlayers.size()<10) {
                        Intent intent = new Intent(SelectPlayersActivity.this, UnderHatActivity.class);
                        intent.putExtra("selectedGame", selectedGameItem);
                        intent.putExtra("selectedPlayers", new ArrayList<>(selectedPlayers));
                        startActivity(intent);
                    }
                    else if(selectedPlayers.size()<1)
                        Toast.makeText(SelectPlayersActivity.this, "Sélectionnez au moins deux joueurs", Toast.LENGTH_SHORT).show();
                    else if(selectedPlayers.size()>9)
                        Toast.makeText(SelectPlayersActivity.this, "Sélectionnez moins de dix joueurs", Toast.LENGTH_SHORT).show();

                }
                if(selectedGameItem.getType().equals(eGames.ShootOut)) {
                    if(selectedPlayers.size()>1) {
                        // Arrêtez la musique et libérez les ressources lorsque l'activité est détruite
                        if (isMusicBound) {
                            musicService.stopMusic();
                            unbindService(musicConnection);
                            isMusicBound = false;
                        }
                        Intent intent = new Intent(SelectPlayersActivity.this, ShootOutActivity.class);
                        intent.putExtra("selectedGame", selectedGameItem);
                        intent.putExtra("selectedPlayers", new ArrayList<>(selectedPlayers));
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(SelectPlayersActivity.this, "Sélectionnez au moins un joueur", Toast.LENGTH_SHORT).show();

                }
                if(selectedGameItem.getType().equals(eGames.MasterMind)) {
                    if(selectedPlayers.size()==1) {
                        // Arrêtez la musique et libérez les ressources lorsque l'activité est détruite
                        if (isMusicBound) {
                            musicService.stopMusic();
                            unbindService(musicConnection);
                            isMusicBound = false;
                        }
                        Intent intent = new Intent(SelectPlayersActivity.this, MasterMindActivity.class);
                        intent.putExtra("selectedGame", selectedGameItem);
                        intent.putExtra("selectedPlayers", new ArrayList<>(selectedPlayers));
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(SelectPlayersActivity.this, "Sélectionnez un seul joueur", Toast.LENGTH_SHORT).show();

                }
            }
        });

        gridView = findViewById(R.id.player_grid_view);
        selectedPlayers = new ArrayList<>();
        displayedPlayers = new ArrayList<>();

        // Ne pas définir le gestionnaire de clic long ici
        playerAdapter = new PlayerItemAdapter(this, displayedPlayers, null, new PlayerItemAdapter.OnPlayerItemClickListener() {
            @Override
            public void onPlayerItemClick(PlayerItem playerItem) {
                vibrate();
                togglePlayerSelection(playerItem);
                updateGridView();
            }
        });

        new GetJoueursAsyncTask(this, db).execute();

        gridView.setAdapter(playerAdapter);
    }


    private void togglePlayerSelection(PlayerItem playerItem) {
        if (selectedPlayers.contains(playerItem)) {
            selectedPlayers.remove(playerItem);
        } else {
            selectedPlayers.add(playerItem);
        }
    }

    private void updateGridView() {
        if (playerAdapter != null && allPlayers != null) {
            playerAdapter.updateSelectedPlayers(selectedPlayers);
            playerAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onJoueursLoaded(List<PlayerItem> joueurs) {
        allPlayers = joueurs;
        displayedPlayers.addAll(allPlayers);
        playerAdapter.notifyDataSetChanged();
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



