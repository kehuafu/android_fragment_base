@file:Suppress("UNCHECKED_CAST")

package com.example.mvr.core.container.inject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.mvr.core.container.IContainer
import com.example.mvr.core.mvvm.BaseViewModel
import com.example.mvr.core.viewbinding.ViewBindingLazy
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


fun <VM : BaseViewModel<*>> AppCompatActivity.injectVM(clazz: Class<*>): VM {
    val classType = findClassFromGenericParameters<VM>(clazz, 1)
    return createLazyViewModel(this as IContainer, classType)
}

fun <VM : BaseViewModel<*>> Fragment.injectVM(clazz: Class<*>): VM {
    val classType = findClassFromGenericParameters<VM>(clazz, 1)
    return createLazyViewModel(this as IContainer, classType)
}

fun <VB : ViewBinding> AppCompatActivity.injectVB(
    clazz: Class<*>,
    layoutInflater: LayoutInflater,
    viewGroup: ViewGroup?,
    attachToRoot: Boolean
): VB {
    val classType = findClassFromGenericParameters<VB>(clazz, 0)
    return createLazyViewBing(classType, layoutInflater, viewGroup, attachToRoot)
}

fun <VB : ViewBinding> Fragment.injectVB(
    clazz: Class<*>,
    layoutInflater: LayoutInflater,
    viewGroup: ViewGroup?,
    attachToRoot: Boolean
): VB {
    val classType = findClassFromGenericParameters<VB>(clazz, 0)
    return createLazyViewBing(classType, layoutInflater, viewGroup, attachToRoot)
}

fun <VM : BaseViewModel<*>> createLazyViewModel(iContainer: IContainer, clazz: Class<VM>): VM {
    var viewModelStore: ViewModelStore? = null
    var factoryPromise: (() -> ViewModelProvider.Factory)? = null
    when (iContainer) {
        is AppCompatActivity -> {
            factoryPromise = {
                iContainer.defaultViewModelProviderFactory
            }
            viewModelStore = iContainer.viewModelStore
        }
        is Fragment -> {
            factoryPromise = {
                iContainer.defaultViewModelProviderFactory
            }
            viewModelStore = iContainer.viewModelStore
        }
    }
    return ViewModelLazy(clazz.kotlin, { viewModelStore!! }, factoryPromise!!).value
}

fun <VB : ViewBinding> createLazyViewBing(
    clazz: Class<VB>,
    inflater: LayoutInflater,
    viewGroup: ViewGroup?,
    attachToRoot: Boolean
): VB {
    return ViewBindingLazy(clazz, inflater, viewGroup, attachToRoot).value
}

fun <T : Any> findClassFromGenericParameters(clazz: Class<*>, position: Int): Class<T> {
    val type: Type? = clazz.genericSuperclass
    val classType = (type as ParameterizedType).actualTypeArguments[position] ?: null
    return classType as Class<T>
}