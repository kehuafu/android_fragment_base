package com.example.demo.chat.widget.camera

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.demo.chat.bean.Message
import com.kehuafu.base.core.container.widget.toast.showToast

class CameraPreviewAdapter(fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return CameraFragment()
    }

    override fun getCount(): Int {
        return 1
    }
}