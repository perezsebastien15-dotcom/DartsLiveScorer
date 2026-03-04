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
import models.gamesModels.UTHPlayerItem;

public class UTHController {

    private int id_partie;
    private int tours;
    private List<UTHPlayerItem> joueurs;
    private List<UTHPlayerItem> rotation;
    private List<Integer> chapeaux = new ArrayList<>();
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
    private Integer scoreABattre = 20;

    private UTHPlayerItem joueurCourant;
    private OnScoreUpdateListener mListener;
    private CommonController commonController;

    public UTHController(Context context) {
        this.db = DartScorerDatabase.getDatabase(context);
    }

    public List<UTHPlayerItem> InitialisePartie(GameItem jeu, List<PlayerItem> joueurs, DartScorerDatabase db, CommonController commonController) {
        this.id_partie = new Random().nextInt();
        this.jeu = jeu;
        this.type = jeu.getType();
        this.db = db;
        this.tours = jeu.getTours();
        this.statutParti = eStates.EnCours;
        this.commonController = commonController;
        return InitialiseJoueurs(joueurs);
    }

    public List<UTHPlayerItem> InitialiseJoueurs(List<PlayerItem> joueurs) {
        Integer val= 0;
        this.joueurs = new ArrayList<>();

        for (PlayerItem joueur : joueurs) {

            do {
                val = new Random().nextInt(9);
            } while (chapeaux.contains(val) || val == 0);

            chapeaux.add(val);
            this.joueurs.add(new UTHPlayerItem(joueur.getId(), joueur.getName(), 0,1,5, val));
        }

        changementJoueur(null);
        return this.joueurs;
    }

    public void MAJFlechette(ScoreButtonItem bouton) {

        if (eButtons.Retour.toString().equals(bouton.getType().toString()))
        {
            String flechette = "";

            if (this.trois != -1) {
                this.joueurCourant.setLance(3,-1,flechette);
                this.joueurCourant.setScore(joueurCourant.getScore() - this.trois);
                this.trois = -1;
            } else if (this.deux != -1) {
                this.joueurCourant.setLance(2,-1,flechette);
                this.joueurCourant.setScore(joueurCourant.getScore() - this.deux);
                this.deux = -1;
            } else if (this.un != -1) {
                this.joueurCourant.setLance(1,-1,flechette);
                this.joueurCourant.setScore(0);
                this.un = -1;
            }

            this.statutParti = eStates.EnCours;
            mListener.onScoreUpdate(0);
        }
        else if (eButtons.Suivant.toString().equals(bouton.getType().toString())) {
            String flechette = "0";

            //Si c'est la premiere flechette
            if (this.un == -1) {
                this.un = 0;
                //On intialise son objet Lancé
                this.joueurCourant.InitialiseLance(new Random().nextInt(),this.id_partie,this.joueurCourant.getId());
                this.joueurCourant.setLance(1,0,flechette);
            }

            //Si c'est sa deuxieme flechette
            else if (this.deux == -1) {
                this.deux = 0;
                this.joueurCourant.setLance(2,0,flechette);
                //Si c'est sa troisieme flechette
            } else if (this.trois == -1) {
                this.trois = 0;
                this.joueurCourant.setLance(3,0,flechette);
            }

            mListener.onScoreUpdate(0);
        }
        else {

            int score = 0;
            String flechette = "";

            //Si c'est des points
            if (eButtons.Points.toString().equals(bouton.getType().toString())) {
                if(getMultiplicateur() == 1)
                    flechette = " "+bouton.getPoint();
                if(getMultiplicateur() == 2)
                    flechette = "D"+bouton.getPoint();
                if(getMultiplicateur() == 3)
                    flechette = "T"+bouton.getPoint();
            }
            //Si c'est 25
            else {

                if(getMultiplicateur() == 1)
                    flechette = "  B";
                if(getMultiplicateur() == 2)
                    flechette = "D-B";
            }
            score = bouton.getPoint() * getMultiplicateur();

            //Si c'est la premiere flechette
            if (this.un == -1) {
                //On intialise son objet Lancé
                this.un = score;
                this.joueurCourant.InitialiseLance(new Random().nextInt(),this.id_partie,this.joueurCourant.getId());
                this.joueurCourant.setLance(1,score,flechette);
                this.joueurCourant.setScore(this.un);
            }
            //Si c'est sa deuxieme flechette
            else if (this.deux == -1) {
                this.deux = score;
                this.joueurCourant.setLance(2,score,flechette);
                this.joueurCourant.setScore(this.un+this.deux);
            //Si c'est sa troisieme flechette
            } else if (this.trois == -1) {
                this.trois = score;
                this.joueurCourant.setLance(3,score,flechette);
                this.joueurCourant.setScore(this.un+this.deux+this.trois);
            }

            mListener.onScoreUpdate(0);
        }
    }

    public void checkSup() {
        if(this.joueurCourant.getScore() < this.scoreABattre)
            this.joueurCourant.setNb_chapeaux(joueurCourant.getNb_chapeaux() - 1);

        if(this.joueurCourant.getNb_chapeaux().equals(0))
            this.joueurCourant.setVivant(false);
    }

    public void EnregistreLance(Long id_joueur, int un, int deux, int trois) {
        for (UTHPlayerItem joueur : joueurs) {
            if (id_joueur == joueur.getId())
                lance = new LanceItem(new Random().nextInt(), this.id_partie, id_joueur, un, deux, trois,0);
        }

        // Enregistrement du lancé

    }

    public void changementJoueur(UTHPlayerItem nouveauJoueur) {
        if (nouveauJoueur == null) {
            this.joueurCourant = this.joueurs.get(0);
            this.rotation = joueurs;
        } else {
            //EnregistreLance(this.joueurCourant.getId(), this.un, this.deux, this.trois);
            this.joueurCourant.setTour(this.joueurCourant.getTour() + 1);
            this.scoreABattre = this.joueurCourant.getScore();
            this.resetLance();

            this.joueurCourant = nouveauJoueur;
        }
        this.joueurCourant.setSelected(true);
    }

    public void resetLance()
    {
        this.trois = -1;
        this.deux = -1;
        this.un = -1;

        joueurCourant.setLance(1,-1,"");
        joueurCourant.setLance(2,-1,"");
        joueurCourant.setLance(3,-1,"");
        joueurCourant.setScore(0);
    }

    public void changementMulti(ScoreButtonItem bouton)
    {
        this.commonController.changeMultiple(bouton);
    }

    public UTHPlayerItem rotationJoueur()
    {
        this.rotation = joueurs;

        do {
            UTHPlayerItem player = this.rotation.get(0);
            player.setSelected(false);

            this.rotation.remove(0);
            this.rotation.add(player);

            this.statutParti = eStates.EnCours;

            this.rotation.get(0).setSelected(true);

        } while (!this.rotation.get(0).getVivant());

        return this.rotation.get(0);
    }

    public void checkFinJeu()
    {
        Integer nbVivant = 0;

        for(UTHPlayerItem play :this.joueurs)
            if(play.getVivant())
                nbVivant += 1;

        if(nbVivant <= 1)
            this.statutParti = eStates.Termine;
    }

    public void checkTimeout()
    {
        int nb_tours = jeu.getTours();
        int nb_fini = 0;
        int vivant = 0;

        for (UTHPlayerItem joueur : joueurs) {
            if(joueur.getVivant()) {
                if (joueur.getTour() > nb_tours)
                    nb_fini += 1;

                vivant +=1;
            }
        }

        if(nb_fini == vivant)
            this.statutParti = eStates.Timeout;
        else
            this.statutParti = eStates.EnCours;
    }

    public boolean checkLastRound()
    {
        int nb_tours = jeu.getTours();

        if(this.joueurCourant.getTour() >= nb_tours)
            this.lastRound = true;

        return this.lastRound;
    }

    public List<UTHPlayerItem> getAdversaires(){
        UTHPlayerItem current = this.getJoueurCourant();

        for(UTHPlayerItem val : this.joueurs) {
            if (current.getId().equals(val.getId()))
                val.setSelected(true);
            else
                val.setSelected(false);
        }

        return this.joueurs;
    }

    public Integer getMultiplicateur()
    {
        return this.commonController.getMultiplicateur();
    }

    public UTHPlayerItem getJoueurCourant(){
        return this.joueurCourant;
    }

    public eStates getStatut()
    {
        return this.statutParti;
    }

    public LanceItem getLance()
    {
        if(this.joueurCourant.getLance() == null)
            return new LanceItem(0,0,joueurCourant.getId(),this.un,this.deux,this.trois,0);
        else
            return this.joueurCourant.getLance();
    }

    public String getWinner()
    {
        UTHPlayerItem gagnant = null;
        String retour ="";

        for (UTHPlayerItem joueur : joueurs) {
            if(joueur.getVivant()) {
                if (gagnant == null) {
                    gagnant = joueur;
                    retour = joueur.getName();
                } else {
                    if (joueur.getNb_chapeaux() > gagnant.getNb_chapeaux()) {
                        retour = joueur.getName();
                        gagnant = joueur;
                    }
                    else if (joueur.getNb_chapeaux().equals(gagnant.getNb_chapeaux())) {
                        retour = retour + "," +joueur.getName();
                    }
                }
            }
        }

        return retour;
    }

    public void setOnScoreUpdateListener(OnScoreUpdateListener listener) {
        mListener = listener;
    }

    public Integer getCible() {
        return this.scoreABattre;
    }
}
