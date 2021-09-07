package com.example.mvr.core.container.delegate

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class BaseViewBindingDelegate<ContainerContext, VB : ViewBinding>(
    val clazz: Class<VB>,
    val thisRefs: ContainerContext? = null
) : ReadOnlyProperty<ContainerContext?, VB>, LifecycleEventObserver {

    private val TAG: String = "BaseViewBindingDelegate"

    private var mCachedVb: VB? = null

    override fun getValue(thisRef: ContainerContext?, property: KProperty<*>): VB {
        return if (mCachedVb == null) {
            val method = clazz.getMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.java
            )
            var layoutInflater: LayoutInflater? = null
            layoutInflater = if (thisRef != null) {
                getLayoutInflater(containerContext = thisRef)
                    ?: throw  NullPointerException(" thisRef=$thisRef is null")
            } else {
                getLayoutInflater(containerContext = thisRefs)
                    ?: throw  NullPointerException(" thisRef=$thisRefs is null")
            }
            @Suppress("UNCHECKED_CAST")
            mCachedVb = method.invoke(null, layoutInflater, null, false) as VB
            mCachedVb!!
        } else {
            mCachedVb!!
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Log.e(TAG, "onStateChanged: source=$source  event=$event  mCachedVb=$mCachedVb")
        if (source.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            destroyed()
        }
    }

    private fun getLayoutInflater(containerContext: ContainerContext?): LayoutInflater? {
        return when (containerContext) {
            is Fragment -> {
                containerContext.layoutInflater
            }
            is Activity -> {
                containerContext.layoutInflater
            }
            is View -> {
                containerContext.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            }
            else -> {
                null
            }
        }
    }

    private fun destroyed() {
        mCachedVb = null
        Log.e(TAG, "destroyed: mCachedVb=$mCachedVb")
    }
}