package com.example.demo.chat.widget.camera;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.PathUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 作者: hzx
 * 时间: 2019/8/13
 * ========================================
 * 描述: 摄像头运行在后台，可拍照、录制、以及camera2获取实时YUV_420_888 数据，并转NV21 ，可用于直播
 */

public class JewxonCameraService extends Service {

    private String mCameraId;
    private CaptureRequest mCaptureRequest;

    class LocalServiceBinder extends Binder {
        JewxonCameraService getService() {
            return JewxonCameraService.this;
        }
    }

    interface PictureCallBack {
        void getLocalPicturePath(String path);

        void getLocalVideoPath(String path);
    }

    private LocalServiceBinder mLocalServiceBinder = new LocalServiceBinder();

    private PictureCallBack mPictureCallBack;

    public void setmPictureCallBack(PictureCallBack mPictureCallBack) {
        this.mPictureCallBack = mPictureCallBack;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mLocalServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initCamera();
    }


    private CameraDevice mCameraDevice;
    private CameraCaptureSession mPreviewSession;
    private Size mVideoSize = new Size(1080, 720);
    private Size mPreviewSize = new Size(1080, 720);
    private MediaRecorder mMediaRecorder;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private TextureView mTextureView;


    private File mMFile;

    private void initCamera() {
        startBackgroundThread();
        setupImageReader2();
        openCamera(null);
    }

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            mCameraOpenCloseLock.release();
            HzxLoger.HzxLog("onOpened----摄像头被打开");
            if (mTextureView != null) {
                if (mTextureView.isAvailable()) {
                    startPreview(mTextureView);
                }
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            HzxLoger.HzxLog("onDisconnected----摄像头已断开");
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

    };
    private String mNextVideoAbsolutePath;
    private CaptureRequest.Builder mPreviewBuilder;
    private ImageReader mMMImageReader2;
    private Surface mMImageReader2_surface;


    @SuppressWarnings("MissingPermission")
    private void openCamera(String cameraId) {
        try {
            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            if (cameraId == null) {
                mCameraId = manager.getCameraIdList()[0];
            } else {
                mCameraId = cameraId;
            }
            HzxLoger.HzxLog("openCamera--->" + mCameraId);
            setUpCameraOutputs();

            mMediaRecorder = new MediaRecorder();
            manager.openCamera(mCameraId, mStateCallback, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 切换摄像头
     */
    public void switchCamera(TextureView textureView, Boolean isBack) {
        try {
            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            if (textureView.isAvailable()) {
                mTextureView = textureView;
            }
            if (isBack) {
                //后置转前置
                closeCamera();
                HzxLoger.HzxLog("switchCamera---->切换前置摄像头");
                openCamera(manager.getCameraIdList()[1]);
            } else {
                //前置转后置
                closeCamera();
                HzxLoger.HzxLog("switchCamera---->切换后置摄像头");
                openCamera(manager.getCameraIdList()[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void setUpCameraOutputs() {
        try {
            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraId);
            Size largest = new Size(1920, 1080);
            mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                    ImageFormat.JPEG, /*maxImages*/2);
            mImageReader.setOnImageAvailableListener(
                    mOnImageAvailableListener, mBackgroundHandler);

            Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            mFlashSupported = available != null && available;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
        }
    }

    private void setupImageReader2() {
        mMMImageReader2 = ImageReader.newInstance(mVideoSize.getWidth(), mVideoSize.getHeight(),
                ImageFormat.YUV_420_888, 1);
        mMMImageReader2.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = reader.acquireLatestImage();
//                HzxLoger.HzxLog("ImageReader--->" + image);
                //转成NV21数据，可用于直播
//                byte[] data = ImageUtil.getBytesFromImageAsType(image, ImageUtil.NV21);
                image.close();
            }
        }, mBackgroundHandler);
        mMImageReader2_surface = mMMImageReader2.getSurface();
    }

    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            closePreviewSession();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mMediaRecorder) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    private List<Surface> mSurfaces = new ArrayList<>();


    public void startPreview(TextureView mTextureView) {
        try {
            closePreviewSession();

            mSurfaces.clear();
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            if (mTextureView != null) {
                SurfaceTexture texture = mTextureView.getSurfaceTexture();
                texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                Surface previewSurface = new Surface(texture);
                mPreviewBuilder.addTarget(previewSurface);
                mSurfaces.add(previewSurface);
            }

//            mPreviewBuilder.addTarget(mImageReader.getSurface());
            mSurfaces.add(mImageReader.getSurface());

            mPreviewBuilder.addTarget(mMImageReader2_surface);
            mSurfaces.add(mMImageReader2_surface);

            mCameraDevice.createCaptureSession(mSurfaces,
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mPreviewSession = session;
                            updatePreview();
                            HzxLoger.HzxLog("onConfigured----预览画面");
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            HzxLoger.HzxLog("onConfigureFailed");
                        }
                    }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            setAutoFlash(mPreviewBuilder);
            mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            mCaptureRequest = mPreviewBuilder.build();
            mPreviewSession.setRepeatingRequest(mCaptureRequest, null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void setUpMediaRecorder() throws IOException {
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
            mNextVideoAbsolutePath = getVideoFilePath(this);
        }
        HzxLoger.HzxLog("mNextVideoAbsolutePath--->" + mNextVideoAbsolutePath);
        mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
        mMediaRecorder.setVideoEncodingBitRate(10000000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        mMediaRecorder.prepare();
    }

    private String getVideoFilePath(Context context) {
        final File dir = context.getExternalFilesDir(null);
        return (dir == null ? "" : (dir.getAbsolutePath() + "/"))
                + System.currentTimeMillis() + ".mp4";
    }

    public void stopRecordingVideo(TextureView mTextureView) {
        mMediaRecorder.stop();
        mMediaRecorder.reset();

        HzxLoger.HzxLog("Video 保存路径-->" + mNextVideoAbsolutePath);
        mPictureCallBack.getLocalVideoPath(mNextVideoAbsolutePath);
        mNextVideoAbsolutePath = null;

        startPreview(mTextureView);
    }

    public void startRecordingVideo(TextureView mTextureView) {
        try {
            closePreviewSession();
            setUpMediaRecorder();

            mSurfaces.clear();

            if (mTextureView != null) {
                SurfaceTexture texture = mTextureView.getSurfaceTexture();
                texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                Surface previewSurface = new Surface(texture);
                mPreviewBuilder.addTarget(previewSurface);
                mSurfaces.add(previewSurface);
            }

            Surface recorderSurface = mMediaRecorder.getSurface();
            mSurfaces.add(recorderSurface);
            mPreviewBuilder.addTarget(recorderSurface);

            mSurfaces.add(mMImageReader2_surface);
            mPreviewBuilder.addTarget(mMImageReader2_surface);


            mCameraDevice.createCaptureSession(mSurfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    mPreviewSession = cameraCaptureSession;
                    updatePreview();

                    mMediaRecorder.start();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    HzxLoger.HzxLog("onConfigureFailed");
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException | IOException e) {
            e.printStackTrace();
        }

    }

    public void takePicture() {
        mMFile = new File(PathUtils.getExternalAppDcimPath(), +System.currentTimeMillis() + ".jpg");
        lockFocus();
    }

    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAITING_LOCK = 1;
    private static final int STATE_WAITING_PRECAPTURE = 2;
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;
    private static final int STATE_PICTURE_TAKEN = 4;
    private int mState = STATE_PREVIEW;

    private void lockFocus() {
        try {
            mPreviewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            mState = STATE_WAITING_LOCK;
            mPreviewSession.capture(mPreviewBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void runPrecaptureSequence() {
        try {
            mPreviewBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            mState = STATE_WAITING_PRECAPTURE;
            mPreviewSession.capture(mPreviewBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private ImageReader mImageReader;
    private boolean mFlashSupported;
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
//            HzxLoger.HzxLog("onImageAvailable--->" + reader.acquireNextImage());
            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mMFile));
        }
    };


    private void captureStillPicture() {
        try {
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

            HzxLoger.HzxLog("开始拍照");
            captureBuilder.addTarget(mImageReader.getSurface());

            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            setAutoFlash(captureBuilder);

            CameraCaptureSession.CaptureCallback CaptureCallback = new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {

                    HzxLoger.HzxLog("保存路径--->" + mMFile.toString());
                    mPictureCallBack.getLocalPicturePath(mMFile.toString());
                    unlockFocus();
                }

                @Override
                public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                    HzxLoger.HzxLog("捕获失败");
                }

                @Override
                public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                    HzxLoger.HzxLog("捕获开始");
                }
            };

            mPreviewSession.stopRepeating();
            mPreviewSession.abortCaptures();
            mPreviewSession.capture(captureBuilder.build(), CaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void unlockFocus() {
        try {
            mPreviewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            setAutoFlash(mPreviewBuilder);
            mPreviewSession.capture(mPreviewBuilder.build(), mCaptureCallback, mBackgroundHandler);
            mState = STATE_PREVIEW;
            mPreviewSession.setRepeatingRequest(mCaptureRequest, null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
    }

    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }

    };

    private void process(CaptureResult result) {
        switch (mState) {
            case STATE_PREVIEW: {
                break;
            }
            case STATE_WAITING_LOCK: {
                Integer afState = result.get(CaptureResult.CONTROL_AF_STATE); //对焦模式
                if (afState == null) {
                    captureStillPicture();
                } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState || CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE); //曝光模式
                    if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    } else {
                        runPrecaptureSequence();
                    }
                } else {
                    mState = STATE_PICTURE_TAKEN;
                    captureStillPicture();
                }
                break;
            }
            case STATE_WAITING_PRECAPTURE: {
                Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                if (aeState == null ||
                        aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                        aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                    mState = STATE_WAITING_NON_PRECAPTURE;
                }
                break;
            }
            case STATE_WAITING_NON_PRECAPTURE: {
                Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                    mState = STATE_PICTURE_TAKEN;
                    captureStillPicture();
                }
                break;
            }
        }
    }

    private void closePreviewSession() {
        if (mPreviewSession != null) {
            mPreviewSession.close();
            mPreviewSession = null;
        }
    }

    @Override
    public void onDestroy() {
        HzxLoger.HzxLog("onDestroy");
        super.onDestroy();

        closeCamera();
        stopBackgroundThread();
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
