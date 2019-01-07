package com.junglezhang.dragimageviewlib;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import com.github.chrisbanes.photoview.PhotoView;
import com.junglezhang.dragimageviewlib.base.DragImage;
import com.junglezhang.dragimageviewlib.utils.GlideUtils;
import com.junglezhang.dragimageviewlib.widget.BaseDragViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by Jungle on 2018/12/24 0024.
 *
 * @desc ViewPager容器
 */
public class DragImageViewer extends AppCompatActivity {

    public static final String SHARE_VIEW_TAG = "share_view";

    private int firstDisplayImageIndex = 0;
    private boolean newPageSelected = false;
    private PhotoView mCurImage;
    private BaseDragViewPager imageViewPager;
    private List<String> pictureList;
    private TextView tv_current_page;
    private PagerAdapter adapter;
    private boolean isStart = true;


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
    public static <T extends DragImage> void startWithElement(Activity context, T image,
                                                              int firstIndex, View shareView) {
        ArrayList<String> urls = new ArrayList<>();
        urls.add(image.getDragImageUrl());
        Intent intent = new Intent(context, DragImageViewer.class);
        intent.putStringArrayListExtra("urls", urls);
        intent.putExtra("index", firstIndex);
        if (shareView == null) {
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
    public static <T extends DragImage> void startWithElement(Activity context, List<T> images,
                                                              int firstIndex, View shareView) {
        ArrayList<String> urls = new ArrayList<>();
        for (T t : images) {
            urls.add(t.getDragImageUrl());
        }
        Intent intent = new Intent(context, DragImageViewer.class);
        intent.putStringArrayListExtra("urls", urls);
        intent.putExtra("index", firstIndex);
        if (shareView == null) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setEnterTransition(new Fade().setDuration(0));
//            getWindow().setExitTransition(new Fade().setDuration(0));
        }
        setContentView(R.layout.activity_photo_browse);
        initView();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fad_in, R.anim.fad_out);
    }

    public void initView() {
        pictureList = getIntent().getStringArrayListExtra("urls");
        firstDisplayImageIndex = Math.min(getIntent().getIntExtra("index", firstDisplayImageIndex), pictureList.size());
        ((TextView) findViewById(R.id.tv_total_page)).setText(String.valueOf(pictureList.size()));
        tv_current_page = findViewById(R.id.tv_current_page);
        tv_current_page.setText(String.valueOf(firstDisplayImageIndex + 1));
        imageViewPager = findViewById(R.id.viewpager);
        setViewPagerAdapter();

        setEnterSharedElementCallback(new SharedElementCallback() {

            @Override
            public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);
            }

            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                isStart = false;
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
                //这里重新适配一下图片，防止gif图在启动的时候停止动画
                if (isStart) {
                    GlideUtils.loadImage(DragImageViewer.this, pictureList.get(firstDisplayImageIndex), sharedView);
                }
            }

        });
    }

    private void setViewPagerAdapter() {
        adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return pictureList == null ? 0 : pictureList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
//                View layout = (View) object;
//                container.removeView(layout);
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
                layout.setOnClickListener(onClickListener);
                container.addView(layout);
                layout.setTag(position);
                if (position == firstDisplayImageIndex) {
                    updateCurrentImageView(position);
                }
                return layout;

            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }
        };

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
                finishAfterTransition();
            }

            @Override
            public void onPictureRelease(View view) {
                finishAfterTransition();
            }
        });
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finishAfterTransition();
        }
    };


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
        imageViewPager.setCurrentShowView(mCurImage);
    }


    @Override
    public void finishAfterTransition() {
        Intent intent = new Intent();
        intent.putExtra("index", imageViewPager.getCurrentItem());
        setResult(RESULT_OK, intent);
        super.finishAfterTransition();
    }
}