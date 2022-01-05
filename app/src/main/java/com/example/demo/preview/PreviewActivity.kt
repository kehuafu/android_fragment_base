package com.example.demo.preview

import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BarUtils
import com.example.demo.chat.bean.Message
import com.example.demo.databinding.FragmentImagePreviewBinding
import com.example.demo.main.mvvm.MainState
import com.example.demo.main.mvvm.MainViewModel
import com.kehuafu.base.core.container.base.BaseActivity
import com.kehuafu.base.core.ktx.showHasResult
import com.kehuafu.base.core.ktx.toObj

open class PreviewActivity :
    BaseActivity<FragmentImagePreviewBinding, MainViewModel, MainState>() {

    companion object {

        const val EXTRAS_PLAY_URL = "com.example.demo.video.EXTRAS_PLAY_URL"
        const val EXTRAS_POSITION = "com.example.demo.video.EXTRAS_POSITION"

        @JvmStatic
        fun showHasResult(playUrl: String, position: Int) {
            ActivityUtils.getTopActivity()
                ?.showHasResult(PreviewActivity::class.java) {
                    putString(EXTRAS_PLAY_URL, playUrl)
                    putInt(EXTRAS_POSITION, position)
                }
        }
    }

    private var previewDataList: MutableList<Message> = mutableListOf()
    private var position: Int = 0

    private lateinit var mAdapter: PreviewAdapter

    override fun onInflateArgs(arguments: Bundle) {
        super.onInflateArgs(arguments)
        previewDataList = arguments.getString(EXTRAS_PLAY_URL, "").toObj()
        position = arguments.getInt(EXTRAS_POSITION, 0)
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        BarUtils.setStatusBarVisibility(this, false)
        withViewBinding {
            previewDataList.reverse()
            mAdapter = PreviewAdapter(supportFragmentManager, previewDataList)
            viewpager.adapter = mAdapter
            viewpager.currentItem = previewDataList.size - 1 - position
        }
    }

    override fun onBackPressed() {
        finish()
    }
}