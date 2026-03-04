package com.example.dartslivescorer.commonActivities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
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
import models.asyncTasks.InsertJoueurAsyncTask;
import models.commonModels.Joueur;
import models.commonModels.MusicService;

public class AddPlayerActivity extends AppCompatActivity {

    private Button retour;
    private Button ajout;
    private EditText nomEditText;
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
        setContentView(R.layout.activity_players_add);

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

        nomEditText = findViewById(R.id.nomEditText);

        // Retour a la liste des joueurs
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

        // Ajouter un joueur
        this.ajout = findViewById(R.id.ajouter_joueur);
        ajout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mettez à jour la base de données
                updateDatabase();
            }
        });

        EditText editTextPlayerName = findViewById(R.id.nomEditText);

        InputFilter inputFilterUpperCase = new InputFilter.AllCaps();
        InputFilter[] inputFilters = new InputFilter[] { inputFilterUpperCase };
        editTextPlayerName.setFilters(inputFilters);
    }

    // Méthode pour générer une couleur aléatoire
    private int getRandomColor() {
        int red = (int) (Math.random() * 256);
        int green = (int) (Math.random() * 256);
        int blue = (int) (Math.random() * 256);

        return Color.rgb(red, green, blue);
    }

    private void updateDatabase() {
        // Récupérer le nom saisi dans l'EditText
        String nom = nomEditText.getText().toString().trim();
        if (!nom.isEmpty()) {
            Joueur joueur = new Joueur();
            joueur.nom = nom;

            // Insérer le joueur dans la base de données
            DartScorerDatabase db = DartScorerDatabase.getDatabase(this);
            // Utilisation de la classe InsertJoueurAsyncTask pour l'insertion asynchrone
            new InsertJoueurAsyncTask(this, db).execute(joueur);

            Intent intent = new Intent(AddPlayerActivity.this, PlayersActivity.class);
            intent.putExtra("MusicServiceId", "uniqueMusicServiceId");
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Veuillez saisir un nom", Toast.LENGTH_SHORT).show();
        }
    }
}
