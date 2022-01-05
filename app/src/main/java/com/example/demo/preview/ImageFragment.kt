package com.example.demo.preview

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.demo.utils.ImageResizeUtil
import com.kehuafu.base.core.ktx.loadImage

class ImageFragment : Fragment() {
    private var mFilePath: String? = null

    companion object {
        fun newInstance(filePath: String): ImageFragment {
            val fragment: ImageFragment = ImageFragment()
            val bundle = Bundle()
            bundle.putString("filePath", filePath)//图片路径
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mFilePath = it.getString("filePath")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val imageView = ImageView(context)
        val margin: Int =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics)
                .toInt()
        imageView.setPadding(margin, 0, margin, 0)
        imageView.loadImage(mFilePath)
        return imageView
    }
}