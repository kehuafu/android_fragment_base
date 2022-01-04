package com.example.demo.fragment.mine

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import com.blankj.utilcode.util.LogUtils
import com.example.demo.R
import com.example.demo.app.AppManager
import com.example.demo.databinding.FragmentMineBinding
import com.example.demo.fragment.mine.mvvm.MineViewModel
import com.example.demo.utils.UriUtil
import com.kehuafu.base.core.container.base.BaseFragment
import com.kehuafu.base.core.ktx.loadImage

open class MineFragment :
    BaseFragment<FragmentMineBinding, MineViewModel, MineViewModel.MineState>() {

    companion object {
        @JvmStatic
        fun newInstance(): MineFragment {
            return MineFragment()
        }
    }

    private var mUserNickName: String = ""
    private var mUserAvatar: String = ""


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(savedInstanceState: Bundle?) {
        withViewBinding {
            userIv.setOnClickListener {
                launchAlbum()
            }
            userEt.doAfterTextChanged {
                mUserNickName = userEt.text.trim().toString()
            }
            userIdTv.text = "UserId:" + AppManager.currentUserID
            userSaveTv.setOnClickListener {
                viewModel.setSelfInfo(
                    mUserNickName,
                    mUserAvatar
                )
            }
        }
    }

    override fun onLoadDataSource() {
        super.onLoadDataSource()
        viewModel.getSelfInfo()
    }

    override fun onStateChanged(state: MineViewModel.MineState) {
        super.onStateChanged(state)
        if (state.selfInfo != null) {
            withViewBinding {
                userIv.loadImage(state.selfInfo.faceUrl, R.drawable.avatar)
                userEt.setText(state.selfInfo.nickName)
            }
        }
        if (state.modify) {
            state.modify = false
            viewModel.getSelfInfo()
        }
    }

    //选取图片
    private val mLauncherAlbum = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        LogUtils.a("aaaaaaa", "mLauncherAlbum--->", it)
//        if (it) return@registerForActivityResult
        mUserAvatar = UriUtil.getFileAbsolutePath(requireContext(), it)
        viewBinding.userIv.loadImage(mUserAvatar)
    }

    //调用相册选择图片
    private fun launchAlbum() {
        mLauncherAlbum.launch("image/*")
    }
}