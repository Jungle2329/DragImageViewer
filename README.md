# DragImageViewer
[![](https://jitpack.io/v/Jungle2329/DragImageViewer.svg)](https://jitpack.io/#Jungle2329/DragImageViewer)

引入
```
dependencies {
        implementation 'com.github.Jungle2329:DragImageViewer:Tag'
}
```

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
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
private void setSharedElementCallback(Activity activity) {
ActivityCompat.setExitSharedElementCallback(activity, new android.support.v4.app.SharedElementCallback() {
    @Override
    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
        super.onMapSharedElements(names, sharedElements);
        sharedElements.put(DragImageViewer.SHARE_VIEW_TAG, ll_user_anwser);
    }
});
}
```
