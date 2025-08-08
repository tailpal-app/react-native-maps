package com.rnmaps.fabric;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.viewmanagers.RNMapsHeatmapManagerDelegate;
import com.facebook.react.viewmanagers.RNMapsHeatmapManagerInterface;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.rnmaps.maps.MapHeatmap;

@ReactModule(name = HeatmapManager.REACT_CLASS)
public class HeatmapManager extends ViewGroupManager<MapHeatmap> implements RNMapsHeatmapManagerInterface<MapHeatmap> {

    private final RNMapsHeatmapManagerDelegate<MapHeatmap, HeatmapManager> delegate =
            new RNMapsHeatmapManagerDelegate<>(this);

    @Override
    public ViewManagerDelegate<MapHeatmap> getDelegate() {
        return delegate;
    }

    public HeatmapManager(ReactApplicationContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    public static final String REACT_CLASS = "RNMapsHeatmap";


    @Override
    public MapHeatmap createViewInstance(ThemedReactContext context) {
        return new MapHeatmap(context);
    }

    @Override
    public void setGradient(MapHeatmap view, @Nullable ReadableMap value) {
        ReadableArray srcColors = value.getArray("colors");
        int[] colors = new int[srcColors.size()];
        for (int i = 0; i < srcColors.size(); i++) {
            colors[i] = srcColors.getInt(i);
        }

        ReadableArray srcStartPoints = value.getArray("startPoints");
        float[] startPoints = new float[srcStartPoints.size()];
        for (int i = 0; i < srcStartPoints.size(); i++) {
            startPoints[i] = (float)srcStartPoints.getDouble(i);
        }

        if (value.hasKey("colorMapSize")) {
            int colorMapSize = value.getInt("colorMapSize");
            view.setGradient(new Gradient(colors, startPoints, colorMapSize));
        } else {
            view.setGradient(new Gradient(colors, startPoints));
        }
    }

    @Override
    public void setOpacity(MapHeatmap view, double value) {
        view.setOpacity(value);
    }

    @Override
    public void setPoints(MapHeatmap view, @Nullable ReadableArray value) {
        WeightedLatLng[] p = new WeightedLatLng[value.size()];
        for (int i = 0; i < value.size(); i++) {
            ReadableMap point = value.getMap(i);
            WeightedLatLng weightedLatLng;
            LatLng latLng = new LatLng(point.getDouble("latitude"), point.getDouble("longitude"));
            if (point.hasKey("weight")) {
                weightedLatLng = new WeightedLatLng(latLng, point.getDouble("weight"));
            } else {
                weightedLatLng = new WeightedLatLng(latLng);
            }
            p[i] = weightedLatLng;
        }
        view.setPoints(p);
    }

    @Override
    public void setRadius(MapHeatmap view, double value) {
        view.setRadius((int) value);
    }
}