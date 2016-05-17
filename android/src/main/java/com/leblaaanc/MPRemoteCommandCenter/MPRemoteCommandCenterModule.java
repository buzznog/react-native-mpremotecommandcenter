package com.leblaaanc.MPRemoteCommandCenter;

import com.facebook.react.bridge.*;

import android.util.Log;
import java.util.Map;

public class MPRemoteCommandCenterModule extends ReactContextBaseJavaModule {

    ReactApplicationContext reactContext;

    public MPRemoteCommandCenterModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "MPRemoteCommandCenter";
    }



    @ReactMethod
    public void setNowPlayingInfo(final ReadableMap info) {
        if (info != null) {
            String artist               = info.getString("artist");
            String albumTitle           = info.getString("albumTitle");
            String albumArtist          = info.getString("albumArtist");
            String title                = info.getString("title");
            String artworkURL           = info.getString("artworkURL");
            String duration             = info.getString("duration");
            String elapsedPlaybackTime  = info.getString("elapsedPlaybackTime");

            System.out.println("Got detail " + artist);
        }

    }

    @ReactMethod
    public void setElapsedPlaybackTime(Integer elapsedPlaybackTime)
    {
        System.out.println("Elasped time " + elapsedPlaybackTime);
    }

}
