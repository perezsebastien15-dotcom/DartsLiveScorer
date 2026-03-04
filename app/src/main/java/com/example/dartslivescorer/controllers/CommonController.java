package com.example.dartslivescorer.controllers;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import com.example.dartslivescorer.enums.eButtons;

import java.util.ArrayList;
import java.util.List;

import models.commonModels.ScoreButtonItem;

public class CommonController {

    private Integer multiplicateur = 1;

    public void EnregistreLance(Long id_joueur, int un, int deux, int trois)
    {
        /*for (PlayerItem joueur : joueurs) {
            if (id_joueur == joueur.getId())
                lance = new LanceItem(new Random().nextInt(), this.id_partie, id_joueur, un, deux, trois);
        }*/

        // Enregistrement du lancé

    }

    public List<ScoreButtonItem> InitScoreButtons() {
        List<ScoreButtonItem> items = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            ScoreButtonItem button = new ScoreButtonItem(i,"" + i, i, eButtons.Points);
            items.add(button);
        }

        ScoreButtonItem button25 = new ScoreButtonItem(21,"Bull", 25, eButtons.Speciaux);
        items.add(button25);
        ScoreButtonItem buttonSimple = new ScoreButtonItem(23,"Simple", 0, eButtons.Multiple);
        items.add(buttonSimple);
        ScoreButtonItem buttonDouble = new ScoreButtonItem(24,"Double", 0, eButtons.Multiple);
        items.add(buttonDouble);
        ScoreButtonItem buttonTriple = new ScoreButtonItem(25,"Triple", 0, eButtons.Multiple);
        items.add(buttonTriple);

        ScoreButtonItem buttonMiss = new ScoreButtonItem(26,"Miss !", 0, eButtons.Suivant);
        items.add(buttonMiss);

        ScoreButtonItem buttonDel = new ScoreButtonItem(27,"<", 0, eButtons.Retour);
        items.add(buttonDel);

        ScoreButtonItem buttonRetour = new ScoreButtonItem(27,"Retour", 0, eButtons.Fin);
        items.add(buttonRetour);

        return items;
    }

    public GridLayout toggleButtons(boolean toggle, GridLayout gridLayout)
    {
        if (toggle) {
            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                View view = gridLayout.getChildAt(i);
                if (view instanceof Button) {
                    Button button = (Button) view;
                    if (button.getText().toString().equals("Miss !") || button.getText().toString().equals("Suivant")) {
                        button.setText("Suivant");
                        button.setTextColor(Color.parseColor("#FA00F6"));
                        button.setEnabled(true);
                    } else if (button.getText().toString().equals("<")) {
                        button.setTextColor(Color.parseColor("#FA00F6"));
                        button.setEnabled(true);
                    } else {
                        button.setTextColor(Color.parseColor("#000000"));
                        button.setEnabled(false);
                    }
                }
            }
        }
        else {
            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                View view = gridLayout.getChildAt(i);
                if (view instanceof Button) {
                    Button button = (Button) view;
                    if (button.getText().toString().equals("Suivant")) {
                        button.setText("Miss !");
                    }

                    button.setEnabled(true);
                    button.setTextColor(Color.parseColor("#FFFFFF"));
                }
            }
        }
        return gridLayout;
    }

    public GridLayout chargeMultiple(GridLayout gridLayout)
    {
        Integer multiple = this.getMultiplicateur();

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View view = gridLayout.getChildAt(i);
            if (view instanceof Button) {
                Button button = (Button) view;
                if (button.getTag().toString().equals(eButtons.Points.toString()))
                {
                    if (multiple.equals(2)) {
                        button.setText("D "+String.valueOf(button.getId()));
                    } else if (multiple.equals(3)) {
                        button.setText("T "+String.valueOf(button.getId()));
                    } else if (multiple.equals(1)) {
                        button.setText(String.valueOf(button.getId()));
                    }
                }
                if(button.getTag().toString().equals(eButtons.Speciaux.toString()) && button.isEnabled())
                {
                    if(multiple == 1) {
                        button.setVisibility(View.VISIBLE);
                        button.setText("Bull");
                    }
                    if(multiple == 2) {
                        button.setVisibility(View.VISIBLE);
                        button.setText("D-Bull");
                    }
                    if(multiple == 3) {
                        button.setVisibility(View.GONE);
                    }
                }
                if (button.getTag().toString().equals(eButtons.Multiple.toString()) && button.isEnabled())
                {
                    if(multiple == 1 && "Simple".equals(button.getText().toString()))
                    {
                        button.getBackground().setAlpha(90);
                        button.setTextColor(Color.YELLOW);
                    }
                    if(multiple == 1 && "Double".equals(button.getText().toString()))
                    {
                        button.getBackground().setAlpha(150);
                        button.setTextColor(Color.WHITE);
                    }
                    if(multiple == 1 && "Triple".equals(button.getText().toString()))
                    {
                        button.getBackground().setAlpha(150);
                        button.setTextColor(Color.WHITE);
                    }

                    if(multiple == 2 && "Simple".equals(button.getText().toString()))
                    {
                        button.getBackground().setAlpha(150);
                        button.setTextColor(Color.WHITE);
                    }
                    if(multiple == 2 && "Double".equals(button.getText().toString()))
                    {
                        button.getBackground().setAlpha(90);
                        button.setTextColor(Color.YELLOW);
                    }
                    if(multiple == 2 && "Triple".equals(button.getText().toString()))
                    {
                        button.getBackground().setAlpha(150);
                        button.setTextColor(Color.WHITE);
                    }

                    if(multiple == 3 && "Simple".equals(button.getText().toString()))
                    {
                        button.getBackground().setAlpha(150);
                        button.setTextColor(Color.WHITE);
                    }
                    if(multiple == 3 && "Double".equals(button.getText().toString()))
                    {
                        button.getBackground().setAlpha(150);
                        button.setTextColor(Color.WHITE);
                    }
                    if(multiple == 3 && "Triple".equals(button.getText().toString()))
                    {
                        button.getBackground().setAlpha(90);
                        button.setTextColor(Color.YELLOW);
                    }
                }
            }
        }
        return gridLayout;
    }

    public void changeMultiple(ScoreButtonItem bouton)
    {
        if (bouton.getLabel().equals("Triple")) {
            this.multiplicateur = 3;
        } else if (bouton.getLabel().equals("Double")) {
            this.multiplicateur = 2;
        } else if (bouton.getLabel().equals("Simple")) {
            this.multiplicateur = 1;
        }
    }

    public Integer getMultiplicateur()
    {
        return this.multiplicateur;
    }

}
