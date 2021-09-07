package com.example.mvr.core.container.delegate

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class ViewLifecycle(v: View) : LifecycleOwner, View.OnAttachStateChangeListener {
    private var mLifecycleRegistry: LifecycleRegistry? = null

    init {
        mLifecycleRegistry = LifecycleRegistry(this)
        v.addOnAttachStateChangeListener(this)
        if (v.isAttachedToWindow) {
            handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry!!
    }

    private fun handleLifecycleEvent(event: Lifecycle.Event) {
        mLifecycleRegistry?.handleLifecycleEvent(event)
    }

    override fun onViewAttachedToWindow(v: View?) {
        handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onViewDetachedFromWindow(v: View?) {
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
}