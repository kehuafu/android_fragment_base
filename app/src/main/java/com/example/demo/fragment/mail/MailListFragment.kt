package com.example.demo.fragment.mail

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo.chat.ChatActivity
import com.example.demo.databinding.FragmentMailListBinding
import com.example.demo.fragment.mail.adapter.FriendListAdapter
import com.example.demo.fragment.mail.mvvm.MailViewModel
import com.kehuafu.base.core.container.base.BaseFragment
import com.kehuafu.base.core.container.base.adapter.BaseRecyclerViewAdapterV2
import com.kehuafu.base.core.container.widget.toast.showToast
import com.tencent.imsdk.v2.V2TIMFriendInfo

class MailListFragment :
    BaseFragment<FragmentMailListBinding, MailViewModel, MailViewModel.MailState>(),
    BaseRecyclerViewAdapterV2.OnItemClickListener<V2TIMFriendInfo> {

    companion object {
        @JvmStatic
        fun newInstance(): MailListFragment {
            return MailListFragment()
        }
    }

    private var mUserId = ""

    private var mFriendListAdapter = FriendListAdapter()

    override fun onViewCreated(savedInstanceState: Bundle?) {
        mFriendListAdapter.setOnItemClickListener(this)
        withViewBinding {
            friendListRv.itemAnimator = null
            friendListRv.layoutManager = LinearLayoutManager(baseActivity)
            friendListRv.adapter = mFriendListAdapter
            userIdEt.doAfterTextChanged {
                mUserId = userIdEt.text.trim().toString()
            }
            addFriendsIv.setOnClickListener {
                viewModel.addFriend(mUserId)
                userIdEt.text.clear()
            }
        }
    }

    override fun onLoadDataSource() {
        super.onLoadDataSource()
        viewModel.getFriendList()
    }

    override fun onStateChanged(state: MailViewModel.MailState) {
        super.onStateChanged(state)
        if (state.add) {
            state.add = false
            showToast("添加成功")
            viewModel.getFriendList()
        }
        if (state.list.isNotEmpty()) {
            mFriendListAdapter.resetItems(state.list)
        }
    }

    override fun onItemClick(itemView: View, item: V2TIMFriendInfo, position: Int?) {
        ChatActivity.showHasResult(item.userID)
    }
}