package com.kekegdsz.imagepicker;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.kekegdsz.imagepicker.entity.LocalMedia;
import com.kekegdsz.imagepicker.widget.SuperCheckBox;

import java.util.ArrayList;


public class ImagePreviewActivity extends ImagePreviewBaseActivity {

    public static final String ISORIGIN = "isOrigin";

    private boolean isOrigin;                      //是否选中原图
    private SuperCheckBox mCbCheck;                //是否选中当前图片的CheckBox
    private SuperCheckBox mCbOrigin;               //原图
    private Button mBtnOk;                         //确认图片的选择
    private View bottomBar;

    public static void start(Context context, ArrayList<LocalMedia> images, int position) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra(EXTRA_SELECTED_IMAGE_POSITION, position);
        intent.putExtra(EXTRA_IMAGE_ITEMS, images);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isOrigin = getIntent().getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
        mBtnOk = topBar.findViewById(R.id.btn_ok);
        mBtnOk.setVisibility(View.VISIBLE);
        bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setVisibility(View.VISIBLE);
        mCbCheck = (SuperCheckBox) findViewById(R.id.cb_check);
        mCbOrigin = (SuperCheckBox) findViewById(R.id.cb_origin);
        LocalMedia item = mImageItems.get(mCurrentPosition);

    }


    /**
     * 单击时，隐藏头和尾
     */
    @Override
    public void onImageSingleTap() {
        if (topBar.getVisibility() == View.VISIBLE) {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_out));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            topBar.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
            tintManager.setStatusBarTintResource(R.color.transparent);//通知栏所需颜色
            //给最外层布局加上这个属性表示，Activity全屏显示，且状态栏被隐藏覆盖掉。
            if (Build.VERSION.SDK_INT >= 16)
                content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_in));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            topBar.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
            tintManager.setStatusBarTintResource(R.color.status_bar);//通知栏所需颜色
            //Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住
            if (Build.VERSION.SDK_INT >= 16)
                content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}
