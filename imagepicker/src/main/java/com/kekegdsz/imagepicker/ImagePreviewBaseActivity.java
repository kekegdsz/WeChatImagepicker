package com.kekegdsz.imagepicker;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kekegdsz.imagepicker.adapter.ImagePageAdapter;
import com.kekegdsz.imagepicker.entity.LocalMedia;
import com.kekegdsz.imagepicker.utils.ScreenUtils;
import com.kekegdsz.imagepicker.widget.ViewPagerFixed;

import java.util.ArrayList;

public abstract class ImagePreviewBaseActivity extends ImageBaseActivity {

    public static final String EXTRA_SELECTED_IMAGE_POSITION = "selected_image_position";
    public static final String EXTRA_IMAGE_ITEMS = "extra_image_items";
    public ArrayList<LocalMedia> mImageItems;      //跳转进ImagePreviewFragment的图片文件夹
    public int mCurrentPosition = 0;
    public View content;
    public View topBar;
    public TextView mTitleCount;
    public ViewPagerFixed mViewPager;
    public ImagePageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview_base);
        mCurrentPosition = getIntent().getIntExtra(EXTRA_SELECTED_IMAGE_POSITION, 0);
        mImageItems = (ArrayList<LocalMedia>) getIntent().getSerializableExtra(EXTRA_IMAGE_ITEMS);

        //初始化控件
        content = findViewById(R.id.content);

        //因为状态栏透明后，布局整体会上移，所以给头部加上状态栏的margin值，保证头部不会被覆盖
        topBar = findViewById(R.id.top_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topBar.getLayoutParams();
            params.topMargin = ScreenUtils.getStatusHeight(this);
            topBar.setLayoutParams(params);
        }
        topBar.findViewById(R.id.btn_ok).setVisibility(View.GONE);
        topBar.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTitleCount = (TextView) findViewById(R.id.tv_des);
        mViewPager = (ViewPagerFixed) findViewById(R.id.viewpager);
        mAdapter = new ImagePageAdapter(this, mImageItems);
        mAdapter.setPhotoViewClickListener(new ImagePageAdapter.PhotoViewClickListener() {
            @Override
            public void OnPhotoTapListener(View view, float v, float v1) {
                onImageSingleTap();
            }
        });
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentPosition, false);

        //初始化当前页面的状态
        mTitleCount.setText(getString(R.string.preview_image_count, mCurrentPosition + 1, mImageItems.size()));
    }

    /**
     * 单击时，隐藏头和尾
     */
    public abstract void onImageSingleTap();
}
