package com.example.dartslivescorer.controllers;

import android.content.Context;

import com.example.dartslivescorer.enums.eButtons;
import com.example.dartslivescorer.enums.eGames;
import com.example.dartslivescorer.enums.eStates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import models.gamesModels.CricketPlayerItem;
import models.gamesModels.CricketValueItem;
import models.DartScorerDatabase;
import models.commonModels.GameItem;
import models.commonModels.LanceItem;
import models.OnScoreUpdateListener;
import models.gamesModels.PlayerItem;
import models.commonModels.ScoreButtonItem;

public class CricketController {

    private int id_partie;
    private int tours;
    private List<CricketPlayerItem> joueurs;
    private List<CricketPlayerItem> rotation;
    private List<CricketPlayerItem> playersInCaseOfSuppr = new ArrayList<>();
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

    private List<CricketValueItem> ItemsCricket;
    private List<CricketValueItem> itemsInCaseOfSuppr = new ArrayList<>();

    private CricketPlayerItem joueurCourant;
    private OnScoreUpdateListener mListener;
    private CommonController commonController;

    private Context context;
    public CricketController(Context context) {
        this.context = context;
        this.db = DartScorerDatabase.getDatabase(context);
    }

    public List<CricketPlayerItem> InitialisePartie(GameItem jeu, List<PlayerItem> joueurs, DartScorerDatabase db, CommonController commonController) {
        this.id_partie = new Random().nextInt();
        this.jeu = jeu;
        this.type = jeu.getType();
        this.db = db;
        this.tours = jeu.getTours();
        this.commonController = commonController;
        this.ItemsCricket = new ArrayList<>();

        if(jeu.getType().equals(eGames.OriginalCricket))
        {
            this.ItemsCricket.add(new CricketValueItem(1,"20",false,true));
            this.ItemsCricket.add(new CricketValueItem(2,"19",false,true));
            this.ItemsCricket.add(new CricketValueItem(3,"18",false,true));
            this.ItemsCricket.add(new CricketValueItem(4,"17",false,true));
            this.ItemsCricket.add(new CricketValueItem(5,"16",false,true));
            this.ItemsCricket.add(new CricketValueItem(6,"15",false,true));
            this.ItemsCricket.add(new CricketValueItem(7,"B",false,true));
        }
        else {
            Set<Integer> generatedValues = new HashSet<>();
            Random random = new Random();

            while (generatedValues.size() < 7) {
                int val = random.nextInt(21);

                // Si le nombre généré est 0, on le remplace par 21
                if (val == 0) {
                    val = 21;
                }

                if(!generatedValues.contains(val))
                    generatedValues.add(val);
            }

            int iter = 1;
            for (Integer i : generatedValues) {
                String value;
                boolean hidden = jeu.getType().equals(eGames.HiddenCricket);
                boolean randomCricket = jeu.getType().equals(eGames.RandomCricket);

                if (i <= 20) {
                    value = String.valueOf(i);
                } else {
                    value = "B";
                }

                this.ItemsCricket.add(new CricketValueItem(iter, value, hidden, randomCricket));
                iter++;
            }
        }

        return InitialiseJoueurs(joueurs);
    }

    public List<CricketPlayerItem> InitialiseJoueurs(List<PlayerItem> joueurs) {
        this.joueurs = new ArrayList<>();
        for (PlayerItem joueur : joueurs) {
            this.joueurs.add(new CricketPlayerItem(joueur.getId(), joueur.getName(), 0,1,this.ItemsCricket,null));
        }

        changementJoueur(null);
        return this.joueurs;
    }

    public void MAJFlechette(ScoreButtonItem bouton) {

        if (eButtons.Retour.toString().equals(bouton.getType().toString()))
        {
            InitPlayersAfterAction(false);
            this.un = -1;
            this.deux = -1;
            this.trois = -1;

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
            Integer multiple = this.getMultiplicateur();

            //Si c'est des points
            if (eButtons.Points.toString().equals(bouton.getType().toString())) {
                score = bouton.getPoint();


                if(multiple == 1)
                    flechette = String.valueOf(bouton.getPoint());
                if(multiple == 2)
                    flechette = "D"+bouton.getPoint();
                if(multiple == 3)
                    flechette = "T"+bouton.getPoint();

            }
            //Si c'est 25
            else {
                score = bouton.getPoint();

                if(multiple == 1)
                    flechette = "B";
                if(multiple == 2)
                    flechette = "D-B";
            }

            CricketValueItem isValeur;

            //Verif si ce qui a ete touché fait parti des valeurs du cricket, et renvoi le num de l'item si ca trouve
            if(!flechette.equals("B") && !flechette.equals("D-B"))
                isValeur = this.verifCricketValue(bouton.getPoint().toString());
            else
                isValeur = this.verifCricketValue("B");

            //Si c'est bien un item
            if(isValeur != null)
            {
                boolean decouvert = checkDecouvert(isValeur);

                //MAJ des points du joueur, renvoi si les autres joueurs doivent prendre des points
                if(flechette.equals("B"))
                    if(decouvert)
                        multiple = 2;
                    else
                        multiple = 1;
                else if(flechette.equals("D-B"))
                    if(decouvert)
                        multiple = 3;
                    else
                        multiple = 2;
                else
                    if(decouvert)
                        multiple += 1;

                score = this.majValuePlayer(isValeur.getNumItem(), multiple, score);

                //verifier que les autres joueurs n'ont pas deja fermés
                //si les autres joueurs doivent prendre des points et si c'est pas fermé
                if(score > 0 && checkSiPoints(isValeur.getNumItem()))
                    this.majScoreAdversaires(isValeur.getNumItem(),score);
            }

            //Si c'est la premiere flechette
            if (this.un == -1) {
                this.un = score;
                //On intialise son objet Lancé
                this.joueurCourant.InitialiseLance(new Random().nextInt(),this.id_partie,this.joueurCourant.getId());
                this.joueurCourant.setLance(1,score,flechette);
            }

            //Si c'est sa deuxieme flechette
            else if (this.deux == -1) {
                this.deux = score;
                this.joueurCourant.setLance(2,score,flechette);
            //Si c'est sa troisieme flechette
            } else if (this.trois == -1) {
                this.trois = score;
                this.joueurCourant.setLance(3,score,flechette);
            }

            if(isValeur != null)
                checkFermeture(isValeur);

            this.statutParti = checkFinJeu();

            mListener.onScoreUpdate(0);
        }
    }

    private boolean checkDecouvert(CricketValueItem isValeur) {
        boolean retour = true;
        Integer numItem = isValeur.getNumItem();
        numItem -= 1;

        CricketValueItem item = this.ItemsCricket.get(numItem);

        if(!item.getHiddenItem())
            retour = false;
        else {
            item.setHidden(false);
            retour = true;
        }

        return retour;
    }

    /* Permet de vérifier si le score fait parti des valeurs du cricket */
    /* Si le score en fait parti on renvoi son id */
    public CricketValueItem verifCricketValue(String val)
        {
        for(CricketValueItem itemval : this.ItemsCricket)
            if(itemval.getLibelleItem().equals(val))
                return itemval;

        return null;
    }

    public boolean checkSiPoints(Integer numItem)
    {
        Integer estFerme = 0;
        boolean metLesPoints = true;

        for (CricketPlayerItem play : this.joueurs) {
            Map<Integer, Integer> val = play.getValues();
            Integer compte = val.get(numItem);

            if (compte == 3)
                estFerme += 1;
        }

        if(estFerme.equals(this.joueurs.size()))
            metLesPoints = false;

        return metLesPoints;
    }

    /* Permet de mettre à jour les éléments touchés du joueur courant */
    /* Si le joueur est a plus de trois tirs dans cette valeur, on renvoi true pour indiquer qu'il faut ajouter les points aux autres joueurs */
    public Integer majValuePlayer(Integer numItem,Integer multiple,Integer score)
    {
        Map<Integer, Integer> values = this.joueurCourant.getValues();
        Integer nb = values.get(numItem);
        Integer retour = 0;

        nb += multiple;

        if(nb > 3) {
            retour = (nb - 3) * score;
            this.joueurCourant.setValues(numItem, 3);
        }
        else
            this.joueurCourant.setValues(numItem, nb);

        return retour;
    }

    /* Permet d'ajouter les points aux joueur(s) si on a touché quelque chose que l'on a fermé */
    public void majScoreAdversaires(Integer numItem,Integer score)
    {
        // Si on est deux, il faut se mettre des points à soi
        if(this.joueurs.size() == 2)
        {
            if(checkValide(numItem))
                this.joueurCourant.setScore(this.joueurCourant.getScore() + score);
        }
        // S'il y a plus de deux joueurs, il faut mettre des points aux autres
        if(this.joueurs.size() > 2)
        {
            for(CricketPlayerItem player : this.joueurs) {
                Map<Integer, Integer> values = player.getValues();
                Integer nb = values.get(numItem);

                if(nb < 3)
                    player.setScore(player.getScore() + score);
            }
        }
    }

    public void EnregistreLance(Long id_joueur, int un, int deux, int trois) {
        for (CricketPlayerItem joueur : joueurs) {
            if (id_joueur == joueur.getId())
                lance = new LanceItem(new Random().nextInt(), this.id_partie, id_joueur, un, deux, trois,0);
        }

        // Enregistrement du lancé

    }

    public void InitPlayersAfterAction(boolean sens)
    {
        if(sens) {
            this.playersInCaseOfSuppr.clear();
            this.itemsInCaseOfSuppr.clear();

            for (CricketPlayerItem joueur : this.joueurs) {
                Map<Integer, Integer> valeursCopie = new HashMap<>();
                for (Map.Entry<Integer, Integer> entry : joueur.getValues().entrySet()) {
                    valeursCopie.put(entry.getKey(), entry.getValue());
                }
                CricketPlayerItem joueurCopie = new CricketPlayerItem(joueur.getId(), joueur.getName(), joueur.getScore(), joueur.getTour(), null, valeursCopie);

                if(joueur.isSelected())
                    joueurCopie.setSelected(true);

                this.playersInCaseOfSuppr.add(joueurCopie);
            }

            for(CricketValueItem item : this.ItemsCricket)
            {
                CricketValueItem temp = new CricketValueItem(item.getNumItem(),item.getLibelleItem(),item.getHiddenItem(),item.getAvailableItem());
                this.itemsInCaseOfSuppr.add(temp);
            }
        }
        else
        {
            this.joueurs.clear();
            this.ItemsCricket.clear();

            for (CricketPlayerItem joueur : this.playersInCaseOfSuppr) {
                Map<Integer, Integer> valeursCopie = new HashMap<>();
                for (Map.Entry<Integer, Integer> entry : joueur.getValues().entrySet()) {
                    valeursCopie.put(entry.getKey(), entry.getValue());
                }
                CricketPlayerItem joueurCopie = new CricketPlayerItem(joueur.getId(), joueur.getName(), joueur.getScore(), joueur.getTour(), null, valeursCopie);

                if(joueur.isSelected()) {
                    joueurCopie.setSelected(true);
                    this.joueurCourant = joueurCopie;
                }

                this.joueurs.add(joueurCopie);
            }

            for(CricketValueItem item : this.itemsInCaseOfSuppr)
            {
                CricketValueItem temp = new CricketValueItem(item.getNumItem(),item.getLibelleItem(),item.getHiddenItem(),item.getAvailableItem());
                this.ItemsCricket.add(temp);
            }
        }

        for(CricketValueItem item : this.ItemsCricket)
        {
            checkFermeture(item);
        }
    }

    public void changementJoueur(CricketPlayerItem nouveauJoueur) {
        if (nouveauJoueur == null) {
            this.joueurCourant = this.joueurs.get(0);
            this.rotation = joueurs;
        } else {
            //EnregistreLance(this.joueurCourant.getId(), this.un, this.deux, this.trois);
            this.joueurCourant.setTour(this.joueurCourant.getTour() + 1);
            this.resetLance();

            this.joueurCourant = nouveauJoueur;
        }
        this.joueurCourant.setSelected(true);
        InitPlayersAfterAction(true);
    }

    public void resetLance()
    {
        this.trois = -1;
        this.deux = -1;
        this.un = -1;

        joueurCourant.setLance(1,-1,"");
        joueurCourant.setLance(2,-1,"");
        joueurCourant.setLance(3,-1,"");
    }

    public void changementMulti(ScoreButtonItem bouton)
    {
        this.commonController.changeMultiple(bouton);
    }

    public CricketPlayerItem rotationJoueur()
    {
        this.rotation = joueurs;
        CricketPlayerItem player = this.rotation.get(0);
        player.setSelected(false);
        this.rotation.remove(0);
        this.rotation.add(player);
        this.statutParti = eStates.EnCours;
        this.rotation.get(0).setSelected(true);
        return this.rotation.get(0);
    }

    public boolean checkValide(Integer numItem) {
        boolean retour = true;
        numItem-=1;

        CricketValueItem item = this.ItemsCricket.get(numItem);

        if(!item.getAvailable())
            retour = false;

        return retour;
    }

    public eStates checkFinJeu()
    {
        eStates etat = eStates.Termine;

        Map<Integer, Integer> valeursCopie = this.joueurCourant.getValues();
        for (Map.Entry<Integer, Integer> entry : valeursCopie.entrySet()) {
            if(!entry.getValue().equals(3))
                etat = eStates.EnCours;
        }

        if(etat.equals(eStates.Termine))
        {
            Integer score = this.joueurCourant.getScore();

            for(CricketPlayerItem joueur : this.joueurs)
            {
                if(this.joueurs.size() > 2) {
                    if (!joueur.isSelected()) {
                        if (joueur.getScore() < score)
                            etat = eStates.EnCours;
                    }
                }
                else
                {
                    if (!joueur.isSelected()) {
                        if (joueur.getScore() > score)
                            etat = eStates.EnCours;
                    }
                }
            }
        }

        return etat;
    }

    public void checkFermeture(CricketValueItem numItem)
    {
        Integer estFerme = 0;
        for (CricketPlayerItem play : this.joueurs) {
            Map<Integer, Integer> val = play.getValues();
            Integer compte = val.get(numItem.getNumItem());
            if (compte == 3)
                estFerme += 1;

        }

        if (estFerme.equals(this.joueurs.size()))
            this.ItemsCricket.get(numItem.getNumItem() - 1).setAvailable(false);
        else
            this.ItemsCricket.get(numItem.getNumItem() - 1).setAvailable(true);
    }

    public void checkTimeout()
    {
        int nb_tours = jeu.getTours();
        //this.statutParti = eStates.Timeout;
        int nb_fini = 0;
        for (CricketPlayerItem joueur : joueurs) {
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
        for (CricketPlayerItem joueur : joueurs) {
            if(joueur.getTour() >= nb_tours)
                nb_fini += 1;
        }
        if(nb_fini == joueurs.size())
            this.lastRound = true;

        return this.lastRound;
    }

    public List<CricketPlayerItem> getAdversaires(){
        CricketPlayerItem current = this.getJoueurCourant();

        for(CricketPlayerItem val : this.joueurs) {
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

    public CricketPlayerItem getJoueurCourant()
    {
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

    public String getWinner()
    {
        CricketPlayerItem gagnant = null;
        String retour ="";

        if(joueurs.size() == 2)
        {
            for (CricketPlayerItem joueur : joueurs) {
                if(gagnant == null) {
                    gagnant = joueur;
                    retour = joueur.getName();
                }
                else
                {
                    if(joueur.getScore() > gagnant.getScore())
                        retour = joueur.getName();
                    else if (joueur.getScore().equals(gagnant.getScore()))
                        retour = "EX AEQUO !";
                }
            }
        }
        else {
            Integer score = 0;
            for (CricketPlayerItem joueur : joueurs) {
                if(gagnant == null) {
                    gagnant = joueur;
                    retour = joueur.getName();
                    score = joueur.getScore();
                }
                else
                {
                    if(joueur.getScore() < score) {
                        retour = joueur.getName();
                        score = joueur.getScore();
                    }
                    if(joueur.getScore().equals(score))
                        retour = retour + "," + joueur.getName();
                }
            }
            if(retour.contains(","))
                retour = "Les joueurs : " + retour + " sont EX AEQUO !";
        }

        return retour;

    }

    public List<CricketValueItem> getCricketValues(){
        return this.ItemsCricket;
    }
    public void setOnScoreUpdateListener(OnScoreUpdateListener listener) {
        mListener = listener;
    }


    /****************************  C I B L E  *************************/
    /*Permet de renvoyer les Items Cricket*/
    public List<CricketValueItem> getValuesItem()
    {
        List<CricketValueItem> retour = new ArrayList<>();

        if(this.jeu.getType().equals(eGames.HiddenCricket)) {
            for (CricketValueItem item : this.ItemsCricket)
                if (!item.getHiddenItem())
                    retour.add(item);
        }
        else
        {
            retour = this.ItemsCricket;
        }

        return retour;
    }

    /****************************  C I B L E  *************************/
    /*Permet de retourner les valeurs touchées par le joueur*/
    public List<Integer> getCurrentPlayerTouch() {
        List<Integer> itemsVal = new ArrayList<>();

        for(CricketValueItem item : this.ItemsCricket)
            if(!item.getHiddenItem())
                itemsVal.add(item.getIntLibelleItem());
       return itemsVal;
    }

    /****************************  C I B L E  *************************/
    /*Permet de retourner les valeurs fermées uniquement par ce joueur*/
    public List<Integer> getClosedPlayerValues()
    {
        List<Integer> fermePlayerVal = new ArrayList<>();
        Map<Integer, Integer> val = this.joueurCourant.getValues();

        for(CricketValueItem numItem : this.ItemsCricket) {
            if(this.jeu.getType().equals(eGames.OriginalCricket) || this.jeu.getType().equals(eGames.RandomCricket) || this.jeu.getType().equals(eGames.HiddenCricket) )
            {
                if(val.get(numItem.getNumItem()).intValue() >= 3 )
                {
                    if(!numItem.getHiddenItem() && !this.getClosedValues().contains(numItem.getIntLibelleItem()))
                        fermePlayerVal.add(numItem.getIntLibelleItem());
                }
            }
        }

        return fermePlayerVal;
    }

    /****************************  C I B L E  *************************/
    /*Permet de retourner les valeurs fermées par tout le monde*/
    public List<Integer> getClosedValues() {
        List<Integer> fermeVal = new ArrayList<>();

        for(CricketValueItem numItem : this.ItemsCricket)
            if(!numItem.getAvailable())
                fermeVal.add(numItem.getIntLibelleItem());

        return fermeVal;
    }
}
