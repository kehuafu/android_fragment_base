package com.example.demo.chat.widget.camera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
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

class CameraFragment : BaseFragment<FragmentCameraBinding, MainViewModel, MainState>(),
    ServiceConnection, JewxonCameraService.PictureCallBack {

    private var mIsRecordingVideo = false
    private var mIsBackCamera = true //默认后置摄像头
    private var mService: JewxonCameraService? = null
    private var mJewxonService: Intent? = null
    private var mFilePath: String = ""

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
                mService!!.takePicture()
            }

            frameLayoutPhoto.setOnLongClickListener {
                showToast("开始摄像")
                mIsRecordingVideo = true
                mService!!.startRecordingVideo(mAutoFitTextureView)
                true
            }

            frameLayoutPhoto.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        HzxLoger.HzxLog("ACTION_DOWN")
                    }
                    MotionEvent.ACTION_UP -> {
                        HzxLoger.HzxLog("ACTION_UP")
                        if (mIsRecordingVideo) {
                            mService!!.stopRecordingVideo(mAutoFitTextureView)
                            mIsRecordingVideo = false
                        }
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        if (mIsRecordingVideo) {
                            mService!!.stopRecordingVideo(mAutoFitTextureView)
                            mIsRecordingVideo = false
                        }
                    }
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
            return true
        }
        return false
    }
}