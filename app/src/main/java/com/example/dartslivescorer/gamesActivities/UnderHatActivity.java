package com.example.dartslivescorer.gamesActivities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.dartslivescorer.R;
import com.example.dartslivescorer.adapters.UTHPlayerAdapter;
import com.example.dartslivescorer.commonActivities.GamesListActivity;
import com.example.dartslivescorer.controllers.CommonController;
import com.example.dartslivescorer.controllers.UTHController;
import com.example.dartslivescorer.enums.eButtons;
import com.example.dartslivescorer.enums.eStates;

import java.util.List;

import models.DartScorerDatabase;
import models.OnScoreUpdateListener;
import models.commonModels.GameItem;
import models.commonModels.LanceItem;
import models.commonModels.ScoreButtonItem;
import models.gamesModels.PlayerItem;
import models.gamesModels.UTHPlayerItem;

public class UnderHatActivity extends AppCompatActivity implements OnScoreUpdateListener {

    private GameItem selectedGame;
    private List<UTHPlayerItem> uthPlayers;
    private List<PlayerItem> selectedPlayers;
    private CommonController commoncontroller;
    private UTHController controller;
    private GridLayout gridLayout;
    private WindowInsetsControllerCompat windowInsetsController;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_under_hat_activity);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        this.selectedGame    = getIntent().getParcelableExtra("selectedGame");
        this.selectedPlayers = getIntent().getParcelableArrayListExtra("selectedPlayers");

        this.controller = new UTHController(this);
        this.controller.setOnScoreUpdateListener(score -> {
            if (eStates.EnCours.equals(controller.getStatut())) MAJInformations();
            if (eStates.Termine.equals(controller.getStatut())) Termine();
        });

        this.commoncontroller = new CommonController();
        this.uthPlayers = controller.InitialisePartie(this.selectedGame, this.selectedPlayers,
                DartScorerDatabase.getDatabase(this), this.commoncontroller);

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

            // Le bouton "Simple" (ID 22) est masqué dans ce jeu
            if (scoreButton.getId() != CommonController.ID_SIMPLE) {
                button.getBackground().setTint(getResources().getColor(R.color.button_bg_default, getTheme()));
                button.getBackground().setAlpha(150);

                button.setOnClickListener(v -> {
                    vibrate();
                    String tag  = button.getTag().toString();
                    String text = button.getText().toString();

                    if (tag.equals(eButtons.Suivant.toString()) && text.equals("Suivant")) {
                        controller.checkSup();
                        controller.changementJoueur(controller.rotationJoueur());
                        if (controller.checkLastRound()) controller.checkTimeout();
                        controller.checkFinJeu();
                        MAJInformations();
                        if (eStates.Termine.equals(controller.getStatut())) Termine();
                        if (eStates.Timeout.equals(controller.getStatut()))  TimeOut();

                    } else if (tag.equals(eButtons.Multiple.toString())) {
                        controller.changementMulti(scoreButton);
                        MAJInformations();

                    } else if (tag.equals(eButtons.Suivant.toString()) && text.equals("Miss !")) {
                        controller.MAJFlechette(scoreButton);
                        MAJInformations();

                    } else if (tag.equals(eButtons.Fin.toString())) {
                        new AlertDialog.Builder(UnderHatActivity.this)
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
                    }
                });
                gridLayout.addView(button);
            } else {
                button.setVisibility(View.GONE);
                gridLayout.addView(button);
            }
        }

        MAJInformations();
    }

    private void MAJInformations() {
        UTHPlayerItem joueur = this.controller.getJoueurCourant();
        LanceItem lance      = this.controller.getLance();
        boolean lastRound    = this.controller.checkLastRound();

        updateAdversairesList();

        TextView flechette   = findViewById(R.id.flechettes);
        TextView score_battre = findViewById(R.id.score_battre);
        TextView score_joueur = findViewById(R.id.score_joueur);
        TextView tour         = findViewById(R.id.nb_tours);

        if (lance.tir_un != -1) {
            flechette.setText(lance.str_tir_un);
            flechette.setVisibility(View.VISIBLE);
        } else {
            flechette.setVisibility(View.GONE);
        }
        if (lance.tir_deux  != -1) flechette.setText(lance.str_tir_un + " - " + lance.str_tir_deux);
        if (lance.tir_trois != -1) flechette.setText(lance.str_tir_un + " - " + lance.str_tir_deux + " - " + lance.str_tir_trois);

        score_battre.setText("Cible : " + controller.getCible());
        score_joueur.setText(joueur.getName() + " : " + joueur.getScore());

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

    private void updateAdversairesList() {
        ((GridView) findViewById(R.id.gridViewAdversaires))
                .setAdapter(new UTHPlayerAdapter(this, controller.getAdversaires()));
    }

    private void TimeOut() {
        MAJInformations();
        afficherDialogueGagnant(controller.getWinner());
    }

    private void Termine() {
        MAJInformations();
        afficherDialogueGagnant(controller.getJoueurCourant().getName());
    }

    private void afficherDialogueGagnant(String gagnant) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (gagnant.contains(",")) { builder.setTitle("Gagnants"); builder.setMessage("Les gagnants sont : " + gagnant); }
        else { builder.setTitle("Gagnant"); builder.setMessage("Le gagnant est : " + gagnant); }
        builder.setPositiveButton("OK", (d, id) -> {
            startActivity(new Intent(getApplicationContext(), GamesListActivity.class));
            finish();
        }).create().show();
    }

    @Override public void onScoreUpdate(int score) {}

    private void vibrate() {
        if (vibrator != null && vibrator.hasVibrator()) vibrator.vibrate(100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vibrator != null)    vibrator.cancel();
        if (mediaPlayer != null) { mediaPlayer.release(); mediaPlayer = null; }
    }
}
