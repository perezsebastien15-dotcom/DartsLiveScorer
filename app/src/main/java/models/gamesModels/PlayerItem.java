package models.gamesModels;
import android.os.Parcel;
import android.os.Parcelable;

public class PlayerItem implements Parcelable{
    private Long id;
    private String name;
    private Integer score;
    private boolean isSelected;
    private Integer tour;

    public PlayerItem(Long id, String name,int score)
    {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    protected PlayerItem(Parcel in) {
        id = in.readLong();
        name = in.readString();
        score = in.readInt();
    }

    public Long getId(){return id;}

    public String getName(){return name;}

    public boolean getSelected(){
        return this.isSelected;
    }

    public Integer getTour(){return tour;}

    public Integer getScore(){return score;}

    public void setScore(Integer score){this.score = score;}

    public void setTour(Integer tour){this.tour = tour;}

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public static final Creator<PlayerItem> CREATOR = new Creator<PlayerItem>() {
        @Override
        public PlayerItem createFromParcel(Parcel in) {
            return new PlayerItem(in);
        }

        @Override
        public PlayerItem[] newArray(int size) {
            return new PlayerItem[size];
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