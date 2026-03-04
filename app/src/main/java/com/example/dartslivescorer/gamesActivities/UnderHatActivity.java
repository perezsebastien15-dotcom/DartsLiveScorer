package com.example.dartslivescorer.gamesActivities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
    private Button retour;
    private WindowInsetsControllerCompat windowInsetsController;

    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_under_hat_activity);

        // Initialise le MediaPlayer avec le fichier audio
        //mediaPlayer = MediaPlayer.create(this, R.raw.bits);
        //playMusic();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Initialize the WindowInsetsControllerCompat
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // Hide the system bars
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        // Récupérez le jeu choisi et les joueurs sélectionnés depuis l'intent
        this.selectedGame = getIntent().getParcelableExtra("selectedGame");
        this.selectedPlayers = getIntent().getParcelableArrayListExtra("selectedPlayers");

        this.controller = new UTHController(this);

        // Définition de l'écouteur pour les mises à jour de score
        this.controller.setOnScoreUpdateListener(new models.OnScoreUpdateListener() {
            @Override
            public void onScoreUpdate(int score) {
                if(eStates.EnCours.equals(controller.getStatut()))
                    MAJInformations();
                if(eStates.Termine.equals(controller.getStatut()))
                    Termine();
            }
        });

        this.commoncontroller = new CommonController();
        this.uthPlayers = controller.InitialisePartie(this.selectedGame, this.selectedPlayers, DartScorerDatabase.getDatabase(this),this.commoncontroller);

        this.gridLayout = findViewById(R.id.gridLayout);

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

            if(scoreButton.getId()  != 22) {
                button.getBackground().setTint(Color.BLACK);
                button.getBackground().setAlpha(150);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vibrate();
                        if ((button.getTag().toString().equals(eButtons.Suivant.toString())) && (button.getText().toString().equals("Suivant"))) {

                            //Verifier si le joueur a gagner, si non il perd un chapeau
                            controller.checkSup();

                            //Changement du joueur
                            controller.changementJoueur(controller.rotationJoueur());

                            //Si c'est le dernier tour
                            if (controller.checkLastRound())
                                controller.checkTimeout();

                            //Si c'est la fin du jeu
                            controller.checkFinJeu();

                            //On met à jour les infos de l'ecran
                            MAJInformations();

                            //Si un joueur a gagne
                            if (eStates.Termine.equals(controller.getStatut()))
                                Termine();

                            //Si on a passé le nb de tour
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(UnderHatActivity.this);
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
                        }
                    }
                });
                gridLayout.addView(button);
            }
            else
            {
                button.setVisibility(View.GONE);
            }
        }

        MAJInformations();
    }

    private void TimeOut() {
        //1 - Afficher le Message
        MAJInformations();

        //2 - Afficher une fenetre OK pour terminer le jeu
        afficherDialogueGagnant(controller.getWinner());
    }

    private void updateAdversairesList() {
        List<UTHPlayerItem> adversaires = controller.getAdversaires();

        UTHPlayerAdapter adapter = new UTHPlayerAdapter(this, adversaires);
        GridView gridViewAdversaires = findViewById(R.id.gridViewAdversaires);
        gridViewAdversaires.setAdapter(adapter);
    }

    private void MAJInformations() {
        UTHPlayerItem joueur = this.controller.getJoueurCourant();
        LanceItem lance = this.controller.getLance();

        boolean lastRound = this.controller.checkLastRound();
        boolean toggle = false;

        updateAdversairesList();

        TextView flechette = findViewById(R.id.flechettes);
        TextView score_battre = findViewById(R.id.score_battre);
        TextView score_joueur = findViewById(R.id.score_joueur);
        TextView tour = findViewById(R.id.nb_tours);

        if (lance.tir_un != -1) {
            flechette.setText(lance.str_tir_un);
            flechette.setVisibility(View.VISIBLE);
        } else {
            flechette.setVisibility(View.GONE);
        }
        if (lance.tir_deux != -1)
            flechette.setText(lance.str_tir_un + " - " + lance.str_tir_deux);
        if (lance.tir_trois != -1)
            flechette.setText(lance.str_tir_un + " - " + lance.str_tir_deux + " - " + lance.str_tir_trois);

        score_battre.setText("Cible : " + String.valueOf(controller.getCible()));
        score_joueur.setText(joueur.getName() + " : " + String.valueOf(joueur.getScore()));

        if (lance.tir_trois != -1)
            this.gridLayout = this.commoncontroller.toggleButtons(true,this.gridLayout);
        else
            this.gridLayout = this.commoncontroller.toggleButtons(false,this.gridLayout);

        if (!toggle)
            this.gridLayout = this.commoncontroller.chargeMultiple(this.gridLayout);

        if (!eStates.Timeout.equals(controller.getStatut()))
            tour.setText("Tour : " + String.valueOf(joueur.getTour()) + " / " + String.valueOf(selectedGame.getTours()));

        if (lastRound)
            tour.setText("Dernier Tour !");
    }

    private void Termine()
    {
        //1 - Afficher le score OK en VERT + Message
        MAJInformations();

        //2 - Afficher une fenetre OK pour terminer le jeu
        afficherDialogueGagnant(controller.getJoueurCourant().getName());
    }

    private void afficherDialogueGagnant(String gagnant) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(gagnant.contains(",")) {
            builder.setTitle("Gagnants");
            builder.setMessage("Les gagnants sont : "+gagnant);
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

    @Override
    public void onScoreUpdate(int score) {    }

    // Fonction pour faire vibrer le téléphone
    private void vibrate() {
        if (vibrator != null && vibrator.hasVibrator()) {
            // Vibration de 100 millisecondes
            vibrator.vibrate(100);
        }
    }

    // Fonction pour jouer la musique
    private void playMusic() {
        if (mediaPlayer != null) {
            // Répéter la musique en boucle
            mediaPlayer.setLooping(true);
            // Démarrer la lecture
            mediaPlayer.start();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Arrête la vibration lors de la destruction de l'activité
        if (vibrator != null) {
            vibrator.cancel();
        }
        // Arrête la musique lors de la destruction de l'activité
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}