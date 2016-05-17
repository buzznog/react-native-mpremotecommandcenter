package com.leblaaanc.MPRemoteCommandCenter;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;


import  android.util.log;

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
    public void setNowPlayingInfo(Map<String, Object> info) {
        String artist               = info.get("artist");
        String albumTitle           = info.get("albumTitle");
        String albumArtist          = info.get("albumArtist");
        String title                = info.get("title");
        String artworkURL           = info.get("artworkURL");
        String duration             = info.get("duration");
        String elapsedPlaybackTime  = info.get("elapsedPlaybackTime");

        Log.i('Got detail ' + artist);

    }
}