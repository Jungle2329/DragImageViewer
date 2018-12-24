package com.junglezhang.dragimageviewer

import com.junglezhang.dragimageviewlib.base.DragImage

/**
 * Created by Jungle on 2018/12/24 0024.
 * @desc TODO
 */
class ImageEntity(var image: String = "") : DragImage {

    override fun getImageUrl(): String {
        return image
    }

}