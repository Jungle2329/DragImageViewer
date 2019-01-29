package com.junglezhang.dragimageviewlib.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Jungle on 2018/12/24 0024.
 *
 * @desc 添加支持gif
 */

public class GlideUtils {

    public static void loadImage(Context mContext, String path, ImageView iv) {
        if (path.endsWith(".gif")) {
            Glide.with(mContext).asGif().load(path).into(iv);
        } else {
            Glide.with(mContext).load(path).into(iv);
        }
    }
}
