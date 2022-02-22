package com.example.demo.chat.widget.camera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.view.MotionEvent
import android.view.View
import com.blankj.utilcode.util.FileUtils
import com.example.demo.R
import com.example.demo.databinding.FragmentCameraBinding
import com.example.demo.main.mvvm.MainState
import com.example.demo.main.mvvm.MainViewModel
import com.kehuafu.base.core.container.base.BaseFragment
import com.kehuafu.base.core.container.widget.toast.showToast
import com.kehuafu.base.core.ktx.loadImage
import com.kehuafu.base.core.ktx.runOnMainThread
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory
import java.io.FileNotFoundException
import java.util.*

class CameraFragment : BaseFragment<FragmentCameraBinding, MainViewModel, MainState>(),
    ServiceConnection, JewxonCameraService.PictureCallBack {

    private var mIsRecordingVideo = false
    private var mIsBackCamera = true //默认后置摄像头
    private var mService: JewxonCameraService? = null
    private var mJewxonService: Intent? = null
    private var mFilePath: String = ""
    private lateinit var mRecorderVideoTimer: Timer

    private var timeCountInMilliSeconds = 15000L //录制视频的最大时间为15秒
    private var countDownTimer: CountDownTimer? = null

    companion object {
        const val RECORDER_VIDEO_MAX_TIME = 15000L //录制视频的最大时间为15秒
    }

    override fun onResume() {
        super.onResume()
        HzxLoger.HzxLog("onResume--->" + viewBinding.mAutoFitTextureView.isAvailable)
        if (viewBinding.mAutoFitTextureView.isAvailable) {
            mService!!.onResume(viewBinding.mAutoFitTextureView)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(savedInstanceState: Bundle?) {
        mJewxonService = Intent(requireActivity(), JewxonCameraService::class.java)

        requireActivity().startService(mJewxonService)
        requireActivity().bindService(mJewxonService, this, Context.BIND_AUTO_CREATE)

        withViewBinding {
            closeCamera.setOnClickListener {
                requireActivity().onBackPressed()
            }

            switchIv.setOnClickListener {
                mService!!.switchCamera(mAutoFitTextureView, mIsBackCamera)
                mIsBackCamera = !mIsBackCamera
            }
            frameLayoutPhoto.setOnClickListener {
                mIsRecordingVideo = false
                mService!!.takePicture()
            }

            frameLayoutPhoto.setOnLongClickListener {
                mIsRecordingVideo = true
                mService!!.startRecordingVideo(mAutoFitTextureView)
                startTimer()
                true
            }

            frameLayoutPhoto.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        HzxLoger.HzxLog("ACTION_DOWN")
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                        HzxLoger.HzxLog("ACTION_UP")
                        if (mIsRecordingVideo) {
                            mService!!.stopRecordingVideo(mAutoFitTextureView)
                            mIsRecordingVideo = false
                            stopCountDownTimer()
                        }
                    }
//                    MotionEvent.ACTION_CANCEL -> {
//                        if (mIsRecordingVideo) {
//                            mService!!.stopRecordingVideo(mAutoFitTextureView)
//                            mIsRecordingVideo = false
//                            stopCountDownTimer()
//                        }
//                    }
                }
                false
            }
            backPreviewVideo.setOnClickListener {
                videoViewController.visibility = View.VISIBLE
                videoViewPreview.visibility = View.GONE
                videoView.release()
                videoView.visibility = View.GONE
                if (viewBinding.mAutoFitTextureView.isAvailable) {
                    mAutoFitTextureView.visibility = View.VISIBLE
                }
                deleteTempFile()
                if (mIsRecordingVideo) {
                    stopTimer()
                }
            }
            tvSendPicture.setOnClickListener {
                val resultIntent = Intent().apply { putExtra("filePath", mFilePath) }
                requireActivity().setResult(Activity.RESULT_OK, resultIntent)
                requireActivity().finish()
                requireActivity().overridePendingTransition(
                    R.anim.activity_in_bottom,
                    R.anim.activity_out_top
                )
            }
        }
    }

    private fun startTimer() {
        withViewBinding {
            circleProgress.scaleX = 1.3F
            circleProgress.scaleY = 1.3F
            frameLayoutVideo.scaleX = 1.3F
            frameLayoutVideo.scaleY = 1.3F
            frameLayoutPhoto.scaleX = 0.5F
            frameLayoutPhoto.scaleY = 0.5F
        }
        startCountDownTimer()
    }

    private fun stopTimer() {
        withViewBinding {
            circleProgress.scaleX = 1.0F
            circleProgress.scaleY = 1.0F
            frameLayoutVideo.scaleX = 1.0F
            frameLayoutVideo.scaleY = 1.0F
            frameLayoutPhoto.scaleX = 1.0F
            frameLayoutPhoto.scaleY = 1.0F
            circleProgress.progress = 0
        }
        stopCountDownTimer()
    }

    private fun startCountDownTimer() {
        countDownTimer = object : CountDownTimer(RECORDER_VIDEO_MAX_TIME, 1000) {
            override fun onTick(millisUntilProgress: Long) {
                val currentM = (millisUntilProgress / 1000).toInt()
                val maxM = (RECORDER_VIDEO_MAX_TIME / 1000).toInt()
                viewBinding.circleProgress.progress =
                    100 - (100 / maxM) * (currentM + 1)
            }

            override fun onFinish() {
                stopTimer()
                if (mIsRecordingVideo) {
                    mService!!.stopRecordingVideo(viewBinding.mAutoFitTextureView)
                    mIsRecordingVideo = false
                }
            }

        }
        countDownTimer!!.start()
    }

    private fun stopCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
    }

    private fun deleteTempFile() {
        try {
            val bool = FileUtils.delete(mFilePath)
            mFilePath = ""
            HzxLoger.HzxLog("删除临时路径结果--->$bool")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        HzxLoger.HzxLog("onServiceConnected----componentName:$name")
        val localServiceBinder: JewxonCameraService.LocalServiceBinder =
            service as JewxonCameraService.LocalServiceBinder
        mService = localServiceBinder.service
        mService!!.setmPictureCallBack(this)
        if (viewBinding.mAutoFitTextureView.isAvailable) {
            mService!!.startPreview(viewBinding.mAutoFitTextureView)
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        HzxLoger.HzxLog("onServiceDisconnected")
    }

    override fun onBindingDied(name: ComponentName) {
        HzxLoger.HzxLog("onBindingDied")
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unbindService(this)
        requireActivity().stopService(mJewxonService)
        if (viewBinding.videoView.isActivated) {
            viewBinding.videoView.release()
        }
    }

    override fun getLocalPicturePath(path: String?) {
        if (!path.isNullOrEmpty()) {
            mFilePath = path
            runOnMainThread({
                withViewBinding {
                    videoViewController.visibility = View.INVISIBLE
                    videoViewPreview.visibility = View.VISIBLE
                    videoPreviewIv.loadImage(path)
                    if (viewBinding.mAutoFitTextureView.isAvailable) {
                        mAutoFitTextureView.visibility = View.GONE
                    }
                }
            })

        }
    }

    override fun getLocalVideoPath(path: String?) {
        if (!path.isNullOrEmpty()) {
            mFilePath = path
            runOnMainThread({
                withViewBinding {
                    videoViewController.visibility = View.GONE
                    videoViewPreview.visibility = View.VISIBLE
                    videoView.visibility = View.VISIBLE
                    videoView.setLooping(true)
                    HzxLoger.HzxLog("播放路径--->$path")
                    videoView.setUrl(path)
                    videoView.setPlayerFactory(IjkPlayerFactory.create())
                    videoView.start()
                    if (viewBinding.mAutoFitTextureView.isAvailable) {
                        mAutoFitTextureView.visibility = View.GONE
                    }
                }
            })
        }
    }

    fun onBackPressed(): Boolean {
        if (mFilePath.isNotEmpty()) {
            withViewBinding {
                videoViewController.visibility = View.VISIBLE
                videoViewPreview.visibility = View.GONE
                videoView.release()
                videoView.visibility = View.GONE
                if (viewBinding.mAutoFitTextureView.isAvailable) {
                    mAutoFitTextureView.visibility = View.VISIBLE
                }
            }
            deleteTempFile()
            stopTimer()
            return true
        }
        return false
    }
}