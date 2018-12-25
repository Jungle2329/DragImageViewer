# DragImageViewer
[![](https://jitpack.io/v/Jungle2329/DragImageViewer.svg)](https://jitpack.io/#Jungle2329/DragImageViewer)


启动DragImageViewer
```
DragImageViewer.startWithElement(this@MainActivity, pictureList, position, imageView)
setSharedElementCallback(this)
```


实现共享元素动画
```
/**
* 接管Activity的setExitSharedElementCallback
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
```
