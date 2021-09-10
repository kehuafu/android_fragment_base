package com.kehuafu.base.core.container.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.kehuafu.base.core.container.IContainer
import com.kehuafu.base.core.container.inject.injectVB
import com.kehuafu.base.core.container.inject.injectVM
import com.kehuafu.base.core.fragment.RouterController
import com.kehuafu.base.core.fragment.constant.NavMode
import com.kehuafu.base.core.mvvm.BaseViewModel
import com.kehuafu.base.core.redux.IState
import com.kehuafu.base.core.viewbinding.IViewBinding


abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel<State>, State : IState> :
    AppCompatActivity(),
    IContainer, IViewBinding {

    protected val viewModel: VM by lazy {
        return@lazy injectVM(this.javaClass)
    }

    override val viewBinding: VB by lazy {
        return@lazy injectVB(this.javaClass, layoutInflater, null, false)
    }

    @Suppress("UNCHECKED_CAST")
    protected fun withViewBinding(block: VB.() -> Unit) {
        block.invoke(viewBinding)
    }

    @Suppress("UNCHECKED_CAST")
    protected fun withViewBinding(): VB {
        return viewBinding
    }

    private val onBackPressedListeners: MutableMap<String, OnBackPressedListener> = HashMap()

    private var exitTime = 0L

    override fun frameLayoutId(): Int {
        return -1
    }

    /**
     * 导航方法，根据路由名跳转
     */
    abstract fun navigation(
        name: Int,
        bundle: Bundle? = null,
        navMode: NavMode = NavMode.SWITCH,
    )

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
        // 2.extra check
        checkArgs()
        if (null == intent?.extras) return
        onLoadDataSource()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1.set view
        setContentView(viewBinding.root)
        // 2.extra check
        checkArgs()
        // 4.Router init
        RouterController.init(this, frameLayoutId())
        // 3.view created
        onViewCreated(savedInstanceState)
        // 5.subscribe state changed
        viewModel.subscribe(lifecycleOwner = this, subscriber = { state: State ->
            onStateChanged(state)
        })
        // 6.load dataSource
        onLoadDataSource()
    }

    override fun onInflateLayout(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToRoot: Boolean
    ): View {
        return viewBinding.root
    }

    override fun onInflateArgs(arguments: Bundle) {
    }

    override fun onLoadDataSource() {
    }

    open fun onStateChanged(state: State) {

    }

    override fun onRelease() {}

    private fun checkArgs() {
        intent.extras?.let {
            onInflateArgs(it)
        }
    }

    override fun onDestroy() {
        onRelease()
        super.onDestroy()
    }

    override fun onBackPressed() {
        for (name in onBackPressedListeners.keys) {
            val listener = onBackPressedListeners[name]
            if (verifyFragment(name) && listener != null && listener.onBackPressed()) {
                return
            }
        }
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        } else {
            if (System.currentTimeMillis() - exitTime > 2000) {
                exitTime = System.currentTimeMillis()
                Toast.makeText(this, "再按一次返回桌面", Toast.LENGTH_SHORT).show()
            } else {
                moveTaskToBack(true)
            }
        }
    }

    /**
     * 验证目标Fragment是否为当前显示Fragment
     *
     * @param fragmentName Fragment
     * @return boolean
     */
    private fun verifyFragment(fragmentName: String): Boolean {
        return supportFragmentManager.findFragmentByTag(fragmentName) != null
    }

    /**
     * 注册返回键监听事件
     * 注册成功后拦截返回键事件并传递给监听者
     *
     * @param fragmentName Fragment
     * @param listener     OnBackPressedListener
     */
    fun registerOnBackPressedListener(
        fragmentName: String,
        listener: OnBackPressedListener
    ) {
        onBackPressedListeners[fragmentName] = listener
    }

    /**
     * 移除返回键监听事件
     *
     * @param fragmentName Fragment
     */
    fun removerOnBackPressedListener(fragmentName: String) {
        onBackPressedListeners.remove(fragmentName)
    }
}

/**
 * 返回键监听事件
 */
interface OnBackPressedListener {
    fun onBackPressed(): Boolean
}