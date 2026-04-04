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
import com.example.dartslivescorer.adapters.CricketPlayerAdapter;
import com.example.dartslivescorer.commonActivities.GamesListActivity;
import com.example.dartslivescorer.commonActivities.TargetViewActivity;
import com.example.dartslivescorer.commonActivities.TouchData;
import com.example.dartslivescorer.controllers.CommonController;
import com.example.dartslivescorer.controllers.CricketController;
import com.example.dartslivescorer.enums.eButtons;
import com.example.dartslivescorer.enums.eGames;
import com.example.dartslivescorer.enums.eStates;

import java.util.List;

import models.DartScorerDatabase;
import models.OnScoreUpdateListener;
import models.commonModels.GameItem;
import models.commonModels.LanceItem;
import models.commonModels.ScoreButtonItem;
import models.gamesModels.CricketPlayerItem;
import models.gamesModels.CricketValueItem;
import models.gamesModels.PlayerItem;

public class CricketGameActivity extends AppCompatActivity implements OnScoreUpdateListener {

    private GameItem selectedGame;
    private List<CricketPlayerItem> cricketPlayers;
    private List<PlayerItem> selectedPlayers;
    private CommonController commoncontroller;
    private CricketController controller;
    private GridLayout gridLayout;
    private WindowInsetsControllerCompat windowInsetsController;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_cricket_activity);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        this.selectedGame    = getIntent().getParcelableExtra("selectedGame");
        this.selectedPlayers = getIntent().getParcelableArrayListExtra("selectedPlayers");

        this.controller = new CricketController(this);
        this.controller.setOnScoreUpdateListener(score -> {
            if (eStates.EnCours.equals(controller.getStatut())) MAJInformations();
            if (eStates.Termine.equals(controller.getStatut())) Termine();
        });

        TextView titre = findViewById(R.id.titre_adversaire);
        titre.setOnTouchListener(onTouchListener);

        if (this.selectedGame.getType().equals(eGames.OriginalCricket)) titre.setText("CRICKET STANDARD !");
        if (this.selectedGame.getType().equals(eGames.HiddenCricket))   titre.setText("HIDDEN CRICKET !");
        if (this.selectedGame.getType().equals(eGames.RandomCricket))   titre.setText("RANDOM CRICKET !");

        this.commoncontroller = new CommonController();
        this.cricketPlayers = controller.InitialisePartie(
                this.selectedGame, this.selectedPlayers,
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
            button.getBackground().setTint(getResources().getColor(R.color.button_bg_default, getTheme()));
            button.getBackground().setAlpha(150);

            button.setOnClickListener(v -> {
                vibrate();
                String tag  = button.getTag().toString();
                String text = button.getText().toString();

                if (tag.equals(eButtons.Suivant.toString()) && text.equals("Suivant")) {
                    controller.changementJoueur(controller.rotationJoueur());
                    controller.InitPlayersAfterAction(true);
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
                    new AlertDialog.Builder(CricketGameActivity.this)
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

        SetCricketValues();
        MAJInformations();
    }

    private void SetCricketValues() {
        List<CricketValueItem> values = this.controller.getCricketValues();
        int[] ids = {R.id.valeur_une, R.id.valeur_deux, R.id.valeur_trois,
                     R.id.valeur_quatre, R.id.valeur_cinq, R.id.valeur_six, R.id.valeur_sept};
        String[] hidden = {"H", "I", "D", "D", "E", "N", "!"};

        for (int i = 0; i < ids.length; i++) {
            TextView tv = findViewById(ids[i]);
            tv.setText(!values.get(i).getHiddenItem() ? values.get(i).getLibelleItem() : hidden[i]);
        }
    }

    private void MAJItemAvailable() {
        List<CricketValueItem> items = controller.getCricketValues();
        int[] ids = {R.id.valeur_une, R.id.valeur_deux, R.id.valeur_trois,
                     R.id.valeur_quatre, R.id.valeur_cinq, R.id.valeur_six, R.id.valeur_sept};

        // ✅ Couleurs via ressources
        int colorActive   = ContextCompat.getColor(this, R.color.cricket_value_active);
        int colorInactive = ContextCompat.getColor(this, android.R.color.darker_gray);

        for (int i = 0; i < ids.length; i++) {
            TextView tv = findViewById(ids[i]);
            tv.setTextColor(items.get(i).getAvailable() ? colorActive : colorInactive);
        }
    }

    private void MAJInformations() {
        LanceItem lance    = this.controller.getLance();
        boolean lastRound  = this.controller.checkLastRound();

        updateAdversairesList();

        TextView tour = findViewById(R.id.nb_tours);

        // ✅ Context passé en paramètre
        if (lance.tir_trois != -1)
            this.gridLayout = this.commoncontroller.toggleButtons(true, this.gridLayout, this);
        else
            this.gridLayout = this.commoncontroller.toggleButtons(false, this.gridLayout, this);

        this.gridLayout = this.commoncontroller.chargeMultiple(this.gridLayout, this);

        if (!eStates.Timeout.equals(controller.getStatut()))
            tour.setText("Tour : " + controller.getJoueurCourant().getTour() + " / " + selectedGame.getTours());

        if (lastRound) tour.setText("Dernier Tour !");

        MAJItemAvailable();
        SetCricketValues();
    }

    private void updateAdversairesList() {
        GridView gridViewAdversaires = findViewById(R.id.gridViewAdversaires);
        gridViewAdversaires.setAdapter(new CricketPlayerAdapter(this, controller.getAdversaires()));
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
        if (gagnant.equals("EX AEQUO !"))       { builder.setTitle("Pas de gagnant"); builder.setMessage(gagnant); }
        else if (gagnant.contains(","))          { builder.setTitle("Gagnants");       builder.setMessage(gagnant); }
        else { builder.setTitle("Gagnant"); builder.setMessage("Le gagnant est : " + gagnant); }

        builder.setPositiveButton("OK", (d, id) -> {
            startActivity(new Intent(getApplicationContext(), GamesListActivity.class));
            finish();
        }).create().show();
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
        List<Integer> touch       = this.controller.getCurrentPlayerTouch();
        List<Integer> closePlayer = this.controller.getClosedPlayerValues();
        List<Integer> close       = this.controller.getClosedValues();
        List<CricketValueItem> items = this.controller.getValuesItem();

        int[] touchInt    = toIntArray(touch);
        int[] closeInt    = toIntArray(close);
        int[] closeSolo   = toIntArray(closePlayer);
        int[] itemCricket = new int[items != null ? items.size() : 0];
        if (items != null)
            for (int i = 0; i < items.size(); i++) itemCricket[i] = items.get(i).getIntLibelleItem();

        TouchData datas = new TouchData(touchInt, closeInt, itemCricket, closeSolo);
        startActivity(new Intent(this, TargetViewActivity.class)
                .putExtra("selectedGame", this.selectedGame)
                .putExtra("touch", datas));
    }

    private int[] toIntArray(List<Integer> list) {
        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
        return arr;
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
