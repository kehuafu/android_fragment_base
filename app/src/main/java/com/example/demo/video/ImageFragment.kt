package com.example.demo.video

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val imageView = ImageView(context)//这里直接通过代码生成了一个ImageView，和在xml中写实现的效果一样。
        val margin: Int =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics)
                .toInt()

        imageView.setPadding(margin, 0, margin, 0)

        mFilePath?.let {
            //这里对预览的图片宽高进行处理：以屏幕宽度为准进行图片裁剪
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(mFilePath, options)

            val screenW = resources.displayMetrics.widthPixels
            val screenH = resources.displayMetrics.heightPixels

            val resizeW = if (options.outWidth > screenW) screenW else options.outWidth
            val resizeH = if (options.outHeight > screenH) screenH else options.outHeight

            mFilePath?.let {
                imageView.setImageBitmap(ImageResizeUtil.resize(it, resizeW, resizeH))
//                imageView.loadImage(it)
            }
        }
        return imageView
    }
}