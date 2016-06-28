package com.leblaaanc.MPRemoteCommandCenter;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.*;

import java.util.Map;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.net.Uri;

import android.os.PowerManager;
import android.os.Looper;
import android.os.Handler;

import android.media.AudioManager;
import android.media.MediaPlayer;

import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;

public class MPRemoteCommandCenterModule extends ReactContextBaseJavaModule {

  ReactApplicationContext _reactContext;
  volatile private MediaPlayer _mediaPlayer;

  volatile boolean isPaused;
  volatile boolean isBuffering;
  AudioManager mAudioManager;
  MediaSessionCompat mSessionCompat;
  final String  NAME = "RNMPRemoteCommandCenterManager";
  final String  TAG = "RNMPRCCM";

  public MPRemoteCommandCenterModule(ReactApplicationContext reactContext) {
    super(reactContext);
    _reactContext = reactContext;
    new Handler(Looper.getMainLooper()).post(new Runnable() {
         @Override
         public void run() {
             init();
         }
    });
  }

  @Override
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public void setNowPlayingInfo(final ReadableMap info) {
    if (info != null) {
        try {
            Log.d(TAG, "==> called setNowPlayingInfo()");

              String artist = info.getString("artist");
              String albumTitle = info.getString("albumTitle");
              String albumArtist = info.getString("albumArtist");
              String title = info.getString("title");
              String artworkURL = info.getString("artworkURL");
              Integer duration = info.getInt("duration");
              Integer elapsedPlaybackTime = info.getInt("elapsedPlaybackTime");

              mSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                     .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                     .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, albumArtist)
                     .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                     .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, albumTitle)
                     .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, artworkURL)
                     .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, artworkURL)
                     .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                     .build());


                if (isMediaPlaying()) {
                  NotificationCompat.Builder builder = new NotificationCompat.Builder(_reactContext);
                  NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();

                  style.setMediaSession(mSessionCompat.getSessionToken());
                  style.setShowCancelButton(true);

                  builder.setAutoCancel(false);
                  builder.setContentTitle(title);
                  builder.setStyle(style);
                  builder.setOngoing(true);

                  //builder.addAction(generateAction(R.drawable.ic_previous, MediaPlayerService.ACTION_PREV, MediaPlayerService.ACTION_PREV));
                  //builder.addAction(generateAction(R.drawable.ic_previous, MediaPlayerService.ACTION_PLAY, MediaPlayerService.ACTION_PLAY));
                  //builder.addAction(generateAction(R.drawable.ic_previous, MediaPlayerService.ACTION_NEXT, MediaPlayerService.ACTION_NEXT));
                  //builder.setLargeIcon();
                  //builder.setSmallIcon(R.drawable.logo);

                  NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(_reactContext);
                  mNotificationManager.notify(200, builder.build());
                }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
  }


  @ReactMethod
  public void setElapsedPlaybackTime(Integer elapsedPlaybackTime)
  {
    if (_mediaPlayer != null) {
        _mediaPlayer = new MediaPlayer();
    }
    _mediaPlayer.seekTo(elapsedPlaybackTime);
    Log.d(TAG, "==> setElapsedPlaybackTime(" + elapsedPlaybackTime + ")");
  }


  synchronized public boolean isMediaPlaying() {
    return _mediaPlayer != null && _mediaPlayer.isPlaying();
  }

  public void init() {
       mSessionCompat = new MediaSessionCompat(_reactContext, NAME, null, null);
       mSessionCompat.setActive(true);
       mSessionCompat.setCallback(new MediaSessionCompatCallback());
       mSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
       mSessionCompat.setActive(true);

       _mediaPlayer = new MediaPlayer();
       _mediaPlayer.setWakeMode(_reactContext, PowerManager.PARTIAL_WAKE_LOCK);
       _mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
   }

  class MediaSessionCompatCallback extends MediaSessionCompat.Callback {
      @Override
      public void onPlay() {
          super.onPlay();
          sendEvent("play");
      }

      @Override
      public void onPause() {
          super.onPause();
          sendEvent("pause");
      }

      @Override
      public void onSkipToNext() {
          super.onSkipToNext();
          sendEvent("nextTrack");
      }

      @Override
      public void onSkipToPrevious() {
          super.onSkipToPrevious();
          sendEvent("previousTrack");
      }

  }

  private void sendEvent(String eventName) {
     Log.d(TAG, " **** ***** ==> " + eventName);
     _reactContext
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
        .emit("RNMPRemoteCommandCenterEvent", eventName);
    }
}
