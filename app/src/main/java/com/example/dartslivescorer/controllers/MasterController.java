package com.example.dartslivescorer.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.example.dartslivescorer.enums.eButtons;
import com.example.dartslivescorer.enums.eStates;
import models.commonModels.GameItem;
import models.commonModels.ScoreButtonItem;
import models.gamesModels.MMPlayerItem;
import models.gamesModels.PlayerItem;

public class MasterController {

    public static final int BULL      = 21;
    public static final int MAX_TOURS = 20;

    private int[]   combi     = new int[3];
    private boolean[] decouvert = new boolean[3];

    private List<MMPlayerItem> joueurs;
    private int indexJoueur = 0;
    private int tourGlobal  = 1;
    private eStates statut  = eStates.EnCours;

    // Fléchettes du tour courant (-1 = non lancée, 0 = Miss)
    private int un    = -1;
    private int deux  = -1;
    private int trois = -1;

    /**
     * Résultat IMMÉDIAT de chaque fléchette, calculé dès le lancer.
     * 0=rien/miss, 1=bonne position (vert), 2=bon chiffre mauvaise pos (orange)
     */
    private int[] resultatsImmediat = new int[]{0, 0, 0};

    /** Résultats du tour complet (après validerTour), pour l'historique. */
    private int[] resultats = new int[]{0, 0, 0};

    // ── Initialisation ────────────────────────────────────────────────────────

    public void InitialisePartie(GameItem jeu, List<PlayerItem> joueursBruts) {
        this.joueurs = new ArrayList<>();
        for (PlayerItem p : joueursBruts)
            this.joueurs.add(new MMPlayerItem(p.getId(), p.getName(), 0, 1));
        genererCombinaison();
    }

    private void genererCombinaison() {
        Set<Integer> choisis = new HashSet<>();
        Random rng = new Random();
        while (choisis.size() < 3)
            choisis.add(rng.nextInt(21) + 1); // 1..21 (21 = Bull)
        int i = 0;
        for (int v : choisis) combi[i++] = v;
        decouvert = new boolean[]{false, false, false};
        resultatsImmediat = new int[]{0, 0, 0};
    }

    // ── Gestion des fléchettes ────────────────────────────────────────────────

    public void MAJFlechette(ScoreButtonItem bouton) {
        if (eButtons.Retour.toString().equals(bouton.getType().toString())) {
            // Annulation — on efface le résultat immédiat de la fléchette annulée
            if      (trois != -1) { trois = -1; resultatsImmediat[2] = 0; }
            else if (deux  != -1) { deux  = -1; resultatsImmediat[1] = 0; }
            else if (un    != -1) { un    = -1; resultatsImmediat[0] = 0; }
            statut = eStates.EnCours;

        } else if (eButtons.Suivant.toString().equals(bouton.getType().toString())) {
            // Miss
            if      (un    == -1) { un    = 0; resultatsImmediat[0] = 0; }
            else if (deux  == -1) { deux  = 0; resultatsImmediat[1] = 0; }
            else if (trois == -1) { trois = 0; resultatsImmediat[2] = 0; }

        } else {
            int val = bouton.getPoint() == 25 ? BULL : bouton.getPoint();
            if      (un    == -1) { un    = val; resultatsImmediat[0] = evaluerTir(val, 0); }
            else if (deux  == -1) { deux  = val; resultatsImmediat[1] = evaluerTir(val, 1); }
            else if (trois == -1) { trois = val; resultatsImmediat[2] = evaluerTir(val, 2); }
        }
    }

    /**
     * Évalue immédiatement un tir à la position donnée.
     * Retourne 1 = bonne position, 2 = bon chiffre mauvaise pos, 0 = rien.
     */
    private int evaluerTir(int val, int pos) {
        if (val == 0) return 0; // Miss
        if (val == combi[pos]) return 1; // Bonne position
        for (int c = 0; c < 3; c++)
            if (c != pos && val == combi[c]) return 2; // Bon chiffre, mauvaise pos
        return 0;
    }

    /**
     * Appelé quand les 3 fléchettes sont lancées.
     * Met à jour decouvert[], resultats[] et le statut.
     */
    public void validerTour() {
        resultats = resultatsImmediat.clone();

        // Mettre à jour decouvert[] pour les bonnes positions
        for (int pos = 0; pos < 3; pos++)
            if (resultats[pos] == 1) decouvert[pos] = true;

        // Victoire : les 3 bonnes positions trouvées ce tour
        if (resultats[0] == 1 && resultats[1] == 1 && resultats[2] == 1) {
            statut = eStates.Termine;
            return;
        }

        // Rotation
        indexJoueur = (indexJoueur + 1) % joueurs.size();
        if (indexJoueur == 0) tourGlobal++;

        if (tourGlobal > MAX_TOURS) {
            statut = eStates.Timeout;
            return;
        }

        // Reset pour le prochain joueur
        un    = -1;
        deux  = -1;
        trois = -1;
        resultatsImmediat = new int[]{0, 0, 0};
        statut = eStates.EnCours;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public MMPlayerItem getJoueurCourant()  { return joueurs.get(indexJoueur); }
    public List<MMPlayerItem> getJoueurs()  { return joueurs; }
    public eStates getStatut()              { return statut; }
    public int getTourGlobal()              { return tourGlobal; }
    public boolean[] getDecouvert()         { return decouvert; }
    public int[] getResultats()             { return resultats; }

    /** Résultat immédiat de chaque fléchette (mis à jour dès le lancer). */
    public int[] getResultatsImmediat()     { return resultatsImmediat; }

    public String[] getCombiLabels() {
        String[] labels = new String[3];
        for (int i = 0; i < 3; i++)
            labels[i] = combi[i] == BULL ? "Bull" : String.valueOf(combi[i]);
        return labels;
    }

    public int getNbFlechettes() {
        if (un    == -1) return 0;
        if (deux  == -1) return 1;
        if (trois == -1) return 2;
        return 3;
    }

    public int getTir(int n) {
        if (n == 1) return un;
        if (n == 2) return deux;
        return trois;
    }

    public static String labelTir(int val) {
        if (val == -1)   return "?";
        if (val == 0)    return "Miss";
        if (val == BULL) return "Bull";
        return String.valueOf(val);
    }
}
