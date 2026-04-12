package com.example.dartslivescorer.commonActivities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputFilter;
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

    private EditText nomEditText;
    private DartScorerDatabase db;
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
        setContentView(R.layout.activity_players_add);

        String musicServiceId = getIntent().getStringExtra("MusicServiceId");
        if (musicServiceId != null)
            bindService(new Intent(this, MusicService.class), musicConnection, Context.BIND_AUTO_CREATE);

        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        nomEditText = findViewById(R.id.nomEditText);
        nomEditText.setFilters(new InputFilter[]{ new InputFilter.AllCaps() });

        Button retour = findViewById(R.id.jeuretour);
        retour.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), PlayersActivity.class)
                    .putExtra("MusicServiceId", "uniqueMusicServiceId"));
            finish();
        });

        Button ajout = findViewById(R.id.ajouter_joueur);
        ajout.setOnClickListener(v -> ajouterJoueur());

        // DB init en background dès l'ouverture de l'écran
        DartScorerDatabase.initAsync(this, () -> db = DartScorerDatabase.getDatabase(this));
    }

    private void ajouterJoueur() {
        String nom = nomEditText.getText().toString().trim();
        if (nom.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir un nom", Toast.LENGTH_SHORT).show();
            return;
        }
        if (db == null) {
            Toast.makeText(this, "Base de données non prête, réessayez", Toast.LENGTH_SHORT).show();
            return;
        }
        Joueur joueur = new Joueur();
        joueur.nom = nom;
        new InsertJoueurAsyncTask(this, db).execute(joueur);
        startActivity(new Intent(this, PlayersActivity.class)
                .putExtra("MusicServiceId", "uniqueMusicServiceId"));
        finish();
    }
}
