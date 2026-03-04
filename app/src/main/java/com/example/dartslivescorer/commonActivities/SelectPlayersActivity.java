package com.example.dartslivescorer.commonActivities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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

import models.DartScorerDatabase;
import models.asyncTasks.GetJoueursAsyncTask;
import models.commonModels.GameItem;
import models.commonModels.MusicService;
import models.gamesModels.PlayerItem;

public class SelectPlayersActivity extends AppCompatActivity implements GetJoueursAsyncTask.OnJoueursLoadedListener {

    private List<PlayerItem> allPlayers;
    private List<PlayerItem> selectedPlayers;
    private List<PlayerItem> displayedPlayers;
    private DartScorerDatabase db;
    private GridView gridView;
    private PlayerItemAdapter playerAdapter;
    private WindowInsetsControllerCompat windowInsetsController;
    private Vibrator vibrator;
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
        setContentView(R.layout.activity_players_selection);

        String musicServiceId = getIntent().getStringExtra("MusicServiceId");
        if (musicServiceId != null)
            bindService(new Intent(this, MusicService.class), musicConnection, Context.BIND_AUTO_CREATE);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        GameItem selectedGameItem = getIntent().getParcelableExtra("selectedGame");
        db = DartScorerDatabase.getDatabase(this);

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
            lancerJeu(selectedGameItem);
        });

        gridView = findViewById(R.id.player_grid_view);
        selectedPlayers  = new ArrayList<>();
        displayedPlayers = new ArrayList<>();

        playerAdapter = new PlayerItemAdapter(this, displayedPlayers, null, playerItem -> {
            vibrate();
            togglePlayerSelection(playerItem);
            updateGridView();
        });

        new GetJoueursAsyncTask(this, db).execute();
        gridView.setAdapter(playerAdapter);
    }

    private void lancerJeu(GameItem selectedGameItem) {
        eGames type = selectedGameItem.getType();

        if (type == eGames.Standard301 || type == eGames.Standard501 || type == eGames.Standard701) {
            if (selectedPlayers.size() > 0)
                startGame(StandardGameActivity.class, selectedGameItem);
            else
                Toast.makeText(this, "Sélectionnez au moins un joueur", Toast.LENGTH_SHORT).show();

        } else if (type == eGames.OriginalCricket || type == eGames.HiddenCricket || type == eGames.RandomCricket) {
            if (selectedPlayers.size() > 1)
                startGame(CricketGameActivity.class, selectedGameItem);
            else
                Toast.makeText(this, "Sélectionnez au moins deux joueurs", Toast.LENGTH_SHORT).show();

        } else if (type == eGames.UnderTheHat) {
            if (selectedPlayers.size() > 1 && selectedPlayers.size() < 10)
                startGame(UnderHatActivity.class, selectedGameItem);
            else if (selectedPlayers.size() < 2)
                Toast.makeText(this, "Sélectionnez au moins deux joueurs", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Sélectionnez moins de dix joueurs", Toast.LENGTH_SHORT).show();

        } else if (type == eGames.ShootOut) {
            if (selectedPlayers.size() > 1)
                startGame(ShootOutActivity.class, selectedGameItem);
            else
                Toast.makeText(this, "Sélectionnez au moins deux joueurs", Toast.LENGTH_SHORT).show();

        } else if (type == eGames.MasterMind) {
            if (selectedPlayers.size() == 1)
                startGame(MasterMindActivity.class, selectedGameItem);
            else
                Toast.makeText(this, "Sélectionnez un seul joueur", Toast.LENGTH_SHORT).show();
        }
    }

    private void startGame(Class<?> activityClass, GameItem selectedGameItem) {
        startActivity(new Intent(this, activityClass)
                .putExtra("selectedGame", selectedGameItem)
                .putExtra("selectedPlayers", new ArrayList<>(selectedPlayers)));
    }

    private void togglePlayerSelection(PlayerItem playerItem) {
        if (selectedPlayers.contains(playerItem)) selectedPlayers.remove(playerItem);
        else selectedPlayers.add(playerItem);
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

    private void vibrate() {
        if (vibrator != null && vibrator.hasVibrator()) vibrator.vibrate(100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vibrator != null) vibrator.cancel();
    }
}
