package com.junglezhang.dragimageviewer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.SharedElementCallback
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.junglezhang.dragimageviewlib.DragImageViewer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val banner1 = "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3609500805,1555342859&fm=26&gp=0.jpg"
    private val banner2 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1544591288175&di=d82793880e749f5c8992427887d26f5e&imgtype=0&src=http%3A%2F%2Fjtgeek.com%2Fwp-content%2Fuploads%2Fandroid-logo.jpg"
    private val images = arrayOf(banner1, banner2)

    private var mReenterState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pictureList = ArrayList<String>()
        pictureList.addAll(Arrays.asList(*images))

        val cnt = Math.min(images.size, ll_container.childCount)
        for (i in 0 until cnt) {
            val imageView = ll_container.getChildAt(i) as ImageView
            Glide.with(this).load(images[i]).into(imageView)
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
                    sharedElements["share_view"] = ll_container.getChildAt(index)
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
