package com.example.dartslivescorer.controllers;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import models.gamesModels.CricketValueItem;
import models.DartScorerDatabase;
import com.example.dartslivescorer.enums.eButtons;
import com.example.dartslivescorer.enums.eStates;

import models.commonModels.GameItem;
import models.commonModels.LanceItem;
import models.gamesModels.MMPlayerItem;
import models.gamesModels.PlayerItem;
import models.commonModels.ScoreButtonItem;

public class MasterController {

    private int id_partie;
    private int tours;
    private GameItem jeu;
    private DartScorerDatabase db;
    private eStates statutParti;
    private boolean lastRound = false;
    private int un = -1;
    private int deux = -1;
    private int trois = -1;
    private int quatre = -1;
    private List<CricketValueItem> ItemsCombi;

    private Integer multiplicateur = 1;
    private MMPlayerItem joueurCourant;

    public MasterController(Context context) {
        this.db = DartScorerDatabase.getDatabase(context);
    }

    /* Initialisation des éléments de la partie */
    public void InitialisePartie(GameItem jeu, List<PlayerItem> joueurs, DartScorerDatabase db) {
        this.id_partie = new Random().nextInt();
        this.joueurCourant = new MMPlayerItem(joueurs.get(0).getId(),joueurs.get(0).getName(),joueurs.get(0).getScore(),joueurs.get(0).getTour());
        this.jeu = jeu;
        this.db = db;
        this.tours = jeu.getTours();
        this.joueurCourant.setTour(1);
        this.ItemsCombi = new ArrayList<>();

        InitialiseCombinaison();
    }

    /* Génération de l combinaison à trouver*/
    public void InitialiseCombinaison() {
        Set<Integer> generatedValues = new HashSet<>();
        Random random = new Random();

        while (generatedValues.size() < 4) {
            int val = random.nextInt(21);

            // Si le nombre généré est 0, on le remplace par 21
            if (val == 0)
                val = 21;

            if(!generatedValues.contains(val))
                generatedValues.add(val);
        }

        int iter = 1;
        for (Integer i : generatedValues) {
            String value;

            if (i <= 20)
                value = String.valueOf(i);
            else
                value = "B";

            this.ItemsCombi.add(new CricketValueItem(iter, value, false, false));
            iter++;
        }
    }

    /* Lorsqu'une flechette est tirée */
    public void MAJFlechette(ScoreButtonItem bouton) {
        /* On efface le dernier tir */
        if (eButtons.Retour.toString().equals(bouton.getType().toString())) {
            if (this.quatre != -1) {
                this.quatre = -1;
            } else if (this.trois != -1) {
                this.trois = -1;
            } else if (this.deux != -1) {
                this.deux = -1;
            } else if (this.un != -1) {
                this.un = -1;
            }

            this.statutParti = eStates.EnCours;
        }
        /* C'est un Miss !*/
        else if (eButtons.Suivant.toString().equals(bouton.getType().toString())) {
            if (this.un == -1) {
                this.un = 0;
            } else if (this.deux == -1) {
                this.deux = 0;
            } else if (this.trois == -1) {
                this.trois = 0;
            } else if (this.quatre == -1) {
                this.quatre = 0;
            }
        }
        /* On traite les points */
        else {
            int score = bouton.getPoint();

            if (this.un == -1) {
                this.un = score;
            } else if (this.deux == -1) {
                this.deux = score;
            } else if (this.trois == -1) {
                this.trois = score;
            } else if (this.quatre == -1) {
                this.quatre = score;
            }
        }
    }

    public void EnregistreLance(Long id_joueur, int un, int deux, int trois) {
        /*for (PlayerItem joueur : joueurs) {
            if (id_joueur == joueur.getId())
                lance = new LanceItem(new Random().nextInt(), this.id_partie, id_joueur, un, deux, trois);
        }*/

        // Enregistrement du lancé

    }

    /* Vérifie si on a atteind le nb de tours du jeu*/
    public void checkTimeout()
    {
        if(joueurCourant.getTour() > jeu.getTours())
            this.statutParti = eStates.Timeout;
        else
            this.statutParti = eStates.EnCours;
    }

    /* Vérifie si c'est le dernier tour */
    public boolean checkLastRound()
    {
        if(joueurCourant.getTour() == jeu.getTours()-1)
            this.lastRound = true;
        else
            this.lastRound = false;

        return this.lastRound;
    }

    /* Retourne le joueur courant*/
    public MMPlayerItem getJoueurCourant(){
        return this.joueurCourant;
    }

    /* Retourne le statut de la partie*/
    public eStates getStatut()
    {
        return this.statutParti;
    }

    /* Retourne un Lancé */
    public LanceItem getLance()
    {
        return new LanceItem(0,0,joueurCourant.getId(),this.un,this.deux,this.trois,this.quatre);
    }

    public List<CricketValueItem> getCombi()
    {
        return this.ItemsCombi;
    }

    public Integer getMultiplicateur()
    {
        return this.multiplicateur;
    }

    public void changementMulti(ScoreButtonItem bouton)
    {
        if (bouton.getLabel().equals("Triple")) {
            this.multiplicateur = 3;
        } else if (bouton.getLabel().equals("Double")) {
            this.multiplicateur = 2;
        } else if (bouton.getLabel().equals("Simple")) {
            this.multiplicateur = 1;
        }
    }
}
