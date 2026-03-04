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
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.dartslivescorer.R;

import models.commonModels.MusicService;

public class IntroGameActivity extends AppCompatActivity {

    private ImageButton newGameImage;
    private ImageButton playersImage;
    private Button newGameButton;
    private Button playersButton;
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
        setContentView(R.layout.activity_intro_game);

        // Liez votre activité au service de musique
        Intent music = new Intent(this, MusicService.class);
        bindService(music, musicConnection, Context.BIND_AUTO_CREATE);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Initialize the WindowInsetsControllerCompat
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // Hide the system bars
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        this.newGameButton = findViewById(R.id.NewGameButton);
        this.playersButton = findViewById(R.id.PlayersListButton);

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate();

                Intent intent = new Intent(getApplicationContext(), GamesListActivity.class);
                intent.putExtra("MusicServiceId", "uniqueMusicServiceId");
                startActivity(intent);
                finish();
            }
        });

        playersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate();

                Intent intent = new Intent(getApplicationContext(), PlayersActivity.class);
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
        // Arrêtez la musique et libérez les ressources lorsque l'activité est détruite
        /*if (isMusicBound) {
            unbindService(musicConnection);
            isMusicBound = false;
        }*/
    }

}
