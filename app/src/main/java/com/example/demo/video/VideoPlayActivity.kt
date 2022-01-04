package com.example.demo.video

import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BarUtils
import com.example.demo.databinding.FragmentVideoPlayBinding
import com.example.demo.main.mvvm.MainState
import com.example.demo.main.mvvm.MainViewModel
import com.kehuafu.base.core.container.base.BaseActivity
import com.kehuafu.base.core.ktx.showHasResult
import xyz.doikki.videocontroller.StandardVideoController
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory

open class VideoPlayActivity : BaseActivity<FragmentVideoPlayBinding, MainViewModel, MainState>() {

    companion object {

        const val EXTRAS_PLAY_URL = "com.example.demo.video.EXTRAS_PLAY_URL"

        @JvmStatic
        fun showHasResult(playUrl: String) {
            ActivityUtils.getTopActivity()
                ?.showHasResult(VideoPlayActivity::class.java) {
                    putString(EXTRAS_PLAY_URL, playUrl)
                }
        }
    }

    private var playUrl: String? = ""

    override fun onInflateArgs(arguments: Bundle) {
        super.onInflateArgs(arguments)
        playUrl = arguments.getString(EXTRAS_PLAY_URL, "")
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        BarUtils.setStatusBarVisibility(this, false)
        withViewBinding {
            val controller = StandardVideoController(this@VideoPlayActivity)
            controller.addDefaultControlComponent("", false)
            videoView.setVideoController(controller)
            videoView.setUrl(playUrl)
            videoView.setPlayerFactory(IjkPlayerFactory.create())
            videoView.start()
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onResume() {
        super.onResume()
        viewBinding.videoView.resume()
    }

    override fun onPause() {
        super.onPause()
        viewBinding.videoView.pause()
    }


    override fun onDestroy() {
        super.onDestroy()
        viewBinding.videoView.clearOnStateChangeListeners()
        viewBinding.videoView.release()
    }
}