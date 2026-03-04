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
    private Button retour;
    private WindowInsetsControllerCompat windowInsetsController;

    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_shoot_out_activity);

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

        this.controller = new ShootOutController(this);

        // Définition de l'écouteur pour les mises à jour de score
        this.controller.setOnScoreUpdateListener(new models.OnScoreUpdateListener() {
            @Override
            public void onScoreUpdate(int score) {
                if(eStates.EnCours.equals(controller.getStatut()))
                    MAJInformations();
                if(eStates.Termine.equals(controller.getStatut()))
                    Termine();
                if(eStates.Timeout.equals(controller.getStatut()))
                    TimeOut();
            }
        });

        this.commoncontroller = new CommonController();
        controller.InitialisePartie(this.selectedGame, this.selectedPlayers, DartScorerDatabase.getDatabase(this),this.commoncontroller);

        this.gridLayout = findViewById(R.id.gridLayout);

        TextView textView = findViewById(R.id.titre_jeu);
        textView.setOnTouchListener(onTouchListener);


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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ShootOutActivity.this);
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
                    } else {
                        controller.MAJFlechette(scoreButton);
                    }
                }
            });
            gridLayout.addView(button);
        }

        MAJInformations();
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
            int[] touchInt = new int[touch.size()];
            int index = 0;

            for(Integer touche : touch) {
                touchInt[index] = touche;
                index+=1;
            }

            TouchData datas = new TouchData(touchInt);

            // Afficher l'activité de la cible
            Intent intent = new Intent(ShootOutActivity.this, TargetViewActivity.class);
            intent.putExtra("selectedGame", this.selectedGame);
            intent.putExtra("touch", datas);
            startActivity(intent);
    }

    private void TimeOut() {
        //1 - Afficher le score OK en VERT + Message
        MAJInformations();

        //2 - Afficher une fenetre OK pour terminer le jeu
        afficherDialogueGagnant(controller.getWinner().getName());
    }

    private void updateAdversairesList() {
        List<ShootOutPlayerItem> adversaires = controller.getAdversaires();
        List<AdversaireItem> adversaireItems = new ArrayList<>();

        for (ShootOutPlayerItem adversaire : adversaires) {
            AdversaireItem item = new AdversaireItem(adversaire.getName(), adversaire.getScore());
            adversaireItems.add(item);
        }

        AdversairesAdapter adapter = new AdversairesAdapter(this, adversaireItems);
        GridView gridViewAdversaires = findViewById(R.id.gridViewAdversaires);
        gridViewAdversaires.setAdapter(adapter);
    }

    private void MAJInformations() {
        ShootOutPlayerItem joueur = this.controller.getJoueurCourant();
        LanceItem lance = this.controller.getLance();
        boolean lastRound = this.controller.checkLastRound();
        boolean toggle = false;

        TextView pointsRestantsTextView = findViewById(R.id.Points_restants);
        TextView nom_joueur = findViewById(R.id.joueur_courant);
        TextView un = findViewById(R.id.premierLance);
        TextView deux = findViewById(R.id.deuxiemeLance);
        TextView trois = findViewById(R.id.troisiemeLance);
        TextView tour = findViewById(R.id.tours);
        TextView multi = findViewById(R.id.multiplicateur);

        TextView zones = findViewById(R.id.zones);
        TextView flech = findViewById(R.id.flech);

        List<Integer> items = joueur.getTouche();
        Integer nb_flech = (this.selectedGame.getTours() - joueur.getTour())*3;
        nb_flech+=3;

        zones.setText("zones : " + (21-items.size()));

        Integer points = 0;
        String nom = joueur.getName();

        if (lance.tir_un != -1) {
            pointsRestantsTextView.setText(String.valueOf(lance.tir_un));
            un.setText("Flechette 1 : "+joueur.getLance().str_tir_un);
            un.setVisibility(View.VISIBLE);
            flech.setText("flech : "+((nb_flech)-1));
        } else {
            un.setVisibility(View.GONE);
            flech.setText("flech : "+(nb_flech));
        }

        if (lance.tir_deux != -1) {
            pointsRestantsTextView.setText(String.valueOf(lance.tir_un + lance.tir_deux));
            deux.setText("Flechette 2 : "+joueur.getLance().str_tir_deux);
            deux.setVisibility(View.VISIBLE);
            flech.setText("flech : "+((nb_flech)-2));
        } else {
            deux.setVisibility(View.GONE);
        }

        if (lance.tir_trois != -1) {
            pointsRestantsTextView.setText(String.valueOf(lance.tir_un + lance.tir_deux + lance.tir_trois));
            trois.setText("Flechette 3 : "+joueur.getLance().str_tir_trois);
            trois.setVisibility(View.VISIBLE);
            flech.setText("flech : "+((nb_flech)-3));
        } else {
            trois.setVisibility(View.GONE);
        }

        multi.setText("Multiplicateur : "+ joueur.getMultiplicateur());
        nom_joueur.setText(nom);

        if(!eStates.Timeout.equals(controller.getStatut()))
            tour.setText("Tour : " + String.valueOf(joueur.getTour()) + " / " + String.valueOf(selectedGame.getTours()));

        pointsRestantsTextView.setText(String.valueOf(joueur.getScore()));

        if(lance.tir_trois != -1)
            this.gridLayout = this.commoncontroller.toggleButtons(true, this.gridLayout);
        else
            this.gridLayout = this.commoncontroller.toggleButtons(false, this.gridLayout);

        if(lastRound)
            tour.setText("Dernier Tour !");

        if(!toggle)
            this.gridLayout = this.commoncontroller.chargeMultiple(this.gridLayout);

        allowButtons();
        updateAdversairesList();
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

    private void allowButtons()
    {
        ShootOutPlayerItem joueur = this.controller.getJoueurCourant();
        List<Integer> items = joueur.getTouche();

        for (int i = 0; i < this.gridLayout.getChildCount(); i++) {
            View view = this.gridLayout.getChildAt(i);
            if (view instanceof Button) {
                Button button = (Button) view;
                if (button.getTag().toString().equals(eButtons.Points.toString()) || button.getTag().toString().equals(eButtons.Speciaux.toString())) {
                    if (items.contains(button.getId()))
                        button.setTextColor(Color.parseColor("#FF0033"));
                    else
                        button.setTextColor(Color.parseColor("#FFFFFF"));
                }
            }
        }
    }

    @Override
    public void onScoreUpdate(int score) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Mettre en pause le jeu
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reprendre le jeu
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Sauvegarder l'état du jeu dans le Bundle
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restaurer l'état du jeu depuis le Bundle
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