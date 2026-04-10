package com.example.dartslivescorer.controllers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.dartslivescorer.enums.eButtons;
import com.example.dartslivescorer.enums.eStates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import models.DartScorerDatabase;
import models.OnScoreUpdateListener;
import models.commonModels.CheckoutTable;
import models.commonModels.GameItem;
import models.commonModels.Lance;
import models.commonModels.LanceItem;
import models.commonModels.Partie;
import models.commonModels.ScoreButtonItem;
import models.gamesModels.PlayerItem;

public class StandardController {

    private long id_partie = -1;
    private int tours;
    private List<PlayerItem> joueurs;
    private int rotationIndex = 0;   // index du joueur courant dans la liste
    private GameItem jeu;
    private DartScorerDatabase db;
    private eStates statutParti;

    /** Suggestions de finition mises à jour à chaque fléchette. */
    private List<String> checkout = new ArrayList<>();

    private boolean lastRound = false;
    private int un    = -1;
    private int deux  = -1;
    private int trois = -1;

    private CommonController commonController;
    private PlayerItem joueurCourant;
    private OnScoreUpdateListener mListener;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public StandardController(Context context) {
        this.db = DartScorerDatabase.getDatabase(context);
    }

    public void InitialisePartie(GameItem jeu, List<PlayerItem> joueurs,
                                 DartScorerDatabase db, CommonController commonController) {
        this.jeu              = jeu;
        this.joueurs          = joueurs;
        this.db               = db;
        this.tours            = jeu.getTours();
        this.commonController = commonController;

        // Crée la Partie en base et retient son id
        executor.execute(() -> {
            Partie p  = new Partie();
            p.type    = jeu.getType().toString();
            p.gagnant = "";
            long newId = db.dartScorerDao().insertPartie(p);
            mainHandler.post(() -> this.id_partie = newId);
        });

        InitialiseJoueurs();
    }

    public void InitialiseJoueurs() {
        for (PlayerItem j : joueurs) {
            j.setScore(jeu.getScore());
            j.setTour(1);
        }
        changementJoueur(null);
    }

    // ── Gestion des fléchettes ────────────────────────────────────────────────

    public void MAJFlechette(ScoreButtonItem bouton) {
        if (eButtons.Retour.toString().equals(bouton.getType().toString())) {
            if      (this.trois != -1) { joueurCourant.setScore(joueurCourant.getScore() + this.trois); this.trois = -1; }
            else if (this.deux  != -1) { joueurCourant.setScore(joueurCourant.getScore() + this.deux);  this.deux  = -1; }
            else if (this.un    != -1) { joueurCourant.setScore(joueurCourant.getScore() + this.un);    this.un    = -1; }
            this.statutParti = eStates.EnCours;
            refreshCheckout();
            mListener.onScoreUpdate(joueurCourant.getScore());

        } else if (eButtons.Suivant.toString().equals(bouton.getType().toString())) {
            // Fléchette manquée / 0 point
            if      (this.un    == -1) this.un    = 0;
            else if (this.deux  == -1) this.deux  = 0;
            else if (this.trois == -1) this.trois = 0;
            refreshCheckout();
            mListener.onScoreUpdate(joueurCourant.getScore());

        } else {
            int score;
            if (eButtons.Points.toString().equals(bouton.getType().toString())
                    || eButtons.Speciaux.toString().equals(bouton.getType().toString())) {
                score = bouton.getPoint() * getMultiplicateur();
            } else {
                score = bouton.getPoint();
            }

            int reste = joueurCourant.getScore() - score;
            this.statutParti = checkValide(score);

            if      (this.un    == -1) { this.un    = score; joueurCourant.setScore(reste); }
            else if (this.deux  == -1) { this.deux  = score; joueurCourant.setScore(reste); }
            else if (this.trois == -1) { this.trois = score; joueurCourant.setScore(reste); }

            refreshCheckout();
            mListener.onScoreUpdate(reste);
        }
    }

    /**
     * Rafraîchit instantanément les suggestions via la table lookup.
     * Appelé à chaque fléchette (pas de thread background nécessaire).
     */
    private void refreshCheckout() {
        int score = joueurCourant.getScore();
        int darts = getNbFlechettes();
        if (score <= 0 || darts == 0) {
            this.checkout = Collections.emptyList();
        } else {
            this.checkout = CheckoutTable.getCheckout(score, darts);
        }
    }

    // ── Changement de joueur ──────────────────────────────────────────────────

    public void changementJoueur(PlayerItem nouveauJoueur) {
        if (nouveauJoueur == null) {
            this.rotationIndex = 0;
            this.joueurCourant = this.joueurs.get(0);
        } else {
            enregistreLance(this.joueurCourant.getId(), this.un, this.deux, this.trois);
            this.joueurCourant.setTour(this.joueurCourant.getTour() + 1);
            this.joueurCourant = nouveauJoueur;
            this.un    = -1;
            this.deux  = -1;
            this.trois = -1;
        }
        refreshCheckout();
    }

    public void resetLance() {
        if (this.trois != -1) { joueurCourant.setScore(joueurCourant.getScore() + this.trois); this.trois = -1; }
        if (this.deux  != -1) { joueurCourant.setScore(joueurCourant.getScore() + this.deux);  this.deux  = -1; }
        if (this.un    != -1) { joueurCourant.setScore(joueurCourant.getScore() + this.un);    this.un    = -1; }
        refreshCheckout();
    }

    // ── Persistance ──────────────────────────────────────────────────────────

    /** Enregistre un tour complet (3 fléchettes) en base, -1 → 0. */
    public void enregistreLance(Long idJoueur, int un, int deux, int trois) {
        executor.execute(() -> {
            Lance l = new Lance();
            l.idPartie  = id_partie;
            l.idJoueur  = idJoueur;
            l.typeJeu   = jeu.getType().toString();
            l.tir_un    = Math.max(un, 0);
            l.tir_deux  = Math.max(deux, 0);
            l.tir_trois = Math.max(trois, 0);
            db.dartScorerDao().insertLance(l);
        });
    }

    /** Enregistre le gagnant de la partie dans la table Partie. */
    public void enregistreGagnant(String nomGagnant) {
        executor.execute(() -> db.dartScorerDao().updateGagnantPartie(id_partie, nomGagnant));
    }

    // ── Utilitaires ──────────────────────────────────────────────────────────

    public void changementMulti(ScoreButtonItem bouton) { this.commonController.changeMultiple(bouton); }

    public PlayerItem rotationJoueur() {
        // Avance l'index de façon circulaire dans la liste des joueurs
        rotationIndex = (rotationIndex + 1) % joueurs.size();
        this.statutParti = eStates.EnCours;
        return joueurs.get(rotationIndex);
    }

    public eStates checkValide(int points) {
        int restant = 0;
        for (PlayerItem j : joueurs)
            if (Objects.equals(joueurCourant.getId(), j.getId())) { restant = j.getScore(); break; }
        if (restant > points) return eStates.EnCours;
        if (restant < points) return eStates.Depasse;
        return eStates.Termine;
    }

    public void checkTimeout() {
        int nb_fini = 0;
        for (PlayerItem j : joueurs) if (j.getTour() > jeu.getTours()) nb_fini++;
        this.statutParti = (nb_fini == joueurs.size()) ? eStates.Timeout : eStates.EnCours;
    }

    public boolean checkLastRound() {
        int nb_fini = 0;
        for (PlayerItem j : joueurs) if (j.getTour() >= jeu.getTours()) nb_fini++;
        if (nb_fini == joueurs.size()) this.lastRound = true;
        return this.lastRound;
    }

    public List<String>  getCombi()            { return this.checkout; }
    public boolean       isImpossibleCheckout() { return CheckoutTable.isImpossible(joueurCourant.getScore()); }

    public List<PlayerItem> getAdversaires() {
        List<PlayerItem> adv = new ArrayList<>();
        for (PlayerItem j : joueurs) {
            if (!j.getId().equals(joueurCourant.getId())) adv.add(j);
        }
        return adv;
    }

    public Integer     getMultiplicateur()  { return this.commonController.getMultiplicateur(); }
    public PlayerItem  getJoueurCourant()   { return this.joueurCourant; }
    public eStates     getStatut()          { return this.statutParti; }

    public LanceItem getLance() {
        return new LanceItem(0, 0, joueurCourant.getId(), this.un, this.deux, this.trois, 0);
    }

    public PlayerItem getWinner() {
        PlayerItem gagnant = null;
        for (PlayerItem j : joueurs)
            if (gagnant == null || j.getScore() < gagnant.getScore()) gagnant = j;
        return gagnant;
    }

    public Integer getNbFlechettes() {
        if (this.un    == -1) return 3;
        if (this.deux  == -1) return 2;
        if (this.trois == -1) return 1;
        return 0;
    }

    public void setOnScoreUpdateListener(OnScoreUpdateListener listener) { mListener = listener; }
}
