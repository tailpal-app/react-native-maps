package com.rnmaps.fabric;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.viewmanagers.RNMapsLocalTileManagerDelegate;
import com.facebook.react.viewmanagers.RNMapsLocalTileManagerInterface;
import com.rnmaps.maps.MapLocalTile;

@ReactModule(name = LocalTileManager.REACT_CLASS)
public class LocalTileManager extends ViewGroupManager<MapLocalTile> implements RNMapsLocalTileManagerInterface<MapLocalTile> {

    private final RNMapsLocalTileManagerDelegate<MapLocalTile, LocalTileManager> delegate =
            new RNMapsLocalTileManagerDelegate<>(this);

    @Override
    public ViewManagerDelegate<MapLocalTile> getDelegate() {
        return delegate;
    }

    public LocalTileManager(ReactApplicationContext reactContext) {
        super();
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) reactContext.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay()
                .getRealMetrics(metrics);
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    public static final String REACT_CLASS = "RNMapsLocalTile";

    public MapLocalTile createViewInstance(ThemedReactContext context) {
        return new MapLocalTile(context);
    }

    @Override
    public void setPathTemplate(MapLocalTile view, @Nullable String value) {
        view.setPathTemplate(value);
    }

    @Override
    public void setTileSize(MapLocalTile view, int value) {
        view.setTileSize(value);
    }

    @Override
    public void setUseAssets(MapLocalTile view, boolean value) {
        view.setUseAssets(value);
    }
}
