package com.example.testapp.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.testapp.mvp.model.ClusterHelperModel;
import com.example.testapp.mvp.model.MarkerModel;
import com.example.testapp.mvp.view.MapsView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@InjectViewState
public class MapsPresenter extends MvpPresenter<MapsView> {

    private static final double CENTER_LAT = 48.980410627489725;
    private static final double CENTER_LNG = 33.29531762748957;
    private static final double WEST_LAT = 49.682233908431904;
    private static final double WEST_LNG = 27.30910688638687;
    private static final double SOUTH_LAT = 46.939901872279606;
    private static final double SOUTH_LNG = 33.42568807303905;
    private static final double EAST_LAT = 48.06527934414339;
    private static final double EAST_LNG = 37.879710383713245;
    private static final int COUNT_OF_MARKER = 100;
    private static final int RANDOM_BOUND = 2;
    private static final int RANDOM_BOUND_FOR_TITLE = 10;

    public void createDataForZoomMap() {
        LatLng latLng = new LatLng(CENTER_LAT, CENTER_LNG);
        getViewState().zoomMap(latLng);
    }

    private List<MarkerModel> generateRandomMarkerModel() {
        List<MarkerModel> markerModels = new ArrayList<>();
        for (int i = 0; i < COUNT_OF_MARKER; i++) {
            MarkerModel model = new MarkerModel();
            if (i % 4 == 0) {
                model.setLat(getRandomLat(CENTER_LAT));
                model.setLng(getRandomLng(CENTER_LNG));
            } else if (i % 3 == 0) {
                model.setLat(getRandomLat(WEST_LAT));
                model.setLng(getRandomLng(WEST_LNG));
            } else if (i % 5 == 0) {
                model.setLat(getRandomLat(SOUTH_LAT));
                model.setLng(getRandomLng(SOUTH_LNG));
            } else {
                model.setLat(getRandomLat(EAST_LAT));
                model.setLng(getRandomLng(EAST_LNG));
            }
            model.setNumber(getRandomNumber());
            markerModels.add(model);
        }
        return markerModels;
    }

    public void generateClusterModelForMarker() {
        List<ClusterHelperModel> clusterHelperModels = new ArrayList<>();
        List<MarkerModel> models = generateRandomMarkerModel();
        for (int i = 0; i < models.size(); i++) {
            MarkerModel markerModel = models.get(i);
            ClusterHelperModel model = new ClusterHelperModel(markerModel.getLat(), markerModel.getLng(),
                    String.valueOf(markerModel.getNumber()), "", markerModel);
            clusterHelperModels.add(model);
        }
        getViewState().showRandomMarker(clusterHelperModels);
    }

    private double getRandomLat(double startPosition) {
        Random random = new Random();
        return startPosition + random.nextDouble() + new Random().nextInt(RANDOM_BOUND);
    }

    private double getRandomLng(double startPosition) {
        Random random = new Random();
        return startPosition + random.nextDouble() - new Random().nextInt(RANDOM_BOUND);
    }

    private int getRandomNumber() {
        return new Random().nextInt(RANDOM_BOUND_FOR_TITLE);
    }

    public String calculateAverage(List<ClusterHelperModel> models) {
        int sum = 0;
        for (ClusterHelperModel model : models) {
            sum += model.getMarkerModel().getNumber();
        }
        int average = sum / models.size();
        return String.valueOf(average);
    }
}
