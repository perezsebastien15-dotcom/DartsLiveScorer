package com.example.dartslivescorer.gamesActivities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.dartslivescorer.R;
import com.example.dartslivescorer.adapters.MMHistoriqueAdapter;
import com.example.dartslivescorer.commonActivities.GamesListActivity;
import com.example.dartslivescorer.controllers.CommonController;
import com.example.dartslivescorer.controllers.MasterController;
import com.example.dartslivescorer.enums.eButtons;
import com.example.dartslivescorer.enums.eStates;

import java.util.ArrayList;
import java.util.List;

import models.commonModels.GameItem;
import models.commonModels.ScoreButtonItem;
import models.gamesModels.MMTryItem;
import models.gamesModels.PlayerItem;

public class MasterMindActivity extends AppCompatActivity {

    private static final String TAG = "MasterMind";

    private GameItem         selectedGame;
    private List<PlayerItem> selectedPlayers;
    private CommonController commoncontroller;
    private MasterController controller;
    private GridLayout       gridLayout;
    private Vibrator         vibrator;

    private final List<MMTryItem>     historique        = new ArrayList<>();
    private       MMHistoriqueAdapter historiqueAdapter;

    // Vues bindées une fois dans onCreate
    private TextView  tvTours, tvJoueur;
    private TextView  combi1, combi2, combi3;
    private TextView  tir1, tir2, tir3;
    private ImageView ind1, ind2, ind3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_master_mind_activity);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        WindowInsetsControllerCompat wic = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        wic.hide(WindowInsetsCompat.Type.systemBars());

        selectedGame    = getIntent().getParcelableExtra("selectedGame");
        selectedPlayers = getIntent().getParcelableArrayListExtra("selectedPlayers");
        if (selectedPlayers == null) selectedPlayers = new ArrayList<>();

        controller       = new MasterController();
        commoncontroller = new CommonController();
        controller.InitialisePartie(selectedGame, selectedPlayers);

        // Binding des vues
        tvTours  = findViewById(R.id.nb_tours);
        tvJoueur = findViewById(R.id.tv_joueur_courant);
        combi1   = findViewById(R.id.combi_un);
        combi2   = findViewById(R.id.combi_deux);
        combi3   = findViewById(R.id.combi_trois);
        tir1     = findViewById(R.id.tir_un);
        tir2     = findViewById(R.id.tir_deux);
        tir3     = findViewById(R.id.tir_trois);
        ind1     = findViewById(R.id.ind_un);
        ind2     = findViewById(R.id.ind_deux);
        ind3     = findViewById(R.id.ind_trois);

        if (tvJoueur == null) Log.e(TAG, "tv_joueur_courant introuvable !");
        if (tir1     == null) Log.e(TAG, "tir_un introuvable !");
        if (ind1     == null) Log.e(TAG, "ind_un introuvable !");

        // Historique
        ListView listView = findViewById(R.id.listHistorique);
        historiqueAdapter = new MMHistoriqueAdapter(this, historique);
        if (listView != null) listView.setAdapter(historiqueAdapter);

        // Clavier
        gridLayout = findViewById(R.id.gridLayout);
        for (ScoreButtonItem scoreButton : commoncontroller.InitScoreButtons()) {
            Button btn = new Button(this);
            btn.setLayoutParams(new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f)));
            btn.setId(scoreButton.getId());
            btn.setText(scoreButton.getLabel());
            btn.setTag(scoreButton.getType());
            btn.getBackground().setTint(getResources().getColor(R.color.button_bg_default, getTheme()));
            btn.getBackground().setAlpha(150);

            btn.setOnClickListener(v -> {
                vibrate();
                String tag  = btn.getTag().toString();
                String text = btn.getText().toString();

                if (tag.equals(eButtons.Fin.toString())) {
                    new AlertDialog.Builder(this)
                            .setTitle("Confirmation")
                            .setMessage("Voulez-vous vraiment quitter ?")
                            .setPositiveButton("Oui", (d, w) -> { startActivity(new Intent(this, GamesListActivity.class)); finish(); })
                            .setNegativeButton("Non", (d, w) -> d.dismiss())
                            .show();

                } else if (tag.equals(eButtons.Multiple.toString())) {
                    // Ignoré en MasterMind

                } else if (tag.equals(eButtons.Suivant.toString()) && text.equals("Suivant")) {
                    validerEtPasser();

                } else {
                    // Lancer de fléchette (ou Miss ou Retour)
                    controller.MAJFlechette(scoreButton);

                    // Affichage immédiat du tir ET de son indicateur
                    rafraichirTirs();

                    // Activer "Suivant" quand les 3 fléchettes sont posées
                    if (controller.getNbFlechettes() == 3) {
                        gridLayout = commoncontroller.toggleButtons(true, gridLayout, this);
                    } else {
                        gridLayout = commoncontroller.toggleButtons(false, gridLayout, this);
                    }
                    gridLayout = commoncontroller.chargeMultiple(gridLayout, this);
                }
            });
            gridLayout.addView(btn);
        }

        rafraichirTout();
    }

    // ── Validation du tour ────────────────────────────────────────────────────

    private void validerEtPasser() {
        String joueurNom = controller.getJoueurCourant().getName();
        int    tour      = controller.getTourGlobal();
        String f1 = MasterController.labelTir(controller.getTir(1));
        String f2 = MasterController.labelTir(controller.getTir(2));
        String f3 = MasterController.labelTir(controller.getTir(3));
        int[]  ri = controller.getResultatsImmediat();

        controller.validerTour();

        // Ajouter à l'historique avec les résultats immédiats (déjà calculés)
        historique.add(0, new MMTryItem(tour, joueurNom, f1, f2, f3, ri[0], ri[1], ri[2]));
        historiqueAdapter.notifyDataSetChanged();

        reinitTirs();

        if (eStates.Termine.equals(controller.getStatut())) {
            rafraichirTout(); revelerCombi(); afficherGagnant(joueurNom); return;
        }
        if (eStates.Timeout.equals(controller.getStatut())) {
            rafraichirTout(); revelerCombi(); afficherTimeout(); return;
        }

        gridLayout = commoncontroller.toggleButtons(false, gridLayout, this);
        gridLayout = commoncontroller.chargeMultiple(gridLayout, this);
        rafraichirTout();
    }

    // ── Affichage des tirs en cours ───────────────────────────────────────────

    @SuppressLint("SetTextI18n")
    private void rafraichirTirs() {
        int[] imm = controller.getResultatsImmediat();
        setTirView(tir1, ind1, controller.getTir(1), imm[0]);
        setTirView(tir2, ind2, controller.getTir(2), imm[1]);
        setTirView(tir3, ind3, controller.getTir(3), imm[2]);
    }

    private void setTirView(TextView tv, ImageView ind, int val, int resultat) {
        if (tv == null) return;
        if (val == -1) {
            tv.setText("·");
            tv.setTextColor(0xFF444444);
            if (ind != null) { ind.setVisibility(View.INVISIBLE); }
            return;
        }
        if (val == 0) {
            tv.setText("Miss");
            tv.setTextColor(0xFF888888);
        } else {
            tv.setText(val == MasterController.BULL ? "Bull" : String.valueOf(val));
            tv.setTextColor(0xFFFFF200);
        }
        // Affichage immédiat de l'indicateur coloré
        if (ind != null) {
            switch (resultat) {
                case 1: // Bonne position → vert
                    ind.setVisibility(View.VISIBLE);
                    ind.setBackgroundColor(0xFF00E676);
                    break;
                case 2: // Bon chiffre, mauvaise position → orange
                    ind.setVisibility(View.VISIBLE);
                    ind.setBackgroundColor(0xFFFF9800);
                    break;
                default: // Rien ou Miss
                    ind.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }

    private void reinitTirs() {
        setTirView(tir1, ind1, -1, 0);
        setTirView(tir2, ind2, -1, 0);
        setTirView(tir3, ind3, -1, 0);
    }

    // ── Affichage complet ─────────────────────────────────────────────────────

    @SuppressLint("SetTextI18n")
    private void rafraichirTout() {
        if (tvTours  != null) tvTours.setText("Tour " + controller.getTourGlobal() + " / " + MasterController.MAX_TOURS);
        if (tvJoueur != null) tvJoueur.setText(controller.getJoueurCourant().getName());

        String[]  labels    = controller.getCombiLabels();
        boolean[] decouvert = controller.getDecouvert();
        setCombiView(combi1, labels[0], decouvert[0]);
        setCombiView(combi2, labels[1], decouvert[1]);
        setCombiView(combi3, labels[2], decouvert[2]);

        rafraichirTirs();
    }

    private void setCombiView(TextView tv, String label, boolean decouvert) {
        if (tv == null) return;
        if (decouvert) {
            tv.setText(label);
            tv.setTextColor(0xFF00E676);
            tv.setBackgroundColor(0x2200E676);
        } else {
            tv.setText("?");
            tv.setTextColor(0xFF888888);
            tv.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void revelerCombi() {
        String[] labels = controller.getCombiLabels();
        if (combi1 != null) { combi1.setText(labels[0]); combi1.setTextColor(0xFFFF5252); combi1.setBackgroundColor(0x22FF0000); }
        if (combi2 != null) { combi2.setText(labels[1]); combi2.setTextColor(0xFFFF5252); combi2.setBackgroundColor(0x22FF0000); }
        if (combi3 != null) { combi3.setText(labels[2]); combi3.setTextColor(0xFFFF5252); combi3.setBackgroundColor(0x22FF0000); }
    }

    private void afficherGagnant(String gagnant) {
        new AlertDialog.Builder(this)
                .setTitle("🏆 Victoire !")
                .setMessage(gagnant + " a découvert la combinaison !")
                .setPositiveButton("OK", (d, w) -> { startActivity(new Intent(this, GamesListActivity.class)); finish(); })
                .show();
    }

    private void afficherTimeout() {
        String[] l = controller.getCombiLabels();
        new AlertDialog.Builder(this)
                .setTitle("Temps écoulé !")
                .setMessage("La combinaison était : " + l[0] + " — " + l[1] + " — " + l[2])
                .setPositiveButton("OK", (d, w) -> { startActivity(new Intent(this, GamesListActivity.class)); finish(); })
                .show();
    }

    private void vibrate() {
        if (vibrator != null && vibrator.hasVibrator()) vibrator.vibrate(100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vibrator != null) vibrator.cancel();
    }
}
