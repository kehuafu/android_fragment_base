package com.example.demo.fragment.conversation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo.app.AppManager
import com.example.demo.app.Router
import com.example.demo.chat.ChatActivity
import com.example.demo.databinding.ItemTabMainBinding
import com.example.demo.fragment.conversation.adapter.ConversationListAdapter
import com.example.demo.common.receiver.LocalEventLifecycleViewModel
import com.example.demo.common.receiver.event.LocalLifecycleEvent
import com.example.demo.databinding.FragmentConversationBinding
import com.example.demo.fragment.conversation.bean.Conversation
import com.example.demo.fragment.conversation.mvvm.MessageViewModel
import com.kehuafu.base.core.container.base.BaseFragment
import com.kehuafu.base.core.container.base.adapter.BaseRecyclerViewAdapterV2
import com.kehuafu.base.core.ktx.viewBindings


class ConversationFragment :
    BaseFragment<FragmentConversationBinding, MessageViewModel, MessageViewModel.MessageState>(),
    BaseRecyclerViewAdapterV2.OnItemClickListener<Conversation>,
    LocalEventLifecycleViewModel.OnLocalEventCallback<LocalLifecycleEvent> {

    companion object {
        @JvmStatic
        fun newInstance(): ConversationFragment {
            return ConversationFragment()
        }
    }
    
    private var mConversationListAdapter = ConversationListAdapter()

    override fun onViewCreated(savedInstanceState: Bundle?) {
        mConversationListAdapter.setOnItemClickListener(this)
        AppManager.localEventLifecycleViewModel.register(this, this)
        withViewBinding {
            messageList.itemAnimator = null
            messageList.layoutManager = LinearLayoutManager(baseActivity)
            messageList.adapter = mConversationListAdapter
        }
    }

    override fun onLoadDataSource() {
        super.onLoadDataSource()
        viewModel.getConversationList()
    }

    override fun onStateChanged(state: MessageViewModel.MessageState) {
        super.onStateChanged(state)
        mConversationListAdapter.resetItems(state.conversationList)
        viewBinding.networkError.isVisible = !state.conn
    }

    override fun onItemClick(itemView: View, item: Conversation, position: Int?) {
        ChatActivity.showHasResult(item.name!!)
    }

    override fun onEventCallback(event: LocalLifecycleEvent) {
        when (event) {
            is LocalLifecycleEvent.ReceivedConversationChangedEvent -> {
                viewModel.getConversationList()
            }
            is LocalLifecycleEvent.NetWorkIsConnectedEvent -> {
                viewModel.netWorkStatusChanged(event.conn)
            }
            else -> {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.localEventLifecycleViewModel.unRegister(this)
    }
}