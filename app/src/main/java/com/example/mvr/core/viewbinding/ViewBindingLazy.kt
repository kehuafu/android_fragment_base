package com.example.mvr.core.viewbinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

class ViewBindingLazy<VB : ViewBinding>(
    private val vbClazz: Class<VB>,
    private val inflater: LayoutInflater?,
    private val parent: ViewGroup?,
    private val attachToParent: Boolean?
) : Lazy<VB> {

    private var cache: VB? = null

    @Suppress("UNCHECKED_CAST")
    override val value: VB
        get() = if (cache == null) {
            val method = vbClazz.getDeclaredMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.java
            )
            method.isAccessible = true
            val invoke = method.invoke(null, inflater, parent, attachToParent)
            invoke as VB
        } else {
            cache!!
        }

    override fun isInitialized(): Boolean = cache != null
}