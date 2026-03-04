package models.gamesModels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import models.commonModels.LanceItem;

public class ShootOutPlayerItem implements Parcelable{
    private Long id;
    private String name;
    private Integer score;
    private boolean isSelected;
    private Integer tour;
    private List<Integer> listTouche;
    private Integer multiplicateur;
    private LanceItem lance;

    public ShootOutPlayerItem(Long id, String name)
    {
        this.id = id;
        this.name = name;
        this.score = 0;
        this.multiplicateur = 1;
        this.tour = 1;
        this.listTouche = new ArrayList<>();
    }

    protected ShootOutPlayerItem(Parcel in) {
        id = in.readLong();
        name = in.readString();
        score = in.readInt();
    }

    public Long getId(){return id;}

    public String getName(){return name;}
    public List<Integer> getTouche()
    {
        return this.listTouche;
    }
    public boolean getSelected(){
        return this.isSelected;
    }

    public void removeLastTouche()
    {
        this.listTouche.remove(this.listTouche.size()-1);
    }

    public Integer getTour(){return tour;}

    public Integer getScore(){return score;}

    public Integer getMultiplicateur(){
        return this.multiplicateur;
    }

    public String getInfos(){return name + " : "+ String.valueOf(this.score) + " (x"+ this.multiplicateur + ")";}

    public void InitialiseLance(int i, int idPartie, Long id) {
        this.lance = new LanceItem(i,idPartie,id,-1,-1,-1,-1);
    }

    public void setLance(Integer value,Integer score,String valeur,boolean ouvre)
    {
        if(value == 1) {
            this.lance.tir_un = score;
            this.lance.str_tir_un = valeur;
            this.lance.ouverture_un = ouvre;
        }
        if(value == 2) {
            this.lance.tir_deux = score;
            this.lance.str_tir_deux = valeur;
            this.lance.ouverture_deux = ouvre;
        }
        if(value == 3) {
            this.lance.tir_trois = score;
            this.lance.str_tir_trois = valeur;
            this.lance.ouverture_trois = ouvre;
        }
    }

    public LanceItem getLance()
    {
        return this.lance;
    }

    public void setTouche(Integer item)
    {
        this.listTouche.add(item);
    }

    public void setScore(Integer score){this.score = score;}

    public void setTour(Integer tour){this.tour = tour;}
    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public void upMultiplicateur()
    {
        this.multiplicateur += 1;
    }

    public void downMultiplicateur(){ this.multiplicateur -= 1;}
    public boolean isSelected() {
        return isSelected;
    }



    public static final Creator<ShootOutPlayerItem> CREATOR = new Creator<ShootOutPlayerItem>() {
        @Override
        public ShootOutPlayerItem createFromParcel(Parcel in) {
            return new ShootOutPlayerItem(in);
        }

        @Override
        public ShootOutPlayerItem[] newArray(int size) {
            return new ShootOutPlayerItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(score);
    }
}