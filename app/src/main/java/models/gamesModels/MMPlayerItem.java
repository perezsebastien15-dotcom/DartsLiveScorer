package models.gamesModels;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import models.commonModels.LanceItem;

public class MMPlayerItem implements Parcelable {

    private Long id;
    private String name;
    private Integer score;
    private Integer tour;
    private LanceItem lance;

    public MMPlayerItem(Long id, String name, Integer score, Integer tour)
    {
        this.id = id;
        this.name = name;
        this.score = score;
        this.tour = tour;
    }

    protected MMPlayerItem(Parcel in) {
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
        if (in.readByte() == 0) {
            tour = null;
        } else {
            tour = in.readInt();
        }
    }

    public static final Creator<MMPlayerItem> CREATOR = new Creator<MMPlayerItem>() {
        @Override
        public MMPlayerItem createFromParcel(Parcel in) {
            return new MMPlayerItem(in);
        }

        @Override
        public MMPlayerItem[] newArray(int size) {
            return new MMPlayerItem[size];
        }
    };


    public Long getId(){return id;}

    public String getName(){return name;}

    public Integer getScore(){return score;}

    public Integer getTour(){return tour;}

    public LanceItem getLance()
    {
        return this.lance;
    }
    public String getInfos(){return name + " : "+ String.valueOf(this.score);}
    public void setLance(Integer value,Integer score,String valeur)
    {
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
