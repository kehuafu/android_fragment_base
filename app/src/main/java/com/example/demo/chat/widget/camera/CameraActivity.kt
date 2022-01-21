package com.example.demo.chat.widget.camera

import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BarUtils
import com.example.demo.R
import com.example.demo.databinding.FragmentCameraPreviewBinding
import com.example.demo.main.mvvm.MainState
import com.example.demo.main.mvvm.MainViewModel
import com.kehuafu.base.core.container.base.BaseActivity
import com.kehuafu.base.core.ktx.show

open class CameraActivity :
    BaseActivity<FragmentCameraPreviewBinding, MainViewModel, MainState>() {

    companion object {
        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()
                ?.show(CameraActivity::class.java)
        }
    }

    private lateinit var mAdapter: CameraPreviewAdapter

    override fun onViewCreated(savedInstanceState: Bundle?) {
        BarUtils.setStatusBarVisibility(this, false)
        overridePendingTransition(R.anim.activity_in_top, R.anim.activity_out_bottom)
        withViewBinding {
            mAdapter = CameraPreviewAdapter(supportFragmentManager)
            viewpager.adapter = mAdapter
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.activity_in_bottom, R.anim.activity_out_top)
    }
}