package com.example.dartslivescorer.gamesActivities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.dartslivescorer.R;
import com.example.dartslivescorer.commonActivities.GamesListActivity;
import com.example.dartslivescorer.controllers.CommonController;
import com.example.dartslivescorer.controllers.MasterController;
import com.example.dartslivescorer.enums.eButtons;
import com.example.dartslivescorer.enums.eStates;

import java.util.List;

import models.DartScorerDatabase;
import models.commonModels.GameItem;
import models.commonModels.LanceItem;
import models.commonModels.ScoreButtonItem;
import models.gamesModels.MMPlayerItem;
import models.gamesModels.PlayerItem;

public class MasterMindActivity extends AppCompatActivity {

    private GameItem selectedGame;
    private List<PlayerItem> selectedPlayers;
    private CommonController commoncontroller;
    private MasterController controller;
    private GridLayout gridLayout;
    private WindowInsetsControllerCompat windowInsetsController;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_master_mind_activity);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        this.selectedGame    = getIntent().getParcelableExtra("selectedGame");
        this.selectedPlayers = getIntent().getParcelableArrayListExtra("selectedPlayers");

        this.controller = new MasterController(this);
        this.controller.InitialisePartie(this.selectedGame, this.selectedPlayers,
                DartScorerDatabase.getDatabase(this));

        this.commoncontroller = new CommonController();
        this.gridLayout = findViewById(R.id.gridLayout);

        List<ScoreButtonItem> scoreButtonList = this.commoncontroller.InitScoreButtons();

        for (ScoreButtonItem scoreButton : scoreButtonList) {
            Button button = new Button(this);
            button.setLayoutParams(new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f)));
            button.setId(scoreButton.getId());
            button.setText(scoreButton.getLabel());
            button.setTag(scoreButton.getType());
            button.getBackground().setTint(getResources().getColor(R.color.button_bg_default, getTheme()));
            button.getBackground().setAlpha(150);

            button.setOnClickListener(v -> {
                vibrate();
                String tag  = button.getTag().toString();
                String text = button.getText().toString();

                if (tag.equals(eButtons.Suivant.toString()) && text.equals("Suivant")) {
                    if (controller.checkLastRound()) controller.checkTimeout();
                    MAJInformations();
                    if (eStates.Timeout.equals(controller.getStatut())) TimeOut();

                } else if (tag.equals(eButtons.Multiple.toString())) {
                    controller.changementMulti(scoreButton);
                    MAJInformations();

                } else if (tag.equals(eButtons.Suivant.toString()) && text.equals("Miss !")) {
                    controller.MAJFlechette(scoreButton);
                    MAJInformations();

                } else if (tag.equals(eButtons.Fin.toString())) {
                    new AlertDialog.Builder(MasterMindActivity.this)
                            .setTitle("Confirmation")
                            .setMessage("Voulez-vous vraiment quitter ?")
                            .setPositiveButton("Oui", (d, w) -> {
                                startActivity(new Intent(getApplicationContext(), GamesListActivity.class));
                                finish();
                            })
                            .setNegativeButton("Non", (d, w) -> d.dismiss())
                            .create().show();
                } else {
                    controller.MAJFlechette(scoreButton);
                    MAJInformations();
                }
            });
            gridLayout.addView(button);
        }

        MAJInformations();
    }

    private void MAJInformations() {
        MMPlayerItem joueur = this.controller.getJoueurCourant();
        LanceItem lance     = this.controller.getLance();
        boolean lastRound   = this.controller.checkLastRound();

        TextView tour = findViewById(R.id.nb_tours);

        // ✅ Context passé en paramètre
        if (lance.tir_trois != -1)
            this.gridLayout = this.commoncontroller.toggleButtons(true, this.gridLayout, this);
        else
            this.gridLayout = this.commoncontroller.toggleButtons(false, this.gridLayout, this);

        this.gridLayout = this.commoncontroller.chargeMultiple(this.gridLayout, this);

        if (!eStates.Timeout.equals(controller.getStatut()))
            tour.setText("Tour : " + joueur.getTour() + " / " + selectedGame.getTours());

        if (lastRound) tour.setText("Dernier Tour !");
    }

    private void TimeOut() {
        MAJInformations();
        // Dialogue de fin à implémenter si besoin
    }

    private void Termine() {
        MAJInformations();
        afficherDialogueGagnant(controller.getJoueurCourant().getName());
    }

    private void afficherDialogueGagnant(String gagnant) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (gagnant.equals("EX AEQUO !"))  { builder.setTitle("Pas de gagnant"); builder.setMessage(gagnant); }
        else if (gagnant.contains(","))    { builder.setTitle("Gagnants");       builder.setMessage(gagnant); }
        else { builder.setTitle("Gagnant"); builder.setMessage("Le gagnant est : " + gagnant); }

        builder.setPositiveButton("OK", (d, id) -> {
            startActivity(new Intent(getApplicationContext(), GamesListActivity.class));
            finish();
        }).create().show();
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
