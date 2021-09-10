package com.example.demo.test.adapter

import android.view.ViewGroup
import com.example.demo.databinding.LayItemTestBinding
import com.example.demo.test.bean.Token
import com.kehuafu.base.core.container.base.adapter.BaseListAdapter
import com.kehuafu.base.core.ktx.viewBindings

/**
 * 测试列表适配器
 * 1.实体->T：Token
 * 2.视图->VB：LayItemTestBinding
 * 3.初始化->①设置状态监听，②属性委托对应的VB
 * 4.UI状态绑定->当数据的state变化时，对应的UI跟随状态改变
 */
class TestListAdapter : BaseListAdapter<Token, LayItemTestBinding>() {

    /**
     * 初始化适配器
     */
    override fun init(parent: ViewGroup): LayItemTestBinding {
        setStateListener(this)
        val viewBinding by parent.viewBindings<LayItemTestBinding>(parent)
        return viewBinding
    }

    /**
     * UI状态绑定
     */
    override fun setState(item: Token, viewBinding: LayItemTestBinding) {
        //TODO:UI与状态绑定，例如
        viewBinding.tvContent.text = item.token
    }
}