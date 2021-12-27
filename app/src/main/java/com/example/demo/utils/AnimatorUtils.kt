package com.example.demo.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator

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
        animatorSet.duration = 80
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.start()
    }
}