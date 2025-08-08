package com.rnmaps.fabric;


import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.viewmanagers.RNMapsMapViewManagerDelegate;
import com.facebook.react.viewmanagers.RNMapsMapViewManagerInterface;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.rnmaps.maps.MapMarker;
import com.rnmaps.maps.MapView;
import com.rnmaps.maps.SizeReportingShadowNode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@ReactModule(name = MapViewManager.REACT_CLASS)
public class MapViewManager extends ViewGroupManager<MapView> implements RNMapsMapViewManagerInterface<MapView> {

    private static boolean rendererInitialized = false;
    private final RNMapsMapViewManagerDelegate<MapView, MapViewManager> delegate =
            new RNMapsMapViewManagerDelegate<>(this);


    public MapViewManager(ReactApplicationContext context) {
        super(context);
    }

    @Override
    public ViewManagerDelegate<MapView> getDelegate() {
        return delegate;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public MapView createViewInstance(ThemedReactContext context) {
        return new MapView(context);
    }

    @Override
    public int getChildCount(MapView view) {
        return view.getFeatureCount();
    }

    @Override
    public View getChildAt(MapView view, int index) {
        return view.getFeatureAt(index);
    }

    @Override
    public void removeViewAt(MapView parent, int index) {
        parent.removeFeatureAt(index);
    }

    @Override
    protected void setupViewRecycling() {
        // override parent to block recycling / allow reliable GoogleMapsOptions passing
    }

    public static final String REACT_CLASS = "RNMapsMapView";

    @Override
    public LayoutShadowNode createShadowNodeInstance() {
        // A custom shadow node is needed in order to pass back the width/height of the map to the
        // view manager so that it can start applying camera moves with bounds.
        return new SizeReportingShadowNode();
    }

    @Override
    public void setCacheEnabled(MapView view, boolean value) {
        view.setCacheEnabled(value);
    }

    @Override
    public void setCamera(MapView view, @Nullable ReadableMap value) {
        view.setCamera(value);
    }

    @Override
    public void setCompassOffset(MapView view, @Nullable ReadableMap value) {
        // not supported
    }

    @Override
    public void setFollowsUserLocation(MapView view, boolean value) {
        // not supported
    }

    @Override
    public void setPoiClickEnabled(MapView view, boolean value) {
        view.setPoiClickEnabled(value);
    }

    @Override
    public void setInitialCamera(MapView view, @Nullable ReadableMap value) {
        CameraPosition camera = MapView.cameraPositionFromMap(value);
        if (camera != null) {
            view.setInitialCameraSet(true);
        }
    }


    @Override
    public void setInitialRegion(MapView view, @Nullable ReadableMap value) {
        view.setInitialRegion(value);
    }

    @Override
    public void setKmlSrc(MapView view, @Nullable String value) {
        view.setKmlSrc(value);
    }

    @Override
    public void setLegalLabelInsets(MapView view, @Nullable ReadableMap value) {
        // not supported
    }

    @Override
    public void setLiteMode(MapView view, boolean value) {
        // do nothing (initialProp)
    }

    @Override
    public void setGoogleMapId(MapView view, @Nullable String value) {
        // do nothing (initialProp)
    }

    @Override
    public void setGoogleRenderer(MapView view, @Nullable String value) {
        if (!rendererInitialized) {
            MapsInitializer.Renderer renderer = MapsInitializer.Renderer.LATEST;
            if ("LEGACY".equals(value)) {
                renderer = MapsInitializer.Renderer.LEGACY;
            }
            MapsInitializer.initialize(getReactApplicationContext(), renderer, r -> Log.d("AirMapRenderer", "Init with renderer: " + r));
            rendererInitialized = true;
        }

    }

    @Override
    public void setLoadingBackgroundColor(MapView view, @Nullable Integer value) {
        view.setLoadingBackgroundColor(value);
    }

    @Override
    public void setLoadingEnabled(MapView view, boolean value) {
        view.setLoadingEnabled(value);
    }

    @Override
    public void setLoadingIndicatorColor(MapView view, @Nullable Integer value) {
        view.setLoadingIndicatorColor(value);
    }

    @Override
    public void setMapPadding(MapView view, @Nullable ReadableMap padding) {
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        double density = (double) view.getResources().getDisplayMetrics().density;

        if (padding != null) {
            if (padding.hasKey("left")) {
                left = (int) (padding.getDouble("left") * density);
            }

            if (padding.hasKey("top")) {
                top = (int) (padding.getDouble("top") * density);
            }

            if (padding.hasKey("right")) {
                right = (int) (padding.getDouble("right") * density);
            }

            if (padding.hasKey("bottom")) {
                bottom = (int) (padding.getDouble("bottom") * density);
            }
        }

        view.applyBaseMapPadding(left, top, right, bottom);
    }

    @Override
    public void addView(MapView parent, View child, int index) {
        if (child instanceof MapMarker && ((MapMarker) child).isLoadingImage()) {
            ((MapMarker) child).setImageLoadedListener((uri, drawable, b) -> {
                parent.addFeature(child, parent.getFeatureCount());
            });
        } else {
            parent.addFeature(child, index);
        }
    }

    @Override
    public void setMapType(MapView view, @Nullable String value) {
        view.setMapType(mapTypeFromStrValue(value));
    }

    private static int mapTypeFromStrValue(String value) {
        int mapType;
        //hybrid | none | satellite | standard | terrain
        if ("hybrid".equals(value)) {
            mapType = GoogleMap.MAP_TYPE_HYBRID;
        } else if ("none".equals(value)) {
            mapType = GoogleMap.MAP_TYPE_NONE;
        } else if ("satellite".equals(value)) {
            mapType = GoogleMap.MAP_TYPE_SATELLITE;
        } else if ("standard".equals(value)) {
            mapType = GoogleMap.MAP_TYPE_NORMAL;
        } else if ("terrain".equals(value)) {
            mapType = GoogleMap.MAP_TYPE_TERRAIN;
        } else {
            mapType = GoogleMap.MAP_TYPE_NORMAL;
        }
        return mapType;
    }

    @Override
    public void setMaxDelta(MapView view, double value) {
        // not supported
    }

    @Override
    public void setMaxZoom(MapView view, float value) {
        view.setMaxZoomLevel(value);
    }

    @Override
    public void setMinDelta(MapView view, double value) {
        // not supported
    }

    @Override
    public void setMinZoom(MapView view, float value) {
        view.setMinZoomLevel(value);
    }

    @Override
    public void setMoveOnMarkerPress(MapView view, boolean value) {
        view.setMoveOnMarkerPress(value);
    }

    @Override
    public void setHandlePanDrag(MapView view, boolean value) {
        view.setHandlePanDrag(value);
    }

    @Override
    public void setPaddingAdjustmentBehavior(MapView view, @Nullable String value) {
        // not supported
    }

    @Override
    public void setPitchEnabled(MapView view, boolean value) {
        view.setPitchEnabled(value);
    }

    @Override
    public void setRegion(MapView view, @Nullable ReadableMap value) {
        view.setRegion(value);
    }

    @Override
    public void setRotateEnabled(MapView view, boolean value) {
        view.setRotateEnabled(value);
    }

    @Override
    public void setScrollDuringRotateOrZoomEnabled(MapView view, boolean value) {
        view.setScrollDuringRotateOrZoomEnabled(value);
    }

    @Override
    public void setScrollEnabled(MapView view, boolean value) {
        view.setScrollEnabled(value);
    }

    @Override
    public void setShowsBuildings(MapView view, boolean value) {
        view.setShowBuildings(value);
    }

    @Override
    public void setShowsCompass(MapView view, boolean value) {
        view.setShowsCompass(value);
    }

    @Override
    public void setShowsIndoorLevelPicker(MapView view, boolean value) {
        view.setShowsIndoorLevelPicker(value);
    }

    @Override
    public void setShowsIndoors(MapView view, boolean value) {
        view.setShowIndoors(value);
    }

    @Override
    public void setShowsMyLocationButton(MapView view, boolean value) {
        view.setShowsMyLocationButton(value);
    }

    @Override
    public void setShowsScale(MapView view, boolean value) {
        // not supported
    }

    @Override
    public void setShowsUserLocation(MapView view, boolean value) {
        view.setShowsUserLocation(value);
    }

    @Override
    public void setTintColor(MapView view, @Nullable Integer value) {
        // not supported
    }

    @Override
    public void setToolbarEnabled(MapView view, boolean value) {
        view.setToolbarEnabled(value);
    }

    @Override
    public void setUserInterfaceStyle(MapView view, @Nullable String value) {
        // do nothing (initialProp)
    }

    @Override
    public void setCustomMapStyleString(MapView view, @Nullable String value) {
        view.setMapStyle(value);
    }

    @Override
    public void setUserLocationAnnotationTitle(MapView view, @Nullable String value) {
        // not supported
    }

    @Override
    public void setUserLocationCalloutEnabled(MapView view, boolean value) {
        // not supported
    }

    @Override
    public void setUserLocationFastestInterval(MapView view, int value) {
        view.setUserLocationFastestInterval(value);
    }

    @Override
    public void setUserLocationPriority(MapView view, @Nullable String value) {
        ///  TODO: map in js
        int priority = switch (value != null ? value : "balanced") {
            case "high" -> Priority.PRIORITY_HIGH_ACCURACY;
            case "low" -> Priority.PRIORITY_LOW_POWER;
            case "passive" -> Priority.PRIORITY_PASSIVE;
            default -> Priority.PRIORITY_BALANCED_POWER_ACCURACY;
        };
        view.setUserLocationPriority(priority);
    }

    @Override
    public void setUserLocationUpdateInterval(MapView view, int value) {
        view.setUserLocationUpdateInterval(value);
    }

    @Override
    public void setZoomControlEnabled(MapView view, boolean value) {
        view.setZoomControlEnabled(value);
    }

    @Override
    public void setZoomEnabled(MapView view, boolean value) {
        view.setZoomEnabled(value);
    }

    @Override
    public void setShowsTraffic(MapView view, boolean value) {
        view.setShowsTraffic(value);
    }

    @Override
    public void setZoomTapEnabled(MapView view, boolean value) {
        // not supported
    }

    @Override
    public void setCameraZoomRange(MapView view, @Nullable ReadableMap value) {
        // not supported
    }

    @Override
    public void animateToRegion(MapView view, String regionJSON, int duration) {
        try {
            JSONObject region = new JSONObject(regionJSON);
            double lng = region.getDouble("longitude");
            double lat = region.getDouble("latitude");
            double lngDelta = region.getDouble("longitudeDelta");
            double latDelta = region.getDouble("latitudeDelta");
            LatLngBounds bounds = new LatLngBounds(
                    new LatLng(lat - latDelta / 2, lng - lngDelta / 2), // southwest
                    new LatLng(lat + latDelta / 2, lng + lngDelta / 2)  // northeast
            );
            view.animateToRegion(bounds, duration);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void setCamera(MapView view, String cameraJSON) {
        try {
            JSONObject camera = new JSONObject(cameraJSON);
            CameraPosition position = view.cameraPositionFromJSON(camera);
            view.moveToCamera(position);
        } catch (JSONException e) {
            Log.e("MapViewManager", "parse camera exception " + e);
        }
    }

    @Override
    public void animateCamera(MapView view, String cameraJSON, int duration) {
        try {
            JSONObject camera = new JSONObject(cameraJSON);
            CameraPosition position = view.cameraPositionFromJSON(camera);
            view.animateToCamera(position, duration);
        } catch (JSONException e) {
            Log.e("MapViewManager", "parse camera exception " + e);
        }
    }

    @Override
    public void fitToElements(MapView view, String edgePaddingJSON, boolean animated) {
        try {
            WritableMap map = null;
            if (edgePaddingJSON != null) {
                JSONObject jsonObject = new JSONObject(edgePaddingJSON);
                map = JSONUtil.convertJsonToWritable(jsonObject);
            }
            view.fitToElements(map, animated);
        } catch (JSONException e) {
            Log.e("MapViewManager", "parse edgePaddingJSON exception " + e);
        }

    }

    @Override
    public void fitToSuppliedMarkers(MapView view, String markersJSON, String edgePaddingJSON, boolean animated) {
        try {
            WritableArray markers = null;
            if (markersJSON != null) {
                JSONArray array = new JSONArray(markersJSON);
                markers = JSONUtil.convertJsonArrayToWritable(array);
            }
            WritableMap edgePadding = null;
            if (edgePaddingJSON != null) {
                JSONObject jsonObject = new JSONObject(edgePaddingJSON);
                edgePadding = JSONUtil.convertJsonToWritable(jsonObject);
            }
            view.fitToSuppliedMarkers(markers, edgePadding, animated);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void fitToCoordinates(MapView view, String coordinatesJSON, String edgePaddingJSON, boolean animated) {
        try {
            WritableArray coordinates = null;
            if (coordinatesJSON != null) {
                JSONArray array = new JSONArray(coordinatesJSON);
                coordinates = JSONUtil.convertJsonArrayToWritable(array);
            }
            WritableMap edgePadding = null;
            if (edgePaddingJSON != null) {
                JSONObject jsonObject = new JSONObject(edgePaddingJSON);
                edgePadding = JSONUtil.convertJsonToWritable(jsonObject);
            }
            view.fitToCoordinates(coordinates, edgePadding, animated);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setIndoorActiveLevelIndex(MapView view, int activeLevelIndex) {
        view.setIndoorActiveLevelIndex(activeLevelIndex);
    }

    @Override
    public void onDropViewInstance(MapView view) {
        view.doDestroy();
        super.onDropViewInstance(view);
    }
}
