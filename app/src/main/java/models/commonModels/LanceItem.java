package models.commonModels;

import java.util.ArrayList;
import java.util.List;

public class LanceItem {

    public int id;
    public int idPartie;
    public Long idJoueur;
    public int tir_un;
    public int tir_deux;
    public int tir_trois;
    public int tir_quatre;

    public String str_tir_un;
    public String str_tir_deux;
    public String str_tir_trois;
    public String str_tir_quatre;

    public boolean ouverture_un;
    public boolean ouverture_deux;
    public boolean ouverture_trois;
    public boolean ouverture_quatre;

    public LanceItem(int id,int idPartie,Long idJoueur, int tir_un, int tir_deux, int tir_trois, int tir_quatre)
    {
        this.id = id;
        this.idPartie = idPartie;
        this.idJoueur = idJoueur;
        this.tir_un = tir_un;
        this.tir_deux = tir_deux;
        this.tir_trois = tir_trois;
        this.tir_quatre = tir_quatre;
    }

    public Integer getFlechette(int value)
    {
        if(value == 1)
            return this.tir_un;
        if(value == 2)
            return this.tir_deux;
        if(value == 3)
            return this.tir_trois;
        if(value == 4)
            return this.tir_quatre;

        return null;
    }

    public List<Integer> getAllFlechette(){
        List<Integer> values = new ArrayList<>();
        values.add(this.tir_un);
        values.add(this.tir_deux);
        values.add(this.tir_trois);
        values.add(this.tir_quatre);
        return values;
    }

    public List<String> getAllStrFlechette(){
        List<String> values = new ArrayList<>();
        values.add(this.str_tir_un);
        values.add(this.str_tir_deux);
        values.add(this.str_tir_trois);
        values.add(this.str_tir_quatre);
        return values;
    }

}
