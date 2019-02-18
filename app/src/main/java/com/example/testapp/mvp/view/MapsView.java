package com.example.testapp.mvp.view;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.MvpView;
import com.example.testapp.mvp.model.ClusterHelperModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface MapsView extends MvpView {

    void zoomMap(@NonNull LatLng latLng);

    void showRandomMarker(@NonNull List<ClusterHelperModel> models);
}
