package com.example.demo.chat.widget.richtext

import android.text.Spannable
import android.util.SparseArray
import android.widget.TextView

/**
 * Interface definition for a resolver that resolve provided data to your rich text.
 *
 * @author Bingding.
 */
interface Resolver {
    /**
     * Resolve your rich text here.
     *
     * @param textView the textView display rich text;
     * @param sp the content of TextView;
     * @param extra extra data if exist；
     * @param listener Callback if need;
     */
    fun resolve(
        textView: TextView,
        sp: Spannable,
        extra: SparseArray<Any>,
        listener: RichTexts.RichTextClickListener
    )
}
