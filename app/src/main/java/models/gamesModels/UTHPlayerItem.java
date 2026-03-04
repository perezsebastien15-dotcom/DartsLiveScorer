package models.gamesModels;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.dartslivescorer.enums.eHats;

import models.commonModels.LanceItem;

public class UTHPlayerItem implements Parcelable {

    private Long id;
    private String name;
    private Integer score;
    private boolean isSelected;
    private Integer tour;
    private LanceItem lance;
    private Integer nb_chapeaux;
    private eHats chapeau;
    private boolean vivant;

    public UTHPlayerItem(Long id, String name, Integer score, Integer tour, Integer nb_chapeaux, Integer typeChap)
    {
        this.id = id;
        this.name = name;
        this.score = score;
        this.tour = tour;
        this.nb_chapeaux = nb_chapeaux;
        this.vivant = true;
        this.InitChap(typeChap);
    }

    private void InitChap(Integer typeChap) {
        switch(typeChap)
        {
            case 1:
                this.chapeau = eHats.chapeau_ba;
                break;
            case 2:
                this.chapeau = eHats.chapeau_dv;
                break;
            case 3:
                this.chapeau = eHats.chapeau_hp;
                break;
            case 4:
                this.chapeau = eHats.chapeau_ij;
                break;
            case 5:
                this.chapeau = eHats.chapeau_ma;
                break;
            case 6:
                this.chapeau = eHats.chapeau_mk;
                break;
            case 7 :
                this.chapeau = eHats.chapeau_pi;
                break;
            case 8 :
                this.chapeau = eHats.chapeau_re;
                break;
            case 9:
                this.chapeau = eHats.chapeau_wt;
                break;
        }
    }

    public boolean getVivant()
    {
        return this.vivant;
    }

    public void setVivant(boolean viv){
        this.vivant = viv;
    }

    protected UTHPlayerItem(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        name = in.readString();
        if (in.readByte() == 0) {
            score = null;
        } else {
            score = in.readInt();
        }
        isSelected = in.readByte() != 0;
        if (in.readByte() == 0) {
            tour = null;
        } else {
            tour = in.readInt();
        }
    }

    public static final Creator<UTHPlayerItem> CREATOR = new Creator<UTHPlayerItem>() {
        @Override
        public UTHPlayerItem createFromParcel(Parcel in) {
            return new UTHPlayerItem(in);
        }

        @Override
        public UTHPlayerItem[] newArray(int size) {
            return new UTHPlayerItem[size];
        }
    };


    public Long getId(){return id;}

    public String getName(){return name;}

    public Integer getScore(){return score;}
    public String getInfos(){return name;}
    public Integer getTour(){return tour;}
    public Integer getNb_chapeaux(){return this.nb_chapeaux;}
    public eHats getChapeau(){return this.chapeau;}

    public LanceItem getLance()
    {
        return this.lance;
    }


    public void setLance(Integer value,Integer score,String valeur)    {
        if(value == 1) {
            this.lance.tir_un = score;
            this.lance.str_tir_un = valeur;
        }
        if(value == 2) {
            this.lance.tir_deux = score;
            this.lance.str_tir_deux = valeur;
        }
        if(value == 3) {
            this.lance.tir_trois = score;
            this.lance.str_tir_trois = valeur;
        }
    }

    public void setScore(Integer score){this.score = score;}
    public void setTour(Integer tour){this.tour = tour;}
    public void setNb_chapeaux(Integer nb_chapeaux){this.nb_chapeaux = nb_chapeaux;}


    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(name);
        if (score == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(score);
        }
        dest.writeByte((byte) (isSelected ? 1 : 0));
        if (tour == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(tour);
        }
    }

    public void InitialiseLance(int i, int idPartie, Long id) {
        this.lance = new LanceItem(i,idPartie,id,-1,-1,-1,-1);
    }
}
