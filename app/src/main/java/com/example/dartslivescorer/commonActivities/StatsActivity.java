package com.example.dartslivescorer.commonActivities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.dartslivescorer.R;
import com.example.dartslivescorer.adapters.PieChartView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import models.DartScorerDatabase;

public class StatsActivity extends AppCompatActivity {

    private DartScorerDatabase db;

    // Joueur reçu par Intent
    private long   joueurId;
    private String joueurNom;

    // Jeu sélectionné
    private String      typeJeuSelectionne = null;
    private List<String> typesJeuxDisponibles = new ArrayList<>();
    private LinearLayout tabsJeux;

    // Cards
    private View cardParties, cardVictoires, cardDefaites, cardWinrate;
    private View cardMoyenne, cardMeilleur, cardMauvais, cardTours;
    private View card180, card140plus, card100plus, cardParfaits;
    private View cardTouchees, cardManquees, cardMoyF1, cardMoyF2, cardMoyF3, cardPrecision;

    // Graphiques
    private PieChartView pieVictoires, pieScores, pieFlechettes;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_stats);

        WindowInsetsControllerCompat wic = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        wic.hide(WindowInsetsCompat.Type.systemBars());

        db = DartScorerDatabase.getDatabase(this);

        // ── Joueur passé par Intent ──────────────────────────────────────────
        joueurId  = getIntent().getLongExtra("playerId", -1);
        joueurNom = getIntent().getStringExtra("playerName");
        if (joueurNom == null) joueurNom = "—";

        ((TextView) findViewById(R.id.stats_nom)).setText(joueurNom);

        // ── Bouton retour ────────────────────────────────────────────────────
        ((Button) findViewById(R.id.jeuretour)).setOnClickListener(v -> {
            startActivity(new Intent(this, PlayersActivity.class));
            finish();
        });

        // ── Cards ────────────────────────────────────────────────────────────
        cardParties   = findViewById(R.id.card_parties);
        cardVictoires = findViewById(R.id.card_victoires);
        cardDefaites  = findViewById(R.id.card_defaites);
        cardWinrate   = findViewById(R.id.card_winrate);
        cardMoyenne   = findViewById(R.id.card_moyenne);
        cardMeilleur  = findViewById(R.id.card_meilleur);
        cardMauvais   = findViewById(R.id.card_mauvais);
        cardTours     = findViewById(R.id.card_tours);
        card180       = findViewById(R.id.card_180);
        card140plus   = findViewById(R.id.card_140plus);
        card100plus   = findViewById(R.id.card_100plus);
        cardParfaits  = findViewById(R.id.card_parfaits);
        cardTouchees  = findViewById(R.id.card_touchees);
        cardManquees  = findViewById(R.id.card_manquees);
        cardMoyF1     = findViewById(R.id.card_moy_f1);
        cardMoyF2     = findViewById(R.id.card_moy_f2);
        cardMoyF3     = findViewById(R.id.card_moy_f3);
        cardPrecision = findViewById(R.id.card_precision);

        // ── Graphiques ───────────────────────────────────────────────────────
        pieVictoires  = findViewById(R.id.pie_victoires);
        pieScores     = findViewById(R.id.pie_scores);
        pieFlechettes = findViewById(R.id.pie_flechettes);

        tabsJeux = findViewById(R.id.tabs_jeux);

        // ── Charger les types de jeux joués puis afficher ────────────────────
        executor.execute(() -> {
            typesJeuxDisponibles = db.dartScorerDao().getTypesJeuxJoues(joueurId);
            runOnUiThread(() -> {
                construireTabs();
                // Sélectionner le premier jeu disponible par défaut
                if (!typesJeuxDisponibles.isEmpty()) {
                    selectionnerJeu(typesJeuxDisponibles.get(0));
                } else {
                    afficherStats("__aucun__");
                }
            });
        });
    }

    // ── Construction des tabs ─────────────────────────────────────────────────
    private void construireTabs() {
        tabsJeux.removeAllViews();
        for (String type : typesJeuxDisponibles) {
            TextView tab = creerTab(labelJeu(type), type);
            tabsJeux.addView(tab);
        }
        if (typesJeuxDisponibles.isEmpty()) {
            TextView vide = new TextView(this);
            vide.setText("Aucune partie enregistrée");
            vide.setTextColor(0xFFAAAAAA);
            vide.setTextSize(14f);
            vide.setPadding(8, 8, 8, 8);
            tabsJeux.addView(vide);
        }
    }

    private TextView creerTab(String label, String typeJeu) {
        TextView tab = new TextView(this);
        tab.setText(label);
        tab.setTextSize(13f);
        tab.setTypeface(ResourcesCompat.getFont(this, R.font.bines), Typeface.BOLD);
        tab.setGravity(Gravity.CENTER);
        tab.setPadding(28, 14, 28, 14);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 8, 0);
        tab.setLayoutParams(lp);

        styleTab(tab, false);
        tab.setOnClickListener(v -> selectionnerJeu(typeJeu));
        tab.setTag(typeJeu);
        return tab;
    }

    private void selectionnerJeu(String typeJeu) {
        typeJeuSelectionne = typeJeu;
        // Mettre à jour le style des tabs
        for (int i = 0; i < tabsJeux.getChildCount(); i++) {
            View child = tabsJeux.getChildAt(i);
            if (child instanceof TextView) {
                boolean actif = typeJeu.equals(child.getTag());
                styleTab((TextView) child, actif);
            }
        }
        afficherStats(typeJeu);
    }

    private void styleTab(TextView tab, boolean actif) {
        if (actif) {
            tab.setBackgroundColor(0xFF00FFEA);
            tab.setTextColor(0xFF111122);
            tab.setShadowLayer(0, 0, 0, 0);
        } else {
            tab.setBackgroundColor(0x33FFFFFF);
            tab.setTextColor(0xFFCCCCCC);
        }
    }

    /** Retourne un libellé lisible pour un type de jeu. */
    private String labelJeu(String type) {
        switch (type) {
            case "Standard301":    return "301";
            case "Standard501":    return "501";
            case "Standard701":    return "701";
            case "OriginalCricket":return "Cricket";
            case "HiddenCricket":  return "Cricket\nHidden";
            case "RandomCricket":  return "Cricket\nRandom";
            case "UnderTheHat":    return "Under\nthe Hat";
            case "MasterMind":     return "Master\nMind";
            case "ShootOut":       return "Shoot\nOut";
            default:               return type;
        }
    }

    // ── Chargement et affichage des stats ─────────────────────────────────────
    private void afficherStats(String typeJeu) {
        executor.execute(() -> {
            // ── Résultats ────────────────────────────────────────────────────
            int parties   = db.dartScorerDao().getNbPartiesJouees(joueurId, typeJeu);
            int victoires = db.dartScorerDao().getNbVictoiresJoueur(joueurNom, typeJeu);
            int defaites  = Math.max(0, parties - victoires);
            float winRate = parties > 0 ? (float) victoires / parties * 100f : 0f;

            // ── Scores ───────────────────────────────────────────────────────
            int   lancers  = db.dartScorerDao().getNbLancersJoueur(joueurId, typeJeu);
            int   sommeTot = db.dartScorerDao().getSommeTotaleJoueur(joueurId, typeJeu);
            int   meilleur = db.dartScorerDao().getMeilleurLancerJoueur(joueurId, typeJeu);
            int   mauvais  = db.dartScorerDao().getMauvaisLancerJoueur(joueurId, typeJeu);
            float moyenne  = lancers > 0 ? (float) sommeTot / lancers : 0f;

            // ── Performances ─────────────────────────────────────────────────
            int nb180     = db.dartScorerDao().getNb180Joueur(joueurId, typeJeu);
            int nb140plus = db.dartScorerDao().getNb140PlusJoueur(joueurId, typeJeu);
            int nb100plus = db.dartScorerDao().getNb100PlusJoueur(joueurId, typeJeu);
            int parfaits  = db.dartScorerDao().getNbToursPArfaits(joueurId, typeJeu);

            // ── Fléchettes ───────────────────────────────────────────────────
            int touchees  = db.dartScorerDao().getNbFlechettesTouchees(joueurId, typeJeu);
            int manquees  = db.dartScorerDao().getNbFlechettesManquees(joueurId, typeJeu);
            int sommeF1   = db.dartScorerDao().getSommeFlechette1(joueurId, typeJeu);
            int sommeF2   = db.dartScorerDao().getSommeFlechette2(joueurId, typeJeu);
            int sommeF3   = db.dartScorerDao().getSommeFlechette3(joueurId, typeJeu);
            float moyF1   = lancers > 0 ? (float) sommeF1 / lancers : 0f;
            float moyF2   = lancers > 0 ? (float) sommeF2 / lancers : 0f;
            float moyF3   = lancers > 0 ? (float) sommeF3 / lancers : 0f;
            int   totalF  = touchees + manquees;
            float prec    = totalF   > 0 ? (float) touchees / totalF * 100f : 0f;

            // ── Répartition ──────────────────────────────────────────────────
            int s0_59    = db.dartScorerDao().getNbLancersAvecScoreEntre(joueurId, typeJeu, 0,   59);
            int s60_99   = db.dartScorerDao().getNbLancersAvecScoreEntre(joueurId, typeJeu, 60,  99);
            int s100_139 = db.dartScorerDao().getNbLancersAvecScoreEntre(joueurId, typeJeu, 100, 139);
            int s140_179 = db.dartScorerDao().getNbLancersAvecScoreEntre(joueurId, typeJeu, 140, 179);
            int s180     = nb180;

            runOnUiThread(() -> {
                // Cards Résultats
                setCard(cardParties,   "PARTIES JOUÉES",  String.valueOf(parties));
                setCard(cardVictoires, "VICTOIRES",       String.valueOf(victoires), 0xFF00E676);
                setCard(cardDefaites,  "DÉFAITES",        String.valueOf(defaites),  0xFFFF5252);
                setCard(cardWinrate,   "TAUX DE VICTOIRE",
                        parties > 0 ? String.format("%.0f%%", winRate) : "—",
                        winRate >= 50f ? 0xFF00E676 : 0xFFFF5252);

                // Cards Scores
                setCard(cardMoyenne,  "MOYENNE / TOUR", lancers > 0 ? String.format("%.1f", moyenne) : "—", 0xFFFFF200);
                setCard(cardMeilleur, "MEILLEUR TOUR",  String.valueOf(meilleur), 0xFF00E676);
                setCard(cardMauvais,  "MOINS BON TOUR", String.valueOf(mauvais),  0xFFFF9800);
                setCard(cardTours,    "TOURS JOUÉS",    String.valueOf(lancers));

                // Cards Performances
                setCard(card180,      "180 🔥",         String.valueOf(nb180),    0xFFFF5252);
                setCard(card140plus,  "TOURS 140+",     String.valueOf(nb140plus), 0xFFFF9800);
                setCard(card100plus,  "TOURS 100+",     String.valueOf(nb100plus), 0xFF40C4FF);
                setCard(cardParfaits, "TOURS PARFAITS",
                        lancers > 0 ? String.format("%d (%.0f%%)", parfaits, (float)parfaits/lancers*100f) : "—",
                        0xFF00E676);

                // Cards Fléchettes
                setCard(cardTouchees,  "FLÉCHETTES OK",    String.valueOf(touchees),  0xFF00E676);
                setCard(cardManquees,  "FLÉCHETTES MISS",  String.valueOf(manquees),  0xFFFF5252);
                setCard(cardMoyF1,     "MOY. FLÉCHETTE 1", String.format("%.1f", moyF1));
                setCard(cardMoyF2,     "MOY. FLÉCHETTE 2", String.format("%.1f", moyF2));
                setCard(cardMoyF3,     "MOY. FLÉCHETTE 3", String.format("%.1f", moyF3));
                setCard(cardPrecision, "PRÉCISION",
                        totalF > 0 ? String.format("%.0f%%", prec) : "—",
                        prec >= 70f ? 0xFF00E676 : prec >= 40f ? 0xFFFF9800 : 0xFFFF5252);

                // Graphique 1 : Victoires / Défaites
                List<PieChartView.Slice> sv = new ArrayList<>();
                if (parties > 0) {
                    sv.add(new PieChartView.Slice("Victoires", victoires, 0xFF00E676));
                    if (defaites > 0)
                        sv.add(new PieChartView.Slice("Défaites", defaites, 0xFFFF5252));
                } else {
                    sv.add(new PieChartView.Slice("Aucune partie", 1, 0xFF444455));
                }
                pieVictoires.setData(sv,
                        parties > 0 ? String.format("%.0f%%", winRate) + "\nwin" : "—",
                        "Victoires / Défaites");

                // Graphique 2 : Répartition des tours
                List<PieChartView.Slice> ss = new ArrayList<>();
                if (s0_59   > 0) ss.add(new PieChartView.Slice("0–59",    s0_59,   0xFFFF5252));
                if (s60_99  > 0) ss.add(new PieChartView.Slice("60–99",   s60_99,  0xFFFF9800));
                if (s100_139> 0) ss.add(new PieChartView.Slice("100–139", s100_139,0xFF40C4FF));
                if (s140_179> 0) ss.add(new PieChartView.Slice("140–179", s140_179,0xFF00E676));
                if (s180    > 0) ss.add(new PieChartView.Slice("180",     s180,    0xFFFFF200));
                if (ss.isEmpty()) ss.add(new PieChartView.Slice("Aucune donnée", 1, 0xFF444455));
                pieScores.setData(ss,
                        lancers > 0 ? String.format("%.1f", moyenne) : "—",
                        "Répartition des tours");

                // Graphique 3 : Précision fléchettes
                List<PieChartView.Slice> sf = new ArrayList<>();
                if (touchees > 0) sf.add(new PieChartView.Slice("Touchées", touchees, 0xFF00E676));
                if (manquees > 0) sf.add(new PieChartView.Slice("Manquées", manquees, 0xFFFF5252));
                if (sf.isEmpty()) sf.add(new PieChartView.Slice("Aucune donnée", 1, 0xFF444455));
                pieFlechettes.setData(sf,
                        totalF > 0 ? String.format("%.0f%%", prec) + "\nok" : "—",
                        "Précision des fléchettes");
            });
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void setCard(View card, String label, String value, int color) {
        ((TextView) card.findViewById(R.id.card_label)).setText(label);
        TextView tv = card.findViewById(R.id.card_value);
        tv.setText(value);
        tv.setTextColor(color);
    }
    private void setCard(View card, String label, String value) {
        setCard(card, label, value, 0xFFFFFFFF);
    }
}
