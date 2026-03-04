package com.example.dartslivescorer.controllers;

import android.content.Context;

import com.example.dartslivescorer.enums.eButtons;
import com.example.dartslivescorer.enums.eGames;
import com.example.dartslivescorer.enums.eStates;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.DartScorerDatabase;
import models.OnScoreUpdateListener;
import models.commonModels.GameItem;
import models.commonModels.LanceItem;
import models.commonModels.ScoreButtonItem;
import models.gamesModels.PlayerItem;
import models.gamesModels.ShootOutPlayerItem;

public class ShootOutController {

    private int id_partie;
    private int tours;
    private List<ShootOutPlayerItem> joueurs;
    private List<ShootOutPlayerItem> rotation;
    private GameItem jeu;
    private LanceItem lance;
    private DartScorerDatabase db;
    private eGames type;
    private eStates statutParti;
    private Integer multiplicateur = 1;
    private boolean lastRound = false;
    private int un = -1;
    private int deux = -1;
    private int trois = -1;

    private ShootOutPlayerItem joueurCourant;
    private OnScoreUpdateListener mListener;
    private CommonController commonController;

    public ShootOutController(Context context) {
        this.db = DartScorerDatabase.getDatabase(context);
    }

    public void InitialisePartie(GameItem jeu, List<PlayerItem> joueurs, DartScorerDatabase db,CommonController commonController) {
        this.id_partie = new Random().nextInt();
        this.jeu = jeu;
        this.type = jeu.getType();
        this.db = db;
        this.tours = jeu.getTours();
        this.commonController = commonController;

        InitialiseJoueurs(joueurs);
        this.statutParti = eStates.EnCours;
    }

    public void InitialiseJoueurs(List<PlayerItem> players) {
        this.joueurs = new ArrayList<>();
        for (PlayerItem joueur : players) {
            this.joueurs.add(new ShootOutPlayerItem(joueur.getId(), joueur.getName()));
        }

        changementJoueur(null);
    }

    public void MAJFlechette(ScoreButtonItem bouton) {

        if (eButtons.Retour.toString().equals(bouton.getType().toString()))
        {
            if(this.joueurCourant.getTouche().size() > 0) {
                String flechette = "";
                LanceItem item = this.joueurCourant.getLance();

                if (this.trois != -1) {
                    if(item.ouverture_trois)
                        this.joueurCourant.removeLastTouche();

                    if(item.tir_trois != 0)
                        this.joueurCourant.downMultiplicateur();

                    this.joueurCourant.setLance(3, -1, flechette,false);
                    this.joueurCourant.setScore(joueurCourant.getScore() - this.trois);

                    this.trois = -1;
                } else if (this.deux != -1) {
                    if(item.ouverture_deux)
                        this.joueurCourant.removeLastTouche();

                    if(item.tir_deux != 0)
                        this.joueurCourant.downMultiplicateur();

                    this.joueurCourant.setLance(2, -1, flechette,false);
                    this.joueurCourant.setScore(joueurCourant.getScore() - this.deux);

                    this.deux = -1;
                } else if (this.un != -1) {
                    if(item.ouverture_un)
                        this.joueurCourant.removeLastTouche();

                    if(item.tir_un != 0)
                        this.joueurCourant.downMultiplicateur();

                    this.joueurCourant.setLance(1, -1, flechette,false);
                    this.joueurCourant.setScore(joueurCourant.getScore() - this.un);

                    this.un = -1;
                }

                this.statutParti = eStates.EnCours;

                mListener.onScoreUpdate(0);
            }

        } else if (eButtons.Suivant.toString().equals(bouton.getType().toString())) {
            String flechette = "0";

            if (this.un == -1) {
                this.un = 0;
                this.joueurCourant.InitialiseLance(new Random().nextInt(),this.id_partie,this.joueurCourant.getId());
                this.joueurCourant.setLance(1,0,flechette,false);
            }
            else if (this.deux == -1) {
                this.deux = 0;
                this.joueurCourant.setLance(2,0,flechette,false);
            } else if (this.trois == -1) {
                this.trois = 0;
                this.joueurCourant.setLance(3,0,flechette,false);
            }

            mListener.onScoreUpdate(0);
        } else {
            int score = 0;
            String flechette = "";

            //Si c'est des points
            if (eButtons.Points.toString().equals(bouton.getType().toString())) {
                if (getMultiplicateur() == 1)
                    flechette = String.valueOf(bouton.getPoint());
                if (getMultiplicateur() == 2)
                    flechette = "D" + bouton.getPoint();
                if (getMultiplicateur() == 3)
                    flechette = "T" + bouton.getPoint();
            }
            //Si c'est 25
            else {

                if (getMultiplicateur() == 1)
                    flechette = "B";
                if (getMultiplicateur() == 2)
                    flechette = "D-B";
            }

            if(this.joueurCourant.getTouche().contains(bouton.getId()))
                flechette += " Ferme !";
            else {
                score = bouton.getPoint() * getMultiplicateur() * this.joueurCourant.getMultiplicateur();
                flechette = String.valueOf(score);
            }

            if (this.un == -1) {
                this.un = score;
                this.joueurCourant.InitialiseLance(new Random().nextInt(), this.id_partie, this.joueurCourant.getId());

                if(!this.joueurCourant.getTouche().contains(bouton.getId())) {
                    this.joueurCourant.upMultiplicateur();
                    this.joueurCourant.setTouche(bouton.getId());
                    this.joueurCourant.setLance(1, score, flechette, true);
                }
                else
                    this.joueurCourant.setLance(1, score, flechette, false);
            } else if (this.deux == -1) {
                this.deux = score;

                if(!this.joueurCourant.getTouche().contains(bouton.getId())) {
                    this.joueurCourant.upMultiplicateur();
                    this.joueurCourant.setTouche(bouton.getId());
                    this.joueurCourant.setLance(2, score, flechette,true);
                }
                else
                    this.joueurCourant.setLance(2, score, flechette,false);

            } else if (this.trois == -1) {
                this.trois = score;

                if(!this.joueurCourant.getTouche().contains(bouton.getId())) {
                    this.joueurCourant.upMultiplicateur();
                    this.joueurCourant.setTouche(bouton.getId());
                    this.joueurCourant.setLance(3, score, flechette,true);
                }
                else
                    this.joueurCourant.setLance(3, score, flechette,false);
            }

            this.joueurCourant.setScore(this.joueurCourant.getScore() + score);

            mListener.onScoreUpdate(0);
        }
    }

    public void changementJoueur(ShootOutPlayerItem nouveauJoueur) {
        if (nouveauJoueur == null) {
            this.joueurCourant = this.joueurs.get(0);
            this.rotation = joueurs;
        }
        else {
            this.joueurCourant.setTour(this.joueurCourant.getTour()+1);

            this.joueurCourant = nouveauJoueur;
            this.un = -1;
            this.deux = -1;
            this.trois = -1;

            this.joueurCourant.setSelected(true);
        }
    }
    public void resetLance()
    {
        if (this.trois != -1) {
            joueurCourant.setScore(joueurCourant.getScore() + this.trois);
            this.trois = 0;
        }
        if (this.deux != -1) {
            joueurCourant.setScore(joueurCourant.getScore() + this.deux);
            this.deux = 0;
        }
        if (this.un != -1) {
            joueurCourant.setScore(joueurCourant.getScore() + this.un);
            this.un = 0;
        }
    }
    public void changementMulti(ScoreButtonItem bouton)
    {
        this.commonController.changeMultiple(bouton);
    }
    public ShootOutPlayerItem rotationJoueur()
    {
        this.rotation = joueurs;
        ShootOutPlayerItem player = this.rotation.get(0);
        this.rotation.remove(0);
        this.rotation.add(player);
        this.statutParti = eStates.EnCours;
        return this.rotation.get(0);
    }

    public void checkTimeout()
    {
        int nb_tours = jeu.getTours();
        //this.statutParti = eStates.Timeout;
        int nb_fini = 0;
        for (ShootOutPlayerItem joueur : joueurs) {
            if(joueur.getTour() > nb_tours)
                nb_fini += 1;
        }
        if(nb_fini == joueurs.size())
            this.statutParti = eStates.Timeout;
        else
            this.statutParti = eStates.EnCours;
    }

    public boolean checkLastRound()
    {
        int nb_tours = jeu.getTours();

        int nb_fini = 0;
        for (ShootOutPlayerItem joueur : joueurs) {
            if(joueur.getTour() >= nb_tours)
                nb_fini += 1;
        }
        if(nb_fini == joueurs.size())
            this.lastRound = true;

        return this.lastRound;
    }
    public List<ShootOutPlayerItem> getAdversaires(){
        List<ShootOutPlayerItem> adv = new ArrayList<>(this.rotation);
        adv.remove(0);
        return adv;
    }
    public Integer getMultiplicateur(){return this.commonController.getMultiplicateur();}
    public ShootOutPlayerItem getJoueurCourant(){
        return this.joueurCourant;
    }
    public eStates getStatut()
    {
        return this.statutParti;
    }

    public LanceItem getLance()
    {
        return new LanceItem(0,0,joueurCourant.getId(),this.un,this.deux,this.trois,0);
    }
    public ShootOutPlayerItem getWinner()
    {
        ShootOutPlayerItem gagnant = null;
        for (ShootOutPlayerItem joueur : joueurs) {
            if(gagnant == null)
                gagnant = joueur;
            else
            {
                if(joueur.getScore() > gagnant.getScore())
                    gagnant = joueur;
            }
        }
        return gagnant;
    }


    public void setOnScoreUpdateListener(OnScoreUpdateListener listener) {
        mListener = listener;
    }

    public List<Integer> getCurrentPlayerTouch() {
        return this.joueurCourant.getTouche();
    }
}
