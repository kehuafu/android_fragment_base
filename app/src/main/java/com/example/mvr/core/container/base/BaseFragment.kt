package com.example.mvr.core.container.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.mvr.core.container.IContainer
import com.example.mvr.core.container.inject.injectVB
import com.example.mvr.core.container.inject.injectVM
import com.example.mvr.core.fragment.RouterController
import com.example.mvr.core.mvvm.BaseViewModel
import com.example.mvr.core.redux.IState
import com.example.mvr.core.viewbinding.IViewBinding

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel<State>, State : IState> :
    Fragment(), IContainer, IViewBinding {


    /**
     * 获取baseActivity方便调用navigation方法进行页面切换
     */
    protected val baseActivity by lazy {
        requireActivity() as BaseActivity<*, *, *>
    }

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1.inflate View and set View
        val rootView =
            onInflateLayout(inflater = inflater, parent = container, attachToRoot = false)
        rootView.setOnTouchListener { v, event -> true }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 3.extra check
        checkArgs()
        // 4.view created
        onViewCreated(savedInstanceState)
        // 5.subscribe state changed
        viewModel.subscribe(lifecycleOwner = this, subscriber = { state: State ->
            onStateChanged(state)
        })
        // 6.load dataSource
        onLoadDataSource()
    }

    override fun frameLayoutId(): Int {
        return -1
    }

    override fun onInflateArgs(arguments: Bundle) {
    }

    override fun onInflateLayout(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToRoot: Boolean
    ): View {
        return viewBinding.root
    }

    override fun onLoadDataSource() {

    }

    open fun onStateChanged(state: State) {

    }

    override fun onRelease() {

    }

    private fun checkArgs() {
        arguments?.let {
            onInflateArgs(it)
        }
    }
}