package com.leblaaanc.MPRemoteCommandCenter;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.*;

import java.util.Map;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.net.Uri;
import android.os.PowerManager;

import android.media.AudioManager;
import android.media.MediaPlayer;

import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;

// ref: http://stackoverflow.com/questions/26569044/how-can-i-create-a-mediacontroller-instance-from-mediasessionmanager
public class MPRemoteCommandCenterModule extends ReactContextBaseJavaModule {

  ReactApplicationContext _reactContext;
  volatile private MediaPlayer _mediaPlayer;

  volatile boolean isPaused;
  volatile boolean isBuffering;
  AudioManager mAudioManager;
  MediaSessionCompat mSessionCompat;
  final String  NAME = "RNMPRemoteCommandCenterManager";

  public MPRemoteCommandCenterModule(ReactApplicationContext reactContext) {
    super(reactContext);
    _reactContext = reactContext;
    init(reactContext);
  }

  @Override
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public void setNowPlayingInfo(final ReadableMap info) {
    if (info != null) {
      String artist = info.getString("artist");
      String albumTitle = info.getString("albumTitle");
      String albumArtist = info.getString("albumArtist");
      String title = info.getString("title");
      String artworkURL = info.getString("artworkURL");
      String duration = info.getString("duration");
      String elapsedPlaybackTime = info.getString("elapsedPlaybackTime");
      mSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
             .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
             .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, albumArtist)
             .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
             .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, albumTitle)
             .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, artworkURL)
             .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, artworkURL)
             .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Long.parseLong(duration))
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

  private void sendEvent(String eventName) {
    _reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit("RNMPRemoteCommandCenterEvent", eventName);
  }

  synchronized public boolean isMediaPlaying() {
    return _mediaPlayer != null && _mediaPlayer.isPlaying();
  }

  // should be first time
  synchronized private void init(ReactApplicationContext context) {
      //create player
      if (_mediaPlayer != null) {
        return;
      }

      //mSessionCompat = new MediaSessionCompat(_reactContext, new MediaSessionImpl(NAME));
      // mSessionCompat.setActive(true);
      // mSessionCompat.setCallback(new MediaSessionCompatCallback());
      // mSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

      _mediaPlayer = new MediaPlayer();
      _mediaPlayer.setWakeMode(_reactContext, PowerManager.PARTIAL_WAKE_LOCK);
      _mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
      //set listeners
      //_mediaPlayer.setOnPreparedListener(this);
      //_mediaPlayer.setOnCompletionListener(this);
      //_mediaPlayer.setScreenOnWhilePlaying(true);
      //_mediaPlayer.setOnErrorListener(this);
      //_mediaPlayer.setOnBufferingUpdateListener(this);
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


}
