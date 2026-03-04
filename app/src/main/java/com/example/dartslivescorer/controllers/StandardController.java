package com.example.dartslivescorer.controllers;

import android.content.Context;

import com.example.dartslivescorer.enums.eButtons;
import com.example.dartslivescorer.enums.eGames;
import com.example.dartslivescorer.enums.eStates;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import models.DartScorerDatabase;
import models.OnScoreUpdateListener;
import models.commonModels.GameItem;
import models.commonModels.LanceItem;
import models.commonModels.ScoreButtonItem;
import models.gamesModels.PlayerItem;

public class StandardController {

    private int id_partie;
    private int tours;
    private List<PlayerItem> joueurs;
    private List<PlayerItem> rotation;
    private GameItem jeu;
    private LanceItem lance;
    private DartScorerDatabase db;
    private eGames type;
    private eStates statutParti;
    private Integer multiplicateur = 1;
    private List<String> meilleureCombinaison = new ArrayList<>();
    private boolean lastRound = false;
    private int un = -1;
    private int deux = -1;
    private int trois = -1;
    private CommonController commonController;

    private PlayerItem joueurCourant;
    private OnScoreUpdateListener mListener;

    public StandardController(Context context) {
        this.db = DartScorerDatabase.getDatabase(context);
    }

    public void InitialisePartie(GameItem jeu, List<PlayerItem> joueurs,DartScorerDatabase db, CommonController commonController) {
        this.id_partie = new Random().nextInt();
        this.joueurs = joueurs;
        this.jeu = jeu;
        this.type = jeu.getType();
        this.db = db;
        this.tours = jeu.getTours();
        this.commonController = commonController;
        InitialiseJoueurs();
    }
    public void InitialiseJoueurs() {
        for (PlayerItem joueur : joueurs) {
            joueur.setScore(jeu.getScore());
            joueur.setTour(1);
        }
        changementJoueur(null);
    }

    public void MAJFlechette(ScoreButtonItem bouton) {

        if (eButtons.Retour.toString().equals(bouton.getType().toString()))
        {
            int reste = joueurCourant.getScore() - bouton.getPoint();
            if (this.trois != -1) {
                joueurCourant.setScore(joueurCourant.getScore() + this.trois);
                this.trois = -1;
            } else if (this.deux != -1) {
                joueurCourant.setScore(joueurCourant.getScore() + this.deux);
                this.deux = -1;
            } else if (this.un != -1) {
                joueurCourant.setScore(joueurCourant.getScore() + this.un);
                this.un = -1;
            }
            if (this.joueurCourant.getSelected() && this.getNbFlechettes() > 0)
                meilleureCombinaison = findBestDartCombinations(this.joueurCourant.getScore(), this.getNbFlechettes());

            this.statutParti = eStates.EnCours;

            mListener.onScoreUpdate(reste);
        } else if (eButtons.Suivant.toString().equals(bouton.getType().toString())) {
            int reste = joueurCourant.getScore() - bouton.getPoint();
            if (this.un == -1) {
                this.un = 0;

            } else if (this.deux == -1) {
                this.deux = 0;
            } else if (this.trois == -1) {
                this.trois = 0;
            }

            mListener.onScoreUpdate(reste);
        } else {
            int reste = 0;
            int score = 0;

            if (eButtons.Points.toString().equals(bouton.getType().toString()) || eButtons.Speciaux.toString().equals(bouton.getType().toString())) {
                reste = joueurCourant.getScore() - (bouton.getPoint() * getMultiplicateur());
                score = bouton.getPoint() * getMultiplicateur();
            }
            else {
                reste = joueurCourant.getScore() - bouton.getPoint();
                score = bouton.getPoint();
            }

            this.statutParti = checkValide(score);

            if (this.un == -1) {
                this.un = score;
                joueurCourant.setScore(reste);
            } else if (this.deux == -1) {
                this.deux = score;
                joueurCourant.setScore(reste);
            } else if (this.trois == -1) {
                this.trois = score;
                joueurCourant.setScore(reste);
            }

            if (this.joueurCourant.getSelected() && this.getNbFlechettes() > 0)
                meilleureCombinaison = findBestDartCombinations(this.joueurCourant.getScore(), this.getNbFlechettes());

            mListener.onScoreUpdate(reste);
        }
    }
    public void EnregistreLance(Long id_joueur, int un, int deux, int trois) {
        /*for (PlayerItem joueur : joueurs) {
            if (id_joueur == joueur.getId())
                lance = new LanceItem(new Random().nextInt(), this.id_partie, id_joueur, un, deux, trois);
        }*/

        // Enregistrement du lancé

    }
    public void changementJoueur(PlayerItem nouveauJoueur) {
        if (nouveauJoueur == null) {
            this.joueurCourant = this.joueurs.get(0);
            this.rotation = joueurs;
        }
        else {
            EnregistreLance(this.joueurCourant.getId(), this.un, this.deux, this.trois);
            this.joueurCourant.setTour(this.joueurCourant.getTour()+1);

            this.joueurCourant = nouveauJoueur;
            this.un = -1;
            this.deux = -1;
            this.trois = -1;

            if (this.joueurCourant.getScore() <= 180) {
                meilleureCombinaison = findBestDartCombinations(this.joueurCourant.getScore(), 3);
                this.joueurCourant.setSelected(true);
            }

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
    public PlayerItem rotationJoueur()
    {
        this.rotation = joueurs;
        PlayerItem player = this.rotation.get(0);
        this.rotation.remove(0);
        this.rotation.add(player);
        this.statutParti = eStates.EnCours;
        return this.rotation.get(0);
    }
    public eStates checkValide(int points) {
        int points_restant = 0;
        eStates retour = eStates.EnCours;

        for (PlayerItem joueur : joueurs) {
            if (Objects.equals(joueurCourant.getId(), joueur.getId())) {
                points_restant = joueur.getScore();
                break;
            }
        }

        if (points_restant > points ) {
            retour = eStates.EnCours;
        }
        if (points_restant < points) {
            retour = eStates.Depasse;
        }
        if (points_restant == points) {
            retour = eStates.Termine;
        }

        return retour;
    }
    public void checkTimeout()
    {
        int nb_tours = jeu.getTours();
        //this.statutParti = eStates.Timeout;
        int nb_fini = 0;
        for (PlayerItem joueur : joueurs) {
            if(joueur.getTour() > nb_tours)
                nb_fini += 1;
        }
        if(nb_fini == joueurs.size())
            this.statutParti = eStates.Timeout;
        else
            this.statutParti = eStates.EnCours;
    }

    public List<String> getCombi(){
        return this.meilleureCombinaison;
    }

    public boolean checkLastRound()
    {
        int nb_tours = jeu.getTours();

        int nb_fini = 0;
        for (PlayerItem joueur : joueurs) {
            if(joueur.getTour() >= nb_tours)
                nb_fini += 1;
        }
        if(nb_fini == joueurs.size())
            this.lastRound = true;

        return this.lastRound;
    }
    public List<PlayerItem> getAdversaires(){
        List<PlayerItem> adv = new ArrayList<>(this.rotation);
        adv.remove(0);
        return adv;
    }
    public Integer getMultiplicateur(){return this.commonController.getMultiplicateur();}
    public PlayerItem getJoueurCourant(){
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
    public PlayerItem getWinner()
    {
        PlayerItem gagnant = null;
        for (PlayerItem joueur : joueurs) {
            if(gagnant == null)
                gagnant = joueur;
            else
            {
                if(joueur.getScore() < gagnant.getScore())
                    gagnant = joueur;
            }
        }
        return gagnant;
    }
    public Integer getNbFlechettes()
    {
        int nb = 0;

        if(this.un == -1 && this.deux == -1 && this.trois == -1)
            nb = 3;
        else if(this.deux == -1 && this.trois == -1)
            nb = 2;
        else if(this.trois == -1)
            nb = 1;

        return nb;
    }

    public static List<String> findBestDartCombinations(int targetScore, int dartsLeft) {
        List<List<String>> allCombinations = new ArrayList<>();
        findCombinations(targetScore, dartsLeft, new ArrayList<>(), allCombinations,1);
        if(allCombinations.size() > 0)
            return allCombinations.get(0);
        else
            return null;
    }
    private static void findCombinations(int targetScore, int dartsLeft, List<String> currentCombination, List<List<String>> allCombinations, int start) {
        if (dartsLeft == 0 && targetScore == 0) {
            allCombinations.add(new ArrayList<>(currentCombination));
            return;
        }

        if (dartsLeft == 0 || targetScore <= 0) {
            return;
        }

        for (int i = start; i <= 20 && i <= targetScore; i++) {
            for (int j = 1; j <= 3; j++) {
                int score = i * j;
                if (score <= targetScore) {
                    String dart = (j == 1 ? "S" + i : (j == 2 ? "D" : "T") + i);
                    currentCombination.add(dart); // Add dart to current combination
                    findCombinations(targetScore - score, dartsLeft - 1, currentCombination, allCombinations, i);
                    currentCombination.remove(currentCombination.size() - 1); // Remove last dart for backtracking
                }
            }
        }
    }
    public void setOnScoreUpdateListener(OnScoreUpdateListener listener) {
        mListener = listener;
    }

}
