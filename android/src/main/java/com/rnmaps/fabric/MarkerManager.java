package com.rnmaps.fabric;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.viewmanagers.RNMapsMarkerManagerDelegate;
import com.facebook.react.viewmanagers.RNMapsMarkerManagerInterface;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.rnmaps.maps.MapCallout;
import com.rnmaps.maps.MapMarker;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

@ReactModule(name = MarkerManager.REACT_CLASS)
public class MarkerManager extends ViewGroupManager<MapMarker> implements RNMapsMarkerManagerInterface<MapMarker> {

    public static class AirMapMarkerSharedIcon {
        private BitmapDescriptor iconBitmapDescriptor;
        private Bitmap bitmap;
        private final Map<MapMarker, Boolean> markers;
        private boolean loadImageStarted;

        public AirMapMarkerSharedIcon() {
            this.markers = new WeakHashMap<>();
            this.loadImageStarted = false;
        }

        /**
         * check whether the load image process started.
         * caller AirMapMarker will only need to load it when this returns true.
         *
         * @return true if it is not started, false otherwise.
         */
        public synchronized boolean shouldLoadImage() {
            if (!this.loadImageStarted) {
                this.loadImageStarted = true;
                return true;
            }
            return false;
        }

        /**
         * subscribe icon update for given marker.
         * <p>
         * The marker is wrapped in weakReference, so no need to remove it explicitly.
         *
         * @param marker
         */
        public synchronized void addMarker(MapMarker marker) {
            this.markers.put(marker, true);
            if (this.iconBitmapDescriptor != null) {
                marker.setIconBitmapDescriptor(this.iconBitmapDescriptor, this.bitmap);
            }
        }

        /**
         * Remove marker from this shared icon.
         * <p>
         * Marker will only need to call it when the marker receives a different marker image uri.
         *
         * @param marker
         */
        public synchronized void removeMarker(MapMarker marker) {
            this.markers.remove(marker);
        }

        /**
         * check if there is markers still listening on this icon.
         * when there are not markers listen on it, we can remove it.
         *
         * @return true if there is, false otherwise
         */
        public synchronized boolean hasMarker() {
            return this.markers.isEmpty();
        }

        /**
         * Update the bitmap descriptor and bitmap for the image uri.
         * And notify all subscribers about the update.
         *
         * @param bitmapDescriptor
         * @param bitmap
         */
        public synchronized void updateIcon(BitmapDescriptor bitmapDescriptor, Bitmap bitmap) {

            this.iconBitmapDescriptor = bitmapDescriptor;
            this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            if (this.markers.isEmpty()) {
                return;
            }

            for (Map.Entry<MapMarker, Boolean> markerEntry : markers.entrySet()) {
                if (markerEntry.getKey() != null) {
                    markerEntry.getKey().setIconBitmapDescriptor(bitmapDescriptor, bitmap);
                }
            }
        }
    }

    private final Map<String, AirMapMarkerSharedIcon> sharedIcons = new ConcurrentHashMap<>();

    /**
     * get the shared icon object, if not existed, create a new one and store it.
     *
     * @param uri
     * @return the icon object for the given uri.
     */
    public AirMapMarkerSharedIcon getSharedIcon(String uri) {
        AirMapMarkerSharedIcon icon = this.sharedIcons.get(uri);
        if (icon == null) {
            synchronized (this) {
                if ((icon = this.sharedIcons.get(uri)) == null) {
                    icon = new AirMapMarkerSharedIcon();
                    this.sharedIcons.put(uri, icon);
                }
            }
        }
        return icon;
    }

    /**
     * Remove the share icon object from our sharedIcons map when no markers are listening for it.
     *
     * @param uri
     */
    public void removeSharedIconIfEmpty(String uri) {
        AirMapMarkerSharedIcon icon = this.sharedIcons.get(uri);
        if (icon == null) {
            return;
        }
        if (!icon.hasMarker()) {
            synchronized (this) {
                if ((icon = this.sharedIcons.get(uri)) != null && !icon.hasMarker()) {
                    this.sharedIcons.remove(uri);
                }
            }
        }
    }

    public MarkerManager(ReactApplicationContext context) {
        super(context);
    }

    private final RNMapsMarkerManagerDelegate<MapMarker, MarkerManager> delegate =
            new RNMapsMarkerManagerDelegate<>(this);

    @Override
    public ViewManagerDelegate<MapMarker> getDelegate() {
        return delegate;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public MapMarker createViewInstance(ThemedReactContext context) {
        return new MapMarker(context, this);
    }

    public static final String REACT_CLASS = "RNMapsMarker";

    @Override
    public void setAnchor(MapMarker view, @Nullable ReadableMap map) {
        double x = map != null && map.hasKey("x") ? map.getDouble("x") : 0.5;
        double y = map != null && map.hasKey("y") ? map.getDouble("y") : 1.0;
        view.setAnchor(x, y);
    }

    @Override
    public void setCalloutAnchor(MapMarker view, @Nullable ReadableMap value) {

    }

    @Override
    public void setImage(MapMarker view, @Nullable ReadableMap value) {
        if (value != null && value.hasKey("uri")) {
            view.setImage(value.getString("uri"));
        } else {
            view.setImage(null);
        }
    }

    @Override
    public void setCalloutOffset(MapMarker view, @Nullable ReadableMap value) {

    }

    @Override
    public void setDisplayPriority(MapMarker view, @Nullable String value) {

    }

    @Override
    public void setCenterOffset(MapMarker view, @Nullable ReadableMap value) {
    }

    @Override
    public void setCoordinate(MapMarker view, @Nullable ReadableMap value) {
        view.setCoordinate(value);
    }

    @Override
    public void setDescription(MapMarker view, @Nullable String value) {
        view.setSnippet(value);
    }

    @Override
    public void setDraggable(MapMarker view, boolean value) {
        view.setDraggable(value);
    }

    @Override
    public void setTitle(MapMarker view, @Nullable String value) {
        view.setTitle(value);
    }

    @Override
    public void setTracksViewChanges(MapMarker view, boolean value) {
        view.setTracksViewChanges(value);
    }

    @Override
    public void setIdentifier(MapMarker view, @Nullable String value) {
        view.setIdentifier(value);
    }

    @Override
    public void setIsPreselected(MapMarker view, boolean value) {

    }

    @Override
    public void setOpacity(MapMarker view, double value) {
        view.setOpacity((float) value);
    }

    @Override
    public void setPinColor(MapMarker view, @Nullable Integer value) {
        float[] hsv = new float[3];
        Color.colorToHSV(value, hsv);
        // NOTE: android only supports a hue
        view.setMarkerHue(hsv[0]);
    }

    @Override
    public void setTitleVisibility(MapMarker view, @Nullable String value) {

    }

    @Override
    public void setSubtitleVisibility(MapMarker view, @Nullable String value) {

    }

    @Override
    public void setUseLegacyPinView(MapMarker view, boolean value) {

    }

    @Override
    public void animateToCoordinates(MapMarker view, double latitude, double longitude, int duration) {
        view.animateToCoodinate(new LatLng(latitude, longitude), duration);
    }

    @Override
    public void setCoordinates(MapMarker view, double latitude, double longitude) {
        view.setCoordinate(new LatLng(latitude, longitude));
    }

    @Override
    public void showCallout(MapMarker view) {
        ((Marker) view.getFeature()).showInfoWindow();
    }

    @Override
    public void hideCallout(MapMarker view) {
        ((Marker) view.getFeature()).hideInfoWindow();
    }

    @Override
    public void redrawCallout(MapMarker view) {

    }

    @Override
    public void redraw(MapMarker view) {
        view.redraw();
    }

    @Override
    public void addView(MapMarker parent, View child, int index) {
        // if an <Callout /> component is a child, then it is a callout view, NOT part of the
        // marker.
        if (child instanceof MapCallout) {
            parent.setCalloutView((MapCallout) child);
        } else {
            super.addView(parent, child, index);
        }
    }
}
