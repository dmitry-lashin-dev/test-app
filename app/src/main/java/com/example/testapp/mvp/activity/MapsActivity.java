package com.example.testapp.mvp.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.testapp.R;
import com.example.testapp.mvp.model.ClusterHelperModel;
import com.example.testapp.mvp.presenter.MapsPresenter;
import com.example.testapp.mvp.utils.CustomAlgorithm;
import com.example.testapp.mvp.view.MapsView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.List;


import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends MvpAppCompatActivity implements OnMapReadyCallback, MapsView {

    private static final float MAP_ZOOM = 4.7f;

    @InjectPresenter MapsPresenter mPresenter;

    private GoogleMap mMap;
    private ClusterManager<ClusterHelperModel> mClusterManager;
    private float mDensity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        mDensity = getResources().getDisplayMetrics().density;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManager.setAlgorithm(new CustomAlgorithm<ClusterHelperModel>());
        mMap.setOnCameraIdleListener(mClusterManager);
        mPresenter.createDataForZoomMap();
    }

    @Override public void zoomMap(@NonNull LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOM));
        mPresenter.generateClusterModelForMarker();
    }

    @Override public void showRandomMarker(@NonNull List<ClusterHelperModel> models) {
        if (mMap == null) return;
        mMap.clear();
        mClusterManager.clearItems();
        mClusterManager.setRenderer(new ClusterRender(this, mMap, mClusterManager));
        for (ClusterHelperModel model : models) {
            mClusterManager.addItem(model);
        }
        mClusterManager.cluster();
    }

    @OnClick(R.id.refresh_btn)
    public void onClickRefresh() {
        mPresenter.generateClusterModelForMarker();
    }

    private class ClusterRender extends DefaultClusterRenderer<ClusterHelperModel> {

        private IconGenerator mIconGenerator;
        private Context mContext;

        public ClusterRender(Context context, GoogleMap map, ClusterManager<ClusterHelperModel> clusterManager) {
            super(context, map, clusterManager);
            mContext = context;
            mIconGenerator = new IconGenerator(context);
        }

        @Override
        protected void onClusterRendered(Cluster<ClusterHelperModel> cluster, Marker marker) {
            super.onClusterRendered(cluster, marker);
        }

        @Override
        protected void onBeforeClusterItemRendered(ClusterHelperModel item, MarkerOptions markerOptions) {
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(item.getTitle()
            ));
            markerOptions.icon(bitmapDescriptor);
            markerOptions.title(item.getTitle());
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<ClusterHelperModel> cluster, MarkerOptions markerOptions) {
            final Drawable clusterIcon = getResources().getDrawable(R.drawable.ic_red_round);
            mIconGenerator.setBackground(clusterIcon);
            mIconGenerator.setTextAppearance(mContext, R.style.ClusterText);
            mIconGenerator.setContentPadding(getPadding(6), getPadding(3),
                    getPadding(3), getPadding(2));
            Bitmap icon = mIconGenerator.makeIcon(mPresenter.calculateAverage((List<ClusterHelperModel>) cluster.getItems()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override protected boolean shouldRenderAsCluster(Cluster<ClusterHelperModel> cluster) {
            return cluster.getSize() > 1;
        }

        private Bitmap getMarkerBitmapFromView(@NonNull String numberText) {

            View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.custom_marker_layout, null);
            TextView markerText = customMarkerView.findViewById(R.id.number_tv);
            markerText.setText(numberText);
            ImageView markerImageView = customMarkerView.findViewById(R.id.marker_image_iv);
            markerImageView.setImageResource(R.drawable.ic_red_round);
            customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
            customMarkerView.buildDrawingCache();
            Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
            Drawable drawable = customMarkerView.getBackground();
            if (drawable != null)
                drawable.draw(canvas);
            customMarkerView.draw(canvas);
            return returnedBitmap;
        }

        private int getPadding(int var) {
            return (int) (var * mDensity);
        }
    }
}
