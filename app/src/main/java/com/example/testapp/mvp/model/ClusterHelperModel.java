package com.example.testapp.mvp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterHelperModel implements ClusterItem, Parcelable {

    private LatLng position;
    private String title;
    private String snippet;
    private MarkerModel mMarkerModel;

    public ClusterHelperModel(double lat, double lng, String title, String snippet, MarkerModel markerModel) {
        this.position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
        mMarkerModel = markerModel;
    }

    @Override public LatLng getPosition() {
        return position;
    }

    @Override public String getTitle() {
        return title;
    }

    @Override public String getSnippet() {
        return snippet;
    }

    public MarkerModel getMarkerModel() {
        return mMarkerModel;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.position, flags);
        dest.writeString(this.title);
        dest.writeString(this.snippet);
        dest.writeParcelable(this.mMarkerModel, flags);
    }

    protected ClusterHelperModel(Parcel in) {
        this.position = in.readParcelable(LatLng.class.getClassLoader());
        this.title = in.readString();
        this.snippet = in.readString();
        this.mMarkerModel = in.readParcelable(MarkerModel.class.getClassLoader());
    }

    public static final Parcelable.Creator<ClusterHelperModel> CREATOR = new Parcelable.Creator<ClusterHelperModel>() {
        @Override
        public ClusterHelperModel createFromParcel(Parcel source) {return new ClusterHelperModel(source);}

        @Override
        public ClusterHelperModel[] newArray(int size) {return new ClusterHelperModel[size];}
    };
}
