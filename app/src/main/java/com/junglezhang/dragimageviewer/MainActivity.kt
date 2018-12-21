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

    //图片随便找的，加载不出来可以换别的
    private val images = arrayOf("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1503684240091&di=a9d641f8424c561d6b5b6051c3b164f5&imgtype=0&src=http%3A%2F%2Ff.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2Fd009b3de9c82d158d14de70c800a19d8bd3e42bb.jpg"
            , "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1503684322968&di=adad2ba7aaeb3c70cf8233a99b5c07ad&imgtype=jpg&src=http%3A%2F%2Fimg2.imgtn.bdimg.com%2Fit%2Fu%3D1312683452%2C3087431303%26fm%3D214%26gp%3D0.jpg")

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
                    sharedElements["tansition_view"] = ll_container.getChildAt(index)
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
