package com.junglezhang.dragimageviewer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.SharedElementCallback
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.junglezhang.dragimageviewlib.DragImageViewer
import com.junglezhang.dragimageviewlib.utils.GlideUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val banner0 = "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3609500805,1555342859&fm=26&gp=0.jpg"
    private val banner1 = "https://bbsimg.zhibo8.cc/bbsimg/2019-01-27/20190127184903_9054.gif"
    private val banner2 = "https://bbsimg.zhibo8.cc/bbsimg/2019-01-27/20190127184903_9054.gif"

    private var mReenterState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pictureList = ArrayList<ImageEntity>()
        val mImageEntity0 = ImageEntity(banner0)
        val mImageEntity1 = ImageEntity(banner1)
        val mImageEntity2 = ImageEntity(banner2)
        pictureList.add(mImageEntity0)
        pictureList.add(mImageEntity1)
        pictureList.add(mImageEntity2)
        for (i in 0 until 3) {
            val imageView = ll_container.getChildAt(i) as ImageView
            GlideUtils.loadImage(this, pictureList[i].image, imageView)
            imageView.setOnClickListener { DragImageViewer.startWithElement(this@MainActivity, pictureList, i, imageView) }

        }
        setSharedElementCallback(this)

    }


    /**
     * 接管Activity的setExitSharedElementCallback
     * @param activity
     */
    fun setSharedElementCallback(activity: Activity) {
        ActivityCompat.setExitSharedElementCallback(activity, object : SharedElementCallback() {
            override fun onMapSharedElements(names: List<String>, sharedElements: MutableMap<String, View>) {
                if (mReenterState != null) {
                    val index = mReenterState!!.getInt("index", 0)
                    sharedElements.clear()
                    sharedElements[DragImageViewer.SHARE_VIEW_TAG] = ll_container.getChildAt(index)
                    mReenterState = null
                }
            }
        })
    }

    override fun onActivityReenter(resultCode: Int, data: Intent) {
        super.onActivityReenter(resultCode, data)
        mReenterState = Bundle(data.extras)
    }
}
