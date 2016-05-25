package com.leblaaanc.MPRemoteCommandCenter;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.*;

import android.util.Log;
import java.util.Map;
import android.support.annotation.Nullable;

public class MPRemoteCommandCenterModule extends ReactContextBaseJavaModule {

  ReactApplicationContext _reactContext;

  public MPRemoteCommandCenterModule(ReactApplicationContext reactContext) {
    super(reactContext);
    _reactContext = reactContext;
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


  private void sendEvent(@Nullable WritableMap params) {
    _reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit("RNMPRemoteCommandCenterEvent", params);
  }

}
