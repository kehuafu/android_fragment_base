package com.example.demo.chat.widget.camera

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import com.example.demo.databinding.FragmentCameraBinding
import com.example.demo.main.mvvm.MainState
import com.example.demo.main.mvvm.MainViewModel
import com.kehuafu.base.core.container.base.BaseFragment

class CameraFragment : BaseFragment<FragmentCameraBinding, MainViewModel, MainState>(),
    ServiceConnection {

    private var mIsRecordingVideo = false
    private var mService: JewxonCameraService? = null
    private var mJewxonService: Intent? = null

    override fun onViewCreated(savedInstanceState: Bundle?) {
        mJewxonService = Intent(requireActivity(), JewxonCameraService::class.java)
        requireActivity().startService(mJewxonService)
        requireActivity().bindService(mJewxonService, this, Context.BIND_AUTO_CREATE)

        withViewBinding {
            closeCamera.setOnClickListener {
                requireActivity().onBackPressed()
            }
            frameLayoutPhoto.setOnClickListener {
                mService!!.takePicture()
            }
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        HzxLoger.HzxLog("onServiceConnected----componentName:$name")
        val localServiceBinder: JewxonCameraService.LocalServiceBinder =
            service as JewxonCameraService.LocalServiceBinder
        mService = localServiceBinder.service
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
    }
}