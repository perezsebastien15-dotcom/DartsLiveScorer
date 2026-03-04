package com.example.dartslivescorer.gamesActivities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
    private Button retour;
    private WindowInsetsControllerCompat windowInsetsController;

    private Vibrator vibrator;

    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_cricket_activity);

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

        this.controller = new CricketController(this);

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

        TextView tour = findViewById(R.id.titre_adversaire);

        TextView tours = findViewById(R.id.nb_tours);
        tour.setOnTouchListener(onTouchListener);

        if(this.selectedGame.getType().equals(eGames.OriginalCricket))
            tour.setText("CRIKET STANDARD !");
        if(this.selectedGame.getType().equals(eGames.HiddenCricket))
            tour.setText("HIDDEN CRIKET !");
        if(this.selectedGame.getType().equals(eGames.RandomCricket))
            tour.setText("RANDOM CRIKET !");


        this.commoncontroller = new CommonController();
        this.cricketPlayers = controller.InitialisePartie(this.selectedGame, this.selectedPlayers, DartScorerDatabase.getDatabase(this), this.commoncontroller);

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

                        controller.changementJoueur(controller.rotationJoueur());
                        controller.InitPlayersAfterAction(true);

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
                        AlertDialog.Builder builder = new AlertDialog.Builder(CricketGameActivity.this);
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

        SetCricketValues();
        MAJInformations();
    }

    private void SetCricketValues() {
        List<CricketValueItem> values = this.controller.getCricketValues();

        TextView un = findViewById(R.id.valeur_une);
        TextView deux = findViewById(R.id.valeur_deux);
        TextView trois = findViewById(R.id.valeur_trois);
        TextView quatre = findViewById(R.id.valeur_quatre);
        TextView cinq = findViewById(R.id.valeur_cinq);
        TextView six = findViewById(R.id.valeur_six);
        TextView sept = findViewById(R.id.valeur_sept);

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
            sept.setText("!");
    }

    private void TimeOut() {
        //1 - Afficher le Message
        MAJInformations();

        //2 - Afficher une fenetre OK pour terminer le jeu
        afficherDialogueGagnant(controller.getWinner());
    }

    private void updateAdversairesList() {
        List<CricketPlayerItem> adversaires = controller.getAdversaires();

        CricketPlayerAdapter adapter = new CricketPlayerAdapter(this, adversaires);
        GridView gridViewAdversaires = findViewById(R.id.gridViewAdversaires);
        gridViewAdversaires.setAdapter(adapter);
    }

    public void MAJItemAvailable()
    {
        TextView valeur_une = findViewById(R.id.valeur_une);
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
            valeur_sept.setTextColor(Color.parseColor("#08FF00"));

    }

    private void MAJInformations() {
        CricketPlayerItem joueur = this.controller.getJoueurCourant();
        LanceItem lance = this.controller.getLance();

        boolean lastRound = this.controller.checkLastRound();
        boolean toggle = false;

        updateAdversairesList();

        TextView tour = findViewById(R.id.nb_tours);

        Integer points = 0;

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

        MAJItemAvailable();
        SetCricketValues();
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


    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            vibrate();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    afficherCible(); // Afficher la cible lorsque le doigt est appuyé
                    return true; // Indique que l'événement a été traité
                default:
                    return false; // Laisser les autres événements non traités
            }
        }
    };

    private void afficherCible() {
        List<Integer> touch = this.controller.getCurrentPlayerTouch();
        List<Integer> closePlayer = this.controller.getClosedPlayerValues();
        List<Integer> close = this.controller.getClosedValues();
        List<CricketValueItem> items = this.controller.getValuesItem();

        int[] touchInt = new int[touch.size()];
        int[] closeInt = new int[close.size()];
        int[] closeSolo = new int[closePlayer.size()];

        int index = 0;

        for (Integer touche : touch) {
            touchInt[index] = touche;
            index += 1;
        }
        index = 0;
        for (Integer ferme : close) {
            closeInt[index] = ferme;
            index += 1;
        }

        int[] itemCricket = new int[0];
        if (items != null && items.size() > 0) {
            itemCricket = new int[items.size()];
            index = 0;
            for (CricketValueItem item : items) {
                itemCricket[index] = item.getIntLibelleItem();
                index += 1;
            }
        }

        index = 0;
        for (Integer item : closePlayer) {
            closeSolo[index] = item;
            index += 1;
        }

        //TouchInt sont les items touchés : Important pour le Hidden
        //closeInt sont les items qu'a fermé le joueur : Permet de mettre en orange les zones
        //Toute les valeurs en dehors de ces deux zones sont grisés
        TouchData datas = new TouchData(touchInt, closeInt, itemCricket, closeSolo);

        // Afficher l'activité de la cible
        Intent intent = new Intent(CricketGameActivity.this, TargetViewActivity.class);
        intent.putExtra("selectedGame", this.selectedGame);
        intent.putExtra("touch", datas);
        startActivity(intent);
    }

    @Override
    public void onScoreUpdate(int score) {

    }
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