package com.example.dartslivescorer.gamesActivities;

import android.annotation.SuppressLint;
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
    private Button retour;
    private WindowInsetsControllerCompat windowInsetsController;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_standard_activity);

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

        this.controller = new StandardController(this);

        TextView jeu = findViewById(R.id.titre_jeu);
        jeu.setText("STANDARD   :   "+String.valueOf(this.selectedGame.getScore()) + "   !");

        // Définition de l'écouteur pour les mises à jour de score
        this.controller.setOnScoreUpdateListener(new models.OnScoreUpdateListener() {
            @Override
            public void onScoreUpdate(int score) {
                if(eStates.EnCours.equals(controller.getStatut()))
                    MAJInformations();
                if(eStates.Depasse.equals(controller.getStatut()))
                    Depassement();
                if(eStates.Termine.equals(controller.getStatut()))
                    Termine();
            }
        });

        this.commoncontroller = new CommonController();
        this.controller.InitialisePartie(this.selectedGame, this.selectedPlayers, DartScorerDatabase.getDatabase(this), this.commoncontroller);

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

            button.getBackground().setTint(Color.BLACK);
            button.getBackground().setAlpha(150);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibrate();
                    if ((button.getTag().toString().equals(eButtons.Suivant.toString())) && (button.getText().toString().equals("Suivant"))) {

                        if(eStates.Depasse.equals(controller.getStatut()))
                            controller.resetLance();

                        controller.changementJoueur(controller.rotationJoueur());

                        if(controller.checkLastRound())
                            controller.checkTimeout();

                        MAJInformations();

                        if(eStates.Timeout.equals(controller.getStatut()))
                            TimeOut();

                    } else if (button.getTag().toString().equals(eButtons.Multiple.toString())) {
                        controller.changementMulti(scoreButton);
                        MAJInformations();
                    }
                    else if (button.getTag().toString().equals(eButtons.Fin.toString())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(StandardGameActivity.this);
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

        MAJInformations();
    }

    private void MAJInformations() {
        PlayerItem joueur = this.controller.getJoueurCourant();
        LanceItem lance = this.controller.getLance();
        boolean lastRound = this.controller.checkLastRound();
        boolean toggle = false;

        TextView pointsRestantsTextView = findViewById(R.id.Points_restants);
        TextView nom_joueur = findViewById(R.id.joueur_courant);
        TextView un = findViewById(R.id.premierLance);
        TextView deux = findViewById(R.id.deuxiemeLance);
        TextView trois = findViewById(R.id.troisiemeLance);
        TextView tour = findViewById(R.id.tours);

        TextView prop_un = findViewById(R.id.prop_un);
        TextView prop_deux = findViewById(R.id.prop_deux);
        TextView prop_trois = findViewById(R.id.prop_trois);

        Integer points = 0;
        String nom = joueur.getName();

        if (lance.tir_un != -1) {
            nom = joueur.getName() + " : " + String.valueOf(lance.tir_un);
            un.setText("Fléchette 1 : " + String.valueOf(lance.tir_un));
            un.setVisibility(View.VISIBLE);
        } else {
            un.setVisibility(View.GONE);
        }

        if (lance.tir_deux != -1) {
            points = lance.tir_un + lance.tir_deux;
            nom = joueur.getName() + " : " + String.valueOf(points);
            deux.setText("Fléchette 2 : " + String.valueOf(lance.tir_deux));
            deux.setVisibility(View.VISIBLE);
        } else {
            deux.setVisibility(View.GONE);
        }

        if (lance.tir_trois != -1) {
            points = lance.tir_un + lance.tir_deux + lance.tir_trois;
            nom = joueur.getName() + " : " + String.valueOf(points);
            trois.setText("Fléchette 3 : " + String.valueOf(lance.tir_trois));
            trois.setVisibility(View.VISIBLE);
            if(!eStates.Depasse.equals(controller.getStatut())) {
                toggle = true;
                this.gridLayout = this.commoncontroller.toggleButtons(toggle, this.gridLayout);
            }
        } else {
            trois.setVisibility(View.GONE);
            if(!eStates.Depasse.equals(controller.getStatut())) {
                toggle = false;
                this.gridLayout = this.commoncontroller.toggleButtons(toggle, this.gridLayout);
            }
        }

        nom_joueur.setText(nom);
        if(!eStates.Timeout.equals(controller.getStatut()))
            tour.setText("Tour : " + String.valueOf(joueur.getTour()) + " / " + String.valueOf(selectedGame.getTours()));

        if(joueur.getScore() <= 180) {
            List<String> combi = controller.getCombi();
            if(combi != null && combi.size()>0) {
                if (combi.size() == 1) {
                    prop_un.setVisibility(View.VISIBLE);
                    prop_deux.setVisibility(View.GONE);
                    prop_trois.setVisibility(View.GONE);
                    prop_un.setText(combi.get(0));
                }
                if (combi.size() == 2) {
                    prop_un.setVisibility(View.VISIBLE);
                    prop_deux.setVisibility(View.VISIBLE);
                    prop_trois.setVisibility(View.GONE);
                    prop_un.setText(combi.get(0));
                    prop_deux.setText(combi.get(1));
                }
                if (combi.size() == 3) {
                    prop_un.setVisibility(View.VISIBLE);
                    prop_deux.setVisibility(View.VISIBLE);
                    prop_trois.setVisibility(View.VISIBLE);
                    prop_un.setText(combi.get(0));
                    prop_deux.setText(combi.get(1));
                    prop_trois.setText(combi.get(2));
                }
            }
            else {
                prop_un.setVisibility(View.GONE);
                prop_deux.setVisibility(View.GONE);
                prop_trois.setVisibility(View.GONE);
            }
        }

        pointsRestantsTextView.setText(String.valueOf(joueur.getScore()));

        if(lastRound)
            tour.setText("Dernier Tour !");

        if(!toggle)
            this.gridLayout = this.commoncontroller.chargeMultiple(this.gridLayout);

        updateAdversairesList();
    }
    private void TimeOut() {
        //1 - Afficher le score OK en VERT + Message
        MAJInformations();

        //2 - Afficher une fenetre OK pour terminer le jeu
        afficherDialogueGagnant(controller.getWinner().getName());
    }
    private void updateAdversairesList() {
        List<PlayerItem> adversaires = controller.getAdversaires();
        List<AdversaireItem> adversaireItems = new ArrayList<>();
        for (PlayerItem adversaire : adversaires) {
            AdversaireItem item = new AdversaireItem(adversaire.getName(), adversaire.getScore());
            adversaireItems.add(item);
        }

        AdversairesAdapter adapter = new AdversairesAdapter(this, adversaireItems);
        GridView gridViewAdversaires = findViewById(R.id.gridViewAdversaires);
        gridViewAdversaires.setAdapter(adapter);
    }
    private void Depassement()
    {
        //1 - Afficher le score dépassé en rouge + Message
        MAJInformations();

        //2 - Mettre les boutons suivant et retour OK
        this.commoncontroller.toggleButtons(true,this.gridLayout);
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
        builder.setTitle("Gagnant");
        builder.setMessage("Le gagnant est : " + gagnant);
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
    public void onScoreUpdate(int score) {}
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
            mediaPlayer.setLooping(false);
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
