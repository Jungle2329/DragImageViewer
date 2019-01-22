# DragImageViewer
[![](https://jitpack.io/v/Jungle2329/DragImageViewer.svg)](https://jitpack.io/#Jungle2329/DragImageViewer)


[使用方法](https://www.jianshu.com/p/7d490d5868a9)


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


调用方法
```

/**
 * Created by Jungle on 2018/12/25 0025.
 *
 * @desc 查看大图使用的工具类，由于这个查看大图的库还在不断完善，可能在调用上会有大的出入，所以所有的操作都尽量
 * 放在这里，方便修改
 */

public class DragImageViewerUtils {

    /**
     * 没有共享元素启动多图查看大图
     * @param mActivity
     * @param list 图片集合
     * @param postion 起始查看图片位置，从0开始
     * @param <T> DragImage子类
     */
    public static <T extends DragImage> void startWithoutElement(Activity mActivity, List<T> list, int postion) {
        DragImageViewer.startWithoutElement(mActivity, list, postion);
    }

    /**
     * 有共享元素启动多图查看大图
     * @param mActivity
     * @param list 图片集合
     * @param postion 起始查看图片位置，从0开始
     * @param view 要共享动画的view
     * @param <T> DragImage子类
     */
    public static <T extends DragImage> void startWithElement(Activity mActivity, List<T> list, int postion, View view) {
        DragImageViewer.startWithElement(mActivity, list, postion, view);
    }

    public static <T extends DragImage> void startWithoutElement(Activity mActivity, T t, int postion) {
        DragImageViewer.startWithoutElement(mActivity, t, postion);
    }

    public static <T extends DragImage> void startWithElement(Activity mActivity, T t, int postion, View view) {
        DragImageViewer.startWithElement(mActivity, t, postion, view);
    }


    /**
     * 接管Activity的setExitSharedElementCallback
     * 针对多图viewpager会把该图片移动走的情况，
     * 待完善，先不用调用
     *
     * @param activity
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void setSharedElementCallback(Activity activity, View view) {
        ActivityCompat.setExitSharedElementCallback(activity, new android.support.v4.app.SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
                sharedElements.put("share_view", view);
            }
        });
    }
}
```
