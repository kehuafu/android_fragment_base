package com.example.demo.utils;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MediaPlayerManager {

    private static final String TAG = "MediaPlayerManager";

    public static int PLAY_STATE_Default = 0;//默认状态，未播放
    public static int PLAY_STATE_PLAYING = 1;//播放中
    public static int PLAY_STATE_FINISHED = 2;//播放完成
    public static int PLAY_STATE_STOP = 3;//播放被终止

    private int currentPlayState = PLAY_STATE_Default;//未播放

    private MediaPlayer mediaplayer = null;

    private AnimationDrawable lastAnimationDrawable = null;

    public MediaPlayer getMediaplayer() {
        return mediaplayer;
    }

    public void setCurrentPlayState(int currentPlayState) {
        this.currentPlayState = currentPlayState;
    }

    public int getCurrentPlayState() {
        return currentPlayState;
    }

    public static MediaPlayerManager getInstance() {
        return SingletionInternalClassHolder.instance;
    }

    public AnimationDrawable getLastAnimationDrawable() {
        return lastAnimationDrawable;
    }

    public void setLastAnimationDrawable(AnimationDrawable lastAnimationDrawable) {
        this.lastAnimationDrawable = lastAnimationDrawable;
    }

    private static class SingletionInternalClassHolder {
        private static final MediaPlayerManager instance = new MediaPlayerManager();

    }

    public void release() {
        if (mediaplayer != null) {
            mediaplayer.reset();
            mediaplayer.release();
            mediaplayer = null;
        }
        if (lastAnimationDrawable != null) {
            lastAnimationDrawable.stop();
            lastAnimationDrawable = null;
        }
    }

    /**
     * </br> This code is trying to do the following from the hidden API
     * </br> SubtitleController sc = new SubtitleController(context, null, null);
     * </br> sc.mHandler = new Handler();
     * </br> mediaplayer.setSubtitleAnchor(sc, null)</p>
     */
    public MediaPlayer getMediaPlayer(Context context) {
        if (mediaplayer == null) {
            mediaplayer = new MediaPlayer();
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            return mediaplayer;
        }
        try {
            Class<?> cMediaTimeProvider = Class.forName("android.media.MediaTimeProvider");
            Class<?> cSubtitleController = Class.forName("android.media.SubtitleController");
            Class<?> iSubtitleControllerAnchor = Class.forName("android.media.SubtitleController$Anchor");
            Class<?> iSubtitleControllerListener = Class.forName("android.media.SubtitleController$Listener");
            Constructor constructor = cSubtitleController.getConstructor(
                    new Class[]{Context.class, cMediaTimeProvider, iSubtitleControllerListener});
            Object subtitleInstance = constructor.newInstance(context, null, null);
            Field f = cSubtitleController.getDeclaredField("mHandler");
            f.setAccessible(true);
            try {
                f.set(subtitleInstance, new Handler());
            } catch (IllegalAccessException e) {
                return mediaplayer;
            } finally {
                f.setAccessible(false);
            }
            Method setsubtitleanchor = mediaplayer.getClass().getMethod("setSubtitleAnchor",
                    cSubtitleController, iSubtitleControllerAnchor);
            setsubtitleanchor.invoke(mediaplayer, subtitleInstance, null);
        } catch (Exception e) {
            Log.d(TAG, "getMediaPlayer crash ,exception = " + e);
        }
        return mediaplayer;
    }
}
