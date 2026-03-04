package com.example.dartslivescorer.gamesActivities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

public class MasterMindActivity extends AppCompatActivity{

    private GameItem selectedGame;
    private List<PlayerItem> selectedPlayers;
    private CommonController commoncontroller;
    private MasterController controller;
    private GridLayout gridLayout;
    private Button retour;
    private WindowInsetsControllerCompat windowInsetsController;
    private Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_master_mind_activity);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Initialize the WindowInsetsControllerCompat
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        // Hide the system bars
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());


        // Récupérez le jeu choisi et les joueurs sélectionnés depuis l'intent
        this.selectedGame = getIntent().getParcelableExtra("selectedGame");
        this.selectedPlayers = getIntent().getParcelableArrayListExtra("selectedPlayers");


        this.controller = new MasterController(this);
        this.controller.InitialisePartie(this.selectedGame, this.selectedPlayers, DartScorerDatabase.getDatabase(this));

        this.gridLayout = findViewById(R.id.gridLayout);

        this.commoncontroller = new CommonController();
        List<ScoreButtonItem> scoreButtonList = this.commoncontroller.InitScoreButtons();

        for (ScoreButtonItem scoreButton : scoreButtonList) {
            Button button = new Button(this);
            button.setLayoutParams(new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f)
            ));

            /* Personnalisation des boutons */
            button.setId(scoreButton.getId());
            button.setText(scoreButton.getLabel());
            button.setTag(scoreButton.getType());

            button.getBackground().setTint(Color.BLACK);
            button.getBackground().setAlpha(150);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibrate();
                    if ((button.getTag().toString().equals(eButtons.Suivant.toString())) && (button.getText().toString().equals("Suivant"))) {

                        if (controller.checkLastRound())
                            controller.checkTimeout();

                        MAJInformations();

                        if (eStates.Timeout.equals(controller.getStatut()))
                            TimeOut();

                    }
                    else if (button.getTag().toString().equals(eButtons.Multiple.toString())) {
                        controller.changementMulti(scoreButton);
                        MAJInformations();
                    }
                    else if ((button.getTag().toString().equals(eButtons.Suivant.toString())) && (button.getText().toString().equals("Miss !"))) {
                        controller.MAJFlechette(scoreButton);
                        MAJInformations();
                    }
                    else if (button.getTag().toString().equals(eButtons.Fin.toString())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MasterMindActivity.this);
                        builder.setTitle("Confirmation");
                        builder.setMessage("Voulez-vous vraiment quitter ?");
                        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Si l'utilisateur clique sur "Oui", quittez l'activité
                                Intent intent = new Intent(getApplicationContext(), GamesListActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Si l'utilisateur clique sur "Non", ne rien faire
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else {
                        controller.MAJFlechette(scoreButton);
                        MAJInformations();
                    }
                }
            });
            gridLayout.addView(button);
        }

        MAJInformations();
    }

    private void MAJInformations() {
        MMPlayerItem joueur = this.controller.getJoueurCourant();
        LanceItem lance = this.controller.getLance();

        boolean lastRound = this.controller.checkLastRound();
        boolean toggle = false;

        TextView tour = findViewById(R.id.nb_tours);

        if(lance.tir_trois != -1)
            this.gridLayout = this.commoncontroller.toggleButtons(true,this.gridLayout);
        else
            this.gridLayout = this.commoncontroller.toggleButtons(false,this.gridLayout);

        if(!toggle)
            this.gridLayout = this.commoncontroller.chargeMultiple(this.gridLayout);

        if(!eStates.Timeout.equals(controller.getStatut()))
            tour.setText("Tour : " + String.valueOf(joueur.getTour()) + " / " + String.valueOf(selectedGame.getTours()));

        if(lastRound)
            tour.setText("Dernier Tour !");

        //MAJItemAvailable();
    }

    public void MAJItemAvailable() {
        /*TextView valeur_une = findViewById(R.id.valeur_une);
        TextView valeur_deux = findViewById(R.id.valeur_deux);
        TextView valeur_trois = findViewById(R.id.valeur_trois);
        TextView valeur_quatre = findViewById(R.id.valeur_quatre);
        TextView valeur_cinq = findViewById(R.id.valeur_cinq);
        TextView valeur_six = findViewById(R.id.valeur_six);
        TextView valeur_sept = findViewById(R.id.valeur_sept);

        List<CricketValueItem> items = controller.getCricketValues();

        if(!items.get(0).getAvailable())
            valeur_une.setTextColor(Color.DKGRAY);
        else
            valeur_une.setTextColor(Color.parseColor("#08FF00"));
        if(!items.get(1).getAvailable())
            valeur_deux.setTextColor(Color.DKGRAY);
        else
            valeur_deux.setTextColor(Color.parseColor("#08FF00"));
        if(!items.get(2).getAvailable())
            valeur_trois.setTextColor(Color.DKGRAY);
        else
            valeur_trois.setTextColor(Color.parseColor("#08FF00"));
        if(!items.get(3).getAvailable())
            valeur_quatre.setTextColor(Color.DKGRAY);
        else
            valeur_quatre.setTextColor(Color.parseColor("#08FF00"));
        if(!items.get(4).getAvailable())
            valeur_cinq.setTextColor(Color.DKGRAY);
        else
            valeur_cinq.setTextColor(Color.parseColor("#08FF00"));
        if(!items.get(5).getAvailable())
            valeur_six.setTextColor(Color.DKGRAY);
        else
            valeur_six.setTextColor(Color.parseColor("#08FF00"));
        if(!items.get(6).getAvailable())
            valeur_sept.setTextColor(Color.DKGRAY);
        else
            valeur_sept.setTextColor(Color.parseColor("#08FF00"));*/

    }
    private void SetCricketValues() {
        /*List<CricketValueItem> values = this.controller.getCombi();

        TextView un = findViewById(R.id.valeur_une);
        TextView deux = findViewById(R.id.valeur_deux);
        TextView trois = findViewById(R.id.valeur_trois);
        TextView quatre = findViewById(R.id.valeur_quatre);

        if(!values.get(0).getHiddenItem())
            un.setText(values.get(0).getLibelleItem());
        else
            un.setText("H");

        if(!values.get(1).getHiddenItem())
            deux.setText(values.get(1).getLibelleItem());
        else
            deux.setText("I");

        if(!values.get(2).getHiddenItem())
            trois.setText(values.get(2).getLibelleItem());
        else
            trois.setText("D");

        if(!values.get(3).getHiddenItem())
            quatre.setText(values.get(3).getLibelleItem());
        else
            quatre.setText("D");

        if(!values.get(4).getHiddenItem())
            cinq.setText(values.get(4).getLibelleItem());
        else
            cinq.setText("E");

        if(!values.get(5).getHiddenItem())
            six.setText(values.get(5).getLibelleItem());
        else
            six.setText("N");

        if(!values.get(6).getHiddenItem())
            sept.setText(values.get(6).getLibelleItem());
        else
            sept.setText("!");*/
    }

    private void TimeOut() {
        //1 - Afficher le Message
        MAJInformations();

        //2 - Afficher une fenetre OK pour terminer le jeu
        //afficherDialogueGagnant(controller.getWinner());
    }
    private void Termine() {
        //1 - Afficher le score OK en VERT + Message
        MAJInformations();

        //2 - Afficher une fenetre OK pour terminer le jeu
        afficherDialogueGagnant(controller.getJoueurCourant().getName());
    }
    private void afficherDialogueGagnant(String gagnant) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(gagnant.equals("EX AEQUO !"))
        {
            builder.setTitle("Pas de gagnant");
            builder.setMessage(gagnant);
        }
        else if(gagnant.contains(",")) {
            builder.setTitle("Gagnants");
            builder.setMessage(gagnant);
        }
        else
        {
            builder.setTitle("Gagnant");
            builder.setMessage("Le gagnant est : " + gagnant);
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getApplicationContext(), GamesListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void vibrate() {
        if (vibrator != null && vibrator.hasVibrator()) {
            // Vibration de 100 millisecondes
            vibrator.vibrate(100);
        }
    }
    private void playMusic() {
        /*if (mediaPlayer != null) {
            // Répéter la musique en boucle
            mediaPlayer.setLooping(true);
            // Démarrer la lecture
            mediaPlayer.start();
        }*/
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Arrête la vibration lors de la destruction de l'activité
        if (vibrator != null) {
            vibrator.cancel();
        }/*
        // Arrête la musique lors de la destruction de l'activité
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }*/
    }
}