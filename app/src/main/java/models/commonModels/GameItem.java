package models.commonModels;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.dartslivescorer.enums.eGames;

public class GameItem implements Parcelable{
    private eGames type;
    private String name;
    private int nb_tours;
    private int score;

    public GameItem(eGames type, String name, Integer tours, Integer score)
    {
        this.name = name;
        this.nb_tours = tours;
        this.score = score;
        this.type = type;
    }
    public Integer getTours(){return nb_tours;}
    public eGames getType(){return type;}
    public String getName(){return name;}
    public Integer getScore(){return score;}

    // Méthodes nécessaires pour Parcelable
    protected GameItem(Parcel in) {
        name = in.readString();
        nb_tours = in.readInt();
        score = in.readInt();
        type = eGames.valueOf(in.readString());
    }

    public static final Parcelable.Creator<GameItem> CREATOR = new Parcelable.Creator<GameItem>() {
        @Override
        public GameItem createFromParcel(Parcel in) {
            return new GameItem(in);
        }

        @Override
        public GameItem[] newArray(int size) {
            return new GameItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(nb_tours);
        dest.writeInt(score);
        dest.writeString(String.valueOf(type));
    }
}
