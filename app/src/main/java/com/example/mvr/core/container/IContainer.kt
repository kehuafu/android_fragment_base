package com.example.mvr.core.container

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

interface IContainer {

    fun frameLayoutId(): Int

    fun onInflateArgs(arguments: Bundle)

    fun onInflateLayout(inflater: LayoutInflater, parent: ViewGroup?, attachToRoot: Boolean): View

    fun onViewCreated(savedInstanceState: Bundle?)

    fun onLoadDataSource()

    fun onRelease()

}