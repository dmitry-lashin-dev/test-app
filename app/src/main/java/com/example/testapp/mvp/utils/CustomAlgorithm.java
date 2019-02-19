package com.example.testapp.mvp.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.algo.Algorithm;
import com.google.maps.android.clustering.algo.StaticCluster;
import com.google.maps.android.geometry.Bounds;
import com.google.maps.android.geometry.Point;
import com.google.maps.android.projection.SphericalMercatorProjection;
import com.google.maps.android.quadtree.PointQuadTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomAlgorithm<T extends ClusterItem> implements Algorithm<T> {

    public static final int MAX_DISTANCE_AT_ZOOM = 100;
    private final Collection<CustomAlgorithm.QuadItem<T>> mItems = new ArrayList();
    private final PointQuadTree<CustomAlgorithm.QuadItem<T>> mQuadTree = new PointQuadTree(0.0D,
            1.0D, 0.0D, 1.0D);
    private static final SphericalMercatorProjection PROJECTION = new SphericalMercatorProjection(1.0D);

    public CustomAlgorithm() {
    }

    public void addItem(T item) {
        CustomAlgorithm.QuadItem<T> quadItem = new CustomAlgorithm.QuadItem(item);
        PointQuadTree var3 = this.mQuadTree;
        synchronized (this.mQuadTree) {
            this.mItems.add(quadItem);
            this.mQuadTree.add(quadItem);
        }
    }

    public void addItems(Collection<T> items) {
        Iterator var2 = items.iterator();

        while (var2.hasNext()) {
            T item = (T) var2.next();
            this.addItem(item);
        }

    }

    public void clearItems() {
        PointQuadTree var1 = this.mQuadTree;
        synchronized (this.mQuadTree) {
            this.mItems.clear();
            this.mQuadTree.clear();
        }
    }

    public void removeItem(T item) {
        CustomAlgorithm.QuadItem<T> quadItem = new CustomAlgorithm.QuadItem(item);
        PointQuadTree var3 = this.mQuadTree;
        synchronized (this.mQuadTree) {
            this.mItems.remove(quadItem);
            this.mQuadTree.remove(quadItem);
        }
    }

    public Set<? extends Cluster<T>> getClusters(double zoom) {
        int discreteZoom = (int) (zoom * 1.35);
        double zoomSpecificSpan = 60.55D / Math.pow(2.0D, (double) discreteZoom) / 180.0D;
        Set<CustomAlgorithm.QuadItem<T>> visitedCandidates = new HashSet();
        Set<Cluster<T>> results = new HashSet();
        Map<QuadItem<T>, Double> distanceToCluster = new HashMap();
        Map<CustomAlgorithm.QuadItem<T>, StaticCluster<T>> itemToCluster = new HashMap();
        PointQuadTree var10 = this.mQuadTree;
        synchronized (this.mQuadTree) {
            Iterator var11 = this.mItems.iterator();

            while (true) {
                label46:
                while (true) {
                    CustomAlgorithm.QuadItem candidate;
                    do {
                        if (!var11.hasNext()) {
                            return results;
                        }

                        candidate = (CustomAlgorithm.QuadItem) var11.next();
                    } while (visitedCandidates.contains(candidate));

                    Bounds searchBounds = this.createBoundsFromSpan(candidate.getPoint(), zoomSpecificSpan);
                    Collection<CustomAlgorithm.QuadItem<T>> clusterItems = this.mQuadTree.search(searchBounds);
                    if (clusterItems.size() == 1) {
                        results.add(candidate);
                        visitedCandidates.add(candidate);
                        distanceToCluster.put(candidate, 0.0D);
                    } else {
                        StaticCluster<T> cluster = new StaticCluster(candidate.mClusterItem.getPosition());
                        results.add(cluster);
                        Iterator var16 = clusterItems.iterator();

                        while (true) {
                            CustomAlgorithm.QuadItem clusterItem;
                            double distance;
                            while (true) {
                                if (!var16.hasNext()) {
                                    visitedCandidates.addAll(clusterItems);
                                    continue label46;
                                }

                                clusterItem = (CustomAlgorithm.QuadItem) var16.next();
                                Double existingDistance = (Double) distanceToCluster.get(clusterItem);
                                distance = this.distanceSquared(clusterItem.getPoint(), candidate.getPoint());
                                if (existingDistance == null) {
                                    break;
                                }

                                if (existingDistance >= distance) {
                                    ((StaticCluster) itemToCluster.get(clusterItem)).remove(clusterItem.mClusterItem);
                                    break;
                                }
                            }

                            distanceToCluster.put(clusterItem, distance);
                            cluster.add((T) clusterItem.mClusterItem);
                            itemToCluster.put(clusterItem, cluster);
                        }
                    }
                }
            }
        }
    }

    public Collection<T> getItems() {
        List<T> items = new ArrayList();
        PointQuadTree var2 = this.mQuadTree;
        synchronized (this.mQuadTree) {
            Iterator var3 = this.mItems.iterator();

            while (var3.hasNext()) {
                CustomAlgorithm.QuadItem<T> quadItem = (CustomAlgorithm.QuadItem) var3.next();
                items.add(quadItem.mClusterItem);
            }

            return items;
        }
    }

    private double distanceSquared(Point a, Point b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
    }

    private Bounds createBoundsFromSpan(Point p, double span) {
        double halfSpan = span / 2.0D;
        return new Bounds(p.x - halfSpan, p.x + halfSpan, p.y - halfSpan, p.y + halfSpan);
    }

    private static class QuadItem<T extends ClusterItem> implements PointQuadTree.Item, Cluster<T> {
        private final T mClusterItem;
        private final Point mPoint;
        private final LatLng mPosition;
        private Set<T> singletonSet;

        private QuadItem(T item) {
            this.mClusterItem = item;
            this.mPosition = item.getPosition();
            this.mPoint = CustomAlgorithm.PROJECTION.toPoint(this.mPosition);
            this.singletonSet = Collections.singleton(this.mClusterItem);
        }

        public Point getPoint() {
            return this.mPoint;
        }

        public LatLng getPosition() {
            return this.mPosition;
        }

        public Set<T> getItems() {
            return this.singletonSet;
        }

        public int getSize() {
            return 1;
        }

        public int hashCode() {
            return this.mClusterItem.hashCode();
        }

        public boolean equals(Object other) {
            return !(other instanceof CustomAlgorithm.QuadItem) ? false : ((CustomAlgorithm.QuadItem) other).mClusterItem.equals(this.mClusterItem);
        }
    }
}
