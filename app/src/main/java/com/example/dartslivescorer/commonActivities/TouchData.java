package com.example.dartslivescorer.commonActivities;

import android.os.Parcel;
import android.os.Parcelable;

public class TouchData implements Parcelable {
    private int[] touchedItems;
    private int[] closedItems;
    private int[] cricketItems;
    private int[] closeSoloItems;
    public TouchData(int[] touchedItems) {
        this.touchedItems = touchedItems;
    }

    protected TouchData(Parcel in) {
        touchedItems = in.createIntArray();
        closedItems = in.createIntArray();
        cricketItems = in.createIntArray();
        closeSoloItems = in.createIntArray();
    }

    public static final Creator<TouchData> CREATOR = new Creator<TouchData>() {
        @Override
        public TouchData createFromParcel(Parcel in) {
            return new TouchData(in);
        }

        @Override
        public TouchData[] newArray(int size) {
            return new TouchData[size];
        }
    };

    public TouchData(int[] touchInt, int[] closeInt,int[] itemsInt,int[] closeSolo) {
        this.touchedItems = touchInt;
        this.closedItems = closeInt;
        this.cricketItems = itemsInt;
        this.closeSoloItems = closeSolo;
    }

    public int[] getTouchedItems() {
        return touchedItems;
    }
    public int[] getClosedItems(){ return closedItems;}
    public int[] getCricketItems(){ return cricketItems;}
    public int[] getCloseSoloItems(){ return closeSoloItems;}
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(touchedItems);
        dest.writeIntArray(closedItems);
        dest.writeIntArray(cricketItems);
        dest.writeIntArray(closeSoloItems);
    }
}
