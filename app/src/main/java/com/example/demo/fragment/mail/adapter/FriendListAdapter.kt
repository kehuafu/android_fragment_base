package com.example.demo.fragment.mail.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.demo.R
import com.example.demo.databinding.LayItemConversationBinding
import com.example.demo.databinding.LayItemFriendBinding
import com.kehuafu.base.core.container.base.adapter.BaseListAdapter
import com.kehuafu.base.core.ktx.loadImage
import com.kehuafu.base.core.ktx.viewBindings
import com.tencent.imsdk.v2.V2TIMFriendInfo

/**
 * 测试列表适配器
 * 1.实体->T：Token
 * 2.视图->VB：LayItemTestBinding
 * 3.初始化->①设置状态监听，②属性委托对应的VB
 * 4.UI状态绑定->当数据的state变化时，对应的UI跟随状态改变
 */
class FriendListAdapter : BaseListAdapter<V2TIMFriendInfo, LayItemFriendBinding>() {

    /**
     * 初始化适配器
     */
    override fun init(parent: ViewGroup): LayItemFriendBinding {
        setStateListener(this)
        val viewBinding by parent.viewBindings<LayItemFriendBinding>(parent)
        return viewBinding
    }

    /**
     * UI状态绑定
     */
    override fun setState(
        item: V2TIMFriendInfo,
        viewBinding: LayItemFriendBinding,
        position: Int
    ) {
        viewBinding.title.text = item.userProfile.nickName
        viewBinding.messageAvatar.loadImage(item.userProfile.faceUrl, R.mipmap.logo)
    }
}