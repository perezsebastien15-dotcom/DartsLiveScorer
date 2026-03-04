package com.example.dartslivescorer.commonActivities;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.dartslivescorer.R;
import com.example.dartslivescorer.adapters.TargetView;
import com.example.dartslivescorer.enums.eGames;

import models.commonModels.GameItem;

public class TargetViewActivity extends AppCompatActivity {

    private GameItem currentGame;
    private Parcelable parcelIntem;
    private int[] touchedItems;
    private int[] closeItems;
    private int[] cricketItems;
    private int[] closeSoloItems;
    private WindowInsetsControllerCompat windowInsetsController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_view);


        // Initialize the WindowInsetsControllerCompat
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // Hide the system bars
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        currentGame = getIntent().getParcelableExtra("selectedGame");
        TouchData touchData = getIntent().getParcelableExtra("touch");

        TargetView targetView = findViewById(R.id.target_view);
        targetView.setGame(currentGame.getType());

        if (touchData != null) {
            if(currentGame.getType().equals(eGames.ShootOut))
            {
                touchedItems = touchData.getTouchedItems();

                if (touchedItems != null)
                    targetView.setTouched(touchedItems);
            }
            if(currentGame.getType().equals(eGames.OriginalCricket)
                    || currentGame.getType().equals(eGames.HiddenCricket)
                    || currentGame.getType().equals(eGames.RandomCricket))
            {

                touchedItems = touchData.getTouchedItems();
                closeItems = touchData.getClosedItems();
                cricketItems = touchData.getCricketItems();
                closeSoloItems = touchData.getCloseSoloItems();

                if (touchedItems != null)
                    targetView.setTouched(touchedItems);
                if (closeItems != null)
                    targetView.setClosed(closeItems);
                if(cricketItems != null)
                    targetView.setCricketItems(cricketItems);
                if(closeSoloItems != null)
                    targetView.setClosedSoloItems(closeSoloItems);
            }
        }

        targetView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    // Fermer l'activité et retourner au jeu
                    finish();
                    return true;
            }
        });
    }
}