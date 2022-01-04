package com.example.demo.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.example.demo.app.App;

public class AudioRecodeUtils {

    //文件路径
    private String filePath;
    //文件夹路径
    private String FolderPath;

    private MediaRecorder mMediaRecorder;

    private MediaPlayer player;
    private final String TAG = "fan";
    public static final int MAX_LENGTH = 1000 * 59;// 最大录音时长1000*59;

    private OnAudioStatusUpdateListener audioStatusUpdateListener;

    private OnAudioPlayStatusListener audioPlayStatusListener;

    private MediaPlayerManager mediaPlayerManager = MediaPlayerManager.getInstance();

    /**
     * 文件存储默认sdcard/record
     */
    public AudioRecodeUtils() {
        //默认保存路径为/sdcard/record/下
        this(PathUtils.getInternalAppCachePath());
    }

    public AudioRecodeUtils(String filePath) {
        File path = new File(filePath);
        if (!path.exists())
            path.mkdirs();
        this.FolderPath = filePath;
    }

    private long startTime;
    private long endTime;


    /**
     * 开始录音 使用mp4格式
     * 录音文件
     *
     * @return
     */
    public void startRecord() {
        // 开始录音
        /* ①Initial：实例化MediaRecorder对象 */
        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        try {
            /* ②setAudioSource/setVedioSource */
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            filePath = FolderPath + System.currentTimeMillis() + ".amr";
            /* ③准备 */
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setMaxDuration(MAX_LENGTH);
            mMediaRecorder.prepare();
            /* ④开始 */
            mMediaRecorder.start();
            // AudioRecord audioRecord.
            /* 获取开始时间* */
            startTime = System.currentTimeMillis();
            updateMicStatus();
            Log.i("fan", "startTime" + startTime);
        } catch (IllegalStateException e) {
            Log.i(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        } catch (IOException e) {
            Log.i(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        }
    }

    /**
     * 停止录音
     */
    public long stopRecord() {
        if (mMediaRecorder == null)
            return 0L;
        endTime = System.currentTimeMillis();
        //有一些网友反应在5.0以上在调用stop的时候会报错，翻阅了一下谷歌文档发现上面确实写的有可能会报错的情况，捕获异常清理一下就行了，感谢大家反馈！
        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            audioStatusUpdateListener.onStop(filePath);
            filePath = "";
        } catch (RuntimeException e) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;

            File file = new File(filePath);
            if (file.exists())
                file.delete();

            filePath = "";

        }
        return endTime - startTime;
    }

    /**
     * 播放录音
     *
     * @param filepath
     */
    public MediaPlayer playRecord(String filepath, AnimationDrawable anim) {
        if (mediaPlayerManager.getCurrentPlayState() == MediaPlayerManager.PLAY_STATE_PLAYING) {
            if (mediaPlayerManager.getLastAnimationDrawable() != null) {
                if (mediaPlayerManager.getMediaplayer() != null) {
                    mediaPlayerManager.getMediaplayer().reset();
                    if (mediaPlayerManager.getLastAnimationDrawable() == anim) {
                        if (anim.isRunning()) {
                            audioPlayStatusListener.onState(MediaPlayerManager.PLAY_STATE_STOP, mediaPlayerManager.getLastAnimationDrawable());
                            mediaPlayerManager.setCurrentPlayState(MediaPlayerManager.PLAY_STATE_STOP);
                            return mediaPlayerManager.getMediaplayer();
                        }
                    }
                    audioPlayStatusListener.onState(MediaPlayerManager.PLAY_STATE_STOP, mediaPlayerManager.getLastAnimationDrawable());
                    mediaPlayerManager.setCurrentPlayState(MediaPlayerManager.PLAY_STATE_STOP);
                    mediaPlayerManager.setLastAnimationDrawable(anim);
                }
            }
        }
        player = mediaPlayerManager.getMediaPlayer(App.getAppContext());
        if (player != null) {
            try {
                player.reset();
                //设置语言的来源
                player.setDataSource(filepath);
                //初始化
                player.prepare();
                //开始播放
                player.start();
                mediaPlayerManager.setCurrentPlayState(MediaPlayerManager.PLAY_STATE_PLAYING);
                audioPlayStatusListener.onState(MediaPlayerManager.PLAY_STATE_PLAYING, anim);
                mediaPlayerManager.setLastAnimationDrawable(anim);
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        int currentPlayState = MediaPlayerManager.PLAY_STATE_FINISHED;
                        mediaPlayerManager.setCurrentPlayState(currentPlayState);
                        audioPlayStatusListener.onState(currentPlayState, anim);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return player;
    }


    /**
     * 取消录音
     */
    public void cancelRecord() {
        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;

        } catch (RuntimeException e) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        File file = new File(filePath);
        if (file.exists())
            file.delete();

        filePath = "";

    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };


    private int BASE = 1;
    private int SPACE = 100;// 间隔取样时间

    public void setOnAudioStatusUpdateListener(OnAudioStatusUpdateListener audioStatusUpdateListener) {
        this.audioStatusUpdateListener = audioStatusUpdateListener;
    }

    public void setOnAudioPlayStatusListener(OnAudioPlayStatusListener audioPlayStatusListener) {
        this.audioPlayStatusListener = audioPlayStatusListener;
    }


    /**
     * 更新麦克状态
     */
    private void updateMicStatus() {

        if (mMediaRecorder != null) {
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;
            double db = 0;// 分贝
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
                if (null != audioStatusUpdateListener) {
                    if ((System.currentTimeMillis() - startTime) / 1000 < 59 * 1000L) {
                        audioStatusUpdateListener.onUpdate(db, System.currentTimeMillis() - startTime);
                    } else {
                        stopRecord();
                    }
                }
            }
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    public interface OnAudioPlayStatusListener {
        /**
         * 播放状态
         *
         * @param playState
         */
        public void onState(int playState, AnimationDrawable anim);
    }

    public interface OnAudioStatusUpdateListener {
        /**
         * 录音中...
         *
         * @param db   当前声音分贝
         * @param time 录音时长
         */
        public void onUpdate(double db, long time);

        /**
         * 停止录音
         *
         * @param filePath 保存路径
         */
        public void onStop(String filePath);

    }
}


