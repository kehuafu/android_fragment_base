package com.example.demo.video

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.demo.chat.bean.Message

class PreviewAdapter(fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var mPreviewDataPathList: MutableList<Message> = ArrayList()

    constructor(fragmentManager: FragmentManager, list: MutableList<Message>?) : this(
        fragmentManager
    ) {
        list?.let {
            mPreviewDataPathList.addAll(list)
        }
    }

    override fun getItem(position: Int): Fragment {
        val mediaData: Message = mPreviewDataPathList[position]
        mediaData.takeIf {
            it.isVideo()
        }?.let {
            return VideoFragment.newInstance(it.messageContent!!)
        }
        mediaData.takeIf {
            it.isImage()
        }?.let {
            return ImageFragment.newInstance(it.messageContent!!)
        }
        return Fragment()
    }

    override fun getCount(): Int {
        return mPreviewDataPathList.size
    }
}