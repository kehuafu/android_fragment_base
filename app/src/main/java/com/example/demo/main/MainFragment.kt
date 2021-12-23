package com.example.demo.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.blankj.utilcode.util.NetworkUtils
import com.example.demo.R
import com.example.demo.app.AppManager
import com.example.demo.common.receiver.event.LocalLifecycleEvent
import com.example.demo.databinding.FragmentMainBinding
import com.example.demo.main.mvvm.MainState
import com.example.demo.main.mvvm.MainViewModel
import com.example.demo.fragment.mail.MailListFragment
import com.example.demo.fragment.conversation.ConversationFragment
import com.example.demo.fragment.mine.MineFragment
import com.example.demo.utils.GenerateTestUserSig
import com.kehuafu.base.core.container.base.BaseFragment
import com.kehuafu.base.core.container.widget.toast.showToast
import com.kehuafu.base.core.fragment.widget.TabLayout
import com.tencent.imsdk.v2.*
import java.lang.IndexOutOfBoundsException

class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel, MainState>(), V2TIMCallback,
    NetworkUtils.OnNetworkStatusChangedListener {

    companion object {
        private const val TAG = "MainFragment"
    }

    private val tabTexts = arrayOf("微信", "通讯录", "我")
    private val tabDrawable = intArrayOf(
        R.drawable.message_icon,
        R.drawable.mail_list_icon,
        R.drawable.mine_icon,
    )

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(
            "MAIN_CURRENT_POSITION",
            viewBinding.tab.getCurrentPosition()
        )
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        setTabView(savedInstanceState)
        initViewPager()
    }

    override fun onLoadDataSource() {
        super.onLoadDataSource()
        AppManager.iCloudImManager.login(
            AppManager.currentUserID,
            GenerateTestUserSig.genTestUserSig(AppManager.currentUserID),
            this
        )
        AppManager.iCloudImManager.addIMSDKListener(object : V2TIMSDKListener() {
            override fun onKickedOffline() {
                super.onKickedOffline()
                //TODO:当前用户被踢下线，此时可以 UI 提示用户，并再次调用 V2TIMManager 的 login() 函数重新登录。
                showToast("当前用户被踢下线")
            }
        })
        AppManager.iCloudConversationManager.addConversationListener(object :
            V2TIMConversationListener() {
            override fun onNewConversation(conversationList: MutableList<V2TIMConversation>?) {
                super.onNewConversation(conversationList)
                showToast("有新的会话列表")
                AppManager.localEventLifecycleViewModel.postLifecycleEvent(
                    LocalLifecycleEvent.ReceivedNewConversationEvent(
                        conversationList!!
                    )
                )
            }

            override fun onConversationChanged(conversationList: MutableList<V2TIMConversation>?) {
                super.onConversationChanged(conversationList)
//                showToast("会话列表有更新")
                AppManager.localEventLifecycleViewModel.postLifecycleEvent(
                    LocalLifecycleEvent.ReceivedConversationChangedEvent(
                        conversationList!!
                    )
                )
            }

            override fun onTotalUnreadMessageCountChanged(totalUnreadCount: Long) {
                super.onTotalUnreadMessageCountChanged(totalUnreadCount)
//                showToast("会话未读-->$totalUnreadCount")
            }
        })
        AppManager.iCloudMessageManager.addAdvancedMsgListener(object : V2TIMAdvancedMsgListener() {
            override fun onRecvNewMessage(msg: V2TIMMessage?) {
                super.onRecvNewMessage(msg)
//                showToast("收到新的消息")
                AppManager.localEventLifecycleViewModel.postLifecycleEvent(
                    LocalLifecycleEvent.ReceivedChatMsgEvent(
                        msg!!
                    )
                )
            }

            override fun onRecvMessageRevoked(msgID: String?) {
                super.onRecvMessageRevoked(msgID)
                showToast("消息已被撤回！")
            }
        })

        NetworkUtils.registerNetworkStatusChangedListener(this)
        NetworkUtils.isAvailableAsync {
            AppManager.localEventLifecycleViewModel.postLifecycleEvent(
                LocalLifecycleEvent.NetWorkIsConnectedEvent(
                    conn = it
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.iCloudImManager.removeIMSDKListener(object : V2TIMSDKListener() {
        })
        AppManager.iCloudConversationManager.removeConversationListener(object :
            V2TIMConversationListener() {})
        AppManager.iCloudImManager.logout(this)
        NetworkUtils.unregisterNetworkStatusChangedListener(this)
    }

    private fun initViewPager() {
        val viewpager = viewBinding.viewpager
        viewpager.offscreenPageLimit = 2
        viewpager.adapter = object :
            FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getCount(): Int {
                return 3
            }

            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> {
                        ConversationFragment.newInstance()
                    }
                    1 -> {
                        MailListFragment.newInstance()
                    }
                    2 -> {
                        MineFragment.newInstance()
                    }
                    else -> {
                        throw IndexOutOfBoundsException()
                    }
                }
            }
        }
    }


    private fun setTabView(savedInstanceState: Bundle?) {
        val tab = viewBinding.tab
        tab.setTabMod(TabLayout.MODE.AUTO)
        for (i in 0..2) {
            val layoutInflater = LayoutInflater.from(requireView().context)
            val tabView: View = layoutInflater.inflate(R.layout.item_tab_main, null)
            val imgTab = tabView.findViewById<ImageView>(R.id.iv_tab_icon)
            val txtTab = tabView.findViewById<TextView>(R.id.tv_tab_name)
            imgTab.setImageDrawable(ContextCompat.getDrawable(imgTab.context, tabDrawable[i]))
            imgTab.setColorFilter(ContextCompat.getColor(imgTab.context, R.color.tab_default))
            txtTab.setTextColor(ContextCompat.getColor(txtTab.context, R.color.tab_default))
            txtTab.text = tabTexts[i]
            tab.addTab(tabView)
        }
        tab.setupWithViewPager(viewBinding.viewpager)
        tab.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tabView: View, position: Int, isRefresh: Boolean) {
                val imgTab = tabView.findViewById<ImageView>(R.id.iv_tab_icon)
                val txtTab = tabView.findViewById<TextView>(R.id.tv_tab_name)
                imgTab.setColorFilter(ContextCompat.getColor(imgTab.context, R.color.tab_selected))
                txtTab.setTextColor(ContextCompat.getColor(txtTab.context, R.color.tab_selected))
                viewBinding.title.text = tabTexts[position]
            }

            override fun onTabUnselected(tabView: View, position: Int) {
                val imgTab = tabView.findViewById<ImageView>(R.id.iv_tab_icon)
                val txtTab = tabView.findViewById<TextView>(R.id.tv_tab_name)
                imgTab.setColorFilter(ContextCompat.getColor(imgTab.context, R.color.tab_default))
                txtTab.setTextColor(ContextCompat.getColor(txtTab.context, R.color.tab_default))
            }
        })
        tab.selectTab(savedInstanceState?.getInt("MAIN_CURRENT_POSITION") ?: 0)
    }

    override fun onSuccess() {
        showToast("IM 登录成功！")
    }

    override fun onError(p0: Int, p1: String?) {
        showToast("IM 登录失败！-->$p1")
    }

    override fun onDisconnected() {
        showToast("网络不可用")
        AppManager.localEventLifecycleViewModel.postLifecycleEvent(
            LocalLifecycleEvent.NetWorkIsConnectedEvent(
                conn = false
            )
        )
    }

    override fun onConnected(networkType: NetworkUtils.NetworkType?) {
        showToast("网络已连接")
        AppManager.iCloudImManager.login(
            AppManager.currentUserID,
            GenerateTestUserSig.genTestUserSig(AppManager.currentUserID),
            this
        )
        AppManager.localEventLifecycleViewModel.postLifecycleEvent(
            LocalLifecycleEvent.NetWorkIsConnectedEvent(
                conn = true
            )
        )
    }
}