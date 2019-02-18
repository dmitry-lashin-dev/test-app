package com.example.testapp.mvp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MarkerModel implements Parcelable {

    private double lat;
    private double lng;
    private int number;

    public MarkerModel() {
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeInt(this.number);
    }

    protected MarkerModel(Parcel in) {
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.number = in.readInt();
    }

    public static final Parcelable.Creator<MarkerModel> CREATOR = new Parcelable.Creator<MarkerModel>() {
        @Override
        public MarkerModel createFromParcel(Parcel source) {return new MarkerModel(source);}

        @Override public MarkerModel[] newArray(int size) {return new MarkerModel[size];}
    };
}
