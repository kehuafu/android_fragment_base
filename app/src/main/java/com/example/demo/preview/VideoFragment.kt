package com.example.demo.preview

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.demo.R
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
            bundle.putString("filePath", filePath)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var videoView: VideoView<IjkPlayer>


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
        videoView = VideoView<IjkPlayer>(requireContext())
        val margin: Int =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics)
                .toInt()
        videoView.setPadding(margin, 0, margin, 0)
        mFilePath?.let {
            val controller = StandardVideoController(requireContext())
            controller.addDefaultControlComponent("", false)
            videoView.setVideoController(controller)
            videoView.setLooping(true)
            videoView.setUrl(mFilePath)
            videoView.setPlayerFactory(IjkPlayerFactory.create())
        }
        return videoView
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            videoView.start()
        } else {
            if (videoView.isPlaying) {
                videoView.pause()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        videoView.resume()
    }

    override fun onPause() {
        super.onPause()
        videoView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.release()
    }
}