package com.example.demo.fragment.conversation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo.app.Router
import com.example.demo.databinding.ItemTabMainBinding
import com.example.demo.fragment.conversation.adapter.ConversationListAdapter
import com.example.demo.chat.bean.Message
import com.example.demo.databinding.FragmentConversationBinding
import com.example.demo.fragment.conversation.mvvm.MessageViewModel
import com.kehuafu.base.core.container.base.BaseFragment
import com.kehuafu.base.core.container.base.adapter.BaseRecyclerViewAdapterV2
import com.kehuafu.base.core.ktx.viewBindings


class ConversationFragment :
    BaseFragment<FragmentConversationBinding, MessageViewModel, MessageViewModel.MessageState>(),
    BaseRecyclerViewAdapterV2.OnItemClickListener<Message> {

    companion object {
        @JvmStatic
        fun newInstance(): ConversationFragment {
            return ConversationFragment()
        }
    }

    private val itemTab by viewBindings<ItemTabMainBinding>()

    private var mMessageListAdapter = ConversationListAdapter()

    override fun onViewCreated(savedInstanceState: Bundle?) {
        mMessageListAdapter.setOnItemClickListener(this)
        withViewBinding {
            messageList.itemAnimator = null
            messageList.layoutManager = LinearLayoutManager(baseActivity)
        }
    }

    override fun onLoadDataSource() {
        super.onLoadDataSource()
        viewModel.getConversationList()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getConversationList()
    }

    override fun onStateChanged(state: MessageViewModel.MessageState) {
        super.onStateChanged(state)
        Log.e("@@", "onStateChanged:messageList--->" + state.conversationList.size)
        viewBinding.messageList.adapter = mMessageListAdapter
        mMessageListAdapter.resetItems(state.conversationList)
    }

    override fun onItemClick(itemView: View, item: Message, position: Int?) {
        val args = Bundle()
        args.putString("name", item.name)
        baseActivity.navigation(Router.CHAT_FRAGMENT, args)
    }
}