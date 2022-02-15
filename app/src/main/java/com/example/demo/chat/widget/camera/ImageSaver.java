package com.example.demo.chat.widget.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;

import com.blankj.utilcode.util.ImageUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 作者: hzx
 * 时间: 2019/8/13
 * ========================================
 * 描述:
 */


public class ImageSaver implements Runnable {
    private final Image mImage;
    private final File mFile;
    private JewxonCameraService.PictureCallBack mPictureCallBack;
    private Boolean isFrontCamera = false;

    ImageSaver(Image image, File file, JewxonCameraService.PictureCallBack pictureCallBack, Boolean frontCamera) {
        HzxLoger.HzxLog("ImageSaver--->" + image);
        mImage = image;
        mFile = file;
        mPictureCallBack = pictureCallBack;
        isFrontCamera = frontCamera;
    }

    @Override
    public void run() {
        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        FileOutputStream bos = null;

        try {
            bos = new FileOutputStream(mFile);
            if (!isFrontCamera) {
                bos.write(bytes);
            } else {
                Bitmap temp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Matrix matrix = new Matrix();
                //图片镜像
                matrix.postScale(-1, 1);
                temp = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, true);
                //Bitmap转换成byte[]
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                temp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] datas = baos.toByteArray();
                bos.write(datas);
                temp.recycle();
                mPictureCallBack.getLocalPicturePath(mFile.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mImage.close();
            if (null != bos) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            buffer.clear();
        }
    }
}
