package com.junglezhang.dragimageviewlib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.junglezhang.dragimageviewlib.base.DragImage;
import com.junglezhang.dragimageviewlib.utils.GlideUtils;
import com.junglezhang.dragimageviewlib.utils.PermissionHelper;
import com.junglezhang.dragimageviewlib.widget.BaseDragViewPager;
import com.junglezhang.dragimageviewlib.widget.ScaleViewPager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * Created by Jungle on 2018/12/24 0024.
 *
 * @desc ViewPager容器
 */
public class DragImageViewer extends AppCompatActivity {

    public static final String SHARE_VIEW_TAG = "share_view";
    //首先显示的图片的位置
    private int firstDisplayImageIndex = 0;
    //确认是新的一页
    private boolean newPageSelected = false;
    //当前PhotoView
    private PhotoView mCurImage;
    private ScaleViewPager imageViewPager;
    private List<String> pictureList;
    private TextView tv_current_page;
    private TextView tv_download;
    private PagerAdapter adapter;
    private PermissionHelper mPermissionHelper;

    /**
     * 无需共享元素动画的单图
     *
     * @param context
     * @param image
     * @param firstIndex
     * @param <T>
     */
    public static <T extends DragImage> void startWithoutElement(Activity context, T image, int firstIndex) {
        startWithElement(context, image, firstIndex, null);
    }

    /**
     * 单图
     *
     * @param context
     * @param image
     * @param firstIndex
     * @param shareView  要共享的控件
     */
    public static <T extends DragImage> void startWithElement(Activity context, T image, int firstIndex, View shareView) {
        //获取当前图片是否是gif，如果是gif就必须关闭共享元素动画，不然gif不能播放
        boolean haveGif = false;
        if(image.getDragImageUrl().endsWith(".gif")) {
            haveGif = true;
        }
        ArrayList<String> urls = new ArrayList<>();
        urls.add(image.getDragImageUrl());
        Intent intent = new Intent(context, DragImageViewer.class);
        intent.putStringArrayListExtra("urls", urls);
        intent.putExtra("index", firstIndex);
        intent.putExtra("haveGif", haveGif);
        if (shareView == null || haveGif) {
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.fad_in, R.anim.fad_out);
        } else {
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(context, shareView, SHARE_VIEW_TAG);
            ActivityCompat.startActivity(context, intent, compat.toBundle());
        }

    }

    /**
     * 无需共享元素动画的多图
     *
     * @param context
     * @param images
     * @param firstIndex
     * @param <T>
     */
    public static <T extends DragImage> void startWithoutElement(Activity context, List<T> images, int firstIndex) {
        startWithElement(context, images, firstIndex, null);
    }

    /**
     * 多图
     *
     * @param context
     * @param images
     * @param firstIndex
     * @param shareView  要共享的控件
     */
    public static <T extends DragImage> void startWithElement(Activity context, List<T> images, int firstIndex, View shareView) {
        //获取当前第一张图片是否是gif，如果是gif就必须关闭共享元素动画，不然gif不能播放
        boolean haveGif = false;
        if(images.get(firstIndex).getDragImageUrl().endsWith(".gif")) {
            haveGif = true;
        }
        ArrayList<String> urls = new ArrayList<>();
        for (T t : images) {
            urls.add(t.getDragImageUrl());
        }
        Intent intent = new Intent(context, DragImageViewer.class);
        intent.putStringArrayListExtra("urls", urls);
        intent.putExtra("index", firstIndex);
        intent.putExtra("haveGif", haveGif);
        if (shareView == null || haveGif) {
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.fad_in, R.anim.fad_out);
        } else {
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(context, shareView, SHARE_VIEW_TAG);
            ActivityCompat.startActivity(context, intent, compat.toBundle());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_browse);
        initView();
        initListener();
    }

    public void initView() {
        mPermissionHelper = new PermissionHelper(this, PermissionHelper.WRITE_EXTERNAL_STORAGE);
        pictureList = getIntent().getStringArrayListExtra("urls");
        firstDisplayImageIndex = Math.min(getIntent().getIntExtra("index", firstDisplayImageIndex), pictureList.size());
        ((TextView) findViewById(R.id.tv_total_page)).setText(String.valueOf(pictureList.size()));
        tv_current_page = findViewById(R.id.tv_current_page);
        tv_current_page.setText(String.valueOf(firstDisplayImageIndex + 1));
        imageViewPager = findViewById(R.id.viewpager);
        tv_download = findViewById(R.id.tv_download);
        setViewPagerAdapter();

        boolean mHaveGif = getIntent().getBooleanExtra("haveGif", false);
        if(!mHaveGif) {
            setEnterSharedElementCallback(new SharedElementCallback() {

                @Override
                public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                    super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);
                }

                @Override
                public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                    super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                }

                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    //onMapSharedElements 只有在页面打开和关闭的时候调用
                    //页面进入的时候先调用onMapSharedElements,onSharedElementStart，后调用onSharedElementEnd
                    //页面退出的时候先调用onMapSharedElements,onSharedElementEnd，后调用onSharedElementStart
                    ViewGroup layout = imageViewPager.findViewWithTag(imageViewPager.getCurrentItem());
                    if (layout == null) {
                        return;
                    }
                    PhotoView sharedView = layout.findViewById(R.id.image_view);
                    sharedElements.clear();
                    sharedElements.put(SHARE_VIEW_TAG, sharedView);
                }
            });
        }
    }

    private void initListener() {
        //下载原图到本地
        tv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePic();
            }
        });
    }


    private void setViewPagerAdapter() {
        adapter = new DragImagePagerAdapter();
        imageViewPager.setAdapter(adapter);
        imageViewPager.setOffscreenPageLimit(1);
        imageViewPager.setCurrentItem(firstDisplayImageIndex);
        imageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset == 0f && newPageSelected) {
                    newPageSelected = false;
                    updateCurrentImageView(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                newPageSelected = true;
                tv_current_page.setText(String.valueOf(position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        imageViewPager.setiAnimClose(new BaseDragViewPager.IAnimClose() {

            @Override
            public void onPictureClick() {
                finishThis();
            }

            @Override
            public void onPictureRelease(View view) {
                finishThis();
            }
        });
    }


    // 初始化每个view的image
    protected void updateCurrentImageView(final int position) {
        View currentLayout = imageViewPager.findViewWithTag(position);
        if (currentLayout == null) {
            ViewCompat.postOnAnimation(imageViewPager, new Runnable() {

                @Override
                public void run() {
                    updateCurrentImageView(position);
                }
            });
            return;
        }
        mCurImage = currentLayout.findViewById(R.id.image_view);
        mCurImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishThis();
            }
        });
        imageViewPager.setCurrentShowView(mCurImage);
    }


    private void finishThis() {
        Intent intent = new Intent();
        intent.putExtra("index", imageViewPager.getCurrentItem());
        setResult(RESULT_OK, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.bindRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fad_in, R.anim.fad_out);
    }

    /**
     * 保存图片
     */
    private void savePic() {
        tv_download.setVisibility(View.GONE);
        mPermissionHelper.checkPermissionForce(new PermissionHelper.ForcePermissionCallbacks() {
            @Override
            public void onPermissionsAllGranted() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String picUrl = pictureList.get(imageViewPager.getCurrentItem());
                        String[] pic = picUrl.split("[.]");
                        try {
                            File mFile = Glide.with(DragImageViewer.this)
                                    .downloadOnly()
                                    .load(picUrl)
                                    .submit()
                                    .get();
                            //获取目录
                            File appDir = new File(Environment.getExternalStorageDirectory(), "Pictures");
                            if (!appDir.exists()) {
                                appDir.mkdirs();
                            }
                            File destFile = new File(appDir, System.currentTimeMillis() + "." + pic[pic.length - 1]);
                            //把gilde下载得到图片复制到定义好的目录中去
                            copy(mFile, destFile);
                            // 最后通知图库更新
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                    Uri.fromFile(new File(destFile.getPath()))));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(DragImageViewer.this, "图片已保存至本地相册", Toast.LENGTH_SHORT).show();
                                    tv_download.setVisibility(View.VISIBLE);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onPermissionsDenied() {
                tv_download.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 复制文件
     *
     * @param source 输入文件
     * @param target 输出文件
     */
    public void copy(File source, File target) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //添加图片到图库
    private void updateToSystemAlbum(Context context, File imageFile) {
        if (null == imageFile) return;
        String imagePath = imageFile.getAbsolutePath();
        String imageName = imageFile.getName();
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), imagePath, imageName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //第二个参数要是 Uri.parse("file://" + imagePath); 其他格式的uri无效
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imagePath)));
    }


    class DragImagePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return pictureList == null ? 0 : pictureList.size();
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View layout = LayoutInflater.from(DragImageViewer.this).inflate(R.layout.layout_browse, null);
            ImageView iv = layout.findViewById(R.id.image_view);
            GlideUtils.loadImage(DragImageViewer.this, pictureList.get(position), iv);
            container.addView(layout);
            layout.setTag(position);
            //保证刚进入的第一次可以下滑
            if (position == firstDisplayImageIndex) {
                updateCurrentImageView(position);
            }
            return layout;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

}