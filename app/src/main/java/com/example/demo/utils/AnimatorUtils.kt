package com.example.demo.utils

import android.animation.*
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.addListener
import androidx.recyclerview.widget.RecyclerView

class AnimatorUtils {

    companion object {


        @JvmStatic
        fun build(): AnimatorUtils = AnimatorUtils()
    }

    /**
     * 软键盘的过渡动画效果
     */
    fun startTranslateY(view: View, destinationY: Float) {
        val animatorSet = AnimatorSet()

        val currentY: Float = view.translationY
        val translateYAnimator =
            ObjectAnimator.ofFloat(view, "translationY", currentY, destinationY)

        val alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)

        animatorSet.play(translateYAnimator).after(alphaAnimator)
        animatorSet.duration = 100
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.start()
    }

    /**
     * 软键盘的过渡动画效果
     */
    fun startMinimumHeight(
        view: View,
        recyclerView: RecyclerView,
        currentHeight: Int,
        size: Int = 0
    ) {
        val animator: ValueAnimator =
            ValueAnimator.ofInt(view.height, currentHeight)
        //设置求值器
        animator.setEvaluator(ArgbEvaluator())
        animator.interpolator = DecelerateInterpolator()
        //设置动画的播放时长
        animator.duration = 300
        animator.start()
        animator.addUpdateListener {
            Log.e("AnimatorUtils", "startMinimumHeight: " + it.animatedValue)
            view.layoutParams.height = it.animatedValue as Int
            view.requestLayout()
            recyclerView.requestLayout()
            if (it.animatedValue as Int >= currentHeight) {
                recyclerView.scrollToPosition(size)
            }
        }
    }

    fun startMinimumHeights(view: View, currentHeight: Int) {
        val animator: ValueAnimator =
            ValueAnimator.ofInt(view.height, currentHeight)
        //设置求值器
        animator.setEvaluator(ArgbEvaluator())
        animator.interpolator = DecelerateInterpolator()
        //设置动画的播放时长
        animator.duration = 300
        animator.start()
        view.layoutParams.height = 1135
        view.requestLayout()
//        animator.addUpdateListener {
//            Log.e("AnimatorUtils", "startMinimumHeights: " + it.animatedValue)
//            view.layoutParams.height = it.animatedValue as Int
//            view.requestLayout()
//        }
    }
}