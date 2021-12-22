package com.kehuafu.base.core.ktx

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 *
 * on 2019-11-11
 *
 * desc:
 */

const val DEFAULT_REQUEST_CODE = 0x88

@JvmOverloads
fun <ActivityContainer : Activity> Activity.show(
    activityClazz: Class<ActivityContainer>,
    dataUri: Uri? = null,
    args: (Bundle.() -> Unit)? = null
) {
    val intent = Intent(this, activityClazz)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    intent.data = dataUri
    args?.let {
        val bundle = Bundle().apply(args)
        intent.putExtras(bundle)
    }
    startActivity(intent)
}

@JvmOverloads
fun <ActivityContainer : Activity> Activity.showHasResult(
    activityClazz: Class<ActivityContainer>,
    requestCode: Int = DEFAULT_REQUEST_CODE,
    dataUri: Uri? = null,
    args: (Bundle.() -> Unit)? = null
) {
    val intent = Intent(this, activityClazz)
    intent.data = dataUri
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    args?.let {
        val bundle = Bundle().apply(args)
        intent.putExtras(bundle)
    }
    startActivityForResult(intent, requestCode)
}

fun <ActivityContainer : Activity> Fragment.show(
    activityClazz: Class<ActivityContainer>,
    dataUri: Uri? = null,
    args: (Bundle.() -> Unit)? = null
) {
    requireActivity().show(activityClazz, dataUri, args)
}

fun <ActivityContainer : Activity> Fragment.showHasResult(
    activityClazz: Class<ActivityContainer>,
    dataUri: Uri? = null,
    requestCode: Int = DEFAULT_REQUEST_CODE,
    args: (Bundle.() -> Unit)? = null
) {
    val intent = Intent(requireContext(), activityClazz)
    intent.data = dataUri
    args?.let {
        val bundle = Bundle().apply(args)
        intent.putExtras(bundle)
    }
    startActivityForResult(intent, requestCode)
}