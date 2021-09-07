package com.example.mvr.test

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvr.test.mvr.TestState
import com.example.mvr.test.mvr.TestViewModel
import com.example.mvr.core.container.base.BaseActivity
import com.example.mvr.core.container.base.adapter.BaseRecyclerViewAdapterV2
import com.example.mvr.core.container.widget.toast.showToast
import com.example.mvr.databinding.ActivityTestBinding
import com.example.mvr.test.adapter.TestListAdapter
import com.example.mvr.test.bean.Token

/**
 * 【测试模块使用示例】
 * 当前视图：ActivityLoginBinding
 * 当前ViewModel:LoginViewModel
 * 登录状态：LoginState
 */
class TestActivity : BaseActivity<ActivityTestBinding, TestViewModel, TestState>(),
    BaseRecyclerViewAdapterV2.OnItemClickListener<Token> {
    companion object {
        private const val TAG = "LoginActivity--->"
    }

    private var mTestListAdapter = TestListAdapter()
    private val data = mutableListOf<Token>()

    override fun onViewCreated() {
        mTestListAdapter.setOnItemClickListener(this)
        withViewBinding {
            rvTest.itemAnimator = null
            rvTest.layoutManager = LinearLayoutManager(this@TestActivity)
            rvTest.adapter = mTestListAdapter
            val token = Token(
                token = "888",
                uid = "xxx222",
                init = false
            )
            data.add(token)
        }
        withViewBinding().btnChange.setOnClickListener {
            viewModel.test("xxx222", "888")
            val token = Token(
                token = "888",
                uid = "xxx222",
                init = false
            )
            data.add(token)
        }
    }

    override fun onLoadDataSource() {
        super.onLoadDataSource()
        Log.d(TAG, "onLoadDataSource: ")
        viewModel.test("xxx111", "456")
    }

    @SuppressLint("SetTextI18n")
    override fun onStateChanged(state: TestState) {
        super.onStateChanged(state)
        Log.d(TAG, "onStateChanged: " + state.token)
        //withViewBinding().uidText.text = state.token.uid
        withViewBinding {
            uidText.text = "uid:${state.token.uid}"
            tokenText.text = "token:${state.token.token}"
        }
        mTestListAdapter.resetItems(data)
    }

    override fun onItemClick(itemView: View, item: Token, position: Int) {
        showToast("点击了$position")
    }
}