package com.example.demo.video

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.LogUtils
import com.kehuafu.base.core.container.widget.toast.showToast
import xyz.doikki.videocontroller.StandardVideoController
import xyz.doikki.videoplayer.ijk.IjkPlayer
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory
import xyz.doikki.videoplayer.player.VideoView

class VideoFragment : Fragment() {
    private var mFilePath: String? = null

    companion object {
        fun newInstance(filePath: String): VideoFragment {
            val fragment: VideoFragment = VideoFragment()
            val bundle = Bundle()
            bundle.putString("filePath", filePath)//图片路径
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mFilePath = it.getString("filePath")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val videoView =
            VideoView<IjkPlayer>(requireContext())
        val margin: Int =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics)
                .toInt()

        videoView.setPadding(margin, 0, margin, 0)
        mFilePath?.let {
            LogUtils.a("Aaaaaaaa", mFilePath)
            showToast("" + mFilePath)
            val controller = StandardVideoController(requireContext())
            controller.addDefaultControlComponent("", false)
            videoView.setVideoController(controller)
            videoView.setUrl(mFilePath)
            videoView.setPlayerFactory(IjkPlayerFactory.create())
            videoView.start()
        }
        return videoView
    }
}