package com.leblaaanc.MPRemoteCommandCenter;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.*;

import android.util.Log;
import java.util.Map;
import java.util.List;
import android.support.annotation.Nullable;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.MediaSession;
import android.content.ComponentName;
import android.content.Context;


// ref: http://stackoverflow.com/questions/26569044/how-can-i-create-a-mediacontroller-instance-from-mediasessionmanager
public class MPRemoteCommandCenterModule extends ReactContextBaseJavaModule {

    ReactApplicationContext _reactContext;

    private MediaPlayer   _mediaPlayer;
    private MediaSession  _mediaSession;
    private final String  NAME = "RNMPRemoteCommandCenterManager";

  public MPRemoteCommandCenterModule(ReactApplicationContext reactContext) {
    super(reactContext);
    _reactContext = reactContext;
    MediaSessionManager msm = (MediaSessionManager)reactContext.getSystemService(Context.MEDIA_SESSION_SERVICE);
    ComponentName cn = new ComponentName(reactContext, "MPRemoteCommandCenterModule");

    //List<MediaController> mc  = msm.getActiveSessions(cn);

  }

  @Override
  public String getName() {
    return NAME;
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

      Log.d(NAME, "setNowPlayingInfo called");
    }

  }

  @ReactMethod
  public void setElapsedPlaybackTime(Integer elapsedPlaybackTime)
  {
    if (_mediaPlayer != null) {
        _mediaPlayer = new MediaPlayer();
    }
    //_mediaPlayer.seekTo(elapsedPlaybackTime);
    Log.d(NAME, "setElapsedPlaybackTime(" + elapsedPlaybackTime + ")");
  }

  private void registerRemoteControlEvents() {

    _mediaSession.setCallback(new MediaSession.Callback() {
         @Override
         public void onPlay() {
            super.onPlay();
            sendEvent("play");
            Log.d(NAME, "onPlay");
         }

         @Override
         public void onPause() {
            super.onPause();
            sendEvent("pause");
            Log.d(NAME, "onPause");
         }

         @Override
         public void onSkipToNext() {
            super.onSkipToNext();
            sendEvent("nextTrack");
            Log.d(NAME, "onSkipToNext");
         }

         @Override
         public void onSkipToPrevious() {
            super.onSkipToPrevious();
            sendEvent("previousTrack");
            Log.d(NAME, "onSkipToPrevious");
         }
    });
  }

  private void sendEvent(String eventName) {
      _reactContext
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
        .emit("RNMPRemoteCommandCenterEvent", eventName);
  }

}
