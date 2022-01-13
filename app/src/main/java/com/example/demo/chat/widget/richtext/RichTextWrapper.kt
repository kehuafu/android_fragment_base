package com.example.demo.chat.widget.richtext

import android.content.Context
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.util.ArrayMap
import android.util.SparseArray
import android.view.View
import android.widget.TextView
import com.example.demo.chat.widget.richtext.RichTexts.RichTextClickListener

/**
 * Wrap the TextView what need supported rich text.
 *
 * @author Bingding.
 */
class RichTextWrapper(textView: TextView) {
    private val mExtra: SparseArray<Any> = SparseArray<Any>()
    private val mResolvers = HashMap<Class<out Resolver>, Resolver?>()
    private val mListenerMap: ArrayMap<String, RichTextClickListener> =
        ArrayMap<String, RichTextClickListener>()
    private val mTextView: TextView = textView
    fun putExtra(key: Int, value: Any?) {
        mExtra.put(key, value)
    }

    fun addResolver(vararg clazzs: Class<out Resolver>) {
        for (clazz in clazzs) {
            if (!mResolvers.containsKey(clazz)) {
                mResolvers[clazz] = null
            }
        }
    }

    fun setOnRichTextListener(clazz: Class<out Resolver>, listener: RichTextClickListener?) {
        if (!mResolvers.containsKey(clazz)) {
            mResolvers[clazz] = null
        }
        mListenerMap[clazz.simpleName] = listener
    }

    val textView: TextView
        get() = mTextView
    val context: Context
        get() = mTextView.getContext()

    private fun resolveText() {
        if (mTextView.text !is Spannable) {
            mTextView.text = SpannableString(mTextView.text)
        }
        val sp: Spannable = mTextView.text as Spannable
        for (clazz in mResolvers.keys) {
            var resolver = mResolvers[clazz]
            if (resolver == null) {
                try {
                    resolver = clazz.newInstance()
                    mResolvers[clazz] = resolver
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                    continue
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                    continue
                }
            }
            val simpleName = clazz.simpleName
            val listener: RichTextClickListener = mListenerMap[simpleName]!!
            resolver?.resolve(mTextView, sp, mExtra, listener)
        }
    }

    fun setOnClickListener(onClickListener: View.OnClickListener) {
        mTextView.setOnClickListener(onClickListener)
    }

    fun setOnLongClickListener(onLongClickListener: View.OnLongClickListener?) {
        mTextView.setOnLongClickListener(onLongClickListener)
    }

    fun setText(text: CharSequence?) {
        mTextView.text = text
        resolveText()
    }

    init {
        mTextView.setOnLongClickListener { true }
        textView.movementMethod = RTMovementMethod.getInstance()
        mTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                resolveText()
            }
        })
    }
}
