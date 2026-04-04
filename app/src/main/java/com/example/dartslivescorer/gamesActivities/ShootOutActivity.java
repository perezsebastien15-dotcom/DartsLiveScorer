package com.example.dartslivescorer.gamesActivities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.dartslivescorer.R;
import com.example.dartslivescorer.adapters.AdversairesAdapter;
import com.example.dartslivescorer.commonActivities.GamesListActivity;
import com.example.dartslivescorer.commonActivities.TargetViewActivity;
import com.example.dartslivescorer.commonActivities.TouchData;
import com.example.dartslivescorer.controllers.CommonController;
import com.example.dartslivescorer.controllers.ShootOutController;
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
import models.gamesModels.ShootOutPlayerItem;

public class ShootOutActivity extends AppCompatActivity implements OnScoreUpdateListener {

    private GameItem selectedGame;
    private List<PlayerItem> selectedPlayers;
    private CommonController commoncontroller;
    private ShootOutController controller;
    private GridLayout gridLayout;
    private WindowInsetsControllerCompat windowInsetsController;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_shoot_out_activity);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        this.selectedGame    = getIntent().getParcelableExtra("selectedGame");
        this.selectedPlayers = getIntent().getParcelableArrayListExtra("selectedPlayers");

        this.controller = new ShootOutController(this);
        this.controller.setOnScoreUpdateListener(score -> {
            if (eStates.EnCours.equals(controller.getStatut()))  MAJInformations();
            if (eStates.Termine.equals(controller.getStatut()))  Termine();
            if (eStates.Timeout.equals(controller.getStatut()))  TimeOut();
        });

        this.commoncontroller = new CommonController();
        controller.InitialisePartie(this.selectedGame, this.selectedPlayers,
                DartScorerDatabase.getDatabase(this), this.commoncontroller);

        this.gridLayout = findViewById(R.id.gridLayout);

        TextView textView = findViewById(R.id.titre_jeu);
        textView.setOnTouchListener(onTouchListener);

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
                    if (eStates.Depasse.equals(controller.getStatut())) controller.resetLance();
                    controller.changementJoueur(controller.rotationJoueur());
                    if (controller.checkLastRound()) controller.checkTimeout();
                    MAJInformations();
                    if (eStates.Timeout.equals(controller.getStatut())) TimeOut();

                } else if (tag.equals(eButtons.Multiple.toString())) {
                    controller.changementMulti(scoreButton);
                    MAJInformations();

                } else if (tag.equals(eButtons.Fin.toString())) {
                    new AlertDialog.Builder(ShootOutActivity.this)
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
        }

        MAJInformations();
    }

    private final View.OnTouchListener onTouchListener = (v, event) -> {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            vibrate();
            afficherCible();
            return true;
        }
        return false;
    };

    private void afficherCible() {
        List<Integer> touch = this.controller.getCurrentPlayerTouch();
        int[] touchInt = new int[touch.size()];
        for (int i = 0; i < touch.size(); i++) touchInt[i] = touch.get(i);

        startActivity(new Intent(this, TargetViewActivity.class)
                .putExtra("selectedGame", this.selectedGame)
                .putExtra("touch", new TouchData(touchInt)));
    }

    private void MAJInformations() {
        ShootOutPlayerItem joueur = this.controller.getJoueurCourant();
        LanceItem lance    = this.controller.getLance();
        boolean lastRound  = this.controller.checkLastRound();

        TextView pointsRestantsTextView = findViewById(R.id.Points_restants);
        TextView nom_joueur = findViewById(R.id.joueur_courant);
        TextView un         = findViewById(R.id.premierLance);
        TextView deux       = findViewById(R.id.deuxiemeLance);
        TextView trois      = findViewById(R.id.troisiemeLance);
        TextView tour       = findViewById(R.id.tours);
        TextView multi      = findViewById(R.id.multiplicateur);
        TextView zones      = findViewById(R.id.zones);
        TextView flech      = findViewById(R.id.flech);

        List<Integer> items = joueur.getTouche();
        int nb_flech = (this.selectedGame.getTours() - joueur.getTour()) * 3 + 3;
        zones.setText("zones : " + (21 - items.size()));

        if (lance.tir_un != -1) {
            un.setText("Flechette 1 : " + joueur.getLance().str_tir_un);
            un.setVisibility(View.VISIBLE);
            flech.setText("flech : " + (nb_flech - 1));
        } else {
            un.setVisibility(View.GONE);
            flech.setText("flech : " + nb_flech);
        }

        if (lance.tir_deux != -1) {
            deux.setText("Flechette 2 : " + joueur.getLance().str_tir_deux);
            deux.setVisibility(View.VISIBLE);
            flech.setText("flech : " + (nb_flech - 2));
        } else { deux.setVisibility(View.GONE); }

        if (lance.tir_trois != -1) {
            trois.setText("Flechette 3 : " + joueur.getLance().str_tir_trois);
            trois.setVisibility(View.VISIBLE);
            flech.setText("flech : " + (nb_flech - 3));
        } else { trois.setVisibility(View.GONE); }

        multi.setText("Multiplicateur : " + joueur.getMultiplicateur());
        nom_joueur.setText(joueur.getName());
        pointsRestantsTextView.setText(String.valueOf(joueur.getScore()));

        if (!eStates.Timeout.equals(controller.getStatut()))
            tour.setText("Tour : " + joueur.getTour() + " / " + selectedGame.getTours());

        // ✅ Context passé en paramètre
        if (lance.tir_trois != -1)
            this.gridLayout = this.commoncontroller.toggleButtons(true, this.gridLayout, this);
        else
            this.gridLayout = this.commoncontroller.toggleButtons(false, this.gridLayout, this);

        if (lastRound) tour.setText("Dernier Tour !");

        this.gridLayout = this.commoncontroller.chargeMultiple(this.gridLayout, this);

        allowButtons();
        updateAdversairesList();
    }

    private void allowButtons() {
        List<Integer> items = this.controller.getJoueurCourant().getTouche();
        // ✅ Couleurs via ressources
        int colorUsed    = ContextCompat.getColor(this, R.color.shootout_used);
        int colorDefault = ContextCompat.getColor(this, R.color.button_text_default);

        for (int i = 0; i < this.gridLayout.getChildCount(); i++) {
            View view = this.gridLayout.getChildAt(i);
            if (view instanceof Button) {
                Button button = (Button) view;
                String tag = button.getTag().toString();
                if (tag.equals(eButtons.Points.toString()) || tag.equals(eButtons.Speciaux.toString()))
                    button.setTextColor(items.contains(button.getId()) ? colorUsed : colorDefault);
            }
        }
    }

    private void updateAdversairesList() {
        List<ShootOutPlayerItem> adversaires = controller.getAdversaires();
        List<AdversaireItem> adversaireItems = new ArrayList<>();
        for (ShootOutPlayerItem a : adversaires)
            adversaireItems.add(new AdversaireItem(a.getName(), a.getScore()));
        ((GridView) findViewById(R.id.gridViewAdversaires))
                .setAdapter(new AdversairesAdapter(this, adversaireItems));
    }

    private void TimeOut() {
        MAJInformations();
        afficherDialogueGagnant(controller.getWinner().getName());
    }

    private void Termine() {
        MAJInformations();
        afficherDialogueGagnant(controller.getJoueurCourant().getName());
    }

    private void afficherDialogueGagnant(String gagnant) {
        new AlertDialog.Builder(this)
                .setTitle("Gagnant")
                .setMessage("Le gagnant est : " + gagnant)
                .setPositiveButton("OK", (d, id) -> {
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
