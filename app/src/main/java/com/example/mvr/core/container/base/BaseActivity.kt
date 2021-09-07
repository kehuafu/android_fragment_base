package com.example.mvr.core.container.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.mvr.core.container.IContainer
import com.example.mvr.core.container.inject.injectVB
import com.example.mvr.core.container.inject.injectVM
import com.example.mvr.core.mvvm.BaseViewModel
import com.example.mvr.core.redux.IState
import com.example.mvr.core.viewbinding.IViewBinding


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
        // 3.view created
        onViewCreated()
        // 4.subscribe state changed
        viewModel.subscribe(lifecycleOwner = this, subscriber = { state: State ->
            onStateChanged(state)
        })
        // 5.load dataSource
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
}