package com.example.mvr.core.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.mvr.core.fragment.constant.NavMode

@SuppressLint("StaticFieldLeak")
object RouterController {

    private var currFragment: Class<out Fragment>? = null

    private lateinit var activity: FragmentActivity

    private var resId: Int = -1

    fun init(context: Context, resId: Int) {
        activity = context as FragmentActivity
        RouterController.resId = resId
    }

    /**
     * 切换Fragment
     */
    fun switcher(
        clazz: Class<out Fragment>,
        bundle: Bundle? = null,
        navMode: NavMode,
        addToBackStack: Boolean = true
    ) {

        if (navMode == NavMode.SWITCH) {
            currFragment = FragmentManagerCore.switcher(
                activity.supportFragmentManager,
                resId,
                currFragment,
                clazz,
                bundle,
                addToBackStack
            )
        } else if (navMode == NavMode.POP_BACK_STACK) {
            currFragment = FragmentManagerCore.pop(activity.supportFragmentManager, clazz)
        }
    }

    /**
     * 替换Fragment
     */
    fun replace(
        clazz: Class<out Fragment>,
        bundle: Bundle? = null,
        navMode: NavMode,
        addToBackStack: Boolean = true
    ) {
        if (navMode == NavMode.REPLACE) {
            currFragment = FragmentManagerCore.replace(
                activity.supportFragmentManager,
                resId,
                clazz,
                bundle,
                addToBackStack
            )
        } else if (navMode == NavMode.POP_BACK_STACK) {
            currFragment = FragmentManagerCore.pop(activity.supportFragmentManager, clazz)
        }
    }
}