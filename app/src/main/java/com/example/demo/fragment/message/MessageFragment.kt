package com.example.demo.fragment.message

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo.app.Router
import com.example.demo.databinding.FragmentMessageBinding
import com.example.demo.databinding.ItemTabMainBinding
import com.example.demo.fragment.message.adapter.MessageListAdapter
import com.example.demo.fragment.message.bean.Message
import com.example.demo.fragment.message.mvvm.MessageViewModel
import com.kehuafu.base.core.container.base.BaseFragment
import com.kehuafu.base.core.container.base.adapter.BaseRecyclerViewAdapterV2
import com.kehuafu.base.core.container.widget.toast.showToast
import com.kehuafu.base.core.ktx.viewBindings


class MessageFragment :
    BaseFragment<FragmentMessageBinding, MessageViewModel, MessageViewModel.MessageState>(),
    BaseRecyclerViewAdapterV2.OnItemClickListener<Message> {

    companion object {
        @JvmStatic
        fun newInstance(): MessageFragment {
            return MessageFragment()
        }
    }

    private val itemTab by viewBindings<ItemTabMainBinding>()

    private var mMessageListAdapter = MessageListAdapter()

    override fun onViewCreated(savedInstanceState: Bundle?) {
        mMessageListAdapter.setOnItemClickListener(this)
        withViewBinding {
            messageList.itemAnimator = null
            messageList.layoutManager = LinearLayoutManager(baseActivity)
        }
    }

    override fun onLoadDataSource() {
        super.onLoadDataSource()
        viewModel.getMessage("123456")
    }

    override fun onStateChanged(state: MessageViewModel.MessageState) {
        super.onStateChanged(state)
        Log.e("@@", "messageList--->" + state.messageList.size)
        viewBinding.messageList.adapter = mMessageListAdapter
        mMessageListAdapter.resetItems(state.messageList)
    }

    override fun onItemClick(itemView: View, item: Message, position: Int) {
        val args = Bundle()
        args.putString("mid", item.mid)
        baseActivity.navigation(Router.CHAT_FRAGMENT, args)
    }
}