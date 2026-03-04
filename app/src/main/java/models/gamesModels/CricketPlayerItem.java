package models.gamesModels;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.commonModels.LanceItem;

public class CricketPlayerItem implements Parcelable {

    private Long id;
    private String name;
    private Integer score;
    private boolean isSelected;
    private Integer tour;
    private Map<Integer, Integer> values;
    private LanceItem lance;

    public CricketPlayerItem(Long id, String name, Integer score, Integer tour, List<CricketValueItem> val, Map<Integer, Integer> map)
    {
        this.id = id;
        this.name = name;
        this.score = score;
        this.tour = tour;

        this.values = new HashMap<>();

        if(map != null)
            this.values = map;
        else {
            for (CricketValueItem item : val)
                this.values.put(item.getNumItem(), 0);
        }
    }

    protected CricketPlayerItem(Parcel in) {
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

    public static final Creator<CricketPlayerItem> CREATOR = new Creator<CricketPlayerItem>() {
        @Override
        public CricketPlayerItem createFromParcel(Parcel in) {
            return new CricketPlayerItem(in);
        }

        @Override
        public CricketPlayerItem[] newArray(int size) {
            return new CricketPlayerItem[size];
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
    public Map<Integer, Integer> getValues()
    {
        if(this.values != null)
            return this.values;
        else
            return new HashMap<>();
    }

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
    public void setValues(Integer num, Integer nb)
    {
        this.values.put(num,nb);
    }

    public void setScore(Integer score){this.score = score;}

    public void setTour(Integer tour){this.tour = tour;}
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
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
