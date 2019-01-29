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

更新日志

```
v1.0.8
1.对gif和普通图片做了区分，更好的支持gif动画，第一个图片是gif的时候关闭共享动画，保证gif动作
2.增加了保存到本地的方法，同时验证权限
```
