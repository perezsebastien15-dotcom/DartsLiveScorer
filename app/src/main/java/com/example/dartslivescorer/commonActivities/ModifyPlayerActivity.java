package com.example.dartslivescorer.commonActivities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.dartslivescorer.R;

import models.DartScorerDatabase;
import models.commonModels.Joueur;
import models.commonModels.MusicService;
import models.asyncTasks.UpdateJoueurAsyncTask;

public class ModifyPlayerActivity extends AppCompatActivity {

    private Button retour;
    private Button modifier;
    private EditText nom;
    private DartScorerDatabase db;
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
        setContentView(R.layout.activity_players_modification);


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

        db = DartScorerDatabase.getDatabase(this);

        Intent intent = getIntent();

        long playerId = intent.getLongExtra("playerId", 0);
        String playerName = intent.getStringExtra("playerName");

        // Retour à la liste des joueurs
        this.retour = findViewById(R.id.jeuretour);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlayersActivity.class);
                intent.putExtra("MusicServiceId", "uniqueMusicServiceId");
                startActivity(intent);
                finish();
            }
        });

        this.nom = findViewById(R.id.nomEditText);
        this.nom.setText(playerName);

        this.modifier = findViewById(R.id.modifier_joueur);
        modifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatabase(playerId, nom.getText().toString().trim());
            }
        });

        EditText editTextPlayerName = findViewById(R.id.nomEditText);

        InputFilter inputFilterUpperCase = new InputFilter.AllCaps();
        InputFilter[] inputFilters = new InputFilter[] { inputFilterUpperCase };
        editTextPlayerName.setFilters(inputFilters);
    }

    private void updateDatabase(Long id, String nom) {
        // Récupérer le nom saisi dans l'EditText
        if (!nom.isEmpty()) {
            Joueur currentPlayer = new Joueur();
            currentPlayer.id = id;
            currentPlayer.nom = nom;

            // Mise à jour du joueur
            new UpdateJoueurAsyncTask(ModifyPlayerActivity.this, db).execute(currentPlayer);

            Intent intent = new Intent(ModifyPlayerActivity.this, PlayersActivity.class);

            intent.putExtra("MusicServiceId", "uniqueMusicServiceId");
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Veuillez saisir un nom", Toast.LENGTH_SHORT).show();
        }
    }
}