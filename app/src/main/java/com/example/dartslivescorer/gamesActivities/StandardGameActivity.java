package com.example.dartslivescorer.gamesActivities;

import android.annotation.SuppressLint;
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
import com.example.dartslivescorer.adapters.AdversairesAdapter;
import com.example.dartslivescorer.commonActivities.GamesListActivity;
import com.example.dartslivescorer.controllers.CommonController;
import com.example.dartslivescorer.controllers.StandardController;
import com.example.dartslivescorer.enums.eButtons;
import com.example.dartslivescorer.enums.eStates;

import java.util.ArrayList;
import java.util.List;

import models.DartScorerDatabase;
import models.OnScoreUpdateListener;
import models.commonModels.AdversaireItem;
import models.commonModels.GameItem;
import models.commonModels.LanceItem;
import models.commonModels.ScoreButtonItem;
import models.gamesModels.PlayerItem;

public class StandardGameActivity extends AppCompatActivity implements OnScoreUpdateListener {

    private GameItem selectedGame;
    private List<PlayerItem> selectedPlayers;
    private CommonController commoncontroller;
    private StandardController controller;
    private GridLayout gridLayout;
    private WindowInsetsControllerCompat windowInsetsController;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_standard_activity);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        this.selectedGame    = getIntent().getParcelableExtra("selectedGame");
        this.selectedPlayers = getIntent().getParcelableArrayListExtra("selectedPlayers");

        this.controller = new StandardController(this);

        TextView jeu = findViewById(R.id.titre_jeu);
        jeu.setText("STANDARD   :   " + this.selectedGame.getScore() + "   !");

        this.controller.setOnScoreUpdateListener(score -> {
            if (eStates.EnCours.equals(controller.getStatut()))  MAJInformations();
            if (eStates.Depasse.equals(controller.getStatut()))  Depassement();
            if (eStates.Termine.equals(controller.getStatut()))  Termine();
        });

        this.commoncontroller = new CommonController();
        this.controller.InitialisePartie(this.selectedGame, this.selectedPlayers,
                DartScorerDatabase.getDatabase(this), this.commoncontroller);

        this.gridLayout = findViewById(R.id.gridLayout);
        List<ScoreButtonItem> scoreButtonList = this.commoncontroller.InitScoreButtons();

        for (ScoreButtonItem scoreButton : scoreButtonList) {
            Button button = new Button(this);
            button.setLayoutParams(new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f)
            ));
            button.setId(scoreButton.getId());
            button.setText(scoreButton.getLabel());
            button.setTag(scoreButton.getType());
            // ✅ Couleur via ressource
            button.getBackground().setTint(getResources().getColor(R.color.button_bg_default, getTheme()));
            button.getBackground().setAlpha(150);

            button.setOnClickListener(v -> {
                vibrate();
                String tag  = button.getTag().toString();
                String text = button.getText().toString();

                if (tag.equals(eButtons.Suivant.toString()) && text.equals("Suivant")) {
                    if (eStates.Depasse.equals(controller.getStatut()))
                        controller.resetLance();
                    controller.changementJoueur(controller.rotationJoueur());
                    if (controller.checkLastRound()) controller.checkTimeout();
                    MAJInformations();
                    if (eStates.Timeout.equals(controller.getStatut())) TimeOut();

                } else if (tag.equals(eButtons.Multiple.toString())) {
                    controller.changementMulti(scoreButton);
                    MAJInformations();

                } else if (tag.equals(eButtons.Fin.toString())) {
                    new AlertDialog.Builder(StandardGameActivity.this)
                            .setTitle("Confirmation")
                            .setMessage("Voulez-vous vraiment quitter ?")
                            .setPositiveButton("Oui", (dialog, which) -> {
                                startActivity(new Intent(getApplicationContext(), GamesListActivity.class));
                                finish();
                            })
                            .setNegativeButton("Non", (dialog, which) -> dialog.dismiss())
                            .create().show();

                } else {
                    controller.MAJFlechette(scoreButton);
                }
            });
            gridLayout.addView(button);
        }

        MAJInformations();
    }

    private void MAJInformations() {
        PlayerItem joueur = this.controller.getJoueurCourant();
        LanceItem  lance  = this.controller.getLance();
        boolean lastRound = this.controller.checkLastRound();
        boolean toggle    = false;

        TextView pointsRestantsTextView = findViewById(R.id.Points_restants);
        TextView nom_joueur             = findViewById(R.id.joueur_courant);
        TextView un                     = findViewById(R.id.premierLance);
        TextView deux                   = findViewById(R.id.deuxiemeLance);
        TextView trois                  = findViewById(R.id.troisiemeLance);
        TextView tour                   = findViewById(R.id.tours);
        TextView prop_un                = findViewById(R.id.prop_un);
        TextView prop_deux              = findViewById(R.id.prop_deux);
        TextView prop_trois             = findViewById(R.id.prop_trois);

        Integer points = 0;
        String nom = joueur.getName();

        if (lance.tir_un != -1) {
            nom = joueur.getName() + " : " + lance.tir_un;
            un.setText("Fléchette 1 : " + lance.tir_un);
            un.setVisibility(View.VISIBLE);
        } else { un.setVisibility(View.GONE); }

        if (lance.tir_deux != -1) {
            points = lance.tir_un + lance.tir_deux;
            nom = joueur.getName() + " : " + points;
            deux.setText("Fléchette 2 : " + lance.tir_deux);
            deux.setVisibility(View.VISIBLE);
        } else { deux.setVisibility(View.GONE); }

        if (lance.tir_trois != -1) {
            points = lance.tir_un + lance.tir_deux + lance.tir_trois;
            nom = joueur.getName() + " : " + points;
            trois.setText("Fléchette 3 : " + lance.tir_trois);
            trois.setVisibility(View.VISIBLE);
            if (!eStates.Depasse.equals(controller.getStatut())) {
                toggle = true;
                // ✅ Context passé en paramètre
                this.gridLayout = this.commoncontroller.toggleButtons(true, this.gridLayout, this);
            }
        } else {
            trois.setVisibility(View.GONE);
            if (!eStates.Depasse.equals(controller.getStatut())) {
                toggle = false;
                this.gridLayout = this.commoncontroller.toggleButtons(false, this.gridLayout, this);
            }
        }

        nom_joueur.setText(nom);
        if (!eStates.Timeout.equals(controller.getStatut()))
            tour.setText("Tour : " + joueur.getTour() + " / " + selectedGame.getTours());

        if (joueur.getScore() <= 180) {
            List<String> combi = controller.getCombi();
            prop_un.setVisibility(View.GONE);
            prop_deux.setVisibility(View.GONE);
            prop_trois.setVisibility(View.GONE);
            if (combi != null && !combi.isEmpty()) {
                if (combi.size() >= 1) { prop_un.setVisibility(View.VISIBLE);    prop_un.setText(combi.get(0)); }
                if (combi.size() >= 2) { prop_deux.setVisibility(View.VISIBLE);  prop_deux.setText(combi.get(1)); }
                if (combi.size() >= 3) { prop_trois.setVisibility(View.VISIBLE); prop_trois.setText(combi.get(2)); }
            }
        }

        pointsRestantsTextView.setText(String.valueOf(joueur.getScore()));
        if (lastRound) tour.setText("Dernier Tour !");

        if (!toggle)
            // ✅ Context passé en paramètre
            this.gridLayout = this.commoncontroller.chargeMultiple(this.gridLayout, this);

        updateAdversairesList();
    }

    private void TimeOut() {
        MAJInformations();
        afficherDialogueGagnant(controller.getWinner().getName());
    }

    private void updateAdversairesList() {
        List<PlayerItem> adversaires = controller.getAdversaires();
        List<AdversaireItem> adversaireItems = new ArrayList<>();
        for (PlayerItem adversaire : adversaires)
            adversaireItems.add(new AdversaireItem(adversaire.getName(), adversaire.getScore()));

        GridView gridViewAdversaires = findViewById(R.id.gridViewAdversaires);
        gridViewAdversaires.setAdapter(new AdversairesAdapter(this, adversaireItems));
    }

    private void Depassement() {
        MAJInformations();
        this.commoncontroller.toggleButtons(true, this.gridLayout, this);
    }

    private void Termine() {
        MAJInformations();
        afficherDialogueGagnant(controller.getJoueurCourant().getName());
    }

    private void afficherDialogueGagnant(String gagnant) {
        new AlertDialog.Builder(this)
                .setTitle("Gagnant")
                .setMessage("Le gagnant est : " + gagnant)
                .setPositiveButton("OK", (dialog, id) -> {
                    startActivity(new Intent(getApplicationContext(), GamesListActivity.class));
                    finish();
                })
                .create().show();
    }

    @Override public void onScoreUpdate(int score) {}

    private void vibrate() {
        if (vibrator != null && vibrator.hasVibrator())
            vibrator.vibrate(100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vibrator != null)   vibrator.cancel();
        if (mediaPlayer != null) { mediaPlayer.release(); mediaPlayer = null; }
    }
}
