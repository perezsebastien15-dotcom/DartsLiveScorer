package com.example.dartslivescorer.controllers;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.core.content.ContextCompat;

import com.example.dartslivescorer.R;
import com.example.dartslivescorer.enums.eButtons;

import java.util.ArrayList;
import java.util.List;

import models.commonModels.ScoreButtonItem;

public class CommonController {

    private Integer multiplicateur = 1;

    // ✅ IDs uniques pour chaque bouton (était : deux boutons avec ID=27)
    public static final int ID_BULL      = 21;
    public static final int ID_SIMPLE    = 22;
    public static final int ID_DOUBLE    = 23;
    public static final int ID_TRIPLE    = 24;
    public static final int ID_MISS      = 25;
    public static final int ID_RETOUR    = 26;
    public static final int ID_FIN       = 27;

    public List<ScoreButtonItem> InitScoreButtons() {
        List<ScoreButtonItem> items = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            items.add(new ScoreButtonItem(i, "" + i, i, eButtons.Points));
        }
        items.add(new ScoreButtonItem(ID_BULL,   "Bull",   25, eButtons.Speciaux));
        items.add(new ScoreButtonItem(ID_SIMPLE,  "Simple",  0, eButtons.Multiple));
        items.add(new ScoreButtonItem(ID_DOUBLE,  "Double",  0, eButtons.Multiple));
        items.add(new ScoreButtonItem(ID_TRIPLE,  "Triple",  0, eButtons.Multiple));
        items.add(new ScoreButtonItem(ID_MISS,    "Miss !",  0, eButtons.Suivant));
        items.add(new ScoreButtonItem(ID_RETOUR,  "<",       0, eButtons.Retour));
        items.add(new ScoreButtonItem(ID_FIN,     "Retour",  0, eButtons.Fin));
        return items;
    }

    // ✅ Couleurs lues depuis colors.xml via Context — plus de Color.parseColor() hardcodé
    public GridLayout toggleButtons(boolean toggle, GridLayout gridLayout, Context context) {
        int colorActive   = ContextCompat.getColor(context, R.color.button_text_active);
        int colorDisabled = ContextCompat.getColor(context, R.color.button_text_disabled);
        int colorDefault  = ContextCompat.getColor(context, R.color.button_text_default);

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View view = gridLayout.getChildAt(i);
            if (!(view instanceof Button)) continue;
            Button button = (Button) view;
            String text = button.getText().toString();
            String tag  = button.getTag() != null ? button.getTag().toString() : "";

            if (toggle) {
                if (text.equals("Miss !") || text.equals("Suivant")) {
                    button.setText("Suivant");
                    button.setTextColor(colorActive);
                    button.setEnabled(true);
                } else if (text.equals("<")) {
                    button.setTextColor(colorActive);
                    button.setEnabled(true);
                } else {
                    button.setTextColor(colorDisabled);
                    button.setEnabled(false);
                }
            } else {
                if (text.equals("Suivant")) button.setText("Miss !");
                button.setEnabled(true);
                button.setTextColor(colorDefault);
            }
        }
        return gridLayout;
    }

    public GridLayout chargeMultiple(GridLayout gridLayout, Context context) {
        Integer multiple = this.getMultiplicateur();

        int colorDefault  = ContextCompat.getColor(context, R.color.button_text_default);
        int colorSelected = ContextCompat.getColor(context, R.color.button_text_selected);

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View view = gridLayout.getChildAt(i);
            if (!(view instanceof Button)) continue;
            Button button = (Button) view;
            String tag = button.getTag() != null ? button.getTag().toString() : "";

            if (tag.equals(eButtons.Points.toString())) {
                if (multiple == 2)
                    button.setText("D " + button.getId());
                else if (multiple == 3)
                    button.setText("T " + button.getId());
                else
                    button.setText(String.valueOf(button.getId()));
            }

            if (tag.equals(eButtons.Speciaux.toString()) && button.isEnabled()) {
                if (multiple == 1) { button.setVisibility(View.VISIBLE); button.setText("Bull"); }
                else if (multiple == 2) { button.setVisibility(View.VISIBLE); button.setText("D-Bull"); }
                else if (multiple == 3) { button.setVisibility(View.GONE); }
            }

            if (tag.equals(eButtons.Multiple.toString()) && button.isEnabled()) {
                String label = button.getText().toString();
                boolean isActive = (multiple == 1 && "Simple".equals(label))
                                || (multiple == 2 && "Double".equals(label))
                                || (multiple == 3 && "Triple".equals(label));
                button.getBackground().setAlpha(isActive ? 90 : 150);
                button.setTextColor(isActive ? colorSelected : colorDefault);
            }
        }
        return gridLayout;
    }

    public void changeMultiple(ScoreButtonItem bouton) {
        if ("Triple".equals(bouton.getLabel()))       this.multiplicateur = 3;
        else if ("Double".equals(bouton.getLabel()))  this.multiplicateur = 2;
        else if ("Simple".equals(bouton.getLabel()))  this.multiplicateur = 1;
    }

    public Integer getMultiplicateur() {
        return this.multiplicateur;
    }
}
